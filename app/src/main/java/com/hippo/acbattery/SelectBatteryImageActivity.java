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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public final class SelectBatteryImageActivity extends Activity implements View.OnClickListener {

    public static final String KEY_DEFAULT = "default";
    public static final String KEY_LIMIT = "limit";
    /**
     * -1 for error, 0 for delete
     */
    public static final String KEY_ID = "id";

    private EditText mEditText;
    private SimpleGridLayout mLayout;

    private boolean mDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_battery_image);

        Intent intent = getIntent();
        mDefault = intent == null || intent.getBooleanExtra(KEY_DEFAULT, true);

        ViewGroup limitView = (ViewGroup) findViewById(R.id.limit);
        mEditText = (EditText) limitView.findViewById(R.id.edit_text);
        mLayout = (SimpleGridLayout) findViewById(R.id.simple_grid_layout);

        if (mDefault) {
            limitView.setVisibility(View.GONE);
        } else {
            if (intent != null) {
                int limit = intent.getIntExtra(KEY_LIMIT, -1);
                if (limit >= 0) {
                    mEditText.setText(Integer.toString(limit));
                }
            }
        }

        mLayout.setMinItemWidth(Utils.dp2pix(this, 72));

        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0, length = BatteryImage.BATTERY_IMAGE_IDS.length; i < length; i++) {
            inflater.inflate(R.layout.item_battery_image, mLayout);
            ImageView image = (ImageView) mLayout.getChildAt(mLayout.getChildCount() - 1);
            image.setImageResource(BatteryImage.BATTERY_IMAGE_IDS[i]);
            image.setOnClickListener(this);
        }

        if (!mDefault) {
            inflater.inflate(R.layout.item_battery_image, mLayout);
            ImageView image = (ImageView) mLayout.getChildAt(mLayout.getChildCount() - 1);
            image.setImageDrawable(new DeleteDrawable(Color.BLACK));
            image.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id;
        int index = mLayout.indexOfChild(v);

        if (index == -1) {
            id = -1;
        } else if (index == BatteryImage.BATTERY_IMAGE_IDS.length) {
            id = 0;
        } else {
            id = BatteryImage.BATTERY_IMAGE_IDS[index];
        }

        Intent intent = new Intent();
        intent.putExtra(KEY_ID, id);

        if (!mDefault) {
            String str = mEditText.getText().toString();
            int limit;
            try {
                limit = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Please type valid number", Toast.LENGTH_SHORT).show(); // TODO hardcode
                return;
            }
            intent.putExtra(KEY_LIMIT, limit);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
