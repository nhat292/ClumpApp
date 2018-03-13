package com.ck.clump.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ck.clump.App;

public class SharedPreference {

    public static final String KEY_API_TOKEN = "KEY_API_TOKEN";
    public static final String GCM_REG_ID = "gcmRegId";
    public static final String KEY_CURRENT_VIEW = "KEY_CURRENT_VIEW";
    public static final String KEY_NOTIFICATION_TYPE = "KEY_NOTIFICATION_TYPE";
    public static final String KEY_CHAT_SHOW = "KEY_CHAT_SHOW";
    public static final String KEY_CHAT_HISTORY_START_TIME = "KEY_CHAT_HISTORY_START_TIME";
    public static final String KEY_CHAT_LAST_MESSAGE_CONTENT = "KEY_CHAT_LAST_MESSAGE_CONTENT";
    public static final String KEY_CHAT_MESSAGE_COUNT = "KEY_CHAT_MESSAGE_COUNT";
    public static final String CURRENT_CHAT_CHANNEL_ID = "CURRENT_CHAT_CHANNEL_ID";
    public static final String KEY_FIRST_TIME = "KEY_FIRST_TIME";
    public static final String KEY_ID_CHANNEL_HIDE = "KEY_ID_CHANNEL_HIDE";
    public static final String KEY_ID_CHANNEL_USER_HIDE = "KEY_ID_CHANNEL_USER_HIDE";
    public static final String KEY_STOP_LOAD_HISTORY = "KEY_STOP_LOAD_HISTORY";
    public static final String KEY_NUMBER_MESSAGE_CHANNEL = "KEY_NUMBER_MESSAGE_CHANNEL";
    public static final String KEY_CHAT_ACTIVITY_ALIVE = "KEY_CHAT_ACTIVITY_ALIVE";
    public static final String KEY_REFRESH_CHAT_WHEN_RESUME = "KEY_REFRESH_CHAT_WHEN_RESUME";
    public static final String KEY_REFRESH_CHAT_LIST_WHEN_RESUME = "KEY_REFRESH_CHAT_LIST_WHEN_RESUME";
    public static final String KEY_REFRESH_CONTACT_LIST_WHEN_RESUME = "KEY_REFRESH_CONTACT_LIST_WHEN_RESUME";

    public static void saveString(String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);
        prefEditor.apply();

    }

    public static String getString(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return pref.getString(key, null);
    }

    public static void saveInt(String key, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt(key, value);
        prefEditor.apply();

    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return pref.getInt(key, defaultValue);
    }

    public static void saveLong(String key, long value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putLong(key, value);
        prefEditor.apply();

    }

    public static long getLong(String key, long defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return pref.getLong(key, defaultValue);
    }

    public static void deleteKeyAndValue(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.remove(key);
        prefEditor.apply();
    }

    public static void saveBool(String key, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.apply();

    }

    public static boolean getBool(String key, boolean defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return pref.getBoolean(key, defaultValue);
    }

    public static void clearAll() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        pref.edit().clear().apply();
    }

}
