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
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BatteryItems {

    public static final String KEY_BATTERY_ITEMS = "battery_items";

    public static final String KEY_ON_BATTERY = "on_battery";
    public static final String KEY_CHARGING = "charging";
    public static final String KEY_DEFAULT = "default";
    public static final String KEY_BATTERIES = "batteries";
    public static final String KEY_LIMIT = "limit";
    public static final String KEY_NAME = "name";

    public static final String DEFAULT_ON_BATTERY_DEFAULT_NAME = "ac32";
    public static final int DEFAULT_ON_BATTERY_DEFAULT_ID = R.drawable.ac32;

    public static final String DEFAULT_CHARGING_DEFAULT_NAME = "ac16";
    public static final int DEFAULT_CHARGING_DEFAULT_ID = R.drawable.ac16;

    private int mOnBatteryDefaultId = DEFAULT_ON_BATTERY_DEFAULT_ID;
    private int mChargingDefaultId = DEFAULT_CHARGING_DEFAULT_ID;

    private List<Integer> mOnBatteryLimitList = new ArrayList<>();
    private List<Integer> mOnBatteryIdList = new ArrayList<>();

    private List<Integer> mChargingLimitList = new ArrayList<>();
    private List<Integer> mChargingIdList = new ArrayList<>();

    public BatteryItems(SharedPreferences sharedPreferences) {
        boolean error = false;
        String items = sharedPreferences.getString(KEY_BATTERY_ITEMS, null);

        if (TextUtils.isEmpty(items)) {
            error = true;
        } else {
            try {
                JSONObject batteryItems = new JSONObject(items);

                JSONObject onBattery = batteryItems.getJSONObject(KEY_ON_BATTERY);
                mOnBatteryDefaultId = getId(onBattery.getString(KEY_DEFAULT), DEFAULT_ON_BATTERY_DEFAULT_ID);
                JSONArray onBatteryArray = onBattery.getJSONArray(KEY_BATTERIES);
                for (int i = 0, length = onBatteryArray.length(); i < length; i++) {
                    JSONObject jo = onBatteryArray.getJSONObject(i);
                    mOnBatteryLimitList.add(jo.getInt(KEY_LIMIT));
                    mOnBatteryIdList.add(getId(jo.getString(KEY_NAME), DEFAULT_ON_BATTERY_DEFAULT_ID));
                }

                JSONObject charging = batteryItems.getJSONObject(KEY_CHARGING);
                mChargingDefaultId = getId(charging.getString(KEY_DEFAULT), DEFAULT_CHARGING_DEFAULT_ID);
                JSONArray chargingArray = charging.getJSONArray(KEY_BATTERIES);
                for (int i = 0, length = chargingArray.length(); i < length; i++) {
                    JSONObject jo = chargingArray.getJSONObject(i);
                    mChargingLimitList.add(jo.getInt(KEY_LIMIT));
                    mChargingIdList.add(getId(jo.getString(KEY_NAME), DEFAULT_CHARGING_DEFAULT_ID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (error) {
            mOnBatteryDefaultId = DEFAULT_ON_BATTERY_DEFAULT_ID;
            mChargingDefaultId = DEFAULT_CHARGING_DEFAULT_ID;

            mOnBatteryLimitList.add(100);
            mOnBatteryLimitList.add(80);
            mOnBatteryLimitList.add(50);
            mOnBatteryLimitList.add(15);
            mOnBatteryLimitList.add(5);
            mOnBatteryLimitList.add(0);

            mOnBatteryIdList.add(R.drawable.ac32);
            mOnBatteryIdList.add(R.drawable.ac31);
            mOnBatteryIdList.add(R.drawable.ac47);
            mOnBatteryIdList.add(R.drawable.ac25);
            mOnBatteryIdList.add(R.drawable.ac36);
            mOnBatteryIdList.add(R.drawable.ac38);

            mChargingLimitList.add(100);
            mChargingLimitList.add(80);
            mChargingLimitList.add(50);
            mChargingLimitList.add(25);
            mChargingLimitList.add(0);

            mChargingIdList.add(R.drawable.ac16);
            mChargingIdList.add(R.drawable.ac17);
            mChargingIdList.add(R.drawable.ac40);
            mChargingIdList.add(R.drawable.ac48);
            mChargingIdList.add(R.drawable.ac50);
        }
    }

    public String getName(int id, String defaultName) {
        String name = BatteryImage.getImageForId(id);
        return name != null ? name : defaultName;
    }

    public int getId(String name, int defaultId) {
        int id = BatteryImage.getIdForImage(name);
        return id != 0 ? id : defaultId;
    }

    public void saveToSharedPreferences(Context context, final SharedPreferences sharedPreferences) {
        try {
            JSONObject batteryItems = new JSONObject();

            JSONObject onBattery = new JSONObject();
            onBattery.put(KEY_DEFAULT, getName(mOnBatteryDefaultId, DEFAULT_ON_BATTERY_DEFAULT_NAME));
            JSONArray onBatteryArray = new JSONArray();

            for (int i = 0, length = mOnBatteryLimitList.size(); i < length; i++) {
                JSONObject jo = new JSONObject();
                jo.put(KEY_LIMIT, mOnBatteryLimitList.get(i));
                jo.put(KEY_NAME, getName(mOnBatteryIdList.get(i), DEFAULT_ON_BATTERY_DEFAULT_NAME));
                onBatteryArray.put(jo);
            }
            onBattery.put(KEY_BATTERIES, onBatteryArray);
            batteryItems.put(KEY_ON_BATTERY, onBattery);

            JSONObject charging = new JSONObject();
            charging.put(KEY_DEFAULT, getName(mChargingDefaultId, DEFAULT_CHARGING_DEFAULT_NAME));
            JSONArray chargingArray = new JSONArray();
            for (int i = 0, length = mChargingLimitList.size(); i < length; i++) {
                JSONObject jo = new JSONObject();
                jo.put(KEY_LIMIT, mChargingLimitList.get(i));
                jo.put(KEY_NAME, getName(mChargingIdList.get(i), DEFAULT_CHARGING_DEFAULT_NAME));
                chargingArray.put(jo);
            }
            charging.put(KEY_BATTERIES, chargingArray);
            batteryItems.put(KEY_CHARGING, charging);

            sharedPreferences.edit().putString(KEY_BATTERY_ITEMS, batteryItems.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Can't save to SharedPreferences", Toast.LENGTH_SHORT).show(); // TODO hardcode
        }
    }

    public int getDrawableId(BatteryInfo batteryInfo) {
        List<Integer> limitList;
        List<Integer> idList;
        int defaultId;

        if (batteryInfo.getPlugged() != 0) {
            limitList = mChargingLimitList;
            idList = mChargingIdList;
            defaultId = mChargingDefaultId;
        } else {
            limitList = mOnBatteryLimitList;
            idList = mOnBatteryIdList;
            defaultId = mOnBatteryDefaultId;
        }

        int index = -1;
        int limit = -1;
        int level = batteryInfo.getLevel();
        for (int i = 0, length = limitList.size(); i < length; i++) {
            int cLimit = limitList.get(i);
            if (level >= cLimit && cLimit > limit) {
                limit = cLimit;
                index = i;
            }
        }

        if (index == -1) {
            return defaultId;
        } else {
            return idList.get(index);
        }
    }

    public int getOnBatteryDefaultId() {
        return mOnBatteryDefaultId;
    }

    public void setOnBatteryDefaultId(int id) {
        mOnBatteryDefaultId = id;
    }

    public int getOnBatterySize() {
        return mOnBatteryIdList.size();
    }

    public int getOnBatteryLimit(int index) {
        return mOnBatteryLimitList.get(index);
    }

    public int getOnBatteryId(int index) {
        return mOnBatteryIdList.get(index);
    }

    public void setOnBattery(int index, int limit, int id) {
        // Check limit
        for (int i = 0, length = mOnBatteryLimitList.size(); i < length; i++) {
            int cLimit = mOnBatteryLimitList.get(i);
            if (limit == cLimit) {
                mOnBatteryLimitList.set(i, limit);
                mOnBatteryIdList.set(i, id);
                if (index != i) {
                    removeOnBattery(index);
                }
                return;
            }
        }

        removeOnBattery(index);
        addOnBattery(limit, id);
    }

    public void addOnBattery(int limit, int id) {
        // Check limit
        for (int i = 0, length = mOnBatteryLimitList.size(); i < length; i++) {
            int cLimit = mOnBatteryLimitList.get(i);
            if (limit > cLimit) {
                mOnBatteryLimitList.add(i, limit);
                mOnBatteryIdList.add(i, id);
                return;
            } else if (limit == cLimit) {
                mOnBatteryLimitList.set(i, limit);
                mOnBatteryIdList.set(i, id);
                return;
            }
        }

        mOnBatteryLimitList.add(limit);
        mOnBatteryIdList.add(id);
    }

    public void removeOnBattery(int index) {
        mOnBatteryIdList.remove(index);
        mOnBatteryLimitList.remove(index);
    }

    public int getChargingDefaultId() {
        return mChargingDefaultId;
    }

    public void setChargingDefaultId(int id) {
        mChargingDefaultId = id;
    }

    public int getChargingSize() {
        return mChargingIdList.size();
    }

    public int getChargingLimit(int index) {
        return mChargingLimitList.get(index);
    }

    public int getChargingId(int index) {
        return mChargingIdList.get(index);
    }

    public void setCharging(int index, int limit, int id) {
        // Check limit
        for (int i = 0, length = mChargingLimitList.size(); i < length; i++) {
            int cLimit = mChargingLimitList.get(i);
            if (limit == cLimit) {
                mChargingLimitList.set(i, limit);
                mChargingIdList.set(i, id);
                if (index != i) {
                    removeCharging(index);
                }
                return;
            }
        }

        removeCharging(index);
        addCharging(limit, id);
    }

    public void addCharging(int limit, int id) {
        for (int i = 0, length = mChargingLimitList.size(); i < length; i++) {
            int cLimit = mChargingLimitList.get(i);
            if (limit > cLimit) {
                mChargingLimitList.add(i, limit);
                mChargingIdList.add(i, id);
                return;
            } else if (limit == cLimit) {
                mChargingLimitList.set(i, limit);
                mChargingIdList.set(i, id);
                return;
            }
        }
        mChargingLimitList.add(limit);
        mChargingIdList.add(id);
    }

    public void removeCharging(int index) {
        mChargingIdList.remove(index);
        mChargingLimitList.remove(index);
    }
}
