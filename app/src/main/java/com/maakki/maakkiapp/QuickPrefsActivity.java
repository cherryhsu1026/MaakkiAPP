package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/4/19.
 */


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class QuickPrefsActivity extends PreferenceActivity {
    private static String appVersion;
    private static String welMsg;
    String strCityName, maakkiID;
    GPSTracker gps;
    SwitchPreference enable_sound, enable_vibrate, enable_display;
    Context context;
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            //preference.setSummary(stringValue);
            //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
//            } else if (preference instanceof EditTextPreference) {
//                EditTextPreference etp = (EditTextPreference) preference;
//                //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
//                etp.setSummary(stringValue);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void setPreferenceSummary(Preference preference, String value) {
        preference.setSummary(value);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
        context = this;
        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);
            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = (Toolbar) bar.getChildAt(0);

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            SharedPreferencesHelper.putSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key7, String.valueOf(gps.getLatitude()));
            SharedPreferencesHelper.putSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key8, String.valueOf(gps.getLongitude()));
        }
        //DaemonReceiver();
        setupSimplePreferencesScreen();
        PreferenceScreen myPfScreen = (PreferenceScreen) findPreference("myPreferenceScreen");
        PreferenceCategory PcOldVer = (PreferenceCategory) findPreference("pc_oldVer");
        PreferenceCategory PcNewVer = (PreferenceCategory) findPreference("pc_newVer");
        if (Build.VERSION.SDK_INT < 26) {
            myPfScreen.removePreference(PcNewVer);
        } else {
            myPfScreen.removePreference(PcOldVer);
        }
        String MaakkiID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        if (!MaakkiID.equals("1")) {
            PreferenceCategory location_notification = (PreferenceCategory) findPreference("location_notification");
            myPfScreen.removePreference(location_notification);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!getCityName().equals("")&gps.canGetLocation) {
            setPreferenceSummary(findPreference("location"), getCityName());
            //Toast.makeText(getApplicationContext(), "CityName:"+strCityName, Toast.LENGTH_LONG).show();
        } else {
            Preference prfLocation = (Preference) findPreference("location");
            PreferenceCategory userInfoCat = (PreferenceCategory) findPreference("user_info");
            userInfoCat.removePreference(prfLocation);
        }
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference("notifications_ringtone"));
        //bindPreferenceSummaryToValue(findPreference("enable_display"));
        //bindPreferenceSummaryToValue(findPreference("welcome_message"));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        welMsg = prefs.getString("welcome_message", "");
        Preference wel_msg = findPreference("welcome_message");
        Preference notify_settingPreference = findPreference("notify_setting");
        notify_settingPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                //for Android 5-7
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);

                // for Android 8 and above
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                startActivity(intent);
                //open browser or intent here
                return true;
            }
        });
        Preference setting_pref = (Preference) findPreference("setting");
        setting_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return true;
            }
        });

        enable_display = (SwitchPreference) findPreference("enable_display");
        enable_vibrate = (SwitchPreference) findPreference("enable_vibrate");
        enable_sound = (SwitchPreference) findPreference("enable_sound");
        enable_display.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isDisplayObject) {
                boolean isdisplay = (Boolean) isDisplayObject;
                if (!isdisplay) {
                    enable_sound.setChecked(false);
                    enable_vibrate.setChecked(false);
                }
                return true;
            }
        });

        wel_msg.setSummary(welMsg);
        //
        final EditTextPreference pref = (EditTextPreference) findPreference("welcome_message");
        //pref.setSummary(pref.getText());
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();
                //Toast.makeText(getApplicationContext(), "stringValue:" + stringValue, Toast.LENGTH_LONG).show();
                //final EditTextPreference pref = (EditTextPreference) findPreference("welcome_message");
                preference.setSummary(stringValue);
                return true;
            }
        });
    }

    public void setSoundChecked(Boolean check) {
        enable_sound.setKey("enable_sound");
        enable_sound.setChecked(check);
    }

    public void setDisplayChecked(Boolean check) {
        enable_display.setChecked(check);
    }

    public void setVibrateChecked(Boolean check) {
        enable_display.setChecked(check);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);

            }
        }

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class CustomDialogPreference extends EditTextPreference {

        public CustomDialogPreference(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public CustomDialogPreference(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void showDialog(Bundle state) {
            super.showDialog(state);
            final Resources res = getContext().getResources();
            final Window window = getDialog().getWindow();
            final int green = res.getColor(android.R.color.holo_green_dark);

            // Title
            final int titleId = res.getIdentifier("alertTitle", "id", "android");
            final View title = window.findViewById(titleId);
            if (title != null) {
                ((TextView) title).setTextColor(green);
            }

            // Title divider
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            final View titleDivider = window.findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(green);
            }

            // EditText
            final View editText = window.findViewById(android.R.id.edit);
            if (editText != null) {
                //editText.setBackground(res.getDrawable(R.drawable.));
            }
        }
    }

    private String getCityName() {
        strCityName = "";
        Context c = getApplication();
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses;
        String strLat = SharedPreferencesHelper.getSharedPreferencesString(c, SharedPreferencesHelper.SharedPreferencesKeys.key7, "");
        String strLon = SharedPreferencesHelper.getSharedPreferencesString(c, SharedPreferencesHelper.SharedPreferencesKeys.key8, "");
        if (!strLat.equals("") && !strLon.equals("")) {
            Double latitude = Double.parseDouble(strLat);
            Double longitude = Double.parseDouble(strLon);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.get(0).getAdminArea() != null) {
                    strCityName += addresses.get(0).getAdminArea() + " ";
                }
                if (addresses.get(0).getLocality() != null) {
                    strCityName += addresses.get(0).getLocality() + " ";
                }
                if (addresses.get(0).getSubLocality() != null) {
                    strCityName += addresses.get(0).getSubLocality();
                }
                //Toast.makeText(context,"State/City:"+stateName+"/"+cityName,Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (Exception e) {

            }
        }
        return strCityName;
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(QuickPrefsActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void DaemonReceiver() {
        //test for DaemonReceiver running
        IntentFilter filter_daemon = new IntentFilter();
        filter_daemon.addAction("INVOKE_CoreReceiver_Running");
        BroadcastReceiver DaemonReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //if(mMemID.equals("90")){
                Toast.makeText(getApplicationContext(), "CoreReceiver..", Toast.LENGTH_SHORT).show();
                //}
            }
        };
        registerReceiver(DaemonReceiver, filter_daemon);
    }

}
