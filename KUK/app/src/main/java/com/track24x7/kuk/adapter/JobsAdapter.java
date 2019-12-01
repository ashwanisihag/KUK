package com.track24x7.kuk.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.track24x7.kuk.R;
import com.track24x7.kuk.activity.MessageActivity;
import com.track24x7.kuk.pojo.JobsPOJO;
import com.track24x7.kuk.pojo.MessagesPOJO;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> {
    private List<JobsPOJO> items;
    Activity activity;
    Fragment fragment;

    public JobsAdapter(Activity activity, Fragment fragment, List<JobsPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_jobs, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.jb_Title.setText("Title: "+ items.get(position).getJobTitle());
        holder.jb_Description.setText("Description: "+ items.get(position).getJobDescription());
        holder.jb_Phone.setText("Contact: "+items.get(position).getContactNumber());
        holder.jb_ContactEmail.setText("Email: "+items.get(position).getEmail());
        holder.jb_Category.setText("Category: "+items.get(position).getCategory());
        holder.jb_PostedBy.setText("Posted by: "+items.get(position).getPostedBy());
        holder.jb_PostedOn.setText("Posted on: "+ items.get(position).getPostedOn().split("T")[0]);
        holder.itemView.setTag(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.jb_Title)
        TextView jb_Title;
        @BindView(R.id.jb_Description)
        TextView jb_Description;
        @BindView(R.id.jb_ContactEmail)
        TextView jb_ContactEmail;
        @BindView(R.id.jb_Phone)
        TextView jb_Phone;
        @BindView(R.id.jb_Category)
        TextView jb_Category;
        @BindView(R.id.jb_PostedOn)
        TextView jb_PostedOn;
        @BindView(R.id.jb_PostedBy)
        TextView jb_PostedBy;
        @BindView(R.id.ll_job)
        LinearLayout ll_job;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
