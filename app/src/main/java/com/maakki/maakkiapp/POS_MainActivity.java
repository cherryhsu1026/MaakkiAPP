package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by ryan on 2017/7/17.
 */

public class POS_MainActivity extends AppCompatActivity {
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebService.asmx";
    Button btn;
    TextView tv_sum;
    //FloatingActionButton fab_cashier,fab_add;
    RelativeLayout rl_cashier;
    int specific_id = 0;
    int rotation;
    Integer cat_no = 0, status;
    Category cat;
    int count = 0, order_id = 0, detail_id = 0;
    double amount = 0;
    List<OrderDetail> listOrderDetail;
    SharedPreferences.Editor prefs;
    Toolbar myToolbar;
    private TabLayout mTabs;
    private ViewPager mViewPager;
    private GridView gv;
    private List<Category> listCategory;
    //private List<OrderDetail> listOrderDetail;
    private ItemAdapter adapter;
    private ImageView imv;
    private CategoryDAO catDAO;
    private OrderDetailDAO odDAO;
    private OrderMasterDAO omDAO;
    private ItemDAO itemDAO;
    private OrderMaster om_comfirm;
    private String errMsg, StoreName, BOCurrency, MR, Discount;
    private boolean isPercent;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item:
                    Intent intent1 = new Intent(POS_MainActivity.this, POS_EditCategory.class);
                    startActivity(intent1);
                    break;
                case R.id.record:
                    Intent intent2 = new Intent(POS_MainActivity.this, OrderComfirm.class);
                    intent2.putExtra("order_id", "0");
                    startActivity(intent2);
                    break;
                case R.id.settings:
                    Intent intent3 = new Intent(POS_MainActivity.this, POSPrefsActivity.class);
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

    //private List<OrderDetail> listComfirmDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pos_main);
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Discount=sharedPrefs.getString("Discount","0");
        BOCurrency = sharedPrefs.getString("credit_options", "RMB");
        StoreName = sharedPrefs.getString("StoreName", "");
        prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        listOrderDetail = new ArrayList<OrderDetail>();
        if (!this.getIntent().getExtras().equals(null)) {
            Bundle bundle = this.getIntent().getExtras();
            order_id = Integer.valueOf(bundle.getString("order_id"));
            omDAO = new OrderMasterDAO(POS_MainActivity.this);
            //om = new OrderMaster();
            odDAO = new OrderDetailDAO(POS_MainActivity.this);
            om_comfirm = new OrderMaster();
            om_comfirm = omDAO.get(Long.parseLong(String.valueOf(order_id)));
            //listComfirmDetail=new ArrayList<OrderDetail>();
            listOrderDetail = odDAO.getOrderDetailByOrderId(Long.parseLong(String.valueOf(order_id)));
            //Toast.makeText(getApplicationContext(), "count:"+listOrderDetail.size(), Toast.LENGTH_LONG).show();
        }

