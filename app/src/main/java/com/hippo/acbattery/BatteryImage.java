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

public class BatteryImage {

    public static final String[] BATTERY_IMAGE_NAMES = {
            "ac01", "ac02", "ac03", "ac04", "ac05",
            "ac06", "ac07", "ac08", "ac09", "ac10",
            "ac11", "ac12", "ac13", "ac14", "ac15",
            "ac16", "ac17", "ac18", "ac19", "ac20",
            "ac21", "ac22", "ac23", "ac24", "ac25",
            "ac26", "ac27", "ac28", "ac29", "ac30",
            "ac31", "ac32", "ac33", "ac34", "ac35",
            "ac36", "ac37", "ac38", "ac39", "ac40",
            "ac41", "ac42", "ac43", "ac44", "ac45",
            "ac46", "ac47", "ac48", "ac49", "ac50",
            "ac51", "ac52", "ac53", "ac54",
    };

    public static final int[] BATTERY_IMAGE_IDS = {
            R.drawable.ac01, R.drawable.ac02, R.drawable.ac03, R.drawable.ac04, R.drawable.ac05,
            R.drawable.ac06, R.drawable.ac07, R.drawable.ac08, R.drawable.ac09, R.drawable.ac10,
            R.drawable.ac11, R.drawable.ac12, R.drawable.ac13, R.drawable.ac14, R.drawable.ac15,
            R.drawable.ac16, R.drawable.ac17, R.drawable.ac18, R.drawable.ac19, R.drawable.ac20,
            R.drawable.ac21, R.drawable.ac22, R.drawable.ac23, R.drawable.ac24, R.drawable.ac25,
            R.drawable.ac26, R.drawable.ac27, R.drawable.ac28, R.drawable.ac29, R.drawable.ac30,
            R.drawable.ac31, R.drawable.ac32, R.drawable.ac33, R.drawable.ac34, R.drawable.ac35,
            R.drawable.ac36, R.drawable.ac37, R.drawable.ac38, R.drawable.ac39, R.drawable.ac40,
            R.drawable.ac41, R.drawable.ac42, R.drawable.ac43, R.drawable.ac44, R.drawable.ac45,
            R.drawable.ac46, R.drawable.ac47, R.drawable.ac48, R.drawable.ac49, R.drawable.ac50,
            R.drawable.ac51, R.drawable.ac52, R.drawable.ac53, R.drawable.ac54,
    };

    private static boolean equals(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    /**
     * @return -1 for not found
     */
    private static <T> int search(T[] array, T value) {
        for (int i = 0, length = array.length; i < length; i++) {
            if (equals(value, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return -1 for not found
     */
    private static int search(int[] array, int value) {
        for (int i = 0, length = array.length; i < length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return 0 for not found
     */
    public static int getIdForImage(String image) {
        int index = search(BATTERY_IMAGE_NAMES, image);
        if (index == -1) {
            return 0;
        } else {
            return BATTERY_IMAGE_IDS[index];
        }
    }

    /**
     * @return null for not found
     */
    public static String getImageForId(int id) {
        int index = search(BATTERY_IMAGE_IDS, id);
        if (index == -1) {
            return null;
        } else {
            return BATTERY_IMAGE_NAMES[index];
        }
    }
}
