package com.track24x7.kuk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.track24x7.kuk.R;
import com.track24x7.kuk.adapter.ChatAdapter;
import com.track24x7.kuk.adapter.MessagesAdapter;
import com.track24x7.kuk.pojo.ChatPOJO;
import com.track24x7.kuk.pojo.MessagesPOJO;
import com.track24x7.kuk.progress.ProgressHUD;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;
import com.track24x7.kuk.util.TagUtils;
import com.track24x7.kuk.util.ToastClass;
import com.track24x7.kuk.util.UtilFunction;
import com.track24x7.kuk.webservice.WebServicesUrls;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMessagesActivity extends BaseMenuActivity {

    @BindView(R.id.rv_chat_messages)
    RecyclerView rv_chat_messages;
    private String user_name,room_name;
    private DatabaseReference root ;
    private String temp_key;
    private Button btn_send_msg;
    //private ImageButton btn_send_image;
    private EditText input_msg;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int RC_PHOTO_PICKER = 1;
    ProgressHUD mProgressHUD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);
        ButterKnife.bind(this);
        btn_send_msg = (Button) findViewById(R.id.btn_send);
        //btn_send_image=(ImageButton) findViewById(R.id.imageBtn);
        input_msg = (EditText) findViewById(R.id.msg_input);
        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(" Room - "+room_name);
        mProgressHUD = ProgressHUD.show(ChatMessagesActivity.this,"Loading chat, please wait...", true,true,null);
        root = FirebaseDatabase.getInstance().getReference().child(room_name);
        attachAdapter();
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!input_msg.getText().toString().trim().isEmpty()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    map2.put("time", currentDateTimeString + "\n");
                    map2.put("name", user_name);
                    map2.put("msg", input_msg.getText().toString() + "\n");

                    message_root.updateChildren(map2);
                    SendNotification(room_name +": " +input_msg.getText().toString());
                    input_msg.setText("");
                }
            }
        });

      /*  btn_send_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose image"), RC_PHOTO_PICKER);
            }
        });*/

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Recieved result from image picker
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            // Get a reference to the location where we'll store our photos
            root = FirebaseDatabase.getInstance().getReference(("chat_photos_" + getString(R.string.app_name)));
            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(getApplicationContext(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            // Send message with Image URL
                            ChatMessage chat = new ChatMessage(downloadUrl.toString(), username);
                            databaseRef.push().setValue(chat);
                            messageTxt.setText("");
                        }
                    });
        }
    }*/


    List<ChatPOJO> chatArrayPOJOS=new ArrayList<>();
    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        try{
            chatStrings.clear();
            ChatPOJO chatPOJOS;
            Iterator i = dataSnapshot.getChildren().iterator();
            while (i.hasNext()){
                chatPOJOS=new ChatPOJO();

                try
                {
                    chatPOJOS.setMessage((String) ((DataSnapshot)i.next()).getValue());
                }
                catch (Exception ex)
                {

                }
                try
                {
                    chatPOJOS.setFrom((String) ((DataSnapshot)i.next()).getValue());
                }
                catch (Exception ex)
                {

                }

                try
                {
                    chatPOJOS.setTime((String) ((DataSnapshot) i.next()).getValue());
                }
                catch (Exception ex)
                {

                }
                chatArrayPOJOS.add(chatPOJOS);
            }

            chatStrings.addAll(chatArrayPOJOS);
            chatListAdapter.notifyDataSetChanged();
            rv_chat_messages.scrollToPosition(chatListAdapter.getItemCount()-1);
            mProgressHUD.dismiss();
        }catch (Exception e){
            mProgressHUD.dismiss();
            e.printStackTrace();
        }
    }
    ChatAdapter chatListAdapter;
    List<ChatPOJO> chatStrings = new ArrayList<>();

    public void attachAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_chat_messages.setHasFixedSize(true);
        rv_chat_messages.setLayoutManager(linearLayoutManager);
        chatListAdapter = new ChatAdapter(this, null, chatStrings);
        rv_chat_messages.setAdapter(chatListAdapter);
        rv_chat_messages.setNestedScrollingEnabled(false);
        rv_chat_messages.setItemAnimator(new DefaultItemAnimator());
    }

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
