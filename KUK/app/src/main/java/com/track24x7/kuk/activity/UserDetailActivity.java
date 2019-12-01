package com.track24x7.kuk.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends BaseMenuActivity {

    @BindView(R.id.cv_profile_pic)
    CircleImageView cv_profile_pic;
    @BindView(R.id.user_detail_institute)
    TextView user_detail_institute;
    @BindView(R.id.user_detail_first_name)
    TextView user_detail_first_name;
    @BindView(R.id.user_detail_last_name)
    TextView user_detail_last_name;
   /* @BindView(R.id.user_detail_RollNo)
    TextView user_detail_RollNo;*/
    @BindView(R.id.user_detail_email)
    TextView user_detail_email;
    @BindView(R.id.user_detail_phone_number)
    TextView user_detail_phone_number;
    @BindView(R.id.user_detail_address)
    TextView user_detail_address;
    @BindView(R.id.user_detail_dob)
    TextView user_detail_dob;
    @BindView(R.id.user_detail_city)
    TextView user_detail_city;
    @BindView(R.id.user_detail_profession)
    TextView user_detail_profession;
    @BindView(R.id.user_detail_state)
    TextView user_detail_state;
    @BindView(R.id.user_detail_postal_code)
    TextView user_detail_postal_code;
    @BindView(R.id.user_detail_joining_year)
    TextView user_detail_joining_year;
    @BindView(R.id.user_detail_leaving_year)
    TextView user_detail_leaving_year;
    @BindView(R.id.user_detail_designation)
    TextView user_detail_designation;
    @BindView(R.id.user_detail_posting)
    TextView user_detail_posting;
    @BindView(R.id.user_detail_house)
    TextView user_detail_house;
    @BindView(R.id.ll_root)
    LinearLayout ll_root;
    @BindView(R.id.user_detail_department)
    TextView user_detail_department;
    @BindView(R.id.user_detail_profileLink)
    TextView user_detail_profileLink;
    @BindView(R.id.btn_show_on_map)
    Button btn_show_on_map;
    @BindView(R.id.btn_send_message)
    Button btn_send_message;
    @BindView(R.id.btn_activate_users)
    Button btn_activate_users;
    @BindView(R.id.btn_delete_user)
    Button btn_delete_user;
    private InterstitialAd mInterstitialAd;

    /*  @BindView(R.id.btn_call)
      Button btn_call;*/
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
        setContentView(R.layout.activity_user_detail);
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
                    Intent intent = new Intent(UserDetailActivity.this, PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
        getSupportActionBar().setTitle("User Details");
        String userId = getIntent().getStringExtra("userID");

        if (userId != null) {
            callAPI(userId);
            getUser(userId);
        }
    }

    public void showUser(final UserListPOJO userListPOJO) {
        user_detail_institute.setText(userListPOJO.getSchool());
        user_detail_first_name.setText(userListPOJO.getFirstName());
        user_detail_last_name.setText(userListPOJO.getLastName());
        //user_detail_RollNo.setText(userListPOJO.getRollNo().toString());

        if (userListPOJO.getPhoneVisible() || Pref.GetStringPref(getApplicationContext(), StringUtils.ROLE, "").contains("Admin")) {
            user_detail_phone_number.setText(userListPOJO.getPhoneNumber());
        } else {
            user_detail_phone_number.setText("Number Hidden");
        }
        if (Pref.GetStringPref(getApplicationContext(), StringUtils.ROLE, "").contains("Admin")) {
            btn_delete_user.setVisibility(View.GONE);
            btn_activate_users.setVisibility(View.GONE);
        } else {
            btn_delete_user.setVisibility(View.VISIBLE);
            btn_activate_users.setVisibility(View.VISIBLE);
        }
        if (userListPOJO.getLocationVisible() || Pref.GetStringPref(getApplicationContext(), StringUtils.ROLE, "").contains("Admin")) {
            btn_show_on_map.setEnabled(true);
        } else {
            btn_show_on_map.setEnabled(false);
        }
        user_detail_email.setText(userListPOJO.getEmail());
        user_detail_address.setText(userListPOJO.getAddress());
        if (userListPOJO.getDateOfBirth() != null) {
            user_detail_dob.setText(userListPOJO.getDateOfBirth().split("T")[0]);
        } else {
            user_detail_dob.setText("");
        }
        user_detail_city.setText(userListPOJO.getCity());
        user_detail_state.setText(userListPOJO.getState());
        user_detail_postal_code.setText(userListPOJO.getPostalCode());
        user_detail_joining_year.setText(UtilFunction.getYear(userListPOJO.getJoiningYear()));
        user_detail_leaving_year.setText(UtilFunction.getYear(userListPOJO.getLeavingYear()));
        user_detail_designation.setText(userListPOJO.getDesignation());
        user_detail_posting.setText(userListPOJO.getPosting());
        user_detail_house.setText(userListPOJO.getHouse());
        user_detail_department.setText(userListPOJO.getDepartment());
        user_detail_profession.setText(userListPOJO.getProfession());
        user_detail_profileLink.setText(userListPOJO.getProfileLink());

       /* switch (userListPOJO.getHouse().toLowerCase()) {
            case "blue":
                ll_root.setBackgroundColor(Color.parseColor("#4FC3F7"));
                break;
            case "red":
                ll_root.setBackgroundColor(Color.parseColor("#FF5722"));
                break;
            case "yellow":
                ll_root.setBackgroundColor(Color.parseColor("#FFF176"));
                break;
            case "green":
                ll_root.setBackgroundColor(Color.parseColor("#A5D6A7"));
                break;
            case "orange":
                ll_root.setBackgroundColor(Color.parseColor("#FFA500"));
                break;
            case "brown":
                ll_root.setBackgroundColor(Color.parseColor("#A52A2A"));
                break;
            case "pink":
                ll_root.setBackgroundColor(Color.parseColor("#FFC0CB"));
                break;
            case "purple":
                ll_root.setBackgroundColor(Color.parseColor("#800080"));
                break;
            case "white":
                ll_root.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case "black":
                ll_root.setBackgroundColor(Color.parseColor("#000000"));
                break;
        }*/

        btn_show_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Log.d(TagUtils.getTag(), "latitude:-" + userListPOJO.getLatitude());
                    Log.d(TagUtils.getTag(), "longitude:-" + userListPOJO.getLongitude());

                    String strUri = "http://maps.google.com/maps?q=loc:" + userListPOJO.getLatitude() + "," + userListPOJO.getLongitude() + " (" + userListPOJO.getFirstName() + " " + userListPOJO.getLastName() + ")";
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_activate_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final JSONObject jsonObject = new JSONObject();

                    jsonObject.put("UserName", userListPOJO.getUserName());

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest getRequest = new StringRequest(Request.Method.PUT, WebServicesUrls.ACTIVATE_USER,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TagUtils.getTag(), "response:-" + response.toString());
                                    if (response.toString().contains("Success") || response.toString().trim().equals("")) {
                                        alert("User Actvativated Successfully");
                                    } else {
                                        alert("User Actvation Failed");
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
                                            Log.d(TagUtils.getTag(), "obj:-" + obj.toString());


                                        } catch (Exception e1) {
                                            // Couldn't properly decode data to string
                                            e1.printStackTrace();
                                        }
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
        });
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(UserDetailActivity.this, MessageActivity.class);
                    String userId = getIntent().getStringExtra("userID");
                    Bundle b = new Bundle();
                    b.putString("userID", userId);
                    intent.putExtra("Screen", "Send Message");
                    intent.putExtras(b);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String userId = getIntent().getStringExtra("userID");
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    StringRequest getRequest = new StringRequest(Request.Method.DELETE, WebServicesUrls.DELETE_USER + "/" + userId,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TagUtils.getTag(), "response:-" + response.toString());
                                    if (response.toString().contains("Deleted")) {
                                        ToastClass.showShortToast(getApplicationContext(), "Delete Success");
                                    } else {
                                        ToastClass.showShortToast(getApplicationContext(), "Delete Failed");
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
                                            ToastClass.showShortToast(getApplicationContext(), "Server error");
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
        });
    }

    public void getUser(String userId) {

        try {
            mProgressHUD = ProgressHUD.show(UserDetailActivity.this, "Getting user details, please wait...", true, true, null);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.GET_USER + userId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            try {
                                UserListPOJO userListPOJO = new Gson().fromJson(response, UserListPOJO.class);
                                showUser(userListPOJO);
                            } catch (Exception e) {
                                e.printStackTrace();
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
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                    ToastClass.showShortToast(getApplicationContext(), "Server error");
                                    alert("Server error");
                                } catch (Exception e1) {
                                    ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            } else {
                                ToastClass.showShortToast(getApplicationContext(), "failed to connect, please try to logout and login again.");
                                alert("failed to connect, please try to logout and login again.");
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

    public void callAPI(String userId) {
        final ProgressHUD mProgressHUD = ProgressHUD.show(UserDetailActivity.this, "Please wait, loading profile picture....", true, true, null);
        String auth = Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, "");
      /*  Glide.with(this)
                .load(new Headers(auth).getUrlWithHeaders(WebServicesUrls.IMAGE_URL + userId))
                .placeholder(R.drawable.ic_default_profile_pic)
                .error(R.drawable.ic_default_profile_pic)
                .dontAnimate()
                .into(cv_profile_pic);*/
        Glide.with(this)
                .load(new Headers(auth).getUrlWithHeaders(WebServicesUrls.IMAGE_URL + userId))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher))
                .into(cv_profile_pic);
        mProgressHUD.dismiss();
    }

}
