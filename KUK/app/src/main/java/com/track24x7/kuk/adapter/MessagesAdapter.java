package com.track24x7.kuk.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.activity.MessageActivity;
import com.track24x7.kuk.activity.UserDetailActivity;
import com.track24x7.kuk.activity.UserMessagesActivity;
import com.track24x7.kuk.pojo.MessagesPOJO;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<MessagesPOJO> items;
    Activity activity;
    Fragment fragment;

    public MessagesAdapter(Activity activity, Fragment fragment, List<MessagesPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_messages, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txt_from.setText(items.get(position).getFrom());
        holder.txt_message.setText(items.get(position).getMessage());
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Intent intent = new Intent(activity, MessageActivity.class);
                intent.putExtra("messageId", items.get(position).getMessageId());
                intent.putExtra("userID", items.get(position).getUserID());
                intent.putExtra("Screen", "Send Message");
                activity.startActivity(intent);
            }
        });
        holder.btn_delete_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    StringRequest getRequest = new StringRequest(Request.Method.DELETE, WebServicesUrls.DELETE_MESSAGES + "/" + items.get(position).getMessageId(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d(TagUtils.getTag(), "response:-" + response.toString());
                                    if (response.toString().contains("Deleted")) {
                                        ToastClass.showShortToast(view.getContext(), "Delete Success");
                                        items.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, items.size());
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TagUtils.getTag(), "error:-" + error.toString());
                                    NetworkResponse response = error.networkResponse;
                                    if (error instanceof ServerError && response != null) {
                                        try {
                                            String res = new String(response.data,
                                                    HttpHeaderParser.parseCharset(response.headers));
                                            // Now you can use any deserializer to make sense of data
                                            JSONObject obj = new JSONObject(res);
                                            ToastClass.showShortToast(view.getContext(), "Server error");
                                        } catch (Exception e1) {
                                            ToastClass.showShortToast(view.getContext(), e1.getMessage());
                                            // Couldn't properly decode data to string
                                            e1.printStackTrace();
                                        }
                                    } else {
                                        ToastClass.showShortToast(view.getContext(), "failed to connect, please try to logout and login again.");
                                    }
                                }
                            }
                    ) {


                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", Pref.GetStringPref(view.getContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(view.getContext(), StringUtils.ACCESS_TOKEN, ""));
                            UtilFunction.printAllValues(headers);
                            return headers;
                        }
                    };
                    queue.add(getRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.ll_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MessageActivity.class);
                intent.putExtra("messageId", items.get(position).getMessageId());
                intent.putExtra("userID", items.get(position).getUserID());
                intent.putExtra("Screen", "Send Message");
                activity.startActivity(intent);
            }
        });


        holder.itemView.setTag(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_message)
        TextView txt_message;
        @BindView(R.id.txt_from)
        TextView txt_from;
        @BindView(R.id.btn_delete_message)
        TextView btn_delete_message;
        @BindView(R.id.btn_reply)
        TextView btn_reply;
        @BindView(R.id.ll_message)
        LinearLayout ll_message;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
