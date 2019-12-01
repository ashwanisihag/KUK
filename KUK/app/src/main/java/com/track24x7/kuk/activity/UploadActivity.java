package com.track24x7.kuk.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.adapter.UsersAdapter;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.pojo.UserListPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.service.LocationService;
import com.track24x7.kuk.util.FileUtils;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.util.IabHelper;
import com.track24x7.kuk.util.IabResult;
import com.track24x7.kuk.util.Inventory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UploadActivity extends BaseMenuActivity {

    @BindView(R.id.upload_pic)
    CircleImageView upload_pic;
    @BindView(R.id.rv_photos)
    RecyclerView rv_photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);
        ButterKnife.bind(this);
        attachAdapter();
        Bundle b = getIntent().getExtras();
        String value = b.getString("Screen");
        getSupportActionBar().setTitle(value);
        upload_pic.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                  checkImagePermission();
              } else {
                  initiatePicker();
              }
          }
      });
      setImageURL();
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

    public void setImageURL() {
        String auth = Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, "");
        Glide.with(this)
                .load(new Headers(auth).getUrlWithHeaders(WebServicesUrls.IMAGE_URL + Pref.GetStringPref(getApplicationContext(), StringUtils.ID, "")))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher))
                .into(upload_pic);
    }

    public void alert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    UsersAdapter userListAdapter;
    List<UserListPOJO> userStrings = new ArrayList<>();

    public void attachAdapter() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_photos.setHasFixedSize(true);
        rv_photos.setLayoutManager(linearLayoutManager);
        userListAdapter = new UsersAdapter(this, null, userStrings);
        rv_photos.setAdapter(userListAdapter);
        rv_photos.setNestedScrollingEnabled(false);
        rv_photos.setItemAnimator(new DefaultItemAnimator());
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
            List<UserListPOJO> userListPOJOS=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                userListPOJOS.add(new Gson().fromJson(jsonArray.optJSONObject(i).toString(),UserListPOJO.class));
            }
            if(userListPOJOS.isEmpty())
            {
                ToastClass.showShortToast(getApplicationContext(), "No one is having birthday today, stay tuned");
                alert("No one is having birthday today, stay tuned");
            }
            userStrings.addAll(userListPOJOS);
            userListAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkImagePermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    2004);
            return;
        } else {
            initiatePicker();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2004: {
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            if (mPaths.size() > 0) {
                if (new File(mPaths.get(0)).exists()) {
                    uploadImage(mPaths.get(0));
                   /* Glide.with(getApplicationContext())
                            .load(mPaths.get(0))
                            .placeholder(R.drawable.ic_default_profile_pic)
                            .error(R.drawable.ic_default_profile_pic)
                            .dontAnimate().into(home_profile_pic);*/
                    Glide.with(getApplicationContext())
                            .load(mPaths.get(0))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_launcher))
                            .into(upload_pic);
                }
            }
        }
    }
    public void initiatePicker() {
        new ImagePicker.Builder(this)
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                .directory(ImagePicker.Directory.DEFAULT)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
    }
    public void uploadImage(String file_path) {

        try {
            Bitmap yourBitmap= BitmapFactory.decodeFile(file_path);
            Bitmap resized = Bitmap.createScaledBitmap(yourBitmap, 1920 , 1080, true);
            file_path=saveBitmapToFile(resized);

            if(new File(file_path).exists()) {
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
                            if(response.contains("Success"))
                            {
                                alert("Photo upload success");
                            }
                            else
                            {
                                alert("Photo upload FAILED");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, "POST_IMAGE", true).execute(WebServicesUrls.POST_ALBUM_FILE);
            }else{
                ToastClass.showShortToast(getApplicationContext(),"Please select correct image");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String saveBitmapToFile(Bitmap bitmap){
        try{
            FileOutputStream out = null;
            try {
                String file_path= FileUtils.getBaseFilePath()+File.separator+System.currentTimeMillis()+".png";
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
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }
}
