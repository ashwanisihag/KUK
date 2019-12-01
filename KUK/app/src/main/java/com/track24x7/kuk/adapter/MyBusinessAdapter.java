package com.track24x7.kuk.adapter;

import android.app.Activity;
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
import com.track24x7.kuk.pojo.BusinessPOJO;
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

public class MyBusinessAdapter extends RecyclerView.Adapter<MyBusinessAdapter.ViewHolder> {
    private List<BusinessPOJO> items;
    Activity activity;
    Fragment fragment;

    public MyBusinessAdapter(Activity activity, Fragment fragment, List<BusinessPOJO> items) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_my_business_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bs_My_Title.setText("Title: "+ items.get(position).getBusinessName());
        holder.bs_My_Description.setText("Description: "+ items.get(position).getBusinessDescription());
        holder.bs_My_Phone.setText("Contact: "+items.get(position).getBusinessContact());
        holder.bs_My_ContactEmail.setText("Email: "+items.get(position).getBusinessEmail());
        holder.bs_My_Category.setText("Category: "+items.get(position).geBusinesstCategory());
        holder.itemView.setTag(items.get(position));
        holder.btn_delete_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    StringRequest getRequest = new StringRequest(Request.Method.DELETE, WebServicesUrls.DELETE_BUSINESS + "/" + items.get(position).getBusinessId(),
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
        @BindView(R.id.bs_My_Title)
        TextView bs_My_Title;
        @BindView(R.id.bs_My_Description)
        TextView bs_My_Description;
        @BindView(R.id.bs_My_ContactEmail)
        TextView bs_My_ContactEmail;
        @BindView(R.id.bs_My_Phone)
        TextView bs_My_Phone;
        @BindView(R.id.bs_My_Category)
        TextView bs_My_Category;
        @BindView(R.id.btn_delete_business)
        Button btn_delete_business;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
