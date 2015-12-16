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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SimpleGridLayout extends ViewGroup {

    private int mMinItemWidth = 1;

    public SimpleGridLayout(Context context) {
        super(context);
    }

    public SimpleGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMinItemWidth(int minItemWidth) {
        mMinItemWidth = minItemWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // Fix width size when width mode is MeasureSpec.UNSPECIFIED
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = mMinItemWidth * 5;
        }

        // Fix min item width
        int minItemWidth;
        if (widthSize < mMinItemWidth) {
            minItemWidth = widthSize;
        } else {
            minItemWidth = mMinItemWidth;
        }

        int row = widthSize / minItemWidth;
        int rowIndex = -1;
        int defaultItemWidth = widthSize / row;
        int remain = widthSize - minItemWidth * row;
        int height = 0;
        int rowHeight = 0;
        for (int index = 0, childCount = getChildCount(); index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            rowIndex = (rowIndex + 1) % row;
            int itemWidth;
            if (rowIndex < remain) {
                itemWidth = defaultItemWidth + 1;
            } else {
                itemWidth = defaultItemWidth;
            }
            if (rowIndex == 0) {
                height += rowHeight;
                rowHeight = 0;
            }

            child.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            rowHeight = Math.max(rowHeight, child.getMeasuredHeight());
        }
        // Add last row height
        height += rowHeight;

        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();

        // Fix min item width
        int minItemWidth;
        if (width < mMinItemWidth) {
            minItemWidth = width;
        } else {
            minItemWidth = mMinItemWidth;
        }

        int row = width / minItemWidth;
        int rowIndex = -1;
        int rowHeight = 0;
        int itemX = 0;
        int itemY = 0;
        for (int index = 0, childCount = getChildCount(); index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            rowIndex = (rowIndex + 1) % row;
            if (rowIndex == 0) {
                itemX = 0;
                itemY += rowHeight;
                rowHeight = 0;
            }

            child.layout(itemX, itemY, itemX + child.getMeasuredWidth(), itemY + child.getMeasuredHeight());

            itemX += child.getMeasuredWidth();
            rowHeight = Math.max(rowHeight, child.getMeasuredHeight());
        }
    }
}
