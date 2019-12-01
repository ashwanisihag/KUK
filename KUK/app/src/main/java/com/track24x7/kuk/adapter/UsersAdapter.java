package com.track24x7.kuk.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.track24x7.kuk.R;
import com.track24x7.kuk.activity.UserDetailActivity;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.UtilFunction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<UserListPOJO> items;
    Activity activity;
    Fragment fragment;

    public UsersAdapter(Activity activity, Fragment fragment, List<UserListPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String schoolName=items.get(position).getSchool();
        holder.user_institute.setText("School :- "+schoolName);
        holder.user_user_name.setText("Name :- "+items.get(position).getFirstName() + " " + items.get(position).getLastName());
        //holder.user_RollNo.setText("Roll No :- "+ items.get(position).getRollNo().toString());
        holder.user_batch.setText("Batch  :- " + UtilFunction.getYear(items.get(position).getLeavingYear()));

        holder.ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UserDetailActivity.class);
                intent.putExtra("userID", items.get(position).getId());
                activity.startActivity(intent);
            }
        });

        if (items.get(position).getHouse() != null) {
            switch (items.get(position).getHouse().toLowerCase()) {
                case "blue":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#4FC3F7"));
                    break;
                case "red":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#FF5722"));
                    break;
                case "yellow":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#FFF176"));
                    break;
                case "green":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#A5D6A7"));
                    break;
                case "brown":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#A52A2A"));
                    break;
                case "white":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    break;
                case "orange":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#FFA500"));
                    break;
                case "purple":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#8B008B"));
                    break;
                case "gray":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#808080"));
                    break;
                case "black":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#000000"));
                    break;
                case "pink":
                    holder.user_user_name.setBackgroundColor(Color.parseColor("#FFC0CB"));
                    break;

            }
        }
        if(Integer.parseInt(Pref.GetStringPref(holder.itemView.getContext(), StringUtils.Batch, ""))>Integer.parseInt(UtilFunction.getYear(items.get(position).getLeavingYear())))
        {
            holder.user_rank.setText("Senior");
        }
        else
        {
            holder.user_rank.setText("Junior");
        }

        holder.itemView.setTag(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_institute)
        TextView user_institute;
        @BindView(R.id.user_user_name)
        TextView user_user_name;
        /*@BindView(R.id.user_RollNo)
        TextView user_RollNo;*/
        @BindView(R.id.user_batch)
        TextView user_batch;
        @BindView(R.id.ll_user)
        LinearLayout ll_user;
        @BindView(R.id.ll_back)
        LinearLayout ll_back;
        @BindView(R.id.user_rank)
        TextView user_rank;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
