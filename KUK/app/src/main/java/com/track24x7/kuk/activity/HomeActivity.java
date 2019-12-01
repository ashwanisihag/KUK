package com.track24x7.kuk.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.adapter.UsersAdapter;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.service.LocationService;
import com.track24x7.kuk.util.FileUtils;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.util.IabBroadcastReceiver;
import com.track24x7.kuk.util.IabException;
import com.track24x7.kuk.util.IabHelper;
import com.track24x7.kuk.util.IabResult;
import com.track24x7.kuk.util.Inventory;
import com.track24x7.kuk.util.NotificationUtils;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.Purchase;
import com.track24x7.kuk.util.Security;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.WebServicesCallBack;
import com.track24x7.kuk.webservice.WebServicesUrls;
import com.track24x7.kuk.webservice.WebUploadService;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseMenuActivity {

    @BindView(R.id.home_spinner_batch)
    Spinner home_spinner_batch;
    @BindView(R.id.rv_users)
    RecyclerView rv_users;
    ProgressHUD mProgressHUD;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mProgressHUD = ProgressHUD.show(HomeActivity.this, "Getting your buddies list..", true, true, null);

        ButterKnife.bind(this);
        if (Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false) == false) {
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
                    Intent intent = new Intent(HomeActivity.this, PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
      /*  mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(config.TOPIC_GLOBAL);
                    updateFirebaseRegId();
                } else if (intent.getAction().equals(config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String messagetitle = intent.getStringExtra("title");
                    Toast.makeText(getApplicationContext(),  message, Toast.LENGTH_LONG).show();
                    //createNotification(title,message);
                }
            }
        };*/
        getSupportActionBar().setTitle("Batchmates");
        addSpinnerAdapter();
        attachAdapter();
    }

    public void addSpinnerAdapter() {
        int startingYear = 1960;
        List<String> yearList = new ArrayList<>();
        while (startingYear < 2019) {
            startingYear = startingYear + 1;
            yearList.add(String.valueOf(startingYear));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        home_spinner_batch.setAdapter(dataAdapter);

        home_spinner_batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getAllUsers(WebServicesUrls.formAllUserUrl("1", "100", home_spinner_batch.getSelectedItem().toString(), home_spinner_batch.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!Pref.GetStringPref(getApplicationContext(), StringUtils.Batch, "").equals("")) {
            home_spinner_batch.setSelection(yearList.indexOf(Pref.GetStringPref(getApplicationContext(), StringUtils.Batch, "")));
        }
    }

    public void getAllUsers(String url) {
        try {
            Log.d(TagUtils.getTag(), "url:-" + url);
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.GET, url,
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
                                Log.d(TagUtils.getTag(), "error:-" + error.toString());
                                ToastClass.showShortToast(getApplicationContext(), "Server error");
                                alert("Server error");
                            } else {
                                if (error.toString().contains("NoConnection")) {
                                    ToastClass.showShortToast(getApplicationContext(), "No Connection");
                                    alert("No Connection");
                                } else if (error.toString().contains("Timeout")) {
                                    ToastClass.showShortToast(getApplicationContext(), "Timeout error, try again please.");
                                    alert("Timeout error, try again please.");
                                } else {
                                    ToastClass.showShortToast(getApplicationContext(), error.toString());
                                    alert(error.toString());
                                }
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

    UsersAdapter userListAdapter;
    List<UserListPOJO> userStrings = new ArrayList<>();

    public void attachAdapter() {
//
//        for(int i=0;i<7;i++){
//            userStrings.add("");
//        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_users.setHasFixedSize(true);
        rv_users.setLayoutManager(linearLayoutManager);
        userListAdapter = new UsersAdapter(this, null, userStrings);
        rv_users.setAdapter(userListAdapter);
        rv_users.setNestedScrollingEnabled(false);
        rv_users.setItemAnimator(new DefaultItemAnimator());
    }

    public void parseResponse(String response) {
        if (response.contains("\"No records found\"")) {
            ToastClass.showShortToast(getApplicationContext(), "No data to display");
            alert("No data to display");
        }
        try {
            List<UserListPOJO> userListPOJOS = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                userListPOJOS.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(), UserListPOJO.class));
            }
            if (userListPOJOS.isEmpty()) {
                ToastClass.showShortToast(getApplicationContext(), "No data to display");
                alert("No data to display");
            }
            userStrings.clear();
            userStrings.addAll(userListPOJOS);
            userListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
