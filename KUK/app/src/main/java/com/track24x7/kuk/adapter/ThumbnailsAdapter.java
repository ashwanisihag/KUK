package com.track24x7.kuk.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.track24x7.kuk.R;
import com.track24x7.kuk.activity.FullPhotoActivity;
import com.track24x7.kuk.pojo.ThumbnailsListPOJO;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.webservice.WebServicesUrls;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> {

    Activity activity;
    Fragment fragment;
    private List<ThumbnailsListPOJO> thumbnailsListPOJO;
    //ProgressHUD mProgressHUD;

    public ThumbnailsAdapter(Activity activity, Fragment fragment, List<ThumbnailsListPOJO> thumbnailsListPOJO) {
        this.thumbnailsListPOJO = thumbnailsListPOJO;
        this.fragment = fragment;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inflate_photos, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //mProgressHUD = ProgressHUD.show(holder.itemView.getContext(),"Loading...", true,true,null);
        holder.textViewName.setText(thumbnailsListPOJO.get(position).getDescription());
        holder.postedBy.setText(thumbnailsListPOJO.get(position).getPostedBy());
        String auth = Pref.GetStringPref(holder.itemView.getContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(holder.itemView.getContext(), StringUtils.ACCESS_TOKEN, "");
        String url=WebServicesUrls.GET_PHOTO + thumbnailsListPOJO.get(position).getId();
        Glide.with(holder.itemView.getContext()).load(new Headers(auth).getUrlWithHeaders(url)).into(holder.imageView);
        holder.ll_image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FullPhotoActivity.class);
                intent.putExtra("photoId", thumbnailsListPOJO.get(position).getId());
                activity.startActivity(intent);
            }
        });
        //mProgressHUD.dismiss();
    }

    @Override
    public int getItemCount() {
        return thumbnailsListPOJO.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView postedBy;
        public ImageView imageView;
        public LinearLayout ll_image_back;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            postedBy = (TextView) itemView.findViewById(R.id.textPostedBy);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            ll_image_back = (LinearLayout) itemView.findViewById(R.id.ll_image_back);
        }
    }
}
