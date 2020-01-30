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

public class OrderComfirm extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebService.asmx";
    ImageView iv_getall, iv_edit, iv_right, iv_left, iv_sum, iv_seat;
    FloatingActionButton fab;
    //final EditText editText_maakki_id;
    int rotation, payment = 1, order_id = 0;
    int vsbp = 0; //VerticalScrollBarPosition
    Toolbar myToolbar;
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
    private String BOCurrency, check_id, customer_title, customer_no, customer_maakkiid;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.item:
                    Intent intent1 = new Intent(OrderComfirm.this, POS_EditCategory.class);
                    startActivity(intent1);
                    // Intent intent1 = new Intent(OrderComfirm.this, POS_EditCategory.class);
                    //startActivity(intent1);
                    break;
                case R.id.record:
                    Intent intent2 = new Intent(OrderComfirm.this, OrderComfirm.class);
                    intent2.putExtra("order_id", "0");
                    startActivity(intent2);
                    break;
                case R.id.settings:
                    Intent intent3 = new Intent(OrderComfirm.this, POSPrefsActivity.class);
                    //intent3.putExtra("order_id", "0");
                    startActivity(intent3);
                    break;
            }
            return true;
        }
    };

    private static String byte2Hex(byte[] data) {
        String hexString = "";
        String stmp = "";

        for (int i = 0; i < data.length; i++) {
            stmp = Integer.toHexString(data[i] & 0XFF);

            if (stmp.length() == 1) {
                hexString = hexString + "0" + stmp;
            } else {
                hexString = hexString + stmp;
            }
        }
        return hexString.toUpperCase();
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act, R.style.MyAlertDialogStyle);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                //Uri uri = Uri.parse("market://search?q=" + "QR Droid Code Scanner");
                Uri uri = Uri.parse("market://search?q=" + "QR Code Reader");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    //act.startActivity(intent);
                    act.startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_comfirm);
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();
        //Get the order_id
        if (!this.getIntent().getExtras().equals(null)) {
            Bundle bundle = this.getIntent().getExtras();
            order_id = Integer.valueOf(bundle.getString("order_id"));
        }
        omDAO = new OrderMasterDAO(OrderComfirm.this);
        odDAO = new OrderDetailDAO(OrderComfirm.this);
        cmDAO = new CheckMasterDAO(OrderComfirm.this);
        cm = new CheckMaster();
        cdDAO = new CheckDetailDAO(OrderComfirm.this);
        cd = new CheckDetail();
        rl_order = (RelativeLayout) findViewById(R.id.rl_order);
        iv_getall = (ImageView) findViewById(R.id.getall);
        lv_om = (ListView) findViewById(R.id.lv_om);
        imv = (ImageView) findViewById(R.id.imv);
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            imv.setImageResource(R.drawable.sales_record_l);
        } else {
            imv.setImageResource(R.drawable.sales_record_p);
        }
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderComfirm.this, POS_MainActivity.class);
                intent.putExtra("order_id", "0");
                startActivity(intent);
            }
        });
        om_count = omDAO.getCount();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_arrow_upward_white_18dp);
        fab.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAscending) {
                    isAscending = false;
                    fab.setImageResource(R.drawable.ic_arrow_downward_white_18dp);
                } else {
                    isAscending = true;
                    fab.setImageResource(R.drawable.ic_arrow_upward_white_18dp);
                }
                Collections.reverse(listOrderMaster);
                adapter.notifyDataSetChanged();
            }
        });
        //order_id存在，先出现该笔订单的详细信息
        setView();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        BOCurrency = sharedPrefs.getString("credit_options", "RMB");
        Discount = Integer.valueOf(sharedPrefs.getString("Discount", "0"));
        if (order_id > 0) {
            lv_om.setVisibility(View.GONE);
            showSpecifiedOrderDetail(order_id);
        } else {
            //没有明确的订单号码，要查的是所有的订单记录
            //text_topic.setVisibility(View.GONE);
            rl_order.setVisibility(View.GONE);
            //text_main.setVisibility(View.VISIBLE);
            iv_getall.setVisibility(View.INVISIBLE);
            myToolbar.setTitle("销售记录");
            //Toast.makeText(getApplicationContext(), "checkdetail_count:" +cdDAO.getCount()+"\ncheckmaster:"+cmDAO.getCount(),Toast.LENGTH_LONG).show();
            if (om_count > 0) {
                showOrderMaster();
            } else {
                //没有订单记录
                fab.setVisibility(View.INVISIBLE);
                imv.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setView() {

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(OrderComfirm.this, POS_MainActivity.class);
                intent.putExtra("order_id", String.valueOf(order_id));
                startActivity(intent);
                finish();
            }
        });

    }

    public void showSpecifiedOrderDetail(final int order_id) {
        fab.setVisibility(View.INVISIBLE);
        myToolbar.setTitle("订单确认");
        lv_om.setVisibility(View.INVISIBLE);
        //Toast.makeText(getApplicationContext(), "order_id:"+order_id , Toast.LENGTH_SHORT).show();
        this.order_id = order_id;
        amount = 0;
        count = 0;
        //sv.post(new Runnable() {
        //    public void run() {
        //      sv.scrollTo(0, vsbp); // these are your x and y coordinates
        //    }
        //});
        rl_order.setVisibility(View.VISIBLE);

        //text_topic.setVisibility(View.GONE);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderComfirm.this, POS_MainActivity.class);
                intent.putExtra("order_id", String.valueOf(order_id));
                startActivity(intent);
            }
        });
        iv_left = (ImageView) findViewById(R.id.iv_left);
        //Toast.makeText(getApplicationContext(), "omDAO.getPreviousOrderId(order_id):" +String.valueOf(omDAO.getPreviousOrderId(order_id)), Toast.LENGTH_LONG).show();
        if (Integer.parseInt(String.valueOf(omDAO.getPreviousOrderId(order_id))) > 0) {
            iv_left.setVisibility(View.VISIBLE);
            iv_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSpecifiedOrderDetail(Integer.parseInt(String.valueOf(omDAO.getPreviousOrderId(order_id))));
                }
            });
        } else {
            iv_left.setVisibility(View.INVISIBLE);
        }
        iv_right = (ImageView) findViewById(R.id.iv_right);
        if (Integer.parseInt(String.valueOf(omDAO.getNextOrderId(order_id))) > 0) {
            iv_right.setVisibility(View.VISIBLE);
            iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSpecifiedOrderDetail(Integer.parseInt(String.valueOf(omDAO.getNextOrderId(order_id))));
                }
            });
        } else {
            iv_right.setVisibility(View.INVISIBLE);
        }

        text_orderno = (TextView) findViewById(R.id.text_orderno);
        vsbp = text_orderno.getVerticalScrollbarPosition();
        text_ordertime = (TextView) findViewById(R.id.text_ordertime);

        text_orderno.setText("" + String.valueOf(order_id));
        ordermaster = omDAO.get(Long.parseLong(String.valueOf(order_id)));
        text_ordertime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ordermaster.getOrderDate())));
        order_content = (TextView) findViewById(R.id.text_ordercontent);

        listOrderDetail = new ArrayList<OrderDetail>();
        listOrderDetail = odDAO.getOrderDetailByOrderId(order_id);
        itDAO = new ItemDAO(OrderComfirm.this);
        String content = "";
        for (int i = 0; i < listOrderDetail.size(); i++) {
            OrderDetail od = listOrderDetail.get(i);
            content += itDAO.getItemName(od.getItemId()) + "\n";
            double sub_total = multiply(itDAO.getPrice(od.getItemId()), od.getQty());
            content += itDAO.getPrice(od.getItemId()) + " * " + od.getQty() + " = " + String.valueOf(sub_total) + "\n\n";

            count += od.getQty();
            amount = add(amount, sub_total);
        }
        order_content.setText(content);
        iv_seat = (ImageView) findViewById(R.id.iv_seat);

        order_tableid = (TextView) findViewById(R.id.text_tableid);
        if (ordermaster.getIsTakeOut() == 0) {
            order_tableid.setVisibility(View.VISIBLE);
            order_tableid.setText(String.valueOf(ordermaster.getTableId()));
            iv_seat.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle));
            iv_seat.setImageResource(R.drawable.ic_event_seat_white_18dp);
        } else {
            order_tableid.setVisibility(View.INVISIBLE);
            iv_seat.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_accent));
            iv_seat.setImageResource(R.drawable.ic_card_giftcard_white_18dp);
        }
        orderitemcount = (TextView) findViewById(R.id.orderitemcount);
        orderitemcount.setText(String.valueOf(count));
        iv_sum = (ImageView) findViewById(R.id.iv_sum);
        rl_countamount = (LinearLayout) findViewById(R.id.rl_countamount);
        //检查这张订单结账了没？
        TextView text_discount = (TextView) findViewById(R.id.text_discount);
        order_sum = (TextView) findViewById(R.id.text_ordersum);
        TextView text_currency = (TextView) findViewById(R.id.text_currency);
        if (ordermaster.getStatus() == 1) {
            cm = cmDAO.getByOrderID(Long.parseLong(String.valueOf(order_id)));
            text_discount.setText("-" + cm.getDiscount() + "%");
            amount = cm.getAmount();
            order_sum.setText(String.valueOf(amount));
            text_currency.setText(cm.getCurrency());
            setIvSumChecked();
        } else {
            iv_edit.setVisibility(View.VISIBLE);
            iv_sum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle));
            rl_countamount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckOrder(order_id);
                }
            });
            iv_seat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTable(order_id);
                }
            });
            text_discount.setText("-" + Discount + "%");
            amount = amount * (100 - Discount) / 100;
            order_sum.setText(String.valueOf(amount));
            text_currency.setText(BOCurrency);
        }
        iv_getall.setVisibility(View.VISIBLE);
        iv_getall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myToolbar.setTitle("订单列表");
                showOrderMaster();
            }
        });
    }

    public String strHourMin(Long time) {
        String str = String.valueOf(new Date().getTime() - time);
        str = new SimpleDateFormat("h:mm a").format(time);
        return str;
    }

    public void setIvSumChecked() {
        iv_edit.setVisibility(View.INVISIBLE);
        iv_sum.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_accent));

        String strPayment = "";
        if (cm.getPaymentId() == 0) {
            strPayment = "现金";
        } else {
            strPayment = "i点";
        }
        String checkedOrderContent = strHourMin(cm.getCheckDate()) + " 已结账，" + strPayment + "\n";
        if (!String.valueOf(cm.getMaakkiId()).equals("0")) {
            checkedOrderContent += String.valueOf(cm.getMaakkiId()) + "\n";
        }
        if (cm.getInvoiceType() == 3) {
            checkedOrderContent += "三联式发票\n";
            checkedOrderContent += cm.getCustomerTitle() + "\n" + cm.getCustomerNo();
        } else {
            checkedOrderContent += "二联式发票\n";
        }
        checkedOrderContent += cm.getInvoiceNo() + "\n";
        checkedOrderContent += order_content.getText();
        order_content.setText(checkedOrderContent);
        rl_countamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
        iv_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

    }

    public void showOrderMaster() {
        //String Gkey="110000M@@kki.c0m1234";
        //showHashCode(getDataHash(Gkey));

        fab.setVisibility(View.VISIBLE);
        lv_om.setVisibility(View.VISIBLE);
        rl_order.setVisibility(View.INVISIBLE);
        listOrderMaster = new ArrayList<OrderMaster>();
        listOrderMaster = omDAO.getAll();

        Collections.reverse(listOrderMaster);
        adapter = new OrderMasterAdapter(getApplicationContext(), R.layout.list_item, listOrderMaster);
        lv_om.setAdapter(adapter);
        //setListViewHeightBasedOnChildren(lv_om);
        //Toast.makeText(OrderComfirm.this, "ListView Height:"+lv_om.getHeight(), Toast.LENGTH_SHORT).show();
        lv_om.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        lv_om.setOnTouchListener(swipeDetector);
        lv_om.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (!swipeDetector.swipeDetected()) {
                    if (rl_order.getVisibility() == View.GONE) {
                        lv_om.setVisibility(View.GONE);
                    }
                    showSpecifiedOrderDetail(Integer.parseInt(String.valueOf(listOrderMaster.get(position).getOrderId())));
                } else {
                    //刪除某張訂單主檔
                    showAlert(v, position);
                }
            }
        });
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            //@Override
            public void onAnimationEnd(Animation arg0) {
                listOrderMaster.remove(index);
                ViewHolder vh = (ViewHolder) v.getTag();
                vh.needInflate = true;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();
        //final int top = v.getTop();
        //Toast.makeText(getApplicationContext(), "collapsetop:"+top , Toast.LENGTH_SHORT).show();
        final int ANIMATION_DURATION = 200;
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pos, menu);
        return true;
    }

    private String FormatString(Double amount) {
        String s = String.valueOf(amount);
        //Double D;
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public double multiply(double v1, int v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public void showAlert(final View v, final int position) {
        //setErrorType(errortype);
        String title = "删除确认";
        String message = "请确认是否要删除这个订单的信息吗？";

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderComfirm.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                v.setVisibility(View.GONE);
                deleteCell(v, position);
                OrderMaster o = listOrderMaster.get(position);

                omDAO.delete(o.getOrderId());
                odDAO.deleteByOrderId(o.getOrderId());
                try {
                    //Toast.makeText(getApplicationContext(), "check_id:" +String.valueOf(cmDAO.getByOrderID(o.getOrderId()).getCheckId()),Toast.LENGTH_LONG).show();
                    cdDAO.deleteByCheckId(cmDAO.getByOrderID(o.getOrderId()).getCheckId());
                    cmDAO.deleteByOrderId(o.getOrderId());
                    //
                    /*List<CheckDetail>listCeckDetail=new ArrayList<CheckDetail>();
                    listCeckDetail=cdDAO.getCheckDetailByCheckId(cmDAO.getByOrderID(o.getOrderId()).getCheckId());
                    String hashcode="";
                    for(int i=0;i<listCeckDetail.size();i++){
                        CheckDetail cd=listCeckDetail.get(i);
                        hashcode+="detail_id"+cd.getDetailId()+"\n";
                    }
                    showHashCode(hashcode);*/
                } catch (Exception e) {
                }
                if (omDAO.getCount() == 0) {
                    //没有订单记录
                    fab.setVisibility(View.INVISIBLE);
                    imv.setVisibility(View.VISIBLE);
                }

                dialog.cancel();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void editTable(int order_id) {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(OrderComfirm.this);
        LayoutInflater inflater = OrderComfirm.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edittable_dialog, null);
        editDialog.setView(view);
        final EditText editText_tableno = (EditText) view.findViewById(R.id.tableid);
        final TextView tv2 = (TextView) view.findViewById(R.id.tv2);
        final TextView tv1 = (TextView) view.findViewById(R.id.tv1);
        editText_tableno.setText(String.valueOf(omDAO.get(order_id).getTableId()));
        editText_tableno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText_tableno.setText("");
                return false;
            }
        });
        Spinner spinner = (Spinner) view.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(OrderComfirm.this,
                R.array.edittable_option_array, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //final String[] statusArray = getResources().getStringArray(R.array.editcategory_status_array);
        spinner.setAdapter(arrayadapter);
        //0代表内用，1代表外带
        option = Integer.parseInt(String.valueOf(omDAO.get(order_id).getIsTakeOut()));
        final int orderid = order_id;
        if (option == 1) {
            tv2.setVisibility(View.INVISIBLE);
            editText_tableno.setVisibility(View.INVISIBLE);
        } else {
            tv1.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        }
        spinner.setSelection(option);

        //spinner.setBackgroundColor(R.drawable.spinner_background);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                option = position;

                if (option == 0) {
                    tv2.setVisibility(View.VISIBLE);
                    editText_tableno.setVisibility(View.VISIBLE);
                } else {
                    tv2.setVisibility(View.INVISIBLE);
                    editText_tableno.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        editDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                Dialog a = (Dialog) arg0;
                EditText editText_tableno = (EditText) a.findViewById(R.id.tableid);
                int table_id;
                try {
                    table_id = Integer.valueOf(editText_tableno.getText().toString());
                } catch (Exception e) {
                    table_id = 0;
                }
                if (option == 0) {
                    order_tableid.setVisibility(View.VISIBLE);
                    order_tableid.setText(editText_tableno.getText());
                    iv_seat.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle));
                    iv_seat.setImageResource(R.drawable.ic_event_seat_white_18dp);
                } else {
                    order_tableid.setVisibility(View.INVISIBLE);
                    iv_seat.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_accent));
                    iv_seat.setImageResource(R.drawable.ic_card_giftcard_white_18dp);
                }
                editOrderMaster_tableId(orderid, option, table_id);

            }

            public void editOrderMaster_tableId(int order_id, int isTakeOut, int table_id) {
                //Toast.makeText(OrderComfirm.this, "order_id："+order_id+"/"+isTakeOut+"/"+table_id, Toast.LENGTH_SHORT).show();
                omDAO.editTableNoByOrderId(Long.parseLong(String.valueOf(order_id)), isTakeOut, table_id);

            }
        });
        editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                //...
            }
        });
        editDialog.show();
    }

    public void CheckOrder(final int order_id) {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(OrderComfirm.this);
        LayoutInflater inflater = OrderComfirm.this.getLayoutInflater();
        view = inflater.inflate(R.layout.checkorder_dialog, null);
        editDialog.setView(view);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        tv_amount = (TextView) view.findViewById(R.id.amount);
        tv_amount.setText(String.valueOf(amount) + " " + BOCurrency + "(i)");
        final RelativeLayout rl_invoicedata = (RelativeLayout) view.findViewById(R.id.rl_invoicedata);
        final RadioButton RB_icredits = (RadioButton) view.findViewById(R.id.icredits);
        //final TextView tv_maakkiid = (TextView) view.findViewById(R.id.tv5);
        final EditText editText_maakki_id = (EditText) view.findViewById(R.id.maakki_id);
        //final TextView tv_nickname = (TextView) view.findViewById(R.id.text_nickname);
        final ImageView iv_scan = (ImageView) view.findViewById(R.id.iv_scan);

        editText_maakki_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //tv_nickname.setVisibility(View.VISIBLE);
                //tv_nickname.setText("WonderGirl");
            }

            @Override
            public void afterTextChanged(Editable s) {
                maakkiid = editText_maakki_id.getText().toString();
                AsyncCallWS_IsMember getNicknameTask = new AsyncCallWS_IsMember();
                getNicknameTask.execute();
                //tv_nickname.setVisibility(View.VISIBLE);
                //tv_nickname.setText("BatMan");
            }
        });


        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pid = "";
                ck = "";
                Intent i = new Intent(OrderComfirm.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                /*String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
                try {
                    Intent intent3 = new Intent(ACTION_SCAN);
                    intent3.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent3, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(OrderComfirm.this, "欢迎使用二维码系统", "请先下载扫描器，再重新开启玛吉APP，这个程序只需要一次。", "开始", "退出").show();
                }*/
            }
        });


        RB_icredits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (RB_icredits.isChecked()) {
                    payment = 1;
                    tv_amount.setText(String.valueOf(amount) + " " + BOCurrency + "(i)");
                    if (pid.equals("") || ck.equals("")) {
                        tv_msg.setVisibility(View.VISIBLE);
                        tv_msg.setText("请扫描绿色二维码");
                    } else {
                        tv_msg.setVisibility(View.GONE);
                        tv_msg.setText("");
                    }
                } else {
                    payment = 0;
                    tv_amount.setText(String.valueOf(amount) + " " + BOCurrency);
                    tv_msg.setVisibility(View.GONE);
                    tv_msg.setText("");
                }
            }
        });
        /*181204*/
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(OrderComfirm.this,
                R.array.checkorder_option_array, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //final String[] statusArray = getResources().getStringArray(R.array.editcategory_status_array);
        spinner.setAdapter(arrayadapter);
        //0代表内用，1代表外带
        option = 0;
        //Integer.parseInt(String.valueOf(omDAO.get(order_id).getIsTakeOut()));
        final int orderid = order_id;
        if (option == 0) {
            rl_invoicedata.setVisibility(View.GONE);

        } else {
            rl_invoicedata.setVisibility(View.VISIBLE);
        }
        spinner.setSelection(option);

        //spinner.setBackgroundColor(R.drawable.spinner_background);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                option = position;
                if (option == 0) {
                    rl_invoicedata.setVisibility(View.GONE);
                } else {
                    rl_invoicedata.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        editDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                Dialog a = (Dialog) arg0;
                //TextView tv_amount = (TextView) a.findViewById(R.id.amount);
                EditText editText_maakkiid = (EditText) a.findViewById(R.id.maakki_id);
                EditText editText_customerTitle = (EditText) a.findViewById(R.id.customer_title);
                customer_title = editText_customerTitle.getText().toString();
                EditText editText_customerNo = (EditText) a.findViewById(R.id.customer_no);
                customer_no = editText_customerNo.getText().toString();
                customer_maakkiid = editText_maakkiid.getText().toString();
                final RadioButton RB_cash = (RadioButton) a.findViewById(R.id.cash);
                //payment 1 是i点；0是现金；
                //payment = 1;
                //如果是现金付款直接结账，如果是i点付款必须先执行AsyncCallWS_POScheck
                if (RB_cash.isChecked()) {
                    //payment = 0;
                    CheckComfirm();
                } else {
                    if (pid.equals("") || ck.equals("")) {
                    } else {
                        AsyncCallWS_POScheck POSCheckTask = new AsyncCallWS_POScheck();
                        POSCheckTask.execute();
                    }
                }


            }
        });
        editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                //...
            }
        });
        editDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            //Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                //Toast.makeText(OrderComfirm.this, "没有正确扫描到二维码", Toast.LENGTH_LONG).show();
                /*AlertDialog alertDialog = new AlertDialog.Builder(OrderComfirm.this).create();
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
            String maakki_id = "";
            EditText editText_maakkiid = (EditText) view.findViewById(R.id.maakki_id);
            //TextView tv_maakkiid=(TextView) view.findViewById(R.id.tv_maakki_id);
            if (contents.contains("maakki_id")) {
                String[] strArray = contents.split("maakki_id=");
                maakki_id = strArray[1];
                //editText_maakkiid.setVisibility(View.VISIBLE);
                //tv_maakkiid.setVisibility(View.INVISIBLE);
                editText_maakkiid.setText(maakki_id);
            } else {
                String[] strArray = contents.split("aspx?");
                String[] strArray1 = strArray[1].split("&");
                String[] strArray2 = strArray1[0].split("=");
                String[] strArray3 = strArray1[1].split("=");
                String[] strArray4 = strArray1[2].split("=");
                pid = strArray2[1];
                maakki_id = strArray3[1];
                ck = strArray4[1];
                //editText_maakkiid.setVisibility(View.INVISIBLE);
                //tv_maakkiid.setVisibility(View.VISIBLE);
                //tv_maakkiid.setText("pid="+pid+"\n"+"maakki_id="+maakkiid+"\n"+"ck="+ck);
            }
            editText_maakkiid.setText(maakki_id);

            if (payment == 1 & (pid.equals("") || ck.equals(""))) {
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText("请扫描绿色二维码");
                //Toast.makeText(OrderComfirm.this, "使用i点付款，请确认绿色二维码", Toast.LENGTH_LONG).show();
                /*AlertDialog alertDialog = new AlertDialog.Builder(OrderComfirm.this).create();
                alertDialog.setTitle("客户端出示错误二维码");
                alertDialog.setMessage("使用i点付款，请确认正确绿色二维码");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();*/
            } else {
                tv_msg.setVisibility(View.GONE);
                tv_msg.setText("");
            }
        }
    }

    private void CheckComfirm() {

        cm.setOrderId(Long.parseLong(String.valueOf(order_id)));
        cm.setCheckDate(new Date().getTime());
        cm.setInvoiceDate(new Date().getTime());
        //cm.setInvoiceNo(invoice_no);
        cm.setStatus(1);
        cm.setAmount(amount);
        cm.setCurrency(BOCurrency);
        cm.setDiscount(Discount);
        if (option == 0) {
            cm.setInvoiceType(2); //2代表二联，3代表三联
        } else {
            cm.setInvoiceType(3);
            cm.setCustomerTitle(customer_title);
            cm.setCustomerNo(customer_no);
        }
        int maakki_id;
        try {
            maakki_id = Integer.parseInt(customer_maakkiid);
        } catch (Exception e) {
            maakki_id = 0;
        }
        cm.setMaakkiId(maakki_id);
        cm.setPaymentId(payment); //0代表现金，1代表i点*/
        //Toast.makeText(OrderComfirm.this, "order_id："+order_id, Toast.LENGTH_SHORT).show();
        OrderMaster orma = omDAO.get(order_id);
        orma.setStatus(1);
        //Toast.makeText(OrderComfirm.this, "status："+orma.getStatus(), Toast.LENGTH_SHORT).show();
        omDAO.update(orma);
        CheckMaster cm1 = cmDAO.insert(cm);
        check_id = String.valueOf(cm1.getCheckId());
        //Toast.makeText(OrderComfirm.this, "check_id："+cm1.getCheckId(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < listOrderDetail.size(); i++) {
            OrderDetail od = listOrderDetail.get(i);
            cd.setCheckId(cm1.getCheckId());
            cd.setItemId(od.getItemId());
            cd.setItemName(itDAO.get(od.getItemId()).getItemName());
            cd.setPrice(itDAO.get(od.getItemId()).getPrice());
            cd.setQty(od.getQty());
            //hashcode+="CheckId:"+cd.getCheckId()+"\n";
            //hashcode+="ItemId:"+cd.getItemId()+"\n";
            //hashcode+="ItemName:"+cd.getItemName()+"\n";
            //hashcode+="Price:"+cd.getPrice()+"*"+cd.getQty()+"\n\n";
            cdDAO.insert(cd);
        }
        //showHashCode(hashcode);
        setIvSumChecked();
    }

    public String getHashCode(String Gkey) { //得到毫秒数

        MessageDigest shaCode = null;
        Date curDate = new Date();
        String dataStructure = Gkey;
        try {
            shaCode = MessageDigest.getInstance("SHA-256");
            shaCode.update(dataStructure.getBytes());
            //System.out.println("dataStructure="+Gkey);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return byte2Hex(shaCode.digest());
    }

    public void showHashCode(String hashcode) {
        //setErrorType(errortype);
        String title = "确认hashcode";
        String message = hashcode;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderComfirm.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void getWebService_POSISMember() {
        //Create request
        String METHOD_NAME = "POSIsMember";
        String SOAP_ACTION = StaticVar.namespace+"POSIsMember";
        //String METHOD_NAME = "getOnlineList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String bo_maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        //(int bo_id, int cs_id, Int64 timeStamp, string identifyStr)
        String timeStamp = String.valueOf(new Date().getTime());
        String identifyStr = getHashCode(bo_maakkiid.trim() + maakkiid.trim() + "M@@kki.c0m" + timeStamp.trim());
        //request.addProperty("mid", maakkiid);
        request.addProperty("bo_id", bo_maakkiid);
        request.addProperty("cs_id", maakkiid);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //soapEnvelope.setOutputSoapObject(Request);
        nickname = "Transformer";

        //SoapObject soap = null;
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            //soap = (SoapObject)envelope.getResponse();
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        try {
            json_read = new JSONObject(tmp);
            errMsg = json_read.getString("errMsg");
            ;
            isMember = json_read.getBoolean("isMember");
            iCredit = json_read.getDouble("cs_iCredit");
            Currency = json_read.getString("cs_currency");
            nickname = json_read.getString("nickname");
        } catch (Exception e) {
            nickname = e.toString();
        }/**/
        //nickname=soapPrimitive.toString();
    }

    private void getWebService_POSCheck() {
        //Create request
        String METHOD_NAME = "POSCheck";
        String SOAP_ACTION = StaticVar.namespace+"POSCheck";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String bo_maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        //int bo_id, int cs_id, Int64 timeStamp, string BOCurrency, int check_id, decimal price, decimal iCredit, int pid, string ck, string identifyStr)

        String timeStamp = String.valueOf(new Date().getTime());
        check_id = "20181211";
        String price = "0";
        String i = "0";
        if (payment == 0) {
            price = String.valueOf(amount);
        } else {
            i = String.valueOf(amount);
        }
        String identifyStr = getHashCode(bo_maakkiid.trim() + maakkiid.trim() + "M@@kki.cc" + timeStamp.trim() + check_id + String.valueOf(amount));
        //request.addProperty("mid", maakkiid);
        request.addProperty("bo_id", bo_maakkiid);
        request.addProperty("cs_id", maakkiid);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("BOCurrency", BOCurrency);
        request.addProperty("check_id", check_id);
        request.addProperty("price", price);
        request.addProperty("iCredit", i);
        request.addProperty("pid", pid);
        request.addProperty("ck", ck);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //soapEnvelope.setOutputSoapObject(Request);
        //nickname="Transformer";

        //SoapObject soap = null;
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            //soap = (SoapObject)envelope.getResponse();
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        //nickname=soapPrimitive.toString();
        String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        try {
            json_read = new JSONObject(tmp);
            errMsg = json_read.getString("errMsg");
            ;
            isMember = json_read.getBoolean("order_id");
        } catch (Exception e) {
        }/**/
        //nickname=soapPrimitive.toString();
    }

    public class ViewHolder {
        public TextView text_orderid;
        public TextView text_orderdate;
        public TextView text_status;
        public ImageView image_icon;
        public boolean needInflate;
    }

    public class OrderMasterAdapter extends ArrayAdapter<OrderMaster> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public OrderMasterAdapter(Context context, int textViewResourceId, List<OrderMaster> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //RecyclerView.ViewHolder holder;
            //LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            final View view;
            ViewHolder holder;
            OrderMaster o = (OrderMaster) getItem(position);

            if (convertView == null) {
                //Toast.makeText(PreNotificationList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(PreNotificationList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item, parent, false);
                setViewHolder(view);
            } else {
                //Toast.makeText(PreNotificationList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();
            //int icon = R.drawable.quote;
            holder.image_icon.setPadding(9, 9, 9, 9);
            holder.image_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.image_icon.setImageResource(R.drawable.ic_event_seat_white_18dp);
            if (o.getIsTakeOut() == 0) {
                holder.image_icon.setImageResource(R.drawable.seat);
                //holder.image_icon.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.tips_circle));
            } else {
                holder.image_icon.setImageResource(R.drawable.carryout);
                //holder.image_icon.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.tips_circle_accent));
            }
            //String mpicfile=s.getPicfile();
            //String pic_url = StaticVar.webURL+ "/function/getImage.aspx?file_id=" + mpicfile + "&width=45&height=45&forcibly=Y&dimg=Y";
            //if(holder.image.getDisplay()==null){
            //new DownloadImageTask(holder.image_icon).execute(pic_url);

            //imageLoader.displayImage(pic_url, holder.image_icon);
            holder.text_orderid.setText(String.valueOf(o.getOrderId()));
            Long time = o.getOrderDate();
            String fm = "";
            String dateFormat = "yyyy-MM-dd HH:mm";
            //String dateFormat = "HH:mm";
            DateFormat formatter = new SimpleDateFormat(dateFormat);
            //Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(time);
            Date date = new Date(time);
            //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
            fm = formatter.format(date);
            if (o.getStatus() < 1) {
                if (o.getIsTakeOut() < 1) {
                    fm += " / " + o.getTableId();
                }
            } else {
                //fm+=" / "+cmDAO.getCount();
                dateFormat = "HH:mm";
                String strHourMin = new SimpleDateFormat(dateFormat).format(cmDAO.getByOrderID(o.getOrderId()).getCheckDate());
                String strCdCount = String.valueOf(cdDAO.getCount());
                //getCheckDetailByCheckId(cmDAO.getByOrderID(o.getOrderId()).getCheckId()).size());
                //String strCdCount=String.valueOf(Long.parseLong(String.valueOf(order_id)));
                fm += " / " + strCdCount + "．$" + cmDAO.getByOrderID(Long.parseLong(String.valueOf(o.getOrderId()))).getAmount() + " / " + strHourMin;
                //
            }
            holder.text_orderdate.setText(fm);
            //Date date =new Date();
            //String dateFormat = "yyyy-MM-dd HH:mm:ss";
            //DateFormat formatter = new SimpleDateFormat(dateFormat);
            //fm=formatter.format(date);
            holder.text_orderdate.setText(fm);
            holder.text_status.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
            if (o.getStatus() > 0) {
                holder.text_status.setText("已結账");
            } else {
                holder.text_status.setText("待結账");
            }
            return view;
        }

        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.image_icon = (ImageView) view.findViewById(R.id.icon);
            vh.text_orderid = (TextView) view.findViewById(R.id.title);
            vh.text_orderdate = (TextView) view.findViewById(R.id.message);
            vh.text_status = (TextView) view.findViewById(R.id.time);
            vh.needInflate = false;
            view.setTag(vh);
        }
    }

    private class AsyncCallWS_IsMember extends AsyncTask<String, Void, Void> {
        TextView tv_nickname = (TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_POSISMember();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            tv_nickname.setVisibility(View.VISIBLE);
            //tv_nickname.setText("Superman");
            tv_nickname.setText(nickname);
        }

        @Override
        protected void onPreExecute() {
            tv_nickname.setVisibility(View.VISIBLE);
            tv_nickname.setText("Waiting..");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private class AsyncCallWS_POScheck extends AsyncTask<String, Void, Void> {
        //TextView tv_nickname=(TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_POSCheck();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String Msg = "结账完成";
            if (!errMsg.equals("")) {
                Msg = errMsg;
            } else {
                CheckComfirm();
            }
            Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "结账中..", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }
}
