package com.track24x7.kuk.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.track24x7.kuk.adapter.NearByAdapter;
import com.track24x7.kuk.adapter.UsersAdapter;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.pojo.NearByPOJO;
import com.track24x7.kuk.pojo.UserListPOJO;
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

public class NearByActivity extends BaseMenuActivity {

    @BindView(R.id.rv_near_by_user)
    RecyclerView rv_near_by_user;
    @BindView(R.id.nearby_spinner_radius)
    Spinner nearby_spinner_radius;
    @BindView(R.id.nearby_spinner_city)
    Spinner nearby_spinner_city;
    @BindView(R.id.btn_refresh)
    Button btn_refresh;
    int check = 0;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);
        ButterKnife.bind(this);
        if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==false) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                    Intent  intent=new Intent(NearByActivity.this,PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLocation();
            }
        });
        Bundle b = getIntent().getExtras();
        String value = b.getString("Screen");
        getSupportActionBar().setTitle(value);
        setSpinnerItems();
        attachAdapter();
        checkLocation();
    }

    public void setSpinnerItems(){
        List<String> items=new ArrayList<>();
        List<String> city=new ArrayList<>();
        for(int i=10;i<101;i=i+5){
            items.add(String.valueOf(i));
        }
        city.add("Delhi");
        city.add("Mumbai");
        city.add("Jaipur");
        city.add("Gurgaon");
        city.add("Chandigarh");
        city.add("Banglore");
        city.add("Kolkata");
        city.add("Surat");
        city.add("Pune");
        city.add("Ahmadabad");
        city.add("Hyderabad");
        city.add("Chennai");
        city.add("Visakhapatnam");
        city.add("Nagpur");
        city.add("Ghaziabad");
        city.add("Lucknow");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nearby_spinner_radius.setAdapter(dataAdapter);

        nearby_spinner_radius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sendLocation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, city);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nearby_spinner_city.setAdapter(dataAdapter2);

        nearby_spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(++check > 1) {
                String lat = "0";
                String lon = "0";
                String city = nearby_spinner_city.getSelectedItem().toString();
                if (city == "Delhi") {
                    lat = "28.644800";
                    lon = "77.216721";
                    sendCityLocation(lat, lon);
                } else if (city == "Mumbai") {
                    lat = "19.228825";
                    lon = "72.854118";
                    sendCityLocation(lat, lon);
                } else if (city == "Gurgaon") {
                    lat = "28.457523";
                    lon = "77.026344";
                    sendCityLocation(lat, lon);
                } else if (city == "Jaipur") {
                    lat = "26.922070";
                    lon = "75.778885";
                    sendCityLocation(lat, lon);
                } else if (city == "Chandigarh") {
                    lat = "30.741482";
                    lon = "76.768066";
                    sendCityLocation(lat, lon);
                } else if (city == "Banglore") {
                    lat = "12.972442";
                    lon = "77.580643";
                    sendCityLocation(lat, lon);
                } else if (city == "Kolkata") {
                    lat = "22.572645";
                    lon = "88.363892";
                    sendCityLocation(lat, lon);
                } else if (city == "Surat") {
                    lat = "21.170240";
                    lon = "72.831062";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Pune") {
                    lat = "18.516726";
                    lon = "73.856255";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Ahmadabad") {
                    lat = "23.033863";
                    lon = "72.585022";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Hyderabad") {
                    lat = "17.387140";
                    lon = "78.491684";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Visakhapatnam") {
                    lat = "";
                    lon = "";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Nagpur") {
                    lat = "17.686815";
                    lon = "83.218483";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Ghaziabad") {
                    lat = "28.667856";
                    lon = "77.449791";
                    sendCityLocation(lat, lon);
                }
                else if (city == "Lucknow") {
                    lat = "26.862410";
                    lon = "81.020348";
                    sendCityLocation(lat, lon);
                }
                else {
                    sendLocation();
                }
            }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void alert(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(str).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
    public void checkLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            double[] latlong = UtilFunction.getLocation(getApplicationContext());
            current_latitude=String.valueOf(latlong[0]);
            current_long=String.valueOf(latlong[1]);
            sendLocation();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2004);
            return;
        } else {
            double[] latlong = UtilFunction.getLocation(getApplicationContext());
            current_latitude=String.valueOf(latlong[0]);
            current_long=String.valueOf(latlong[1]);
            sendLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2004: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    double[] latlong = UtilFunction.getLocation(getApplicationContext());
                    current_latitude=String.valueOf(latlong[0]);
                    current_long=String.valueOf(latlong[1]);
                    sendLocation();
                }
                return;
            }

        }
    }

    public void parseResponse(String response){
       if(response.contains("\"No records found\""))
        {
            ToastClass.showShortToast(getApplicationContext(), "No data to display");
            alert("No data to display");
        }
        try{
            userStrings.clear();
            JSONArray jsonArray=new JSONArray(response);
            List<NearByPOJO> nearByPOJOS=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                nearByPOJOS.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(),NearByPOJO.class));
            }
            if(nearByPOJOS.isEmpty())
            {
                ToastClass.showShortToast(getApplicationContext(), "No data to display");
                alert("No data to display");
            }
            else {
                ToastClass.showShortToast(getApplicationContext(), "Touch member for more details");
            }
            userStrings.addAll(nearByPOJOS);
            userListAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    NearByAdapter userListAdapter;
    List<NearByPOJO> userStrings = new ArrayList<>();

    public void attachAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_near_by_user.setHasFixedSize(true);
        rv_near_by_user.setLayoutManager(linearLayoutManager);
        userListAdapter = new NearByAdapter(this, null, userStrings);
        rv_near_by_user.setAdapter(userListAdapter);
        rv_near_by_user.setNestedScrollingEnabled(false);
        rv_near_by_user.setItemAnimator(new DefaultItemAnimator());
    }

    String current_latitude="";
    String current_long="";

    public void sendCityLocation(String lat, String lon) {
        final ProgressHUD mProgressHUD = ProgressHUD.show(NearByActivity.this,"Please wait...", true,true,null);

        try {
            final JSONObject jsonObject = new JSONObject();
            Bundle b = getIntent().getExtras();
            String filter = b.getString("Filter");
            jsonObject.put("Filter",filter);
            jsonObject.put("CityLatitude", lat);
            jsonObject.put("CityLongitude", lon);
            jsonObject.put("Radius", "50");
            jsonObject.put("CitySearch", "True");

            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.PUT, WebServicesUrls.UPDATE_LOCATION,
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
            mProgressHUD.dismiss();
            e.printStackTrace();
        }
    }

    public void sendLocation() {
        try {
            final ProgressHUD mProgressHUD = ProgressHUD.show(NearByActivity.this,"Please wait...", true,true,null);
            final JSONObject jsonObject = new JSONObject();
            Bundle b = getIntent().getExtras();
            String filter = b.getString("Filter");
            jsonObject.put("Filter",filter);
            jsonObject.put("Latitude", current_latitude);
            jsonObject.put("Longitude", current_long);
            jsonObject.put("Radius", nearby_spinner_radius.getSelectedItem().toString());

            jsonObject.put("CitySearch", "False");
            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.PUT, WebServicesUrls.UPDATE_LOCATION,
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
}
