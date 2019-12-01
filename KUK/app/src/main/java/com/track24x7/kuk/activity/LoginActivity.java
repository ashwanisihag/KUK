package com.track24x7.kuk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.track24x7.kuk.R;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.IabHelper;
import com.track24x7.kuk.util.IabResult;
import com.track24x7.kuk.util.Inventory;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.Purchase;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.webservice.WebServiceBase;
import com.track24x7.kuk.webservice.WebServicesCallBack;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_register)
    TextView login_register;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.login_user_name)
    EditText login_user_name;
    @BindView(R.id.login_password)
    EditText login_password;
    @BindView(R.id.login_forgot_password)
    TextView login_forgot_password;
    ProgressHUD mProgressHUD;
    static final String SKU_PREMIUM = "pro_version";
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6PUTr7lZCKld+7S2o7H7FQkkaSEsmyIsbsiW+m+524YNRpIMtboXaeNEY52SG3TsMjRJBin32F/X6jUTi6ANQAiLtRAjAU3biHcNSdRcZhelpgw586fpAEak1A61eKspeucle6mYthbmOdi3kMgKhBlWxtfxw7GrjAw/3JKLvTJHxp3j/nhVEfhB9s2J1wwpcPFU5zO7K2+eiE1TBODR3lX6tNcL8RVX3qkBrUAZWNkP17XujwvNmeNBFdKDSLqw1Z4BIjDtc8nk20Tpndyi1Yss3WN9G8LmnBeMDoI3Wp8pez+vPxSsBEyUtgeXRWIsFvgCyru+r9CxH/bhH+fi3QIDAQAB";
    boolean mIsPremium = false;
    IabHelper mHelper;

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false);
                return;
            }
            // Is it a failure?
            if (result.isFailure()) {
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false);
                return;
            }

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            if (mIsPremium) {
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, true);
                purchasedAppStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false);
                    //alert("Problem setting up in-app billing: " + result);
                    Toast.makeText(getApplicationContext(), "Problem setting up in-app billing: " + result, Toast.LENGTH_LONG).show();
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false);
                    return;
                }
                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
          /*      mBroadcastReceiver = new IabBroadcastReceiver(BirthdaysActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);
*/
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (Exception e) {
                    Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false);
                    //alert("Error querying inventory. Another async operation in progress.");
                    Toast.makeText(getApplicationContext(), "Error querying inventory. Another async operation in progress", Toast.LENGTH_LONG).show();
                }
            }
        });

        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login_user_name.getText().toString().length() > 0
                        && login_password.getText().toString().length() > 0) {
                    callLoginAPI();
                } else {
                    ToastClass.showShortToast(getApplicationContext(), "Please enter all details properly");
                }
            }
        });

        login_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        getSupportActionBar().setTitle("Login");

    }

    public void purchasedAppStatus() {
        final ProgressHUD mProgressHUD = ProgressHUD.show(LoginActivity.this, "Sending, please wait...", true, true, null);

        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("PurchaseStatus", "True");
            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.POST, WebServicesUrls.POST_PURCHASE_STATUS,
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
//                            error.printStackTrace();
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

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    public void alert(String message) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void callLoginAPI() {
        mProgressHUD = ProgressHUD.show(LoginActivity.this, "Getting you to family now, please wait...", true, true, null);
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("username", login_user_name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password", login_password.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
        new WebServiceBase(nameValuePairs, null, this, new WebServicesCallBack() {
            @Override
            public void onGetMsg(String apicall, String response) {
                mProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String access_token = jsonObject.getString("access_token");
                    String token_type = jsonObject.getString("token_type");
                    String UserName = jsonObject.getString("UserName");
                    String FirstName = jsonObject.getString("FirstName");
                    String LastName = jsonObject.getString("LastName");
                    String Purchased = jsonObject.getString("Purchased");
                    String Role = jsonObject.getString("Role");
                    String Id = jsonObject.getString("Id");
                    String Batch = jsonObject.getString("Batch");
                    String School = jsonObject.getString("School");
                    Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_LOGIN, true);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, access_token);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, token_type);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.USERNAME, UserName);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.FIRSTNAME, FirstName);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.LASTNAME, LastName);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.ROLE, Role);
                    if(mIsPremium == false) {
                        if (!TextUtils.isEmpty(Purchased)) {
                            if (Purchased.contentEquals("Purchased")) {
                                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, true);
                            } else {
                                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, false);
                            }
                        }
                    }
                    try {
                        String btch = Batch.split(" ")[0].split("-")[2];
                        Pref.SetStringPref(getApplicationContext(), StringUtils.Batch, btch);
                    } catch (Exception e) {
                        login_password.setText("");
                        e.printStackTrace();
                        mProgressHUD.dismiss();
                        ToastClass.showShortToast(getApplicationContext(), e.getMessage());
                    }
                    Pref.SetStringPref(getApplicationContext(), StringUtils.ID, Id);
                    Pref.SetStringPref(getApplicationContext(), StringUtils.SCHOOL, School);
                    startActivity(new Intent(LoginActivity.this, SplashActivity.class));

                } catch (Exception e) {
                    try {
                        login_password.setText("");
                        JSONObject jsonObject = new JSONObject(response);
                        String error = jsonObject.optString("error");
                        String error_description = jsonObject.optString("error_description");
                        ToastClass.showShortToast(getApplicationContext(), error_description);
                    } catch (Exception e1) {
                        login_password.setText("");
                        mProgressHUD.dismiss();
                        ToastClass.showShortToast(getApplicationContext(), e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        }, "LOGIN_API", false).execute(WebServicesUrls.TOKEN);
    }

}
