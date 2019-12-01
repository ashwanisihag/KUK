package com.track24x7.kuk.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.adapter.JobsAdapter;
import com.track24x7.kuk.adapter.MyJobsAdapter;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.pojo.JobsPOJO;
import com.track24x7.kuk.pojo.MessagesPOJO;
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

public class MyPostedJobsActivity extends BaseMenuActivity {

    @BindView(R.id.rv_my_jobs)
    RecyclerView rv_my_jobs;
    MyJobsAdapter jobsListAdapter;
    List<JobsPOJO> jobsStrings = new ArrayList<>();
    ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_jobs);
        mProgressHUD = ProgressHUD.show(MyPostedJobsActivity.this, "Getting your business list...", true, true, null);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        String value = b.getString("Screen");
        getSupportActionBar().setTitle(value);
        attachAdapter();
        getMyJobs();
    }

    public void attachAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_my_jobs.setHasFixedSize(true);
        rv_my_jobs.setLayoutManager(linearLayoutManager);
        jobsListAdapter = new MyJobsAdapter(this, null, jobsStrings);
        rv_my_jobs.setAdapter(jobsListAdapter);
        rv_my_jobs.setNestedScrollingEnabled(false);
        rv_my_jobs.setItemAnimator(new DefaultItemAnimator());
    }

    public void getMyJobs() {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.GET_MY_JOBS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                                parseResponse(response);
                                mProgressHUD.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressHUD.dismiss();
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers));
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                    ToastClass.showShortToast(getApplicationContext(), "Server error");
                                    alert("Server error");
                                } catch (Exception e1) {
                                    ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            }
                            else
                            {
                                 ToastClass.showShortToast(getApplicationContext(),"failed to connect, please try to logout and login again.");
                                alert("failed to connect, please try to logout and login again.");
                            }
                        }
                    }
            ) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, ""));
                    UtilFunction.printAllValues(headers);
                    return headers;
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseResponse(String response) {
        if(response.contains("\"No records found\""))
        {
            ToastClass.showShortToast(getApplicationContext(), "No data to display");
            alert("No data to display");
        }
        try {
            jobsStrings.clear();
            JSONArray jsonArray = new JSONArray(response);
            List<JobsPOJO> jobsPOJOS = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jobsPOJOS.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(), JobsPOJO.class));
            }
            if(jobsPOJOS.isEmpty())
            {
                ToastClass.showShortToast(getApplicationContext(), "No data to display");
                alert("No data to display");
            }
            jobsStrings.addAll(jobsPOJOS);
            jobsListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alert(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(str).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}