        mTabs = (TabLayout) findViewById(R.id.tabs);
        //
        cat = new Category();
        listCategory = new ArrayList<Category>();
        catDAO = new CategoryDAO(this);
        tv_sum = (TextView) findViewById(R.id.tv_sum);
        tv_sum.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.check);
        imv = (ImageView) findViewById(R.id.imv);
        odDAO = new OrderDetailDAO(POS_MainActivity.this);
        itemDAO = new ItemDAO(POS_MainActivity.this);
        setView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AsyncCallWS_getBOData getBODataTask = new AsyncCallWS_getBOData();
        getBODataTask.execute();
        //Toast.makeText(getApplicationContext(), "order_id:"+order_id+"\nlistOrderDetail.size():"+listOrderDetail.size(), Toast.LENGTH_LONG).show();
    }

    public void setView() {
        listCategory = catDAO.getActivated();
        cat_no = listCategory.size();
        //Toast.makeText(getApplicationContext(), "setView():"+order_id, Toast.LENGTH_LONG).show();
        //subtitle = (TextView) findViewById(R.id.item_subtitle);
        rl_cashier = (RelativeLayout) findViewById(R.id.rl_cashier);

        if (cat_no > 0) {
            imv.setVisibility(View.INVISIBLE);
            rl_cashier.setVisibility(View.VISIBLE);
            //title.setVisibility(View.INVISIBLE);
            //fab_add.setVisibility(View.INVISIBLE);
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            /*if (mViewPager != null) {
                setupViewPager(mViewPager);
            }*/
            mViewPager.setVisibility(View.VISIBLE);
            for (int i = 0; i < cat_no; i++) {
                cat = listCategory.get(i);
                //mTabs.setTag(i);
                mTabs.addTab(mTabs.newTab().setText(cat.getCatName()));
            }
            mTabs.setVisibility(View.VISIBLE);
            //mTabs.setOnClickListener(mTabOnClickListener);
            mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            //mTabs.setupWithViewPager(mViewPager);
            if (cat_no < 5) {
                mTabs.setTabMode(TabLayout.MODE_FIXED);
            }

            mViewPager.setAdapter(new SamplePagerAdapter());
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));

        } else {
            rl_cashier.setVisibility(View.INVISIBLE);
            imv.setVisibility(View.VISIBLE);
            if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                imv.setImageResource(R.drawable.no_category_l);
            } else {
                imv.setImageResource(R.drawable.no_category_p);
            }
            //fab_add.setVisibility(View.VISIBLE);
            //fab_add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
            imv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewCategory();
                }
            });
        }


        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                //Intent intent = new Intent(POS_MainActivity.this, MainActivity.class);
                //startActivity(intent);
            }
        });
        if (order_id > 0) {
            myToolbar.setTitle("编辑订单：" + order_id);
            for (int i = 0; i < listOrderDetail.size(); i++) {
                count += listOrderDetail.get(i).getQty();
                double sub_totla = multiply(itemDAO.get(listOrderDetail.get(i).getItemId()).getPrice(), listOrderDetail.get(i).getQty());
                amount = add(amount, sub_totla);
                ;
            }
            //listOrderDetail=listComfirmDetail;
            CheckToComfirm();
        } else {
            if (!StoreName.equals("")) {
                myToolbar.setTitle(StoreName);
            }
        }
        //myToolbar.setTitle("POS 1.0");
    }

    public void addNewCategory() {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(POS_MainActivity.this);
        LayoutInflater inflater = POS_MainActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.editcategory_dialog, null);
        editDialog.setView(view);
        EditText editText_gp = (EditText) view.findViewById(R.id.groupid);
        editText_gp.setText("0");
        Spinner spinner = (Spinner) view.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(POS_MainActivity.this,
                R.array.editcategory_status_newadd_array, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //final String[] statusArray = getResources().getStringArray(R.array.editcategory_status_array);
        spinner.setAdapter(arrayadapter);
        status = 0;
        spinner.setSelection(status);

        //spinner.setBackgroundColor(R.drawable.spinner_background);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                status = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        //String[] status ={"停用","启用","删除"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,status);
        editDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //
                Dialog a = (Dialog) arg0;
                EditText editText_cat = (EditText) a.findViewById(R.id.catname);
                EditText editText_gp = (EditText) a.findViewById(R.id.groupid);
                Spinner spinner = (Spinner) a.findViewById(R.id.status_spinner);
                //Toast.makeText(getApplicationContext(), editText_cat.getText().toString(),Toast.LENGTH_LONG).show();
                //subtitle.setText(editText_cat.getText().toString());
                String catname = editText_cat.getText().toString();
                int groupid = 0;
                try {
                    groupid = Integer.parseInt(editText_gp.getText().toString());
                } catch (Exception e) {
                }
                //addNewCategory(catname,groupid,status);
                //reload=true;/**/
                //tv.setText(editText_cat.getText().toString());
                //private void addNewCategory(String cname,int g,int status) {
                for (int i = 1; i < listCategory.size(); i++) {
                    Category c = listCategory.get(i);
                    //除了"新增"之外，全部往后退一位给新分类让位
                    c.setPosition(c.getPosition() + 1);
                }
                Category nc = new Category();
                nc.setCatName(catname);
                nc.setGroupId(groupid);
                nc.setPosition(1);
                nc.setStatus(status);
                Date LastDate = new Date();
                long timeMilliseconds = LastDate.getTime();
                nc.setLastModify(timeMilliseconds);
                nc = catDAO.insert(nc);
                //long cat_id=nc.getCatId();
                Bundle bundle = new Bundle();
                bundle.putString("catname", nc.getCatName());
                bundle.putString("catid", String.valueOf(nc.getCatId()));
                Intent i = new Intent(POS_MainActivity.this, POS_EditItem.class);
                i.putExtras(bundle);
                startActivity(i);
                //setView();;
                /*listCategory.add(1,nc);*/

                //adapter.notifyDataSetChanged();
                //}

            }


        });
        editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                //...
            }
        });
        editDialog.show();
        /**/
        //RefreshView(reload);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pos, menu);
        return true;
    }

    public double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public double multiply(double v1, int v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public void CheckToComfirm() {
        btn.setText("CHECK" + "．" + count + "．" + amount + " " + BOCurrency);
        btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_red));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int order_id=0;
                //存订单主檔
                OrderMasterDAO omDAO = new OrderMasterDAO(POS_MainActivity.this);
                //20181212OrderMasterDAO omDAO = new OrderMasterDAO(getApplicationContext());
                OrderMaster om = new OrderMaster();
                long time = new Date().getTime();
                om.setOrderDate(time);
                if (order_id > 0) {
                    om.setOrderId(Long.parseLong(String.valueOf(order_id)));
                    om.setTableId(om_comfirm.getTableId());
                    om.setIsTakeOut(om.getIsTakeOut());
                    omDAO.update(om);
                } else {
                    om.setTableId(0);
                    om.setIsTakeOut(0);
                    OrderMaster nom = omDAO.insert(om);
                    order_id = Integer.parseInt(String.valueOf(nom.getOrderId()));
                }/**/
                //得到订单编号，存详细檔

                for (int i = 0; i < listOrderDetail.size(); i++) {
                    OrderDetail od = listOrderDetail.get(i);
                    od.setOrderId(order_id);
                    //旧的orderdetail就是update

                    //if(i==specific_id){ }
                    if (od.getDetailId() > 0) {
                        //Toast.makeText(getApplicationContext(), "點了 :"+itemDAO.get(od.getItemId()).getItemName()+" "+od.getQty()+"個", Toast.LENGTH_LONG).show();
                        odDAO.update(od);
                    }
                    //new orderdetail must be added in odDAO
                    else {
                        odDAO.insert(od);
                    }
                }
                //到下一步：订单确认
                Intent i = new Intent(POS_MainActivity.this, OrderComfirm.class);
                i.putExtra("order_id", String.valueOf(order_id));
                startActivity(i);
            }
        });
    }

    private void getWebService_getBOData() {
        //Create request(int bo_id, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "getBOData";
        String SOAP_ACTION = StaticVar.namespace+"getBOData";
        //String METHOD_NAME = "getOnlineList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String bo_maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        //(int bo_id, int cs_id, Int64 timeStamp, string identifyStr)
        String timeStamp = String.valueOf(new Date().getTime());
        String identifyStr = getHashCode(bo_maakkiid.trim() + "M@@kki.cc" + timeStamp.trim()).toUpperCase();
        //identifyStr bo_id+"M@@kki.cc"+timeStamp  (hash256)
        request.addProperty("bo_id", bo_maakkiid);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

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
            StoreName = json_read.getString("StoreName");
            BOCurrency = json_read.getString("BOCurrency");
            isPercent = json_read.getBoolean("isPercent");
            MR = json_read.getString("MR");
            Discount = json_read.getString("discount");
        } catch (Exception e) {
            //BOCurrency=e.toString()+"/"+identifyStr+"/"+tmp;
        }
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

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return cat_no;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            container.addView(view);

            //title.setVisibility(View.VISIBLE);
            //subtitle.setVisibility(View.VISIBLE);
            //title.setText(String.valueOf(position + 1));
            /**/

            List<Item> listActivatedItem = new ArrayList<Item>();

            ItemDAO itemDAO = new ItemDAO(POS_MainActivity.this);
            //Item item=new Item();
            long cat_id = listCategory.get(position).getCatId();
            final Category c = listCategory.get(position);
            listActivatedItem = itemDAO.getActivatedItemByCategory(cat_id);
            ImageView imv_pager = (ImageView) view.findViewById(R.id.imv_pager);
            gv = (GridView) view.findViewById(R.id.gridview);
            if (listActivatedItem.size() == 0) {
                imv_pager.setVisibility(View.VISIBLE);
                if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                    imv_pager.setImageResource(R.drawable.no_item_l);
                } else {
                    imv_pager.setImageResource(R.drawable.no_item_p);
                }
                imv_pager.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("catname", c.getCatName());
                        bundle.putString("catid", String.valueOf(c.getCatId()));
                        Intent i = new Intent(POS_MainActivity.this, POS_EditItem.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                gv.setVisibility(View.INVISIBLE);
                //fab_cashier.setVisibility(View.INVISIBLE);
            } else {
                gv.setVisibility(View.VISIBLE);
                imv_pager.setVisibility(View.GONE);
                //fab_cashier.setVisibility(View.VISIBLE);
            }
            //listActivatedItem=itemDAO.getAll();
            //int count = listActivatedItem.size();
            //Toast.makeText(getApplicationContext(), "ActivatedItemNo :"+count, Toast.LENGTH_LONG).show();
            //item.setItemName("新增");
            //listActivatedItem.add(0,item);
            //}
            adapter = new ItemAdapter(POS_MainActivity.this, listActivatedItem);
            gv.setAdapter(adapter);
            /**/
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class ItemAdapter extends BaseAdapter {
        private Context mContext;
        private List<Item> listItem;
        //private String TAG = "PGGURU";
        //private ImageLoaderConfiguration config;
        //private DisplayImageOptions options;
        //private ImageLoader imageLoader;

        public ItemAdapter(Context c, List<Item> navDrawerItems) {
            mContext = c;
            this.listItem = navDrawerItems;
        }

        public int getCount() {
            return listItem.size();
        }

        public Object getItem(int position) {
            return listItem.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            //RecyclerView.ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final Item i = listItem.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                convertView = mInflater.inflate(R.layout.main_item, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.text_itemname);
                holder.tv_qty = (TextView) convertView.findViewById(R.id.text_qty);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            //listOrderDetail start to be functional
            for (int j = 0; j < listOrderDetail.size(); j++) {
                if (i.getItemId() == listOrderDetail.get(j).getItemId()) {
                    //Toast.makeText(getApplicationContext(), "ItemName:"+i.getItemName(), Toast.LENGTH_LONG).show();
                    holder.item_count = listOrderDetail.get(j).getQty();
                    holder.tv_qty.setVisibility(View.VISIBLE);
                    holder.tv_qty.setText(String.valueOf(holder.item_count));
                    //count+=holder.item_count;
                    //amount=add(amount,i.getPrice()*listComfirmDetail.get(j).getQty());
                }
            }
            holder.tv_name.setText(i.getItemName());
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tv_qty.setVisibility(View.VISIBLE);
                    holder.item_count++;
                    count++;
                    amount = add(amount, i.getPrice());
                    holder.tv_qty.setText(String.valueOf(holder.item_count));
                    tv_sum.setText(FormatString(amount));

                    //每当有品项被 Click，就必须检查
                    boolean hasOrdered = false;
                    for (int j = 0; j < listOrderDetail.size(); j++) {
                        if (i.getItemId() == listOrderDetail.get(j).getItemId()) {
                            listOrderDetail.get(j).setQty(holder.item_count);
                            specific_id = j;
                            //Toast.makeText(getApplicationContext(), "點了 :"+i.getItemName()+" "+holder.item_count+"個", Toast.LENGTH_LONG).show();
                            hasOrdered = true;
                        }
                    }
                    if (!hasOrdered) {
                        OrderDetail od = new OrderDetail();
                        od.setItemId(i.getItemId());
                        od.setQty(holder.item_count);
                        if (order_id > 0) {
                            od.setOrderId(Long.parseLong(String.valueOf(order_id)));
                        }
                        //新加入的orderdetail是没有detail_id
                        listOrderDetail.add(od);
                    }
                    //Click Check Button
                    if (count > 0) {
                        CheckToComfirm();
                    } else {
                        btn.setText("CHECK");
                        btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_grey));
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Do nothing
                            }
                        });
                    }
                }
            });
            holder.tv_name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    for (int j = 0; j < listOrderDetail.size(); j++) {
                        if (listOrderDetail.get(j).getItemId() == i.getItemId()) {
                            //Toast.makeText(getApplicationContext(), "ItemName:"+i.getItemName()+" removed", Toast.LENGTH_LONG).show();
                            listOrderDetail.remove(listOrderDetail.get(j));
                            //delete odDAO里特定的 orderdetail
                            //odDAO.deleteByItemId(order_id,listOrderDetail.get(j).getItemId());
                            odDAO.deleteByItemId(order_id, i.getItemId());
                        }
                    }
                    holder.tv_qty.setVisibility(View.INVISIBLE);
                    int q = holder.item_count;
                    Double a = q * i.getPrice();
                    count = count - q;
                    amount = sub(amount, a);
                    holder.item_count = 0;
                    tv_sum.setText(FormatString(amount));

                    if (count > 0) {
                        btn.setText("CHECK" + "．" + count + "．" + amount + " " + BOCurrency);
                        btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_red));
                    } else {
                        //刪除所有的點餐，一件不剩
                        if (order_id > 0) {
                            btn.setText("刪除這张点餐單?");
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    omDAO.delete(Long.parseLong(String.valueOf(order_id)));
                                    odDAO.deleteByOrderId(Long.parseLong(String.valueOf(order_id)));
                                    Intent i = new Intent(POS_MainActivity.this, OrderComfirm.class);
                                    i.putExtra("order_id", "0");
                                    startActivity(i);

                                }
                            });
                        } else {
                            btn.setText("CHECK");
                            btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_grey));
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Do nothing
                                }
                            });
                        }
                    }
                    //String parameter="count:"+count+"/listOrderDetail.size():"+listOrderDetail.size();
                    //Toast.makeText(getApplicationContext(), parameter,Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            return convertView;
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

        public double sub(double v1, double v2) {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.subtract(b2).doubleValue();
        }

        public class ViewHolder {
            public TextView tv_name;
            public TextView tv_qty;
            public int item_count = 0;
        }

    }

    private class AsyncCallWS_getBOData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_getBOData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            prefs.putString("credit_options", BOCurrency);
            prefs.putString("StoreName", StoreName);
            prefs.putString("Discount", Discount);
            prefs.putString("isPercent", String.valueOf(isPercent));
            prefs.putString("MR", MR);
            prefs.commit();
            if (!StoreName.equals("")& order_id == 0) {
                myToolbar.setTitle(StoreName);
            }
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(), BOCurrency , Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }


}
