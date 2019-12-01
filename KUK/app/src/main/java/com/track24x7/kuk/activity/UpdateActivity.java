package com.track24x7.kuk.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.track24x7.kuk.util.FileUtils;
import com.track24x7.kuk.util.Headers;
import com.track24x7.kuk.webservice.WebServicesCallBack;
import com.track24x7.kuk.webservice.WebUploadService;
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
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.ResponseListCallback;
import com.track24x7.kuk.webservice.WebServiceBaseResponseList;
import com.track24x7.kuk.webservice.WebServicesUrls;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateActivity extends BaseMenuActivity implements  com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {
    //List<String> instituteList=null;
    String[] instituteList;
    @BindView(R.id.ed_dob)
    EditText ed_dob;
    @BindView(R.id.ed_profile_pic)
    CircleImageView ed_profile_pic;
    /*    @BindView(R.id.ed_id)
        EditText ed_id;
        @BindView(R.id.ed_user_name)
        EditText ed_user_name;*/
    @BindView(R.id.ed_spinner_institute)
    Spinner ed_spinner_institute;
    @BindView(R.id.ed_first_name)
    EditText ed_first_name;
    @BindView(R.id.ed_last_name)
    EditText ed_last_name;
    @BindView(R.id.ed_phone_number)
    EditText ed_phone_number;
    @BindView(R.id.ed_email)
    EditText ed_email;
    @BindView(R.id.ed_address)
    EditText ed_address;
    @BindView(R.id.ed_city)
    EditText ed_city;
    @BindView(R.id.ed_postal_code)
    EditText ed_postal_code;
   /* @BindView(R.id.ed_roll_no)
    EditText ed_roll_no;*/
    @BindView(R.id.ed_spinner_joining_year)
    Spinner ed_spinner_joining_year;
    @BindView(R.id.ed_spinner_leaving_year)
    Spinner ed_spinner_leaving_year;
    @BindView(R.id.ed_designation)
    EditText ed_designation;
    @BindView(R.id.ed_posting)
    EditText ed_posting;
    @BindView(R.id.ed_spinner_blood_group)
    Spinner ed_spinner_blood_group;
    @BindView(R.id.ed_department)
    EditText ed_department;
    @BindView(R.id.ed_profileLink)
    EditText ed_profileLink;
    @BindView(R.id.btn_update)
    Button btn_update;
   /* @BindView(R.id.ed_spinner_house)
    Spinner ed_spinner_house;*/
    @BindView(R.id.ed_spinner_Professions)
    Spinner ed_spinner_Professions;
    @BindView(R.id.ed_spinner_state)
    Spinner ed_spinner_state;
    @BindView(R.id.ed_checkbox_phoneVisible)
    CheckBox ed_checkbox_phoneVisible;
    @BindView(R.id.ed_checkbox_locationVisible)
    CheckBox ed_checkbox_locationVisible;
    private InterstitialAd mInterstitialAd;
    private Activity activity;
    private Context context;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        activity = UpdateActivity.this;
        context = this;
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
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
                    Intent  intent=new Intent(UpdateActivity.this,PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
        Bundle b = getIntent().getExtras();
        String value = b.getString("Screen");
        getSupportActionBar().setTitle(value);
        Log.e(TagUtils.getTag(),"update activity running");
        instituteList = getResources().getStringArray(R.array.school_list_registration);
       /* instituteList = new ArrayList<>();
        instituteList.add("Ghorakhal");
        instituteList.add("Balachadi");
        instituteList.add("Imphal");
        instituteList.add("Purulia");
        instituteList.add("Bhubaneswar");
        instituteList.add("Kazhakootam");
        instituteList.add("Rewa");
        instituteList.add("Bijapur");
        instituteList.add("Kapurthala");
        instituteList.add("Satara");
        instituteList.add("Chittorgarh");
        instituteList.add("Korukonda");
        instituteList.add("Sujanpur Tira");
        instituteList.add("Goalpara");
        instituteList.add("Kunjpura");
        instituteList.add("Amaravathinagar");
        instituteList.add("Balachadi");
        instituteList.add("Tilaiya");
        instituteList.add("Gopalganj");
        instituteList.add("Nagrota");
        instituteList.add("Punglwa");
        instituteList.add("Ambikapur");
        instituteList.add("Rewari");
        instituteList.add("Kodagu");
        instituteList.add("Kalikiri");
        instituteList.add("Lucknow");
        instituteList.add("Chhing Chhip");
        instituteList.add("Nalanda");
        Collections.sort(instituteList);*/

        ed_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TagUtils.getTag(),"date picker showing");
                selectedEditText = ed_dob;
                openDatePicker();
            }
        });
        setImageURL();
        ed_profile_pic.setOnClickListener(new View.OnClickListener() {
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
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidEdit(ed_first_name, ed_last_name, ed_phone_number, ed_dob)) {

                    if (ed_phone_number.getText().toString().length() >= 10) {
                        if (isValid(ed_email.getText().toString().trim())) {
                            updateUser();
                        } else {
                            ToastClass.showShortToast(getApplicationContext(), "Please Enter Valid Email");
                        }
                    } else {
                        ToastClass.showShortToast(getApplicationContext(), "Please Enter Valid Phone Number");
                    }
                }

            }

        });

        addSpinnerAdapter(ed_spinner_joining_year);
        addSpinnerAdapter(ed_spinner_leaving_year);
        addSchoolSpinner(ed_spinner_institute);
        getALLStates();
    }
    public boolean checkCameraExists() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void addSchoolSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, instituteList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

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
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void setImageURL() {
        String auth = Pref.GetStringPref(getApplicationContext(), StringUtils.TOKEN_TYPE, "") + " " + Pref.GetStringPref(getApplicationContext(), StringUtils.ACCESS_TOKEN, "");
        Glide.with(this)
                .load(new Headers(auth).getUrlWithHeaders(WebServicesUrls.IMAGE_URL + Pref.GetStringPref(getApplicationContext(), StringUtils.ID, "")))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher))
                .into(ed_profile_pic);
    }
    public void getUser(String id) {
        final ProgressHUD  mProgressHUD = ProgressHUD.show(UpdateActivity.this, "Please wait...", true, true, null);

        try {

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.GET, WebServicesUrls.GET_USER + id,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            try {
                                UserListPOJO userListPOJO = new Gson().fromJson(response, UserListPOJO.class);
                                showUser(userListPOJO);
                            } catch (Exception e) {
                                ToastClass.showShortToast(getApplicationContext(), e.getMessage());
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
            mProgressHUD.dismiss();
            ToastClass.showShortToast(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        }
    }
    UserListPOJO userListPOJO;
    public void showUser(final UserListPOJO userListPOJO) {
        this.userListPOJO=userListPOJO;
        /*ed_id.setText(userListPOJO.getId());
        ed_user_name.setText(userListPOJO.getUserName());*/
        ed_first_name.setText(userListPOJO.getFirstName());
        ed_last_name.setText(userListPOJO.getLastName());
        //ed_roll_no.setText(userListPOJO.getRollNo().toString());
        if(userListPOJO.getPhoneVisible()) {
            ed_checkbox_phoneVisible.setChecked(true);
        }
        else
        {
            ed_checkbox_phoneVisible.setChecked(false);
        }
        if(userListPOJO.getLocationVisible()) {
            ed_checkbox_locationVisible.setChecked(true);
        }
        else
        {
            ed_checkbox_locationVisible.setChecked(false);
        }
        ed_phone_number.setText(userListPOJO.getPhoneNumber());
        ed_email.setText(userListPOJO.getEmail());
        ed_address.setText(userListPOJO.getAddress());
        if (userListPOJO.getDateOfBirth() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            try {
                d = simpleDateFormat.parse(userListPOJO.getDateOfBirth().split("T")[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            ed_dob.setText(sdf.format(d));
        } else {
            ed_dob.setText("");
        }
        ed_city.setText(userListPOJO.getCity());
        ed_postal_code.setText(userListPOJO.getPostalCode());
        ed_designation.setText(userListPOJO.getDesignation());
        ed_posting.setText(userListPOJO.getPosting());
        ed_department.setText(userListPOJO.getDepartment());
        ed_profileLink.setText(userListPOJO.getProfileLink());
        if (userListPOJO.getJoiningYear() != null) {
            int instituteIndex = getSchoolIndex(userListPOJO.getSchool().split("-")[0]);
            int joiningindex = getPosition(userListPOJO.getJoiningYear().split("-")[0]);
            int leaving_year = getPosition(userListPOJO.getLeavingYear().split("-")[0]);
            int stateIndex = getStateIndex(userListPOJO.getState());
            int bloodIndex = getBloodGroupIndex(userListPOJO.getBloodGroup());
            int houseIndex = getHouseIndex(userListPOJO.getHouse());
            int professionIndex = getProfessionsIndex(userListPOJO.getProfession());
            try {

                if (instituteIndex != -1) {
                    ed_spinner_institute.setSelection(instituteIndex);
                }
                if (joiningindex != -1) {
                    ed_spinner_joining_year.setSelection(joiningindex);
                }

                if (leaving_year != -1) {
                    ed_spinner_leaving_year.setSelection(leaving_year);
                }

                if (stateIndex != -1) {
                    ed_spinner_state.setSelection(stateIndex);
                }

                if (bloodIndex != -1) {
                    ed_spinner_blood_group.setSelection(bloodIndex);
                }

               /* if (houseIndex != -1) {
                    ed_spinner_house.setSelection(houseIndex);
                }*/
                if (professionIndex != -1) {
                    ed_spinner_Professions.setSelection(professionIndex);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
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

    public int getHouseIndex(String house) {
        String[] houseStrings = getResources().getStringArray(R.array.house_color);
        /*List<String> houseStrings = new ArrayList<>();
        houseStrings.add("Red");
        houseStrings.add("Blue");
        houseStrings.add("Green");
        houseStrings.add("Yellow");
        houseStrings.add("Orange");
        houseStrings.add("Pink");
        houseStrings.add("Purple");
        houseStrings.add("Brown");
        houseStrings.add("White");
        houseStrings.add("Black");*/

        int index = -1;

        for (int i = 0; i < houseStrings.length; i++) {
            if (house.equals(houseStrings[i])) {
                index = i;
            }
        }
        return index;

    }

    public int getProfessionsIndex(String profession) {
        String[] professionsStrings = getResources().getStringArray(R.array.business_category);
/*        List<String>professionsStrings = new ArrayList<>();
        professionsStrings.add("Accountant");
        professionsStrings.add("Actor");
        professionsStrings.add("Actress");
        professionsStrings.add("Air Traffic Controller");
        professionsStrings.add("Architect");
        professionsStrings.add("Artist");
        professionsStrings.add("Attorney");
        professionsStrings.add("Banker");
        professionsStrings.add("Bartender");
        professionsStrings.add("Barber");
        professionsStrings.add("Bookkeeper");
        professionsStrings.add("Builder");
        professionsStrings.add("Businessman");
        professionsStrings.add("Businesswoman");
        professionsStrings.add("Businessperson");
        professionsStrings.add("Butcher");
        professionsStrings.add("Carpenter");
        professionsStrings.add("Cashier");
        professionsStrings.add("Chef");
        professionsStrings.add("Coach");
        professionsStrings.add("Defence Personnel");
        professionsStrings.add("Dental Hygienist");
        professionsStrings.add("Dentist");
        professionsStrings.add("Designer");
        professionsStrings.add("Developer");
        professionsStrings.add("Dietician");
        professionsStrings.add("Doctor");
        professionsStrings.add("Economist");
        professionsStrings.add("Editor");
        professionsStrings.add("Electrician");
        professionsStrings.add("Engineer");
        professionsStrings.add("Farmer");
        professionsStrings.add("Freeman");
        professionsStrings.add("Filmmaker");
        professionsStrings.add("Fisherman");
        professionsStrings.add("Flight Attendant");
        professionsStrings.add("Jeweler");
        professionsStrings.add("Judge");
        professionsStrings.add("Lawyer");
        professionsStrings.add("Mechanic");
        professionsStrings.add("Musician");
        professionsStrings.add("Nutritionist");
        professionsStrings.add("Nurse");
        professionsStrings.add("Optician");
        professionsStrings.add("Painter");
        professionsStrings.add("Pharmacist");
        professionsStrings.add("Photographer");
        professionsStrings.add("Physician");
        professionsStrings.add("Physician's Assistant");
        professionsStrings.add("Pilot");
        professionsStrings.add("Plumber");
        professionsStrings.add("Police Officer");
        professionsStrings.add("Politician");
        professionsStrings.add("Professor");
        professionsStrings.add("Programmer");
        professionsStrings.add("Psychologist");
        professionsStrings.add("Receptionist");
        professionsStrings.add("Salesman");
        professionsStrings.add("Salesperson");
        professionsStrings.add("Saleswoman");
        professionsStrings.add("Secretary");
        professionsStrings.add("Singer");
        professionsStrings.add("Student");
        professionsStrings.add("Surgeon");
        professionsStrings.add("Teacher");
        professionsStrings.add("Therapist");
        professionsStrings.add("Translator");
        professionsStrings.add("Translator");
        professionsStrings.add("Undertaker");
        professionsStrings.add("Veterinarian");
        professionsStrings.add("Videographer");
        professionsStrings.add("Waiter");
        professionsStrings.add("Waitress");
        professionsStrings.add("Writer");*/

        int index = -1;

        for (int i = 0; i < professionsStrings.length; i++) {
            if (profession.equals(professionsStrings[i])) {
                index = i;
            }
        }
        return index;

    }
    public int getSchoolIndex(String house) {
        int index = -1;
        for (int i = 0; i < instituteList.length; i++) {
            if (house.equals(instituteList[i])) {
                index = i;
            }
        }
        return index;
    }

    public int getBloodGroupIndex(String bg) {
        String[] bgStrings = getResources().getStringArray(R.array.blood_group);

       /* List<String> bgStrings = new ArrayList<String>();
        bgStrings.add("O−");
        bgStrings.add("O+");
        bgStrings.add("A−");
        bgStrings.add("A+");
        bgStrings.add("B−");
        bgStrings.add("B+");
        bgStrings.add("AB−");
        bgStrings.add("AB+");*/
        int index = -1;
        for (int i = 0; i < bgStrings.length; i++) {
            if (bg.equals(bgStrings[i])) {
                index = i;
            }
        }
        return index;
    }

    public int getStateIndex(String state) {

        int index = -1;

        for (int i = 0; i < statesPOJOS.size(); i++) {
            if (statesPOJOS.get(i).getStateName().equals(state)) {
                index = i;
            }
        }

        return index;
    }

    public int getPosition(String val) {
        int startingYear = 1960;
        List<String> yearList = new ArrayList<>();
        while (startingYear < 2019) {
            startingYear = startingYear + 1;
            yearList.add(String.valueOf(startingYear));
        }

        int index = -1;
        for (int i = 0; i < yearList.size(); i++) {
            String str = yearList.get(i);
            if (str.equalsIgnoreCase(val)) {
                index = i;
            }
        }

        return index;
    }


    public void addSpinnerAdapter(Spinner spinner) {
        int startingYear = 1960;
        List<String> yearList = new ArrayList<>();
        while (startingYear < 2019) {
            startingYear = startingYear + 1;
            yearList.add(String.valueOf(startingYear));
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }

    public boolean checkValidEdit(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().length() == 0) {
                return false;
            }
        }
        return true;
    }

    List<StatesPOJO> statesPOJOS = new ArrayList<>();

    public void getALLStates() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(new BasicNameValuePair("",""));
        new WebServiceBaseResponseList<StatesPOJO>(nameValuePairs, this, new ResponseListCallback<StatesPOJO>() {
            @Override
            public void onGetMsg(ResponseListPOJO<StatesPOJO> responseListPOJO) {
                try {
                    statesPOJOS.addAll(responseListPOJO.getResultList());
                    List<String> stringList = new ArrayList<>();
                    for (StatesPOJO statesPOJO : statesPOJOS) {
                        stringList.add(statesPOJO.getStateName());
                    }

                    setStatesSpinnerAdapter(ed_spinner_state, stringList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getUser(Pref.GetStringPref(getApplicationContext(), StringUtils.ID, ""));
            }
        }, StatesPOJO.class, "get_all_states", true).execute(WebServicesUrls.GET_STATES_OF_INDIA);
    }

    public void setStatesSpinnerAdapter(Spinner spinner, List<String> items) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        items); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setSelection(12);
    }



    public void updateUser() {
        final  ProgressHUD mProgressHUD = ProgressHUD.show(UpdateActivity.this, "Updating, please wait...", true, true, null);

        try {
            final JSONObject jsonObject = new JSONObject();
            Bundle b = getIntent().getExtras();
            jsonObject.put("School", ed_spinner_institute.getSelectedItem().toString());
            jsonObject.put("Id", Pref.GetStringPref(getApplicationContext(), StringUtils.ID, ""));
            jsonObject.put("UserName", Pref.GetStringPref(getApplicationContext(), StringUtils.USERNAME, ""));
            jsonObject.put("FirstName", ed_first_name.getText().toString());
            jsonObject.put("LastName", ed_last_name.getText().toString());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date d = simpleDateFormat.parse(ed_dob.getText().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            jsonObject.put("DateOfBirth", sdf.format(d));
            jsonObject.put("PhoneNumber", ed_phone_number.getText().toString());
            jsonObject.put("Email", ed_email.getText().toString());
            jsonObject.put("Address", ed_address.getText().toString());
            jsonObject.put("City", ed_city.getText().toString());
            jsonObject.put("State", ed_spinner_state.getSelectedItem().toString());
            jsonObject.put("PostalCode", ed_postal_code.getText().toString());
            jsonObject.put("RollNo", "0000");
            jsonObject.put("JoiningYear", ed_spinner_joining_year.getSelectedItem().toString() + "-01-01");
            jsonObject.put("LeavingYear", ed_spinner_leaving_year.getSelectedItem().toString() + "-01-01");
            jsonObject.put("Designation", ed_designation.getText().toString());
            jsonObject.put("Posting", ed_posting.getText().toString());
            jsonObject.put("BloodGroup", ed_spinner_blood_group.getSelectedItem().toString());
            if(ed_checkbox_phoneVisible.isChecked()) {
                jsonObject.put("PhoneVisible",true);
            }
            else
            {
                jsonObject.put("PhoneVisible",false);
            }
            if(ed_checkbox_locationVisible.isChecked()) {
                jsonObject.put("ShowLocation",true);
            }
            else
            {
                jsonObject.put("ShowLocation",false);
            }
//            jsonObject.put("Photo", et_ph.getText().toString());
//            jsonObject.put("Latitude", et_imei.getText().toString());
//            jsonObject.put("Longitude", et_imei.getText().toString());
            //jsonObject.put("House", ed_spinner_house.getSelectedItem().toString());
            jsonObject.put("Profession", ed_spinner_Professions.getSelectedItem().toString());
            jsonObject.put("UserRole", "User");
            jsonObject.put("Department", ed_department.getText().toString());
            jsonObject.put("ProfileLink", ed_profileLink.getText().toString());

           /* jsonObject.put("FirstName", "sihag");
            jsonObject.put("LastName", "ashwani");
            jsonObject.put("DateOfBirth", "11/02/1979");
            jsonObject.put("PhoneNumber", "9876301824");
            jsonObject.put("Email", "ashwanisihag2@gmail.com");
            jsonObject.put("Address", "asdcdscsd");
            jsonObject.put("City", "Delhi");
            jsonObject.put("State", "Delhi");
            jsonObject.put("PostalCode", "110017");
            jsonObject.put("RollNo", "12345678");
            jsonObject.put("JoiningYear", "11-02-2001");
            jsonObject.put("LeavingYear", "11-02-2006");
            jsonObject.put("Designation", "General");
            jsonObject.put("Posting", "Kashmir");
            jsonObject.put("BloodGroup", "B+");
            jsonObject.put("House", "501");*/


            Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest getRequest = new StringRequest(Request.Method.PUT, WebServicesUrls.UPDATE_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressHUD.dismiss();
                            Log.d(TagUtils.getTag(), "response:-" + response.toString());
                            if (response.toString().contains("Success") || response.toString().trim().equals("")) {
                                alert("User Updated Successfully");
                            } else {
                                alert("User Failed");
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


    public void alert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void uploadImage(String file_path) {

        try {
            Bitmap yourBitmap= BitmapFactory.decodeFile(file_path);
            Bitmap resized = Bitmap.createScaledBitmap(yourBitmap, 300, 300, true);
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
                            ToastClass.showShortToast(getApplicationContext(),response);
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
                }, "POST_IMAGE", true).execute(WebServicesUrls.POST_FILE);
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


    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
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

    public void openDatePicker() {
        Log.d(TagUtils.getTag(),"date picker showing");
        int defaultYear=2018;
        int defaultMonth=07;
        int defaultDay=27;
        if(ed_dob.getText().toString().length()>0){
            Log.e(TagUtils.getTag(),"dob:-"+ed_dob.getText().toString());
            defaultYear=Integer.parseInt(ed_dob.getText().toString().split("-")[0]);
            defaultMonth=Integer.parseInt(ed_dob.getText().toString().split("-")[1]);
            defaultDay=Integer.parseInt(ed_dob.getText().toString().split("-")[2]);
        }

        Log.e(TagUtils.getTag(),"defaultyear:-"+defaultYear);
        Log.e(TagUtils.getTag(),"defaultMonth:-"+defaultMonth);
        Log.e(TagUtils.getTag(),"defaultDay:-"+defaultDay);

        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .defaultDate(defaultYear, defaultMonth, defaultDay)
                .maxDate(2020, 0, 1)
                .minDate(1900, 0, 1)
                .build()
                .show();
    }

    EditText selectedEditText;

/*
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month = "";
        String day = "";
        if ((monthOfYear + 1) < 10) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = String.valueOf(monthOfYear + 1);
        }

        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = String.valueOf(dayOfMonth);
        }

        String date = day + "-" + month + "-" + year;

        selectedEditText.setText(date);
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            if (mPaths.size() > 0) {
                uploadImage(mPaths.get(0));
               /* Glide.with(getApplicationContext())
                        .load(mPaths.get(0))
                        .placeholder(R.drawable.ic_default_profile_pic)
                        .error(R.drawable.ic_default_profile_pic)
                        .dontAnimate().into(ed_profile_pic);*/
                Glide.with(this)
                        .load(mPaths.get(0))
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_launcher))
                        .into(ed_profile_pic);
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ed_dob.setText(sdf.format(calendar.getTime()));
    }

    /*@Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String month = "";
        String day = "";
        if ((monthOfYear + 1) < 10) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = String.valueOf(monthOfYear + 1);
        }

        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = String.valueOf(dayOfMonth);
        }

        String date = day + "-" + month + "-" + year;
        Log.d(TagUtils.getTag(), "date :-" + date);
        selectedEditText.setText(date);
    }*/
}
