package com.track24x7.kuk.adapter;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.track24x7.kuk.R;
import com.track24x7.kuk.pojo.ChatPOJO;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private List<ChatPOJO> items;
    Activity activity;
    Fragment fragment;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatAdapter(Activity activity, Fragment fragment, List<ChatPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
        name= Pref.GetStringPref(activity.getBaseContext(), StringUtils.USERNAME,"") +"_"+ Pref.GetStringPref(activity.getBaseContext(), StringUtils.SCHOOL,"")+"_"+ Pref.GetStringPref(activity.getBaseContext(), StringUtils.Batch,"");
    }

    @Override
    public int getItemViewType(int position) {
        ChatPOJO message = (ChatPOJO)items.get(position);

        if (message.getFrom().equals(name)){
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

 /*   @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_chat_messages, parent, false);
        return new ViewHolder(v);
    }
*/
 // Inflates the appropriate layout according to the ViewType.
 @Override
 public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     View view;
     if (viewType == VIEW_TYPE_MESSAGE_SENT) {
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_my_chat_messages, parent, false);
         return new SentMessageHolder(view);
     } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_chat_messages, parent, false);
         return new ReceivedMessageHolder(view);
     }
     else
     {
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_chat_messages, parent, false);
         return new ReceivedMessageHolder(view);
     }
 }
    String  name="";
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ChatPOJO chat = (ChatPOJO)items.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(chat);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(chat);
        }
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView txt_chat_time = (TextView) itemView.findViewById(R.id.txt_my_chat_time);
        TextView txt_chat_message = (TextView) itemView.findViewById(R.id.txt_my_chat_message);
        SentMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        void bind(ChatPOJO chat) {
            if(!TextUtils.isEmpty(chat.getTime())) {
                txt_chat_time.setText(chat.getTime());
            }
            if(!TextUtils.isEmpty(chat.getMessage())) {
                txt_chat_message.setText(chat.getMessage());
            }
            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        //ImageView profileImage;
        TextView txt_chat_time = (TextView) itemView.findViewById(R.id.txt_chat_time);
        TextView txt_chat_message = (TextView) itemView.findViewById(R.id.txt_chat_message);
        TextView txt_chat_from = (TextView) itemView.findViewById(R.id.txt_chat_from);
        ImageView  image = new ImageView(activity);

        ReceivedMessageHolder(View itemView) {
            super(itemView);
        }

        void bind(ChatPOJO chat) {
            if(!TextUtils.isEmpty(chat.getTime())) {
                txt_chat_time.setText(chat.getTime());
            }
            if(!TextUtils.isEmpty(chat.getMessage())) {
                txt_chat_message.setText(chat.getMessage());
            }

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(chat.getCreatedAt()));
            if(!TextUtils.isEmpty(chat.getFrom())) {
                txt_chat_from.setText(chat.getFrom());
            }
            if (chat.getMessage().startsWith("https://firebasestorage.googleapis.com/") || chat.getMessage().startsWith("content://")) {
                txt_chat_message.setVisibility(View.INVISIBLE);
                image.setVisibility(View.VISIBLE);
                //Load image
                Glide.with(activity)
                        .load(chat.getMessage())
                        .into(image);
            } else {
                txt_chat_message.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                txt_chat_message.setText(chat.getMessage());
            }
            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

   /* public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_chat_message)
        TextView txt_chat_message;
*//*        @BindView(R.id.text_message_time)
        TextView text_message_time;*//*
        @BindView(R.id.txt_chat_from)
        TextView txt_chat_from;
        @BindView(R.id.ll_chat_message)
        LinearLayout ll_chat_message;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }*/
}
