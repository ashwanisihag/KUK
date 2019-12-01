package com.track24x7.kuk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.track24x7.kuk.R;
import com.track24x7.kuk.progress.ProgressHUD;
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

public class ChatActivity extends BaseMenuActivity {

    private Button  add_room;
    private EditText room_name;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        add_room = (Button) findViewById(R.id.btn_add_room);
        room_name = (EditText) findViewById(R.id.room_name_edittext);
        listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);
        listView.setAdapter(arrayAdapter);
        name= Pref.GetStringPref(getApplicationContext(), StringUtils.USERNAME,"") +"_"+ Pref.GetStringPref(getApplicationContext(), StringUtils.SCHOOL,"")+"_"+ Pref.GetStringPref(getApplicationContext(), StringUtils.Batch,"");
        //request_user_name();

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToastClass.showShortToast(getApplicationContext(),"We are one, so please chat in Kukians room only, we will think about future rooms later");
                if(!(room_name.getText().toString().trim().isEmpty()))
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(room_name.getText().toString(), "");
                    root.updateChildren(map);
                    SendNotification(room_name.getText().toString() +" room created");
                    room_name.setText("");
                }
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            final ProgressHUD mProgressHUD = ProgressHUD.show(ChatActivity.this,"Loading school chat rooms, please wait...", true,true,null);

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    String roomName=((DataSnapshot)i.next()).getKey();
                    //if(roomName.equalsIgnoreCase("Sainiks"))
                    {
                        set.add(roomName);
                    }
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);
                Collections.sort(list_of_rooms);
                arrayAdapter.notifyDataSetChanged();
                mProgressHUD.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),ChatMessagesActivity.class);
                intent.putExtra("room_name",((TextView)view).getText().toString() );
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });

    }

/*    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }*/
public void SendNotification(String msg) {

    try {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("Message", msg);
        jsonObject.put("UserID", Pref.GetStringPref(getApplicationContext(), StringUtils.ID, ""));
        jsonObject.put("Title", "Chat");
        Log.d(TagUtils.getTag(), "json Object:-" + jsonObject.toString());
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest getRequest = new StringRequest(Request.Method.POST, WebServicesUrls.NOTIFICATION_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TagUtils.getTag(), "response:-" + response.toString());
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
                                JSONObject obj = new JSONObject(res);
                                ToastClass.showShortToast(getApplicationContext(),"Server error");
                            } catch (Exception e1) {
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

}
