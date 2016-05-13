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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class PlusDrawable extends Drawable {

    private final Paint mPaint;
    private final RectF mRectF;

    public PlusDrawable(int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mRectF = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        RectF rectF = mRectF;
        int m;
        int n;

        m = rect.width() / 7 * 3;
        n = rect.height() / 7;
        rectF.left = rect.left + m;
        rectF.right = rect.right - m;
        rectF.top = rect.top + n;
        rectF.bottom = rect.bottom - n;
        canvas.drawRect(rectF, mPaint);

        m = rect.height() / 7 * 3;
        n = rect.width() / 7;
        rectF.left = rect.left + n;
        rectF.right = rect.right - n;
        rectF.top = rect.top + m;
        rectF.bottom = rect.bottom - m;
        canvas.drawRect(rectF, mPaint);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
