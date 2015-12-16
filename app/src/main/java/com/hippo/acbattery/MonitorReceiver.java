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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MonitorReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            BatteryInfo batteryInfo = new BatteryInfo(intent);
            Intent updateIntent = new Intent(context, UpdateService.class);
            updateIntent.setAction(UpdateService.ACTION_BATTERY_CHANGED);
            batteryInfo.saveToIntent(updateIntent);
            context.startService(updateIntent);
        } else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            Intent updateIntent = new Intent(context, UpdateService.class);
            updateIntent.setAction(UpdateService.ACTION_BATTERY_LOW);
            context.startService(updateIntent);
        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            Intent updateIntent = new Intent(context, UpdateService.class);
            updateIntent.setAction(UpdateService.ACTION_BATTERY_OKAY);
            context.startService(updateIntent);
        }
    }
}
