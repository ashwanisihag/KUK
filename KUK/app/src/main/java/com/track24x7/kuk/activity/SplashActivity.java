package com.track24x7.kuk.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.track24x7.kuk.R;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;

public class SplashActivity extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_LOGIN,false)){
                    intent=new Intent(SplashActivity.this,BirthdaysActivity.class);
                }else{
                    intent=new Intent(SplashActivity.this,LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },2000);
    }
}
