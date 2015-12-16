/*
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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class SettingsActivity extends Activity implements View.OnClickListener {

    private static final int TYPE_SHIFT = 28;
    private static final int TYPE_MASK  = 0xf << TYPE_SHIFT;

    private static final int TYPE_ON_BATTERY = 0 << TYPE_SHIFT;
    private static final int TYPE_ON_BATTERY_DEFAULT = 1 << TYPE_SHIFT;
    private static final int TYPE_ON_BATTERY_ADD = 2 << TYPE_SHIFT;
    private static final int TYPE_CHARGING = 3 << TYPE_SHIFT;
    private static final int TYPE_CHARGING_DEFAULT = 4 << TYPE_SHIFT;
    private static final int TYPE_CHARGING_ADD = 5 << TYPE_SHIFT;

    private LinearLayout mOnBatteryDefault;
    private LinearLayout mOnBattery;
    private LinearLayout mOnBatteryAdd;

    private LinearLayout mChargingDefault;
    private LinearLayout mCharging;
    private LinearLayout mChargingAdd;

    private SharedPreferences mSharedPreferences;
    private BatteryItems mBatteryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mOnBatteryDefault = (LinearLayout) findViewById(R.id.on_battery_default);
        mOnBattery = (LinearLayout) findViewById(R.id.on_battery);
        mOnBatteryAdd = (LinearLayout) findViewById(R.id.on_battery_add);
        mChargingDefault = (LinearLayout) findViewById(R.id.charging_default);
        mCharging = (LinearLayout) findViewById(R.id.charging);
        mChargingAdd = (LinearLayout) findViewById(R.id.charging_add);
        TextView about = (TextView) findViewById(R.id.about);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mBatteryItems = new BatteryItems(mSharedPreferences);

        updateLayout();
        setBatteryItem(mOnBatteryAdd, R.string.add, new PlusDrawable(Color.BLACK));
        setBatteryItem(mChargingAdd, R.string.add, new PlusDrawable(Color.BLACK));
        about.setText(Html.fromHtml(getString(R.string.about_text)));
        about.setMovementMethod(LinkMovementMethod.getInstance());

        mOnBatteryDefault.setOnClickListener(this);
        mChargingDefault.setOnClickListener(this);
        mOnBatteryAdd.setOnClickListener(this);
        mChargingAdd.setOnClickListener(this);

        if (BatteryWidgetProvider.getNumberOfWidgets(this) > 0) {
            // Ensure service is running
            startService(new Intent(this, MonitorService.class));
            // Notify update widget
            notifyUpdateWidget();
        }
    }

    private void updateLayout() {
        setBatteryItem(mOnBatteryDefault, R.string._default, mBatteryItems.getOnBatteryDefaultId());
        setBatteryItem(mChargingDefault, R.string._default, mBatteryItems.getChargingDefaultId());

        LayoutInflater inflater = getLayoutInflater();
        mOnBattery.removeAllViews();
        for (int i = 0, length = mBatteryItems.getOnBatterySize(); i < length; i++) {
            inflater.inflate(R.layout.item_battery, mOnBattery, true);
            LinearLayout view = (LinearLayout) mOnBattery.getChildAt(mOnBattery.getChildCount() - 1);
            setBatteryItem(view, "≥ " + mBatteryItems.getOnBatteryLimit(i), mBatteryItems.getOnBatteryId(i));
            view.setOnClickListener(this);
        }
        mCharging.removeAllViews();
        for (int i = 0, length = mBatteryItems.getChargingSize(); i < length; i++) {
            inflater.inflate(R.layout.item_battery, mCharging, true);
            LinearLayout view = (LinearLayout) mCharging.getChildAt(mCharging.getChildCount() - 1);
            setBatteryItem(view, "≥ " + mBatteryItems.getChargingLimit(i), mBatteryItems.getChargingId(i));
            view.setOnClickListener(this);
        }
    }

    private void setBatteryItem(LinearLayout view, String text, int resId) {
        ((TextView) view.findViewById(R.id.text)).setText(text);
        ((ImageView) view.findViewById(R.id.image)).setImageResource(resId);
    }

    private void setBatteryItem(LinearLayout view, int stringId, int resId) {
        ((TextView) view.findViewById(R.id.text)).setText(stringId);
        ((ImageView) view.findViewById(R.id.image)).setImageResource(resId);
    }

    private void setBatteryItem(LinearLayout view, int stringId, Drawable drawable) {
        ((TextView) view.findViewById(R.id.text)).setText(stringId);
        ((ImageView) view.findViewById(R.id.image)).setImageDrawable(drawable);
    }

    private static int makeRequestCode(int index, int type) {
        return (index & ~TYPE_MASK) | (type & TYPE_MASK);
    }

    public static int getIndex(int requestCode) {
        return requestCode & ~TYPE_MASK;
    }

    public static int getType(int requestCode) {
        return requestCode & TYPE_MASK;
    }

    @Override
    public void onClick(View v) {
        if (mOnBatteryDefault == v) {
            Intent intent = new Intent(this, SelectBatteryImageActivity.class);
            intent.putExtra(SelectBatteryImageActivity.KEY_DEFAULT, true);
            startActivityForResult(intent, makeRequestCode(0, TYPE_ON_BATTERY_DEFAULT));
        } else if (mChargingDefault == v) {
            Intent intent = new Intent(this, SelectBatteryImageActivity.class);
            intent.putExtra(SelectBatteryImageActivity.KEY_DEFAULT, true);
            startActivityForResult(intent, makeRequestCode(0, TYPE_CHARGING_DEFAULT));
        } else if (mOnBatteryAdd == v) {
            Intent intent = new Intent(this, SelectBatteryImageActivity.class);
            intent.putExtra(SelectBatteryImageActivity.KEY_DEFAULT, false);
            startActivityForResult(intent, makeRequestCode(0, TYPE_ON_BATTERY_ADD));
        } else if (mChargingAdd == v) {
            Intent intent = new Intent(this, SelectBatteryImageActivity.class);
            intent.putExtra(SelectBatteryImageActivity.KEY_DEFAULT, false);
            startActivityForResult(intent, makeRequestCode(0, TYPE_CHARGING_ADD));
        } else {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            BatteryItems batteryItems = new BatteryItems(sharedPreferences);

            int index = mOnBattery.indexOfChild(v);
            if (index != -1) {
                Intent intent = new Intent(this, SelectBatteryImageActivity.class);
                intent.putExtra(SelectBatteryImageActivity.KEY_DEFAULT, false);
                intent.putExtra(SelectBatteryImageActivity.KEY_LIMIT, batteryItems.getOnBatteryLimit(index));
                startActivityForResult(intent, makeRequestCode(index, TYPE_ON_BATTERY));
            } else {
                index = mCharging.indexOfChild(v);
                if (index != -1) {
                    Intent intent = new Intent(this, SelectBatteryImageActivity.class);
                    intent.putExtra(SelectBatteryImageActivity.KEY_DEFAULT, false);
                    intent.putExtra(SelectBatteryImageActivity.KEY_LIMIT, batteryItems.getChargingLimit(index));
                    startActivityForResult(intent, makeRequestCode(index, TYPE_CHARGING));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        int index = getIndex(requestCode);
        int type = getType(requestCode);
        int limit = -1;
        int id = -1;

        if (data != null) {
            limit = data.getIntExtra(SelectBatteryImageActivity.KEY_LIMIT, -1);
            id = data.getIntExtra(SelectBatteryImageActivity.KEY_ID, -1);
        }

        switch (type) {
            case TYPE_ON_BATTERY:
                if (index == -1 || limit < 0) {
                    return;
                }
                if (id == 0) {
                    mBatteryItems.removeOnBattery(index);
                } else if (id > 0) {
                    mBatteryItems.setOnBattery(index, limit, id);
                }
                break;
            case TYPE_ON_BATTERY_DEFAULT:
                if (id <= 0) {
                    return;
                }
                mBatteryItems.setOnBatteryDefaultId(id);
                break;
            case TYPE_ON_BATTERY_ADD:
                if (limit < 0 || id <= 0) {
                    return;
                }
                mBatteryItems.addOnBattery(limit, id);
                break;
            case TYPE_CHARGING:
                if (index == -1 || limit < 0) {
                    return;
                }
                if (id == 0) {
                    mBatteryItems.removeCharging(index);
                } else if (id > 0) {
                    mBatteryItems.setCharging(index, limit, id);
                }
                break;
            case TYPE_CHARGING_DEFAULT:
                if (id <= 0) {
                    return;
                }
                mBatteryItems.setChargingDefaultId(id);
                break;
            case TYPE_CHARGING_ADD:
                if (limit < 0 || id <= 0) {
                    return;
                }
                mBatteryItems.addCharging(limit, id);
                break;
        }

        mBatteryItems.saveToSharedPreferences(this, mSharedPreferences);
        updateLayout();

        // Notify update widget
        notifyUpdateWidget();
    }

    private void notifyUpdateWidget() {
        int[] ids = BatteryWidgetProvider.getWidgetIds(this);
        Intent intent = new Intent(this, BatteryWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
