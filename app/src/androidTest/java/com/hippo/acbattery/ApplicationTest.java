package com.hippo.acbattery;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.test.ApplicationTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final int TEXT_SIZE_DP = 12;
    private static final int TEXT_OUTLINE_DP = 2;

    public ApplicationTest() {
        super(Application.class);
    }

    public void testPreview() throws FileNotFoundException {
        final int width = 216;
        final int height = 258;

        Context context = getContext();

        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setARGB(255, 0, 0, 0);
        strokePaint.setTextAlign(Paint.Align.LEFT);
        strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setTextSize(Utils.dp2pix(context, TEXT_SIZE_DP));
        strokePaint.setStrokeWidth(2 * Utils.dp2pix(context, TEXT_OUTLINE_DP));

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(Utils.dp2pix(context, TEXT_SIZE_DP));

        Rect rect = new Rect();

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.battery_widget, null);
        viewGroup.setLayoutParams(new ViewGroup.LayoutParams(width, height));

        ImageView image = (ImageView) viewGroup.findViewById(R.id.image);
        ImageView temperature = (ImageView) viewGroup.findViewById(R.id.temperature);
        ImageView level = (ImageView) viewGroup.findViewById(R.id.level);

        image.setImageResource(R.drawable.ac16);
        temperature.setImageBitmap(UpdateService.createOutlineStringBitmap(
                context, "23.3℃", strokePaint, textPaint, rect));
        level.setImageBitmap(UpdateService.createOutlineStringBitmap(
                context, "100%", strokePaint, textPaint, rect));

        viewGroup.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        viewGroup.layout(0, 0, width, height);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        viewGroup.draw(canvas);

        OutputStream os = new FileOutputStream(new File(getContext().getFilesDir(), "preview.png"));
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
    }
}