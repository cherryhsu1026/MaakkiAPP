package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ryan on 2017/7/17.
 */

public class POS_EditItem extends AppCompatActivity {
    GridView gv;
    Integer count = 0;
    long catid = 0;
    String catname;
    private Category cat;
    private Item item;
    private ItemAdapter adapter;
    private List<Item> listItem;
    private ItemDAO itemDAO;
    //private Category1DAO c1DAO;
    private int status;
    private TextView tv;
    private EditText editText_cat, editText_item;
    private Toolbar myToolbar;
    //private SwipeDetector swipeDetector = new SwipeDetector();
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item:
                    Intent intent1 = new Intent(POS_EditItem.this, POS_EditCategory.class);
                    startActivity(intent1);
                    break;
                case R.id.record:
                    Intent intent2 = new Intent(POS_EditItem.this, OrderComfirm.class);
                    intent2.putExtra("order_id", "0");
                    startActivity(intent2);
                    break;
                case R.id.settings:
                    Intent intent3 = new Intent(POS_EditItem.this, POSPrefsActivity.class);
                    startActivity(intent3);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcategory);
        if (!this.getIntent().getExtras().equals(null)) {
            Bundle bundle = this.getIntent().getExtras();
            catname = bundle.getString("catname");
            catid = Long.parseLong(bundle.getString("catid"));
        }
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                //Intent intent = new Intent(POS_EditItem.this, POS_EditCategory.class);
                //startActivity(intent);
            }
        });
        FloatingActionButton fab_cashier = (FloatingActionButton) findViewById(R.id.cashier_button);
        fab_cashier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POS_EditItem.this, POS_MainActivity.class);
                i.putExtra("order_id", "0");
                startActivity(i);
            }
        });
        gv = (GridView) findViewById(R.id.gridview);

        listItem = new ArrayList<Item>();
        itemDAO = new ItemDAO(this);
        item = new Item();
        listItem = itemDAO.getDisplayedItemByCategory(catid);
        item.setItemName("新增");
        listItem.add(0, item);
        count = listItem.size() - 1;
        myToolbar.setTitle(catname + " (" + count + ")");

        adapter = new ItemAdapter(this, listItem);
        gv.setAdapter(adapter);
        //gv.setOnDragListener(new GvDragListener());
        /*gv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_drag));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(null, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                return false;
            }
        });*/

        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                //v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_drag));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(null, shadowBuilder, v, 0);
                //position_draged=position;
                //v.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        //setOnLonglick();
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //tv = (TextView) findViewById(R.id.text);
                //新增一个品项
                if (position == 0) {
                    AddNewItem();
                }
                //Update an existed item
                else {
                    UpdateExistedItem(position);
                }
                //return true;
            }
        });

    }

    public void UpdateExistedItem(int position) {
        //final Category c1=listCategory.get(position);
        AlertDialog.Builder editDialog = new AlertDialog.Builder(POS_EditItem.this);
        LayoutInflater inflater = POS_EditItem.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edititem_dialog, null);
        editDialog.setView(view);
        final int pt = position;
        final Item i = listItem.get(position);
        EditText editText_itemname = (EditText) view.findViewById(R.id.itemname);
        EditText editText_price = (EditText) view.findViewById(R.id.price);
        Spinner spinner = (Spinner) view.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(POS_EditItem.this,
                R.array.editcategory_status_array, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //final String[] statusArray = getResources().getStringArray(R.array.editcategory_status_array);
        spinner.setAdapter(arrayadapter);
        status = i.getStatus();
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
        //Toast.makeText(getApplicationContext(), c.getCatName()+"/"+c.getGroupId(),Toast.LENGTH_LONG).show();
        //subtitle.setText(editText_cat.getText().toString());
        editText_itemname.setText(i.getItemName());
        editText_price.setText(String.valueOf(i.getPrice()));
        final long itemid = i.getItemId();
        editDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //
                Dialog a = (Dialog) arg0;
                EditText editText_itemname = (EditText) a.findViewById(R.id.itemname);
                EditText editText_price = (EditText) a.findViewById(R.id.price);
                String itemname = editText_itemname.getText().toString();
                Double price = 0.0;
                try {
                    price = Double.parseDouble(editText_price.getText().toString());
                } catch (Exception e) {
                }
                updateCategory(itemid, pt, itemname, price, status);
            }

            public void updateCategory(long itemid, int position, String itemname, Double price, int status) {
                //
                Item ni = new Item();
                ni.setItemId(itemid);
                ni.setItemName(itemname);
                ni.setCatId(catid);
                ni.setItemType(1);
                ni.setPrice(price);
                ni.setStatus(status);
                ni.setPosition(position);
                Date LastDate = new Date();
                long timeMilliseconds = LastDate.getTime();
                ni.setLastModify(timeMilliseconds);
                //移除旧的，增加新的
                listItem.remove(position);
                if (status < 2) {
                    listItem.add(position, ni);
                }
                count = listItem.size() - 1;
                myToolbar.setTitle(catname + " (" + count + ")");
                adapter.notifyDataSetChanged();
                itemDAO.update(ni);
                //c1DAO.update(ni);
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

    public void AddNewItem() {
        //Toast.makeText(getApplicationContext(), "Hi..",Toast.LENGTH_LONG).show();
        AlertDialog.Builder editDialog = new AlertDialog.Builder(POS_EditItem.this);
        LayoutInflater inflater = POS_EditItem.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edititem_dialog, null);
        editDialog.setView(view);
        //EditText editText_itemname = (EditText) view.findViewById(R.id.itemname);
        //editText_itemname.setHint("输入品名");
        //EditText editText_price = (EditText) view.findViewById(R.id.price);
        //editText_price.setText("输入价格");
        Spinner spinner = (Spinner) view.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(POS_EditItem.this,
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
                EditText editText_itemname = (EditText) a.findViewById(R.id.itemname);
                EditText editText_price = (EditText) a.findViewById(R.id.price);
                //Spinner spinner = (Spinner) a.findViewById(R.id.status_spinner);
                //Toast.makeText(getApplicationContext(), editText_cat.getText().toString(),Toast.LENGTH_LONG).show();
                //subtitle.setText(editText_cat.getText().toString());
                String itemname = editText_itemname.getText().toString();
                double price = 0.0;
                try {
                    price = Double.parseDouble(editText_price.getText().toString());
                } catch (Exception e) {
                }
                addNewItem(itemname, price, status);
                //tv.setText(editText_cat.getText().toString());
            }

            public void addNewItem(String itemname, double price, int status) {
                for (int i = 1; i < listItem.size(); i++) {
                    Item it = listItem.get(i);
                    //除了"新增"之外，全部往后退一位给新分类让位
                    it.setPosition(it.getPosition() + 1);
                }
                Item ni = new Item();
                ni.setItemName(itemname);
                ni.setCatId(catid);
                ni.setItemType(1);
                ni.setPrice(price);
                ni.setPosition(1);
                ni.setStatus(status);
                Date LastDate = new Date();
                long timeMilliseconds = LastDate.getTime();
                ni.setLastModify(timeMilliseconds);
                itemDAO.insert(ni);
                listItem.add(1, ni);
                count = listItem.size() - 1;
                myToolbar.setTitle(catname + "(" + count + ")");
                adapter.notifyDataSetChanged();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pos, menu);
        return true;
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
            Item i = listItem.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                convertView = mInflater.inflate(R.layout.item_item, null);
                holder = new ViewHolder();
                //holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.tv = (TextView) convertView.findViewById(R.id.text);
                //holder.friendsrow = (LinearLayout) convertView.findViewById(R.id.llayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(i.getItemName());
            holder.position = position;
            /*if (position == 0) {
                holder.tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.maakki_red));
                holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                //holder.tv.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_playlist_add_white_18dp));
            }*/
            if (position == 0) {
                holder.tv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_red));
                holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                //holder.tv.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_playlist_add_white_18dp));
            } else {
                holder.tv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back));
                if (i.getStatus() > 0) {
                    holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale2));
                } else {
                    holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.my_spinner_text_color));
                }
                convertView.setId(position);
                convertView.setOnDragListener(new GvDragListener());
            }

            return convertView;

        }

        /*********
         * Create a holder Class to contain inflated xml file elements
         *********/
        public class ViewHolder {
            public TextView tv;
            public int position;
        }
    }

    class GvDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_drag));
                    //TextView tv=(TextView)v.findViewById(R.id.text_itemname);
                    //tv.setText(String.valueOf(v.getId()));
                    //Toast.makeText(getApplicationContext(), "v.getId():"+String.valueOf(v.getId()),Toast.LENGTH_LONG).show();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackground(getResources().getDrawable(R.drawable.container));
                    break;
                case DragEvent.ACTION_DROP:
                    View a = (View) event.getLocalState();
                    //Toast.makeText(getApplicationContext(), "a.getId():"+String.valueOf(a.getId()),Toast.LENGTH_LONG).show();
                    //ViewGroup parent = (ViewGroup) a.getParent();
                    //v是被动的 a是主动的
                    //Item iv=listItem.get(v.getId());
                    Item ia = listItem.get(a.getId());
                    //主动地position换成被动的
                    //ia.setPosition(iv.getPosition());
                    listItem.remove(a.getId());
                    listItem.add(v.getId(), ia);
                    adapter.notifyDataSetChanged();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_grey1));
                    for (int i = 0; i < listItem.size(); i++) {
                        Item it = listItem.get(i);
                        it.setPosition(i);
                        itemDAO.update(it);
                    }
                    break;
            }
            return true;
        }
    }
}