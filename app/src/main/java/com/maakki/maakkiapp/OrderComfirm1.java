package com.maakki.maakkiapp;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by ryan on 2017/7/17.
 */

public class OrderComfirm1 extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    Toolbar myToolbar;
    /*private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    ImageView iv_getall, iv_edit, iv_right, iv_left, iv_sum, iv_seat;
    FloatingActionButton fab;
    //final EditText editText_maakki_id;
    int rotation, payment = 1, order_id = 0;
    int vsbp = 0; //VerticalScrollBarPosition
    //boolean reload=false;
    int om_count = 0, count;
    double amount;
    View view;
    String pid, maakkiid, ck;
    String errMsg = "";
    Boolean isMember = false;
    Double iCredit = 0.0;
    String Currency = "RMB";
    String nickname = "";
    Integer Discount;
    private OrderMasterAdapter adapter;
    private SwipeDetector swipeDetector = new SwipeDetector();
    private RelativeLayout rl_order;
    private TextView tv_msg, text_orderno, text_ordertime, order_content, order_sum, orderitemcount, order_tableid, tv_amount;
    private ListView lv_om;
    private int option;
    //private ScrollView sv;
    private Category category;
    private List<Category> listCategory;
    private CategoryDAO catDAO;
    private OrderMaster ordermaster;
    private List<OrderMaster> listOrderMaster;
    private OrderMasterDAO omDAO;
    private ItemDAO itDAO;
    private OrderDetail orderdetail;
    private List<OrderDetail> listOrderDetail;
    private OrderDetailDAO odDAO;
    private boolean isAscending = false;
    private CheckMasterDAO cmDAO;
    private CheckMaster cm;
    private CheckDetailDAO cdDAO;
    private CheckDetail cd;
    private LinearLayout rl_countamount;
    private ImageView imv;
    private String BOCurrency, check_id, customer_title, customer_no, customer_maakkiid;*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            //Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                //Toast.makeText(OrderComfirm1.this, "没有正确扫描到二维码", Toast.LENGTH_LONG).show();
                /*AlertDialog alertDialog = new AlertDialog.Builder(OrderComfirm1.this).create();
                alertDialog.setTitle("扫码错误");
                alertDialog.setMessage("没有正确扫描到二维码");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();*/
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String contents = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Toast.makeText(OrderComfirm1.this,"result:"+contents,Toast.LENGTH_LONG).show();
        }
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.item:
                    Intent i = new Intent(OrderComfirm1.this, QrCodeActivity.class);
                    startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                    break;

            }
            return true;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_comfirm);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pos, menu);
        return true;
    }

}
