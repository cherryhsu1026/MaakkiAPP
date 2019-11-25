package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/4.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ryan on 2016/2/27.
 */
public class SharedPreferencesHelper {
    public static void putSharedPreferencesInt(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static void putSharedPreferencesBoolean(Context context, String key, boolean val) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesString(Context context, String key, String val) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesFloat(Context context, String key, float val) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putFloat(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesLong(Context context, String key, long val) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(key, val);
        edit.commit();
    }

    public static long getSharedPreferencesLong(Context context, String key, long _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(key, _default);
    }

    public static float getSharedPreferencesFloat(Context context, String key, float _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getFloat(key, _default);
    }

    public static String getSharedPreferencesString(Context context, String key, String _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, _default);
    }

    public static int getSharedPreferencesInt(Context context, String key, int _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, _default);
    }

    public static boolean getSharedPreferencesBoolean(Context context, String key, boolean _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, _default);
    }

    public static class SharedPreferencesKeys {
        public static final String key0 = "mMaakkiID";
        public static final String key1 = "mMemID";
        public static final String key2 = "mName";
        public static final String key3 = "mPicfile";
        public static final String key4 = "isServiceOn";
        public static final String key5 = "isPageErr";
        public static final String key6 = "Time_Waiting_Sponsor";
        public static final String key7 = "Latitude";
        public static final String key8 = "Longitude";
        public static final String key9 = "ShareTitle";
        public static final String key10 = "ShareImageUrl";
        public static final String key11 = "LastNotificationTime";
        public static final String key12 = "lasttime_getfriendlist";
        public static final String key13 = "CustomerServiceLastOnlineTime";
        public static final String key14 = "SupplierAnswerMemTime";
        public static final String key15 = "isReadStatement_imaimai";
        public static final String key16 = "iCreditsRecallRate";
        public static final String key17 ="statement_ver_date";
        public static final String key18 ="m_lantitude";
        public static final String key19 ="m_longitude";
        public static final String key20 ="Spnsor_Target_Maakki_id";
        public static final String key21 ="is_Sonsor_USD";
        public static final String key22 ="Spnsor_default_iCredits";
        public static final String key23 ="Spnsor_default_type";
        public static final String key24 ="Last_getResenvelope_Time";
    }

}

