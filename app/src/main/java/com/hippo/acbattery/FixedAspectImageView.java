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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class FixedAspectImageView extends ImageView {

    private static final int[] MIN_ATTRS = {
            android.R.attr.minWidth,
            android.R.attr.minHeight
    };

    private static final int[] ATTRS = {
            android.R.attr.adjustViewBounds,
            android.R.attr.maxWidth,
            android.R.attr.maxHeight
    };

    private int mMinWidth = 0;
    private int mMinHeight = 0;
    private int mMaxWidth = Integer.MAX_VALUE;
    private int mMaxHeight = Integer.MAX_VALUE;
    private boolean mAdjustViewBounds = false;

    // width / height
    private float mAspect = -1f;

    public FixedAspectImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FixedAspectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FixedAspectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @SuppressWarnings("ResourceType")
    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a;

        // Make sure we get value from xml
        a = context.obtainStyledAttributes(attrs, MIN_ATTRS, defStyle, 0);
        setMinimumWidth(a.getDimensionPixelSize(0, 0));
        setMinimumHeight(a.getDimensionPixelSize(1, 0));
        a.recycle();

        a = context.obtainStyledAttributes(attrs, ATTRS, defStyle, 0);
        setAdjustViewBounds(a.getBoolean(0, false));
        setMaxWidth(a.getDimensionPixelSize(1, Integer.MAX_VALUE));
        setMaxHeight(a.getDimensionPixelSize(2, Integer.MAX_VALUE));
        a.recycle();

        a = context.obtainStyledAttributes(
                attrs, R.styleable.FixedAspectImageView, defStyle, 0);
        setAspect(a.getFloat(R.styleable.FixedAspectImageView_aspect, -1f));
        a.recycle();
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(minWidth);
        mMinWidth = minWidth;
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(minHeight);
        mMinHeight = minHeight;
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
        mMaxWidth = maxWidth;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        super.setMaxHeight(maxHeight);
        mMaxHeight = maxHeight;
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        super.setAdjustViewBounds(adjustViewBounds);
        mAdjustViewBounds = adjustViewBounds;
    }

    /**
     * Enable aspect will set AdjustViewBounds true.
     * Any negative float to disable it,
     * disable Aspect will not disable AdjustViewBounds.
     *
     * @param ratio width/height
     */
    public void setAspect(float ratio) {
        if (ratio > 0) {
            mAspect = ratio;
        } else {
            mAspect = -1f;
        }
        requestLayout();
    }

    public float getAspect() {
        return mAspect;
    }

    /**
     * Returns the input value x clamped to the range [min, max].
     */
    public static int clamp(int x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    private int resolveAdjustedSize(int desiredSize, int minSize, int maxSize,
            int measureSpec) {
        int result = desiredSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize =  View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                // Parent says we can be as big as we want. Just don't be smaller
                // than min size, and don't be larger than max size.
                result = clamp(desiredSize, minSize, maxSize);
                break;
            case View.MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be smaller
                // than min size, and don't be larger than max size.
                result = Math.min(clamp(desiredSize, minSize, maxSize), specSize);
                break;
            case View.MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private boolean isSizeAcceptable(int size, int minSize, int maxSize, int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize =  View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                // Parent says we can be as big as we want. Just don't be smaller
                // than min size, and don't be larger than max size.
                return size >= minSize && size <= maxSize;
            case View.MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be smaller
                // than min size, and don't be larger than max size.
                return size <= specSize && size >= minSize && size <= maxSize;
            case View.MeasureSpec.EXACTLY:
                // No choice.
                return size == specSize;
            default:
                // WTF? Return true to make you happy. (´・ω・`)
                return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w;
        int h;

        // Desired aspect ratio of the view's contents (not including padding)
        float desiredAspect = 0.0f;

        // We are allowed to change the view's width
        boolean resizeWidth = false;

        // We are allowed to change the view's height
        boolean resizeHeight = false;

        final int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);

        Drawable drawable = getDrawable();
        if (drawable == null) {
            // If no drawable, its intrinsic size is 0.
            w = h = 0;

            // Aspect is forced set.
            if (mAspect > 0.0f) {
                resizeWidth = widthSpecMode != View.MeasureSpec.EXACTLY;
                resizeHeight = heightSpecMode != View.MeasureSpec.EXACTLY;
                desiredAspect = mAspect;
            }
        } else {
            w = drawable.getIntrinsicWidth();
            h = drawable.getIntrinsicHeight();
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;

            if (mAdjustViewBounds) {
                // We are supposed to adjust view bounds to match the aspect
                // ratio of our drawable. See if that is possible.
                resizeWidth = widthSpecMode != View.MeasureSpec.EXACTLY;
                resizeHeight = heightSpecMode != View.MeasureSpec.EXACTLY;
                desiredAspect = (float) w / (float) h;
            } else if (mAspect > 0.0f) {
                // Aspect is forced set.
                resizeWidth = widthSpecMode != View.MeasureSpec.EXACTLY;
                resizeHeight = heightSpecMode != View.MeasureSpec.EXACTLY;
                desiredAspect = mAspect;
            }
        }

        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int widthSize;
        int heightSize;

        if (resizeWidth || resizeHeight) {
            // If we get here, it means we want to resize to match the
            // drawables aspect ratio, and we have the freedom to change at
            // least one dimension.

            // Get the max possible width given our constraints
            widthSize = resolveAdjustedSize(w + pLeft + pRight, mMinWidth, mMaxWidth, widthMeasureSpec);

            // Get the max possible height given our constraints
            heightSize = resolveAdjustedSize(h + pTop + pBottom, mMinHeight, mMaxHeight, heightMeasureSpec);

            if (desiredAspect != 0.0f) {
                // See what our actual aspect ratio is
                float actualAspect = (float)(widthSize - pLeft - pRight) /
                        (heightSize - pTop - pBottom);

                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {
                    boolean done = false;

                    // Try adjusting width to be proportional to height
                    if (resizeWidth) {
                        int newWidth = (int)(desiredAspect * (heightSize - pTop - pBottom)) +
                                pLeft + pRight;

                        // Allow the width to outgrow its original estimate if height is fixed.
                        //if (!resizeHeight) {
                        //widthSize = resolveAdjustedSize(newWidth, mMinWidth, mMaxWidth, widthMeasureSpec);
                        //}

                        if (isSizeAcceptable(newWidth, mMinWidth, mMaxWidth, widthMeasureSpec)) {
                            widthSize = newWidth;
                            done = true;
                        }
                    }

                    // Try adjusting height to be proportional to width
                    if (!done && resizeHeight) {
                        int newHeight = (int)((widthSize - pLeft - pRight) / desiredAspect) +
                                pTop + pBottom;

                        // Allow the height to outgrow its original estimate if width is fixed.
                        if (!resizeWidth) {
                            heightSize = resolveAdjustedSize(newHeight, mMinHeight, mMaxHeight,
                                    heightMeasureSpec);
                        }

                        if (isSizeAcceptable(newHeight, mMinHeight, mMaxHeight, heightMeasureSpec)) {
                            heightSize = newHeight;
                        }
                    }
                }
            }
        } else {
            // We are either don't want to preserve the drawables aspect ratio,
            // or we are not allowed to change view dimensions. Just measure in
            // the normal way.
            w += pLeft + pRight;
            h += pTop + pBottom;

            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());

            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec. Will take the desired size, unless a different size
     * is imposed by the constraints. The returned value is a compound integer,
     * with the resolved size in the {@link #MEASURED_SIZE_MASK} bits and
     * optionally the bit {@link #MEASURED_STATE_TOO_SMALL} set if the
     * resulting size is smaller than the size the view wants to be.
     *
     * @param size How big the view wants to be.
     * @param measureSpec Constraints imposed by the parent.
     * @param childMeasuredState Size information bit mask for the view's
     *                           children.
     * @return Size information bit mask as defined by
     *         {@link #MEASURED_SIZE_MASK} and
     *         {@link #MEASURED_STATE_TOO_SMALL}.
     */
    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        final int result;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | 0x01000000;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = size;
        }
        return result | (childMeasuredState & 0xff000000);
    }
}
