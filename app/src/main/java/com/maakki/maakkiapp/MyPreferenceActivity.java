package com.maakki.maakkiapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class MyPreferenceActivity extends PreferenceActivity {
    SwitchPreference enable_sound, enable_vibrate, enable_display;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        enable_display = (SwitchPreference) findPreference("enable_display");
        enable_vibrate = (SwitchPreference) findPreference("enable_vibrate");
        enable_sound = (SwitchPreference) findPreference("enable_sound");
    }

    public void setSoundChecked(Boolean check) {
        //enable_sound.setKey("enable_sound");
        enable_sound.setChecked(check);
    }

    public void setDisplayChecked(Boolean check) {
        enable_display.setChecked(check);
    }

    public void setVibrateChecked(Boolean check) {
        enable_display.setChecked(check);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }
}