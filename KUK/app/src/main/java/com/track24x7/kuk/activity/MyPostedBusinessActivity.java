package com.track24x7.kuk.activity;

import android.app.AlertDialog;
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
import com.track24x7.kuk.adapter.BusinessAdapter;
import com.track24x7.kuk.adapter.MyBusinessAdapter;
import com.track24x7.kuk.pojo.BusinessPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPostedBusinessActivity extends BaseMenuActivity {

    @BindView(R.id.rv_my_business)
    RecyclerView rv_my_business;
    ProgressHUD mProgressHUD;

    public void alert(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(str).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_business);
        mProgressHUD = ProgressHUD.show(MyPostedBusinessActivity.this,"Getting your business list...", true,true,null);

        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        String value = b.getString("Screen");
        getSupportActionBar().setTitle(value);
        attachAdapter();
        getMyBusiness();
    }

    MyBusinessAdapter businessListAdapter;
    List<BusinessPOJO> businessStrings = new ArrayList<>();

    public void attachAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_my_business.setHasFixedSize(true);
        rv_my_business.setLayoutManager(linearLayoutManager);
        businessListAdapter = new MyBusinessAdapter(this, null, businessStrings);
        rv_my_business.setAdapter(businessListAdapter);
        rv_my_business.setNestedScrollingEnabled(false);
        rv_my_business.setItemAnimator(new DefaultItemAnimator());
    }

    public void getMyBusiness() {
        try {
            final JSONObject jsonObject = new JSONObject();
            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.GET_MY_BUSINESS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TagUtils.getTag(), "response:-" + response);
                            parseResponse(response);
                            mProgressHUD.dismiss();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "error:-" + error.toString());
//                            error.printStackTrace();
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
                public byte[] getBody() throws AuthFailureError {
                    return jsonObject.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, ""));
                    headers.put("Content-Type", "application/json");
//                    UtilityFunction.printAllValues(headers);
                    return headers;
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseResponse(String response){
       if(response.contains("\"No records found\""))
        {
            ToastClass.showShortToast(getApplicationContext(), "No data to display");
            alert("No data to display");
        }
        try{
            businessStrings.clear();
            JSONArray jsonArray=new JSONArray(response);
            List<BusinessPOJO> businessPOJOS=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                businessPOJOS.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(),BusinessPOJO.class));
            }
            if(businessPOJOS.isEmpty())
            {
                ToastClass.showShortToast(getApplicationContext(), "No data to display");
                alert("No data to display");
            }
            businessStrings.addAll(businessPOJOS);
            businessListAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
