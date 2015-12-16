/*
 * Copyright 2015 Erkan Molla
 * Copyright 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.acbattery;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class UpdateService extends IntentService {

    public static final String ACTION_BATTERY_CHANGED = "com.hippo.battery.action.BATTERY_CHANGED";
    public static final String ACTION_BATTERY_LOW = "com.hippo.battery.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "com.hippo.battery.action.BATTERY_OKAY";
    public static final String ACTION_WIDGET_UPDATE = "com.hippo.battery.action.WIDGET_UPDATE";
    public static final String ACTION_UPDATE = "com.hippo.battery.action.UPDATE";

    public static final String EXTRA_WIDGET_IDS = "com.hippo.battery.extra.WIDGET_IDS";

    private static final int TEXT_SIZE_DP = 12;
    private static final int TEXT_OUTLINE_DP = 2;

    private Paint mStrokePaint;
    private Paint mTextPaint;
    private Rect mRect;

    private boolean mInit = false;

    public UpdateService() {
        super("UpdateService");

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setARGB(255, 0, 0, 0);
        mStrokePaint.setTextAlign(Paint.Align.LEFT);
        mStrokePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mStrokePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setARGB(255, 255, 255, 255);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mRect = new Rect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (ACTION_BATTERY_CHANGED.equals(action)) {
            // Save battery info
            BatteryInfo newBatteryInfo = new BatteryInfo(intent);
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            newBatteryInfo.saveToSharedPreferences(sharedPreferences);
            // Update widget
            RemoteViews remoteViews = createRemoteViews(newBatteryInfo);
            ComponentName componentName = new ComponentName(this, BatteryWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        } else if (ACTION_BATTERY_LOW.equals(action)) {
            // TODO
        } else if (ACTION_BATTERY_OKAY.equals(action)) {
            // TODO
        } else if (ACTION_WIDGET_UPDATE.equals(action)) {
            // Load battery info and update widget
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            BatteryInfo batteryInfo = new BatteryInfo(sharedPreferences);
            RemoteViews remoteViews = createRemoteViews(batteryInfo);
            final int[] widgetIds = intent.getIntArrayExtra(EXTRA_WIDGET_IDS);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(widgetIds, remoteViews);
        } else if (ACTION_UPDATE.equals(action)) {
            // Load battery info and update widget
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            BatteryInfo batteryInfo = new BatteryInfo(sharedPreferences);
            RemoteViews remoteViews = createRemoteViews(batteryInfo);
            ComponentName componentName = new ComponentName(this, BatteryWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }

    private Bitmap getStringBitmap(String str) {
        if (!mInit) {
            mInit = true;
            mStrokePaint.setTextSize(Utils.dp2pix(this, TEXT_SIZE_DP));
            mStrokePaint.setStrokeWidth(2 * Utils.dp2pix(this, TEXT_OUTLINE_DP));
            mTextPaint.setTextSize(Utils.dp2pix(this, TEXT_SIZE_DP));
        }

        mStrokePaint.getTextBounds(str, 0, str.length(), mRect);

        int offset = Utils.dp2pix(this, TEXT_OUTLINE_DP);
        Bitmap bitmap = Bitmap.createBitmap(
                mRect.width() + offset * 2, mRect.height() + offset * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        canvas.drawText(str, -mRect.left + offset, -mRect.top + offset, mStrokePaint);
        canvas.drawText(str, -mRect.left + offset, -mRect.top + offset, mTextPaint);

        return bitmap;
    }

    private RemoteViews createRemoteViews(BatteryInfo batteryInfo) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.battery_widget);
        remoteViews.setImageViewBitmap(R.id.level,
                getStringBitmap(Integer.toString(batteryInfo.getLevel()) + "%"));
        remoteViews.setImageViewBitmap(R.id.temperature,
                getStringBitmap(Float.toString(batteryInfo.getTemperature() / 10.0f) + "℃"));

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        remoteViews.setImageViewResource(R.id.image,
                new BatteryItems(sharedPreferences).getDrawableId(batteryInfo));
        return remoteViews;
    }
}
