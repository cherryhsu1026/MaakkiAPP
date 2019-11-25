package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/4/19.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class ShowSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_settings_layout);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StringBuilder builder = new StringBuilder();

        builder.append("n" + sharedPrefs.getBoolean("enable_notification", true));
        builder.append("n" + sharedPrefs.getString("options_notification", "sound_vibrate"));
        builder.append("n" + sharedPrefs.getString("welcome_message", "歡迎來到瑪吉"));

        TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
        settingsTextView.setText(builder.toString());

    }

}
