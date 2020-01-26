package com.maakki.maakkiapp;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//public class MapsActivity extends AppCompatActivity{
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final long MIN_TIME_BW_UPDATES = 400;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private Marker m_marker;
    private TextView tv_message;
    private Location location;
    private LatLng m_L;
    private boolean isTargeted=false,isNotify=false;
    private LatLng center = new LatLng(-34, 151);
    private GPSTracker gps;
    String markertitle = "Marker in Sydney", mMemID;
    Context mContext;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private List<Store_maps> listStoreMaps = new ArrayList<Store_maps>();
    private BroadcastReceiver locationReceiver,locationReceiver1,DaemonReceiver;
    private StoreDAO storeDAO;
    private List<Marker> markerList;
    private String strCityName;
    private SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefs;
    Menu menu;
    boolean hasm_marker=false;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(isNotify){
                menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_volume_up_white_24dp));
            }else{
                menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_volume_off_white_24dp));
            }
            switch (menuItem.getItemId()) {
                case R.id.sound_active:
                    //Toast.makeText(getApplicationContext(), "isNotify:"+isNotify.toString(),Toast.LENGTH_LONG).show();
                    if (isNotify) {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_volume_off_white_24dp));
                        isNotify = false;
                    } else {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_volume_up_white_24dp));
                        isNotify = true;
                    }
                    prefs.putBoolean("enable_location_notification", isNotify);
                    prefs.commit();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext = getApplicationContext();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        isNotify=sharedPrefs.getBoolean("enable_location_notification", false);
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        if (mMemID.equals("90")) {
            String strLan = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key18, "0.0");
            String strLon = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key19, "0.0");
            Double lan = Double.parseDouble(strLan);
            Double lon = Double.parseDouble(strLon);
            m_L = new LatLng(lan, lon);
        }
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        //getLocation();
        tv_message=(TextView)findViewById(R.id.tv_message);
        gps = new GPSTracker(MapsActivity.this);
        center = new LatLng(gps.getLatitude(), gps.getLongitude());
        tv_message.setText(getCityName(gps.getLatitude(),gps.getLongitude()));
        markertitle = "Here I am!";
        //getCountryCode
        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        //Toast.makeText(getApplicationContext(),"I'm in "+locale+".",Toast.LENGTH_LONG).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerList = new ArrayList<>();

        //Current m_marker location
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_ONm_markerLOCATIONCHANGED");
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(mMemID.equals("90")){
                    Toast.makeText(getApplicationContext(),"moving..", Toast.LENGTH_SHORT).show();
                    Double lat=Double.parseDouble(intent.getStringExtra("latitude"));
                    Double lon=Double.parseDouble(intent.getStringExtra("longitude"));
                    m_marker.setPosition(new LatLng(lat,lon));
                    if(isTargeted){
                        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(lat,lon));
                        //mMap.animateCamera(cameraUpdate);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17.0f));
                        AsyncCallWS_getAddress getAddressTask=new AsyncCallWS_getAddress();
                        getAddressTask.execute(String.valueOf(lat),String.valueOf(lon));
                        //tv_message.setText(getCityName(lat,lon));
                        //showAlertDialog("Location",getAddressFromLocation(lat,lon));
                    }
                }
            }
        };
        registerReceiver(locationReceiver, filter);
        //Current center location
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("INVOKE_ONLOCATIONCHANGED");
        locationReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(getApplicationContext(),"Hi", Toast.LENGTH_SHORT).show();
                location = gps.getLocation();
                if (location != null) {
                    center = new LatLng(location.getLatitude(), location.getLongitude());
                    if(!isTargeted){
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(center);
                        mMap.animateCamera(cameraUpdate);
                    }
                }
            }
        };
        registerReceiver(locationReceiver1, filter1);
        final ImageView iv_search=(ImageView)findViewById(R.id.iv_search);
        //final ImageView iv_notify=(ImageView)findViewById(R.id.iv_notify);

        RelativeLayout RL_function=(RelativeLayout)findViewById(R.id.RL_function);
        if(mMemID.equals("90")){
            RL_function.setVisibility(View.VISIBLE);
        }else{
            RL_function.setVisibility(View.GONE);
        }
        iv_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTargeted){
                    isTargeted=false;
                    iv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
                }else{
                    isTargeted=true;
                    iv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_maakki_red));
                    tv_message.setText(getCityName(m_L.latitude,m_L.longitude));
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //LatLng center = new LatLng(-34, 151);
        //String markertitle="Marker in Sydney";
        //mMap.setMyLocationEnabled(true);

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //useMapLocation = false;
                //mMap.clear();
                return false;
            }
        });

        //Marker marker = mMap.addMarker(new MarkerOptions().position(center).title(markertitle));
        //markerList.add(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        getStoreList_Client(mMap);
        //Toast.makeText(getApplicationContext(), "Map is ready!",Toast.LENGTH_LONG).show();

    }

    private void getStoreList_Client(GoogleMap mMap) {
        storeDAO = new StoreDAO(getApplicationContext());
        int count = storeDAO.getCount();

        //count=10;
        //Toast.makeText(getApplicationContext(), "count:"+String.valueOf(count),Toast.LENGTH_LONG).show();
        if (count > 0) {
            //Toast.makeText(getApplicationContext(), "count>0",Toast.LENGTH_LONG).show();
            Store store = new Store();
            List<Store> allstore = storeDAO.getAll();//应该是这行程式有问题
            int storecount = allstore.size();
            //Toast.makeText(getApplicationContext(), "storecount:"+String.valueOf(storecount),Toast.LENGTH_LONG).show();
            //storecount=10;
            for (int i = 0; i < storecount; i++) {
                Store_maps sm = new Store_maps();
                store = allstore.get(i);
                int sid = store.getStoreID();
                int mid = store.getMaakkiID();
                String strAdd = store.getAddress();
                String strSname = store.getStoreName();
                String picfile = store.getPicfile();
                LatLng L;
                if (mMemID.equals("90") & mid == 10006) {
                        L = m_L;
                } else {
                    L = new LatLng(store.getLatitude(), store.getLongitude());
                }

                Boolean isOpen = false;
                if (store.getIsOpen().equals("Y")) {
                    isOpen = true;
                }
                //Toast.makeText(getApplicationContext(), "store("+String.valueOf(i)+"):"+String.valueOf(L)+"/isOpen:"+String.valueOf(isOpen),Toast.LENGTH_LONG).show();
                if (!strAdd.equals("") & isOpen) {
                    if (L != null) {
                        //如果轉LatLng沒成功
                        //if(!String.valueOf(getLocationFromAddress(this,strAdd)))){
                        sm.setLatLng(L);
                        sm.setMaakkiid(mid);
                        //s.setLatLng(getLocationFromAddress(this,strAdd));
                        sm.setStorename(strSname);
                        sm.setAddress(strAdd);
                        sm.setPicfile(picfile);
                        sm.setStoreid(sid);
                        listStoreMaps.add(sm);
                        //}
                    }
                }
            }
            //same as onPostExecute
            int lscount = listStoreMaps.size();
            Toast.makeText(getApplicationContext(), "发现" + String.valueOf(lscount) + "间玛吉商店", Toast.LENGTH_LONG).show();

            for (int i = 0; i < lscount; i++) {
                //int j = i + 1;
                String sname = listStoreMaps.get(i).getStorename();
                String add = listStoreMaps.get(i).getAddress();
                LatLng latlng = listStoreMaps.get(i).getLatLng();
                int sid = listStoreMaps.get(i).getStoreid();
                //
                Marker marker = mMap.addMarker(new MarkerOptions().position(
                        latlng)
                        .title(sname)
                        .snippet(add));
                //.setSnippet(String.valueOf(sid)
                if(listStoreMaps.get(i).getMaakkiid()==10006){
                    m_marker=marker;
                    hasm_marker=true;
                }
                marker.setTag(sid);
                markerList.add(marker);
            }

            if(!hasm_marker & mMemID.equals("90")){
                m_marker= mMap.addMarker(new MarkerOptions().position(m_L)
                        .title("n/a")
                        .snippet("nowhere"));
                markerList.add(m_marker);
            }
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String mMaakkiID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
                    String ssid = String.valueOf(marker.getTag());
                    String redUrl =  StaticVar.webURL+"/BOStoreData.aspx?entrepot_id=" + ssid + "&mid=" + mMaakkiID;
                    Intent intent = new Intent(getApplicationContext(), WebMain2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("redirUrl", redUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markerList) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 160; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMemID.equals("90")) {
            getMenuInflater().inflate(R.menu.menu_issound, menu);
            this.menu = menu;
            if (!isNotify) {
                menu.getItem(0)
                        .setIcon(ContextCompat
                                .getDrawable(this, R.drawable.baseline_volume_off_white_24dp));
            }
        }
        return true;
    }

    private String getCityName(Double lat,Double lon) {
        strCityName = "";
        Context c = getApplication();
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                strCityName=addresses.get(0).getAddressLine(0)+"\n";
               } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (Exception e) {

            }
        return strCityName;
    }

    private String getAddressFromLocation(final double latitude,final double longitude) {
        String result = "";
        final Context c = getApplication();

        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getSubLocality()).append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getAdminArea()).append("\n");
                //sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        } catch (IOException e) {
            //Log.e(TAG, "Unable connect to Geocoder", e);
        }
        return result;
    }
    private class AsyncCallWS_getAddress extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            Double lat=Double.parseDouble(params[0]);
            Double lon=Double.parseDouble(params[1]);
            getCityName(lat,lon);
            return getCityName(lat,lon);
        }

        @Override
        protected void onPostExecute(String result) {
           tv_message.setText(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
