package com.maakki.maakkiapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by ryan on 2016/8/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Friend> friendItems;
    private String TAG = "PGGURU";
    private ImageLoaderConfiguration config;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    public ImageAdapter(Context c, List<Friend> navDrawerItems) {
        mContext = c;
        this.friendItems = navDrawerItems;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();

        config = new ImageLoaderConfiguration.Builder(c)
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader.getInstance().init(config);
    }

    public int getCount() {
        return friendItems.size();
    }

    public Object getItem(int position) {
        return friendItems.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        //RecyclerView.ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Friend f = friendItems.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = mInflater.inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            //holder.friendsrow = (LinearLayout) convertView.findViewById(R.id.llayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //CharSequence nickname=f.getNickName();
        String nickname = f.getNickName();
        //CharSequence cs = new String(nickname);
        //nickname=dispalyNickname_afterprocessed(cs);
        //holder.text.setText(dispalyNickname_afterprocessed(cs));
        holder.text.setText(nickname);

        String mpicfile = f.getPicfilePath();
        String pic_url = "http://www.maakki.com/function/getImage.aspx?file_id=" + mpicfile + "&width=120&height=120&forcibly=Y&dimg=Y";
        //if(holder.image.getDisplay()==null){
        //new DownloadImageTask(holder.image).execute(pic_url);
        //}
        imageLoader.displayImage(pic_url, holder.image);
        return convertView;

    }

    protected String dispalyNickname_afterprocessed(CharSequence nickname) {
        String nickname_after = "";
        int maxLength = 8;
        int len = 0;
        final int l = nickname.length();
        if (l > 4) {
            for (int i = 0; i < maxLength; i++) {
                char tmp = nickname.charAt(i);
                /*if (tmp >= 0x20 && tmp <= 0x7E) {
                    // 字元值 32~126 是 ASCII 半形字元的範圍
                    len++;
                    if(len<maxLength-1){nickname_after+=tmp;}
                } else {
                    // 非半形字元
                    len+=2;
                    if(len<maxLength-2){nickname_after+=tmp;}
                }*/
            }
        } else {
            //nickname_after=String.valueOf(nickname);
        }

        return nickname_after;
        //return String.valueOf(nickname_after.length());
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public class ViewHolder {
        public TextView text;
        public ImageView image;
        //public LinearLayout friendsrow;
    }

}
