package com.track24x7.kuk.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.track24x7.kuk.R;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.FileUtils;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.webservice.WebServicesCallBack;
import com.track24x7.kuk.webservice.WebServicesUrls;
import com.track24x7.kuk.webservice.WebUploadService;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FullPhotoActivity extends BaseMenuActivity {
    private ImageView imageFullPhoto;
    private TextView textFullPhotoName;
    ProgressHUD mProgressHUD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo);
        mProgressHUD = ProgressHUD.show(FullPhotoActivity.this,"Loading photo...", true,true,null);
        Bundle b = getIntent().getExtras();
        String photoId = b.getString("photoId");

        imageFullPhoto = (ImageView) findViewById(R.id.imageFullPhoto);
        textFullPhotoName = (TextView) findViewById(R.id.textFullPhotoName);
        String auth = Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, "");
        String url=WebServicesUrls.GET_PHOTO_FULL + photoId;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Glide.with(getApplicationContext()).load(new Headers(auth).getUrlWithHeaders(url))
                .apply(new RequestOptions().override(width, height)
                .placeholder(R.drawable.custom_progressbar).centerCrop())
                .into(imageFullPhoto);

        mProgressHUD.dismiss();
    }
}