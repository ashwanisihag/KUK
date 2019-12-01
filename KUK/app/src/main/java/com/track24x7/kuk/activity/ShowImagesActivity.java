package com.track24x7.kuk.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.adapter.ThumbnailsAdapter;
import com.track24x7.kuk.pojo.ThumbnailsListPOJO;
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

public class ShowImagesActivity extends BaseMenuActivity {
    //recyclerview object
    @BindView(R.id.thumbNails_recyclerView)
    RecyclerView thumbNails_recyclerView;


    ThumbnailsAdapter thumbnailsAdapter;
    List<ThumbnailsListPOJO> thumbNailsStrings = new ArrayList<>();
    ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);
        ButterKnife.bind(this);
        mProgressHUD = ProgressHUD.show(ShowImagesActivity.this, "Getting images, please wait...", true, true, null);
        attachAdapter();
        getThumbnailsIds();
    }
    public void getThumbnailsIds() {
        try {

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.GET_THUMBNAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            parseResponse(response);
                            if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==false) {
                                //loadAdd();
                            }
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
                                    alert(e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            }
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

    public void attachAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        thumbNails_recyclerView.setHasFixedSize(true);
        thumbNails_recyclerView.setLayoutManager(linearLayoutManager);
        thumbnailsAdapter = new ThumbnailsAdapter(this, null, thumbNailsStrings);
        thumbNails_recyclerView.setAdapter(thumbnailsAdapter);
        thumbNails_recyclerView.setNestedScrollingEnabled(false);
        thumbNails_recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void alert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void parseResponse(String response){
        if(response.contains("\"No records found\""))
        {
            ToastClass.showShortToast(getApplicationContext(), "No data to display");
            alert("No data to display");
        }
        try{
            thumbNailsStrings.clear();
            JSONArray jsonArray=new JSONArray(response);
            List<ThumbnailsListPOJO> thumbnailsListPOJO=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                thumbnailsListPOJO.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(),ThumbnailsListPOJO.class));
            }
            if(thumbnailsListPOJO.isEmpty())
            {
                ToastClass.showShortToast(getApplicationContext(), "No images, stay tuned");
                alert("No images, stay tuned");
            }
            thumbNailsStrings.addAll(thumbnailsListPOJO);
            thumbnailsAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
