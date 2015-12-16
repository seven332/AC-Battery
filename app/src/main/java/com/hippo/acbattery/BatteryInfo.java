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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;

public class BatteryInfo {

    public static final String EXTRA_STATUS = BatteryManager.EXTRA_STATUS;
    public static final String EXTRA_HEALTH = BatteryManager.EXTRA_HEALTH;
    public static final String EXTRA_PRESENT = BatteryManager.EXTRA_PRESENT;
    public static final String EXTRA_LEVEL = BatteryManager.EXTRA_LEVEL;
    public static final String EXTRA_SCALE = BatteryManager.EXTRA_SCALE;
    public static final String EXTRA_ICON_SMALL = BatteryManager.EXTRA_ICON_SMALL;
    public static final String EXTRA_PLUGGED = BatteryManager.EXTRA_PLUGGED;
    public static final String EXTRA_VOLTAGE = BatteryManager.EXTRA_VOLTAGE;
    public static final String EXTRA_TEMPERATURE = BatteryManager.EXTRA_TEMPERATURE;
    public static final String EXTRA_TECHNOLOGY = BatteryManager.EXTRA_TECHNOLOGY;

    private int mStatus;
    private int mHealth;
    private boolean mPresent;
    private int mLevel;
    private int mScale;
    private int mIconSmallResId;
    private int mPlugged;
    private int mVoltage;
    private int mTemperature;
    private String mTechnology;

    public BatteryInfo(final Intent intent) {
        mStatus = intent.getIntExtra(EXTRA_STATUS, 0);
        mHealth = intent.getIntExtra(EXTRA_HEALTH, 0);
        mPresent = intent.getBooleanExtra(EXTRA_PRESENT, false);
        mLevel = intent.getIntExtra(EXTRA_LEVEL, 0);
        mScale = intent.getIntExtra(EXTRA_SCALE, 0);
        mIconSmallResId = intent.getIntExtra(EXTRA_ICON_SMALL, 0);
        mPlugged = intent.getIntExtra(EXTRA_PLUGGED, 0);
        mVoltage = intent.getIntExtra(EXTRA_VOLTAGE, 0);
        mTemperature = intent.getIntExtra(EXTRA_TEMPERATURE, 0);
        mTechnology = intent.getStringExtra(EXTRA_TECHNOLOGY);
    }

    public BatteryInfo(final SharedPreferences sharedPreferences) {
        mStatus = sharedPreferences.getInt(EXTRA_STATUS, 0);
        mHealth = sharedPreferences.getInt(EXTRA_HEALTH, 0);
        mPresent = sharedPreferences.getBoolean(EXTRA_PRESENT, false);
        mLevel = sharedPreferences.getInt(EXTRA_LEVEL, 0);
        mScale = sharedPreferences.getInt(EXTRA_SCALE, 0);
        mIconSmallResId = sharedPreferences.getInt(EXTRA_ICON_SMALL, 0);
        mPlugged = sharedPreferences.getInt(EXTRA_PLUGGED, 0);
        mVoltage = sharedPreferences.getInt(EXTRA_VOLTAGE, 0);
        mTemperature = sharedPreferences.getInt(EXTRA_TEMPERATURE, 0);
        mTechnology = sharedPreferences.getString(EXTRA_TECHNOLOGY, "Unknown");
    }

    public void saveToIntent(final Intent intent) {
        intent.putExtra(EXTRA_STATUS, mStatus);
        intent.putExtra(EXTRA_HEALTH, mHealth);
        intent.putExtra(EXTRA_PRESENT, mPresent);
        intent.putExtra(EXTRA_LEVEL, mLevel);
        intent.putExtra(EXTRA_SCALE, mScale);
        intent.putExtra(EXTRA_ICON_SMALL, mIconSmallResId);
        intent.putExtra(EXTRA_PLUGGED, mPlugged);
        intent.putExtra(EXTRA_VOLTAGE, mVoltage);
        intent.putExtra(EXTRA_TEMPERATURE, mTemperature);
        intent.putExtra(EXTRA_TECHNOLOGY, mTechnology);
    }

    public void saveToSharedPreferences(final SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(EXTRA_STATUS, mStatus);
        editor.putInt(EXTRA_HEALTH, mHealth);
        editor.putBoolean(EXTRA_PRESENT, mPresent);
        editor.putInt(EXTRA_LEVEL, mLevel);
        editor.putInt(EXTRA_SCALE, mScale);
        editor.putInt(EXTRA_ICON_SMALL, mIconSmallResId);
        editor.putInt(EXTRA_PLUGGED, mPlugged);
        editor.putInt(EXTRA_VOLTAGE, mVoltage);
        editor.putInt(EXTRA_TEMPERATURE, mTemperature);
        editor.putString(EXTRA_TECHNOLOGY, mTechnology);
        editor.apply();
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public boolean isCharging() {
        return mStatus == BatteryManager.BATTERY_STATUS_CHARGING;
    }

    public int getHealth() {
        return mHealth;
    }

    public void setHealth(int health) {
        mHealth = health;
    }

    public boolean isPresent() {
        return mPresent;
    }

    public void setPresent(boolean present) {
        mPresent = present;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public int getScale() {
        return mScale;
    }

    public void setScale(int scale) {
        mScale = scale;
    }

    public int getIconSmallResId() {
        return mIconSmallResId;
    }

    public void setIconSmallResId(int iconSmallResId) {
        mIconSmallResId = iconSmallResId;
    }

    public int getPlugged() {
        return mPlugged;
    }

    public void setPlugged(int plugged) {
        mPlugged = plugged;
    }

    public int getVoltage() {
        return mVoltage;
    }

    public void setVoltage(int voltage) {
        mVoltage = voltage;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature) {
        mTemperature = temperature;
    }

    public String getTechnology() {
        return mTechnology;
    }

    public void setTechnology(String technology) {
        mTechnology = technology;
    }
}
