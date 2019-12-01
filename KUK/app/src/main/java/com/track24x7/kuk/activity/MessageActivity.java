package com.track24x7.kuk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.track24x7.kuk.app.config;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.track24x7.kuk.R;
import com.track24x7.kuk.pojo.ResponseListPOJO;
import com.track24x7.kuk.pojo.StatesPOJO;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.webservice.ResponseListCallback;
import com.track24x7.kuk.webservice.WebServiceBaseResponseList;
import com.track24x7.kuk.webservice.WebServicesUrls;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends BaseMenuActivity  {

    @BindView(R.id.ms_Message)
    EditText ms_Message;
    @BindView(R.id.btn_Send)
    Button btn_Send;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        String value = b.getString("Screen");

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
                    Intent  intent=new Intent(MessageActivity.this,PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
        Log.e(TagUtils.getTag(),"Message activity running");

        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkValidEdit(ms_Message)) {

                    ToastClass.showShortToast(getApplicationContext(), "Please Enter Message");
                }
                else
                {
                        SendMessage();
                }

            }

        });
    }
    public boolean checkValidEdit(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().length() == 0) {
                return false;
            }
        }
        return true;
    }

    public void SendMessage() {
        final  ProgressHUD mProgressHUD = ProgressHUD.show(MessageActivity.this, "Sending, please wait...", true, true, null);

        try {
            final JSONObject jsonObject = new JSONObject();
            Bundle b = getIntent().getExtras();
            String userId = b.getString("userID");
            jsonObject.put("Message", ms_Message.getText().toString());
            jsonObject.put("userID", userId);
            jsonObject.put("groupID", "0");

            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.POST, WebServicesUrls.MESSAGE_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if (response.toString().contains("Posted")) {
                                alert("Message sent successfully");
                            } else {
                                alert("Message failed");
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
                                    ToastClass.showShortToast(getApplicationContext(),"Server error");
                                    alert("Server error");
                                } catch (Exception e1) {
                                    alert(e1.getMessage());
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
            ToastClass.showShortToast(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        }

    }


    /*public void SendNotification() {
        final  ProgressHUD mProgressHUD = ProgressHUD.show(MessageActivity.this, "", true, true, null);

        try {
            final JSONObject jsonObject = new JSONObject();
            Bundle b = getIntent().getExtras();
            String userId = b.getString("userID");
            jsonObject.put("Message", ms_Message.getText().toString());
            jsonObject.put("UserID", userId);
            jsonObject.put("Title", "Notification");
            jsonObject.put("Message",  ms_Message.getText().toString());
            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.POST, WebServicesUrls.NOTIFICATION_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if (response.toString().contains("Sent")) {
                                ToastClass.showShortToast(getApplicationContext(),"Notification sent successfully");
                                alert("Notification sent successfully");
                            } else {
                                ToastClass.showShortToast(getApplicationContext(),"Notification failed");
                                alert("Notification failed");
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
                                    ToastClass.showShortToast(getApplicationContext(),"Server error");
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
            ToastClass.showShortToast(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        }

    }
*/

    public void alert(String messageType) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(messageType).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

}
