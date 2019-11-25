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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;*/

/**
 * Created by ryan on 2017/7/17.
 */

public class POS_EditCategory extends AppCompatActivity {
    GridView gv;
    //Integer count=0;
    //FloatingActionButton fab;
    //private Category cat;
    private CategoryAdapter adapter;
    private List<Category> listCategory;
    private CategoryDAO catDAO;

    private int status;
    private TextView tv;
    private EditText editText_cat, editText_item;
    private SwipeDetector swipeDetector = new SwipeDetector();
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item:
                    Intent intent1 = new Intent(POS_EditCategory.this, POS_EditCategory.class);
                    startActivity(intent1);
                    break;
                case R.id.record:
                    Intent intent2 = new Intent(POS_EditCategory.this, OrderComfirm.class);
                    intent2.putExtra("order_id", "0");
                    startActivity(intent2);
                    break;
                case R.id.settings:
                    Intent intent3 = new Intent(POS_EditCategory.this, POSPrefsActivity.class);
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(POS_EditCategory.this, POS_MainActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton fab_cashier = (FloatingActionButton) findViewById(R.id.cashier_button);
        fab_cashier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POS_EditCategory.this, POS_MainActivity.class);
                i.putExtra("order_id", "0");
                startActivity(i);
            }
        });
        gv = (GridView) findViewById(R.id.gridview);

        listCategory = new ArrayList<Category>();

        catDAO = new CategoryDAO(this);
        Category c = new Category();
        try {
            listCategory = catDAO.getDisplayedCategory();
            //count = catDAO.getCount();
        } catch (Exception e) {
        }
        //if(count==0){
        c.setCatName("新增");
        listCategory.add(0, c);
        //}
        adapter = new CategoryAdapter(this, listCategory);
        gv.setAdapter(adapter);
        //setOnClick
        /*gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(null, shadowBuilder, v, 0);
                return true;
            }
        });*/
        //setOnLonglick();
        /*gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //return true;
            }
        });*/
    }

    public void EditItemByCategory(int position) {
        Category c = listCategory.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("catname", c.getCatName());
        bundle.putString("catid", String.valueOf(c.getCatId()));
        Intent i = new Intent(POS_EditCategory.this, POS_EditItem.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void AddNewCategory() {
        AlertDialog.Builder editDialog = new AlertDialog.Builder(POS_EditCategory.this);
        LayoutInflater inflater = POS_EditCategory.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.editcategory_dialog, null);
        editDialog.setView(view);
        EditText editText_gp = (EditText) view.findViewById(R.id.groupid);
        editText_gp.setText("0");
        Spinner spinner = (Spinner) view.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(POS_EditCategory.this,
                R.array.editcategory_status_array, R.layout.spinner_text);
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
                addNewCategory(catname, groupid, status);
                //tv.setText(editText_cat.getText().toString());
            }

            public void addNewCategory(String catname, int groupid, int status) {
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
                catDAO.insert(nc);
                listCategory.add(1, nc);
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

    public void EditExistedCategory(int position) {
        //final Category c1=listCategory.get(position);
        AlertDialog.Builder editDialog = new AlertDialog.Builder(POS_EditCategory.this);
        LayoutInflater inflater = POS_EditCategory.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.editcategory_dialog, null);
        editDialog.setView(view);
        final int pt = position;
        final Category c = listCategory.get(position);
        EditText editText_cat = (EditText) view.findViewById(R.id.catname);
        EditText editText_gp = (EditText) view.findViewById(R.id.groupid);
        Spinner spinner = (Spinner) view.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(POS_EditCategory.this,
                R.array.editcategory_status_array, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //final String[] statusArray = getResources().getStringArray(R.array.editcategory_status_array);
        spinner.setAdapter(arrayadapter);
        status = c.getStatus();
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
        editText_cat.setText(c.getCatName());
        editText_gp.setText(String.valueOf(c.getGroupId()));
        final long cid = c.getCatId();
        editDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //
                Dialog a = (Dialog) arg0;
                EditText editText_cat = (EditText) a.findViewById(R.id.catname);
                EditText editText_gp = (EditText) a.findViewById(R.id.groupid);
                String catname = editText_cat.getText().toString();
                int groupid = 0;
                try {
                    groupid = Integer.parseInt(editText_gp.getText().toString());
                } catch (Exception e) {
                }
                updateCategory(cid, pt, catname, groupid, status);
                //tv.setText(editText_cat.getText().toString());
            }

            public void updateCategory(long cid, int position, String catname, int groupid, int status) {
                //
                Category nc = new Category();
                nc.setCatId(cid);
                nc.setCatName(catname);
                nc.setPosition(position);
                nc.setGroupId(groupid);
                nc.setStatus(status);
                Date LastDate = new Date();
                long timeMilliseconds = LastDate.getTime();
                nc.setLastModify(timeMilliseconds);
                listCategory.remove(position);
                //如果不是被刪除了，加回來顯示的List裡面
                if (status < 2) {
                    listCategory.add(position, nc);
                }
                adapter.notifyDataSetChanged();
                catDAO.update(nc);
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

    public class CategoryAdapter extends BaseAdapter {
        private Context mContext;
        private List<Category> listCategory;
        //private String TAG = "PGGURU";
        //private ImageLoaderConfiguration config;
        //private DisplayImageOptions options;
        //private ImageLoader imageLoader;

        public CategoryAdapter(Context c, List<Category> navDrawerItems) {
            mContext = c;
            this.listCategory = navDrawerItems;
        }

        public int getCount() {
            return listCategory.size();
        }

        public Object getItem(int position) {
            return listCategory.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            //RecyclerView.ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            Category c = listCategory.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                convertView = mInflater.inflate(R.layout.category_item, null);
                holder = new ViewHolder();
                //holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.rl = (RelativeLayout) convertView.findViewById(R.id.rl_CI);
                holder.tv = (TextView) convertView.findViewById(R.id.text_catname);
                holder.iv_edit = (ImageView) convertView.findViewById(R.id.iv_edit);
                holder.iv_move = (ImageView) convertView.findViewById(R.id.iv_move);
                holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(c.getCatName());
            /*//新增一个分类
            if (position == 0) {
                AddNewCategory();
            } else {
                //EditExistedCategory(position);
                EditItemByCategory(position);
            }*/
            if (position == 0) {
                convertView.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddNewCategory();
                    }
                });

                holder.iv_edit.setVisibility(View.INVISIBLE);
                holder.iv_move.setVisibility(View.INVISIBLE);
                holder.iv_add.setVisibility(View.VISIBLE);
                holder.rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_red));
                holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                //holder.tv.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_playlist_add_white_18dp));
            } else {
                holder.rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back));
                holder.iv_edit.setVisibility(View.VISIBLE);
                holder.iv_move.setVisibility(View.GONE);
                holder.iv_add.setVisibility(View.GONE);
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDrag(null, shadowBuilder, v, 0);
                        return true;
                    }
                });
                convertView.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditItemByCategory(position);
                    }
                });

                holder.iv_edit.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditExistedCategory(position);
                    }
                });
                holder.tv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.white));
                if (c.getStatus() > 0) {
                    holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale2));
                } else {
                    holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.my_spinner_text_color));
                }
            }

            convertView.setId(position);
            convertView.setOnDragListener(new GvDragListener());
            return convertView;

        }

        /*********
         * Create a holder Class to contain inflated xml file elements
         *********/
        public class ViewHolder {

            public RelativeLayout rl;
            public TextView tv;
            public ImageView iv_edit;
            public ImageView iv_move;
            public ImageView iv_add;
        }

    }

    class GvDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_grey));
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
                    Category ic = listCategory.get(a.getId());
                    //主动地position换成被动的
                    //ia.setPosition(iv.getPosition());
                    listCategory.remove(a.getId());
                    listCategory.add(v.getId(), ic);
                    adapter.notifyDataSetChanged();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_grey1));
                    for (int i = 0; i < listCategory.size(); i++) {
                        Category it = listCategory.get(i);
                        it.setPosition(i);
                        catDAO.update(it);
                    }
                    break;
            }
            return true;
        }
    }
}