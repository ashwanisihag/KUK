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
import android.text.TextUtils;
import com.track24x7.kuk.R;
import com.track24x7.kuk.activity.UserDetailActivity;
import com.track24x7.kuk.pojo.NearByPOJO;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.UtilFunction;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {
    private List<NearByPOJO> items;
    Activity activity;
    Fragment fragment;

    public NearByAdapter(Activity activity, Fragment fragment, List<NearByPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_near_by_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String schoolName=items.get(position).getSchool();
        /*if(schoolName.contains("Ghorakhal"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ghorakhal);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Balachadi"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.balachadi);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Imphal"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.imphal);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Purulia"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.purulia);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Bhubaneswar"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.bhubaneswa);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Kazhakootam"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.kazhakootam);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Rewa"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.rewa);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Bijapur"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.bijapur);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Kapurthala"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.kapurthala);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Satara"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.satara);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Korukonda"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.korukonda);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Goalpara"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.goalpara);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Amaravathinagar"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.amaravathinagar);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Sujanpur Tira"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.sujanpur);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Kunjpura"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.kunjpura);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Tilaiya"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.tilaiya);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        } else if(schoolName.contains("Chittorgarh"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.chittorgarh);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Gopalganj"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.gopalganj);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }
        else if(schoolName.contains("Nagrota"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.nagrota);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Punglwa"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.punglwa);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Ambikapur"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ambikapur);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Rewari"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.rewari);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Kodagu"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.kodagu);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Lucknow"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.lucknow);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Kalikiri"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.kalikiri);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Chhing Chhip"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.chhingchhip);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);

        }    else if(schoolName.contains("Nalanda"))
        {
            Drawable img = ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.nalanda);
            img.setBounds(0, 0, 150, 150);
            holder.inf_nearby_institute.setCompoundDrawables(null, img, null, null);
        }

        holder.inf_nearby_institute.setText("School :- " +items.get(position).getSchool());
        holder.inf_nearby_user_name.setText("Name :- " +items.get(position).getFirstName() + " " + items.get(position).getLastName());

        if (items.get(position).getCity() != null && !TextUtils.isEmpty(items.get(position).getCity())) {
            holder.inf_nearby_city.setText("City :- " + items.get(position).getCity());
        }
        else {
            holder.inf_nearby_city.setText("City :- Not Specified");
        }

        if (items.get(position).getPosting() != null && !TextUtils.isEmpty(items.get(position).getPosting())) {
            holder.inf_nearby_posting.setText("Posted at :- " + items.get(position).getPosting());
        }
        else {
            holder.inf_nearby_posting.setText("Posted at :- Not Specified");
        }

        //holder.inf_nearby_RollNo.setText("Roll No :- "+ items.get(position).getRollNo().toString());

        if (items.get(position).getDepartment() != null && !TextUtils.isEmpty(items.get(position).getDepartment())) {
            holder.inf_nearby_department.setText("Department :- " + items.get(position).getDepartment());
        }
        else {
            holder.inf_nearby_department.setText("Department :- Not Specified");
        }
        if (items.get(position).getProfession() != null && !TextUtils.isEmpty(items.get(position).getProfession())) {
            holder.inf_nearby_profession.setText("Profession :- " + items.get(position).getProfession());
        }
        else {
            holder.inf_nearby_profession.setText("Profession :- Not Specified");
        }
        if (items.get(position).getDesignation() != null && !TextUtils.isEmpty(items.get(position).getDesignation())) {
            holder.inf_nearby_designation.setText("Designation :- " + items.get(position).getDesignation());
        }
        else {
            holder.inf_nearby_designation.setText("Designation :- Not Specified");
        }

        //holder.inf_nearby_phoneNumber.setText("Phone :- "+ items.get(position).getPhoneNumber());

        holder.inf_nearby_joining_year.setText("Joined in :- "+ UtilFunction.getYear(items.get(position).getJoiningYear()));
        try {
            double distance=items.get(position).getDistance()/1000;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            holder.inf_nearby_distance.setText("Distance  :- " + df.format(distance)+" km");
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.inf_nearby_days_ago.setText("Location updated :- "+ items.get(position).getDaysAgo());

        holder.ll_nearby_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, UserDetailActivity.class);
                intent.putExtra("userID",items.get(position).getId());
                activity.startActivity(intent);
            }
        });
*/
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

        @BindView(R.id.inf_nearby_institute)
        TextView inf_nearby_institute;
        @BindView(R.id.inf_nearby_user_name)
        TextView inf_nearby_user_name;
       @BindView(R.id.inf_nearby_joining_year)
        TextView inf_nearby_joining_year;
        //@BindView(R.id.inf_nearby_phone_number)
        //TextView inf_nearby_phoneNumber;
        @BindView(R.id.inf_nearby_days_ago)
        TextView inf_nearby_days_ago;
        @BindView(R.id.inf_nearby_designation)
        TextView inf_nearby_designation;
        @BindView(R.id.inf_nearby_department)
        TextView inf_nearby_department;
        @BindView(R.id.inf_nearby_profession)
        TextView inf_nearby_profession;
        /*@BindView(R.id.inf_nearby_RollNo)
        TextView inf_nearby_RollNo;*/
        @BindView(R.id.inf_nearby_city)
        TextView inf_nearby_city;
        @BindView(R.id.inf_nearby_posting)
        TextView inf_nearby_posting;
        @BindView(R.id.inf_nearby_distance)
        TextView inf_nearby_distance;
        @BindView(R.id.ll_nearby_user)
        LinearLayout ll_nearby_user;
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
