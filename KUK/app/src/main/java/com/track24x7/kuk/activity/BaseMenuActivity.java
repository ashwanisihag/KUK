package com.track24x7.kuk.activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.track24x7.kuk.R;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.service.LocationService;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.webservice.WebServicesUrls;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaseMenuActivity extends AppCompatActivity {

    Intent intent;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item_down = menu.findItem(R.id.nav_get_full_version);
        if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==false) {
            item_down.setVisible(true);
        }
        else
        {
            item_down.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                try {
                    stopService(new Intent(BaseMenuActivity.this, LocationService.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_LOGIN, false);
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, false);
                Pref.SetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.ROLE, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.USERNAME, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.FIRSTNAME, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.LASTNAME, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.Batch, "");
                Pref.SetStringPref(getApplicationContext(), StringUtils.SCHOOL, "");
                startActivity(new Intent(BaseMenuActivity.this, LoginActivity.class));
                finishAffinity();

                break;
           /* case R.id.nav_birthdays:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {

                    intent = new Intent(BaseMenuActivity.this, BaseMenuActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Birthdays"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;*/
          /*  case R.id.nav_upload_photos:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, PhotosActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "Upload Photos"); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_download_photos:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, ShowImagesActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "Download Photos"); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;*/
            case R.id.nav_get_full_version:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==false)
                {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                    startActivity(intent);
                }
                break;
            /*case R.id.nav_online:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, OnlineMembersActivity.class);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;*/
            case R.id.nav_search_Kukians:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, SearchActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Filter", "Branch"); //Your id
                    b.putString("Screen", "Search"); //Your id
                    //b.putString("School", Pref.GetStringPref(getApplicationContext(), StringUtils.SCHOOL, "")); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_post_jobs:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, PostJobActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Post Jobs"); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_posted_jobs:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, PostedJobsActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Posted Jobs"); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_my_posted_jobs: {
                intent = new Intent(BaseMenuActivity.this, MyPostedJobsActivity.class);
                b = new Bundle();
                b.putString("Screen", "My Posted Jobs"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
            break;
            case R.id.nav_posted_Requirements:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, PostedRequirementActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Posted Requirements"); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_my_posted_Requirements: {
                intent = new Intent(BaseMenuActivity.this, MyPostedRequirementActivity.class);
                b = new Bundle();
                b.putString("Screen", "My Posted Requirements"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
            break;
            case R.id.nav_post_Requirements:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, PostRequirementActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Post Requirements"); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_nearby_Kukians:
                   if (Pref.GetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, false)) {
                        intent = new Intent(BaseMenuActivity.this, NearByActivity.class);
                        b = new Bundle();
                        b.putString("Filter", "Branch"); //Your id
                        b.putString("Screen", "Near by Kukians"); //Your id
                        //b.putString("School", Pref.GetStringPref(getApplicationContext(), StringUtils.SCHOOL, "")); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                    } else {
                        alert("To see nearby people please switch on location update");
                    }

                startActivity(intent);
                break;
            case R.id.nav_my_messages:
            {
                intent=new Intent(BaseMenuActivity.this,UserMessagesActivity.class);
                b = new Bundle();
                b.putString("Screen", "Inbox"); //Your id
                startActivity(intent);
            }
            break;
            /*case R.id.nav_party:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {

                    if (Pref.GetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, false)) {
                        intent = new Intent(BaseMenuActivity.this, NearByPartyActivity.class);
                        b = new Bundle();
                        b.putString("Filter", "Party"); //Your id
                        b.putString("Screen", "Near by get together"); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                    } else {
                        alert("To see nearby get-together please switch on location update");
                    }
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;*/
            case R.id.nav_nearby_schoolmates:
                   if (Pref.GetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, false)) {
                        intent = new Intent(BaseMenuActivity.this, NearByActivity.class);
                        b = new Bundle();
                        b.putString("Filter", "School"); //Your id
                        b.putString("Screen", "Near by Schoolmates"); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                    } else {
                        alert("To see nearby people please switch on location update");
                    }
                startActivity(intent);
                break;
            case R.id.nav_nearby_batchmates:
                    if (Pref.GetBooleanPref(getApplicationContext(), StringUtils.LOCATION_ACCESS, false)) {
                        intent = new Intent(BaseMenuActivity.this, NearByActivity.class);
                        b = new Bundle();
                        b.putString("Filter", "Batch"); //Your id
                        b.putString("Screen", "Near by Batchmates"); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                    } else {
                        alert("To see nearby people please switch on location update");
                    }
               startActivity(intent);
                break;
            case R.id.nav_update_user:
                intent=new Intent(BaseMenuActivity.this,UpdateActivity.class);
                b = new Bundle();
                b.putString("Screen", "Update Details"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                break;
            case R.id.nav_news:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {

                    intent=new Intent(BaseMenuActivity.this,NewsActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Todays News"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_post_news:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(BaseMenuActivity.this, PostNewsActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Post News"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            /*case R.id.nav_chat:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent = new Intent(getApplicationContext(), ChatMessagesActivity.class);
                    intent.putExtra("room_name", "Kukians");
                    String name = Pref.GetStringPref(getApplicationContext(), StringUtils.FIRSTNAME, "") + "_" + Pref.GetStringPref(getApplicationContext(), StringUtils.LASTNAME, "") + "_" + Pref.GetStringPref(getApplicationContext(), StringUtils.SCHOOL, "") + "_" + Pref.GetStringPref(getApplicationContext(), StringUtils.Batch, "");
                    intent.putExtra("user_name", name);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;*/
            case R.id.nav_post_business:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent=new Intent(BaseMenuActivity.this,PostBusinessActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Post Business"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_home:
                intent=new Intent(BaseMenuActivity.this,HomeActivity.class);
                b = new Bundle();
                b.putString("Screen", "Batchmates"); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            /*case R.id.nav_view_photos:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent=new Intent(BaseMenuActivity.this,ShowImagesActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "View Photos"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_upload_photos:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent=new Intent(BaseMenuActivity.this,PhotosActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Upload Photos"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;*/
            case R.id.nav_posted_business:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent=new Intent(BaseMenuActivity.this,PostedBusinessActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "Posted Business"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_my_posted_business:
                if( Pref.GetBooleanPref(getApplicationContext(), StringUtils.IS_PAID,false)==true) {
                    intent=new Intent(BaseMenuActivity.this,MyPostedBusinessActivity.class);
                    b = new Bundle();
                    b.putString("Screen", "My Posted Business"); //Your id
                    intent.putExtras(b);
                }
                else {
                    intent=new Intent(BaseMenuActivity.this,PurchaseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.nav_share:
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
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.nav_about:
                startActivity(new Intent(BaseMenuActivity.this,AboutUsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
