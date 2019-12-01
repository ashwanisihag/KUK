package com.track24x7.kuk.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.adapter.UsersAdapter;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.pojo.NewsPOJO;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.service.LocationService;
import com.track24x7.kuk.util.FileUtils;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.util.IabHelper;
import com.track24x7.kuk.util.IabResult;
import com.track24x7.kuk.util.Inventory;
import com.track24x7.kuk.util.NotificationUtils;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.Purchase;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BirthdaysActivity extends BaseMenuActivity {
    @BindView(R.id.home_profile_pic)
    CircleImageView home_profile_pic;
    @BindView(R.id.home_profile_name)
    TextView home_profile_name;
    @BindView(R.id.switch_location)
    Switch switch_location;
    @BindView(R.id.btn_share)
    FloatingActionButton btn_share;
    @BindView(R.id.rv_birthday_boys)
    RecyclerView rv_birthday_boys;
    private InterstitialAd mInterstitialAd;
    private Activity activity;
    private Context context;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;

    ProgressHUD mProgressHUD;
    Intent intent;
    Bundle b;

    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    //IabBroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdays);
        ButterKnife.bind(this);
        activity = BirthdaysActivity.this;
        context = this;
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        mProgressHUD = ProgressHUD.show(BirthdaysActivity.this, "Get a gift for them guys while i get the list...", true, true, null);
        attachAdapter();

        getSupportActionBar().setTitle("Today's Bday List");

        home_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View view) {
                if (checkCameraExists()) {
                    if (permissionsToRequest.size() > 0) {
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                                ALL_PERMISSIONS_RESULT);
                    } else {
                        Toast.makeText(context, "Permissions already granted.", Toast.LENGTH_LONG).show();
                        showFileChooser();
                    }
                } else {
                    Toast.makeText(activity, "Camera not available.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Kukians");
                    String sAux = "\nDear Kukians\n" +
                            "(Ex-students of all the sainik schools in India)\n" +
                            "\n" +
                            "This is an amazing app developed by one if the ex-students of a Sainik School.\n" +
                            "It brings together all ex-students of all sainik schools on one platform.\n" +
                            "You can connect with them and project your requirements.\n" +
                            ".\n" +
                            "I have downloaded this app and find it to be good.\n" +
                            ".\n" +
                            "Recommended app for all Kukians.\n" +
                            ".\n" +
                            "I also recommend for you to upgrade to paid version by paying *₹500/-* only.\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.track24x7.kuk \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

        switch_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, b);
                if (b) {
                    showLocation();
                } else {
                    try {
                        stopService(new Intent(BirthdaysActivity.this, LocationService.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        home_profile_name.setText("Welcome: " + Pref.GetStringPref(getApplicationContext(), StringUtils.FIRSTNAME, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.LASTNAME, ""));
        setImageURL();
        switch_location.setChecked(Pref.GetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, false));
        // getAlerts();
        updateFirebaseRegId();
        getBirthdayMembers();
        getVersion();
        /*if (Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(Pref.GetStringPref(this, StringUtils.Batch, "")) > 5) {
            availableForParty();
        }*/
    }

/*    private void availableForParty() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Get together");
        alertDialog.setMessage("Any plans for get-together today?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "May be", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateStatus("May be");
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                updateStatus("No");
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateStatus("Yes");
            }
        });
        alertDialog.show();

    }*/


    public void updateStatus(String status) {
        final ProgressHUD mProgressHUD = ProgressHUD.show(BirthdaysActivity.this, "Sending, please wait...", true, true, null);

        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("Status", status);
            jsonObject.put("userID", Pref.GetStringPref(getApplicationContext(), StringUtils.ID, ""));

            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.POST, WebServicesUrls.POST_STATUS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if (response.toString().contains("Added")) {
                                ToastClass.showShortToast(getApplicationContext(), "Status added");
                            } else if (response.toString().contains("Updated")) {
                                ToastClass.showShortToast(getApplicationContext(), "Status updated");
                            } else {
                                ToastClass.showShortToast(getApplicationContext(), "Status failed");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "error:-" + error.toString());
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers));
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                    ToastClass.showShortToast(getApplicationContext(), "Server error");
                                    //alert("Server error");
                                } catch (Exception e1) {
                                    alert(e1.getMessage());
                                    ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            } else {
                                ToastClass.showShortToast(getApplicationContext(), "failed to connect, please try to logout and login again.");
                                //alert("failed to connect, please try to logout and login again.");
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
            ToastClass.showShortToast(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAdd() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6593186571085283~3729602775");
        mInterstitialAd = new InterstitialAd(getApplicationContext());
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
                //alert("Please purchase full version to support Kukians team");
                Intent intent = new Intent(BirthdaysActivity.this, PurchaseActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

       /* // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());*/
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void updateFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        if (!TextUtils.isEmpty(regId)) {
            updateRegistrationId(regId);
        }
        //Log.e(TAG, "Firebase reg id: " + regId);
    }

    public void showLocation() {
        //alertLocation("Location update will make you visible to everyone?");
        checkLocation();
        Toast.makeText(getApplicationContext(), "Location update will make you visible to everyone", Toast.LENGTH_LONG).show();
    }

    public void setImageURL() {
        String auth = Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, "");
      /*  Glide.with(this)
                .load(new Headers(auth).getUrlWithHeaders(WebServicesUrls.IMAGE_URL + Pref.GetStringPref(getApplicationContext(), StringUtils.ID, "")))
                .placeholder(R.drawable.ic_default_profile_pic)
                .error(R.drawable.ic_default_profile_pic)
                .dontAnimate()
                .into(home_profile_pic);*/

        Glide.with(this)
                .load(new Headers(auth).getUrlWithHeaders(WebServicesUrls.IMAGE_URL + Pref.GetStringPref(getApplicationContext(), StringUtils.ID, "")))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher))
                .into(home_profile_pic);
    }


    public void updateRegistrationId(String id) {
        try {
            String version = "NA";
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", id);
            jsonObject.put("appVersion", version);
            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.POST, WebServicesUrls.UPDATE_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if (response.toString().contains("Added")) {
                                ToastClass.showShortToast(getApplicationContext(), "Registration Id added");
                            } else if (response.toString().contains("Updated")) {
                                ToastClass.showShortToast(getApplicationContext(), "Registration Id updated");
                            } else {
                                ToastClass.showShortToast(getApplicationContext(), "Registration id failed");
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
                                    ToastClass.showShortToast(getApplicationContext(), response.toString());
                                } catch (Exception e1) {
                                    ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            } else {
                                ToastClass.showShortToast(getApplicationContext(), "failed to connect, please try to logout and login again.");
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
            ToastClass.showShortToast(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        }
    }
    /*public void getAlerts() {

        try {
            final ProgressHUD  mProgressHUD = ProgressHUD.show(HomeActivity.this, "Wait please..", true, true, null);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.ALERTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if(!TextUtils.isEmpty(response.toString()) && response.toString().length()>5)
                            {
                                alert(response.toString());
                            }
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
                                    JSONObject obj = new JSONObject(res);
                                    Log.d(TagUtils.getTag(), "error:-" + res);
                                   ToastClass.showShortToast(getApplicationContext(), "Server error");

                                } catch (Exception e1) {
                                    mProgressHUD.dismiss();
                                    ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            }
                            else
                            {
                                 ToastClass.showShortToast(getApplicationContext(),"failed to connect, please try to logout and login again.");
                            }
                        }
                    }
            ) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, ""));
//                    UtilityFunction.printAllValues(headers);
                    return headers;
                }
            };
            queue.add(getRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void getVersion() {

        try {
            final ProgressHUD mProgressHUD = ProgressHUD.show(BirthdaysActivity.this, "Checking app version...", true, true, null);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.GET_VERSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            String version;
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if (!TextUtils.isEmpty(response.toString())) {
                                try {
                                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                    version = pInfo.versionName;
                                    try {
                                        response = response.replaceAll("^\"|\"$", "");
                                        if (Double.valueOf(response.toString()) > Double.valueOf(version)) {
                                            alert("You are using old version, please update.");
                                        }
                                    } catch (NumberFormatException nfe) {
                                    }

                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
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
                                    JSONObject obj = new JSONObject(res);
                                    Log.d(TagUtils.getTag(), "error:-" + res);
                                    ToastClass.showShortToast(getApplicationContext(), "Server error");

                                } catch (Exception e1) {
                                    mProgressHUD.dismiss();
                                    ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            } else {
                                ToastClass.showShortToast(getApplicationContext(), "failed to connect, please try to logout and login again.");
                            }
                        }
                    }
            ) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, ""));
//                    UtilityFunction.printAllValues(headers);
                    return headers;
                }
            };
            queue.add(getRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkLocation() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            startLocationService();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2004);
            startLocationService();
            return;
        } else {
            startLocationService();
        }
    }

    public boolean checkCameraExists() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            String msg = "These permissions are mandatory for the application. Please allow access.";
                            showMessageOKCancel(msg,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Toast.makeText(context, "Permissions garanted.", Toast.LENGTH_LONG).show();
                    showFileChooser();
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void startLocationService() {
        try {
            if (!isMyServiceRunning(LocationService.class)) {
                startService(new Intent(BirthdaysActivity.this, LocationService.class));
            }
        } catch (Exception ex) {

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void getBirthdayMembers() {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.BIRTHDAYS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            parseResponse(response);
                            if (Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false) == false) {
                                loadAdd();
                            }
                            mProgressHUD.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "error:-" + error.toString());;
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
                                ToastClass.showShortToast(getApplicationContext(), "failed to connect, please try to logout and login again.");
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

 /*   public void alert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }*/

    UsersAdapter userListAdapter;
    List<UserListPOJO> userStrings = new ArrayList<>();

    public void attachAdapter() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_birthday_boys.setHasFixedSize(true);
        rv_birthday_boys.setLayoutManager(linearLayoutManager);
        userListAdapter = new UsersAdapter(this, null, userStrings);
        rv_birthday_boys.setAdapter(userListAdapter);
        rv_birthday_boys.setNestedScrollingEnabled(false);
        rv_birthday_boys.setItemAnimator(new DefaultItemAnimator());
    }

    public void parseResponse(String response) {
        if (response.contains("\"No records found\"")) {
            ToastClass.showShortToast(getApplicationContext(), "No data to display");
            alert("Sorry, you cannot have party today!");
        }
        try {
            userStrings.clear();
            JSONArray jsonArray = new JSONArray(response);
            List<UserListPOJO> userListPOJOS = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                userListPOJOS.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(), UserListPOJO.class));
            }

            userStrings.addAll(userListPOJOS);
            userListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFileChooser() {
        new ImagePicker.Builder(this)
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                .directory(ImagePicker.Directory.DEFAULT)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            if (mPaths.size() > 0) {
                if (new File(mPaths.get(0)).exists()) {
                    uploadImage(mPaths.get(0));
                    Glide.with(getApplicationContext())
                            .load(mPaths.get(0))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_launcher))
                            .into(home_profile_pic);
                }
            }
        }
    }

    public void uploadImage(String file_path) {

        try {
            Bitmap yourBitmap = BitmapFactory.decodeFile(file_path);
            Bitmap resized = Bitmap.createScaledBitmap(yourBitmap, 300, 300, true);
            file_path = saveBitmapToFile(resized);

            if (new File(file_path).exists()) {
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("attachment", new FileBody(new File(file_path)));
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, ""));
                final String finalFile_path = file_path;
                new WebUploadService(reqEntity, this, headers, new WebServicesCallBack() {
                    @Override
                    public void onGetMsg(String apicall, String response) {
                        try {
                            new File(finalFile_path).delete();
                            ToastClass.showShortToast(getApplicationContext(), response);
                            if (response.contains("Success")) {
                                alert("Photo upload success");
                            } else {
                                alert("Photo upload FAILED");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, "POST_IMAGE", true).execute(WebServicesUrls.POST_FILE);
            } else {
                ToastClass.showShortToast(getApplicationContext(), "Please select correct image");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String saveBitmapToFile(Bitmap bitmap) {
        try {
            FileOutputStream out = null;
            try {
                String file_path = FileUtils.getBaseFilePath() + File.separator + System.currentTimeMillis() + ".png";
                out = new FileOutputStream(file_path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored

                return file_path;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

/*   public void alertLocation(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(str).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                checkLocation();
            }
        }).show();
    }*/
}
