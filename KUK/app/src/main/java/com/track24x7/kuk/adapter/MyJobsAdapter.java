package com.track24x7.kuk.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.track24x7.kuk.pojo.JobsPOJO;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ashwani Sihag on 03-11-2017.
 */

public class MyJobsAdapter extends RecyclerView.Adapter<MyJobsAdapter.ViewHolder> {
    private List<JobsPOJO> items;
    Activity activity;
    Fragment fragment;

    public MyJobsAdapter(Activity activity, Fragment fragment, List<JobsPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_my_jobs, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.jb_Title.setText("Title: "+ items.get(position).getJobTitle());
        holder.jb_My_Description.setText("Description: "+ items.get(position).getJobDescription());
        holder.jb_My_Phone.setText("Contact: "+items.get(position).getContactNumber());
        holder.jb_My_ContactEmail.setText("Email: "+items.get(position).getEmail());
        holder.jb_My_Category.setText("Category: "+items.get(position).getCategory());
        holder.jb_My_PostedBy.setText("Posted by: "+items.get(position).getPostedBy());
        holder.jb_My_PostedOn.setText("Posted on: "+ items.get(position).getPostedOn().split("T")[0]);
        holder.itemView.setTag(items.get(position));

        holder.btn_delete_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    StringRequest getRequest = new StringRequest(Request.Method.DELETE, WebServicesUrls.DELETE_JOB + "/" + items.get(position).getJobId(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d(TagUtils.getTag(), "response:-" + response.toString());
                                    if (response.toString().contains("Deleted")) {
                                        ToastClass.showShortToast(view.getContext(), "Delete Success");
                                        items.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, items.size());
                                    } else {
                                        ToastClass.showShortToast(view.getContext(), "Delete Failed");
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TagUtils.getTag(), "error:-" + error.toString());
//                            error.printStackTrace();
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
                                    }
                                    else
                                    {
                                        ToastClass.showShortToast(view.getContext(),"failed to connect, please try to logout and login again.");
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.jb_My_Title)
        TextView jb_Title;
        @BindView(R.id.jb_My_Description)
        TextView jb_My_Description;
        @BindView(R.id.jb_My_ContactEmail)
        TextView jb_My_ContactEmail;
        @BindView(R.id.jb_My_Phone)
        TextView jb_My_Phone;
        @BindView(R.id.jb_My_Category)
        TextView jb_My_Category;
        @BindView(R.id.jb_My_PostedOn)
        TextView jb_My_PostedOn;
        @BindView(R.id.jb_My_PostedBy)
        TextView jb_My_PostedBy;
        @BindView(R.id.btn_delete_job)
        Button btn_delete_job;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
