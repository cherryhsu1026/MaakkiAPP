package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/7/24.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessagesListAdapter extends BaseAdapter {

    private Context context;
    private List<maakkiMessage> messagesItems;

    public MessagesListAdapter(Context context, List<maakkiMessage> navDrawerItems) {
        this.context = context;
        this.messagesItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        maakkiMessage m = messagesItems.get(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (messagesItems.get(position).getIsSelf()) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        txtMsg.setText(m.getMessage());
        lblFrom.setText(m.getFromName());

        lblFrom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_red.class);
                Bundle bundle = new Bundle();
                maakkiMessage m = messagesItems.get(position);
                String cMemID = m.getMemid();
                String cName = m.getFromName();
                bundle.putString("isPrivate", "");
                bundle.putString("nName", cName);
                bundle.putString("Contactid", cMemID);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
