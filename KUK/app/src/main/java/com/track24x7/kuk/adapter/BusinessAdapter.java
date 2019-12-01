package com.track24x7.kuk.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.track24x7.kuk.R;
import com.track24x7.kuk.pojo.BusinessPOJO;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {
    private List<BusinessPOJO> items;
    Activity activity;
    Fragment fragment;

    public BusinessAdapter(Activity activity, Fragment fragment, List<BusinessPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_business_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bs_Title.setText("Title: "+ items.get(position).getBusinessName());
        holder.bs_Description.setText("Description: "+ items.get(position).getBusinessDescription());
        holder.bs_Phone.setText("Contact: "+items.get(position).getBusinessContact());
        holder.bs_ContactEmail.setText("Email: "+items.get(position).getBusinessEmail());
        holder.bs_Category.setText("Category: "+items.get(position).geBusinesstCategory());
        holder.bs_PostedBy.setText("Posted by: "+items.get(position).getPostedBy());
        holder.itemView.setTag(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bs_Title)
        TextView bs_Title;
        @BindView(R.id.bs_Description)
        TextView bs_Description;
        @BindView(R.id.bs_ContactEmail)
        TextView bs_ContactEmail;
        @BindView(R.id.bs_Phone)
        TextView bs_Phone;
        @BindView(R.id.bs_Category)
        TextView bs_Category;
        @BindView(R.id.bs_PostedBy)
        TextView bs_PostedBy;
        @BindView(R.id.ll_business_list)
        LinearLayout ll_business_list;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
