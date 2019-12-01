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
import com.track24x7.kuk.pojo.RequirementPOJO;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class RequirementAdapter extends RecyclerView.Adapter<RequirementAdapter.ViewHolder> {
    private List<RequirementPOJO> items;
    Activity activity;
    Fragment fragment;

    public RequirementAdapter(Activity activity, Fragment fragment, List<RequirementPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_requirement, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.rq_Description.setText("Description: "+ items.get(position).getRequirementDescription());
        holder.rq_Phone.setText("Contact: "+items.get(position).getContactNumber());
        holder.rq_ContactEmail.setText("Email: "+items.get(position).getEmail());
        holder.rq_Category.setText("Category: "+items.get(position).getCategory());
        holder.rq_PostedBy.setText("Posted by: "+items.get(position).getPostedBy());
        holder.rq_PostedOn.setText("Posted on: "+ items.get(position).getPostedOn().split("T")[0]);
        holder.itemView.setTag(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rq_Description)
        TextView rq_Description;
        @BindView(R.id.rq_ContactEmail)
        TextView rq_ContactEmail;
        @BindView(R.id.rq_Phone)
        TextView rq_Phone;
        @BindView(R.id.rq_Category)
        TextView rq_Category;
        @BindView(R.id.rq_PostedOn)
        TextView rq_PostedOn;
        @BindView(R.id.rq_PostedBy)
        TextView rq_PostedBy;
        @BindView(R.id.ll_requirement)
        LinearLayout ll_requirement;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
