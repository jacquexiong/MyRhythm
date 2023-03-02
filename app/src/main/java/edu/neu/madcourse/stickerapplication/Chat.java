package edu.neu.madcourse.stickerapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    TextView totalSentTV;
    int totalSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        totalSentTV = findViewById(R.id.totalSent);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        totalSent = 0;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("myCh", "My Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://stickerchat-fbabe-default-rtdb.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://stickerchat-fbabe-default-rtdb.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("unseen", "true");
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
                messageArea.setText("");
            }
        });



        reference1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                String message;
                Integer sticker_index;
                if (map.get("message") != null) {
                    message = map.get("message").toString();
                    String userName = map.get("user").toString();
                    if (userName.equals(UserDetails.username)) {
                        if(map.containsKey("unseen")) reference1.child(dataSnapshot.getKey()).child("unseen").setValue("false");
                        addMessageBox("You:\n" + message, 1);
                    }
                    else
                    {
                        if(map.containsKey("unseen") && map.get("unseen").equals("true")) {
                            setNotification(UserDetails.chatWith + ":  " + message);
                            reference1.child(dataSnapshot.getKey()).child("unseen").setValue("false");
                        }
                        addMessageBox(UserDetails.chatWith + ":\n" + message, 2);
                    }
                }
                else if (map.get("sticker_index") != null){
                    sticker_index = Integer.valueOf(map.get("sticker_index").toString());
                    String userName = map.get("user").toString();
                    totalSent++;
                    totalSentTV.setText("You already sent " + totalSent +" stickers! Slide right for more!!" );

                    if (userName.equals(UserDetails.username)) {
                        if(map.containsKey("unseen")) reference1.child(dataSnapshot.getKey()).child("unseen").setValue("false");
                        addStickerBox(sticker_index, 1);
                    } else {
                        if(map.containsKey("unseen") && map.get("unseen").equals("true")) {
                            setNotification(UserDetails.chatWith + " sent you a sticker!");
                            reference1.child(dataSnapshot.getKey()).child("unseen").setValue("false");
                        }
                        addStickerBox(sticker_index, 2);
                    }
                }
            }
                //Integer sticker_index = Integer.valueOf(map.get("sticker_index").toString());


//                if(userName.equals(UserDetails.username)){
//
//                    addMessageBox("You:-\n" + message, 1);
//                    if(sticker_index >= 0) {
//                        addStickerBox(sticker_index, 1);
//                    }
//                    else {
//                        addMessageBox("You:-\n" + message, 1);
//                    }
//                }
//                else{
//                    if(sticker_index >= 0) {
//                        addStickerBox(sticker_index, 2);
//                    }
//                    else {
//                        addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
//                    }
//                    addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
//                }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{

            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }



    public void addStickerBox(int index, int type){
        pl.droidsonroids.gif.GifImageView imageView = new pl.droidsonroids.gif.GifImageView(Chat.this);
        TextView textView = new TextView(Chat.this);
        //TextView textView = new TextView(Chat.this);
        if(index == 0){
            //Toast.makeText(Chat.this, "Clicked google icon", Toast.LENGTH_LONG).show();
            imageView.setImageResource(R.drawable.eat);
        }
        else if (index == 1){
            imageView.setImageResource(R.drawable.cry);
        }
        else if (index == 2){
            imageView.setImageResource(R.drawable.hahaha);
        }
        else if (index == 3) {
            imageView.setImageResource(R.drawable.hungry);
        }
        else if (index == 4) {
            imageView.setImageResource(R.drawable.icecream);
        }
        else if (index == 5) {
            imageView.setImageResource(R.drawable.sweaty);
        }

//        LinearLayout linearLayout = new LinearLayout(Chat.this);
//        linearLayout.setLayoutParams((ViewGroup.LayoutParams.MATCH_PARENT, 300);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setPadding(0,0,0,10);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        lp.setMargins(0,0,0,10);
        LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp_text);
        imageView.setLayoutParams(lp);

        if(type == 1) {
            textView.setText("You:" );
            textView.setBackgroundResource(R.drawable.rounded_corner1);
            imageView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            textView.setText(UserDetails.chatWith + ":");
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            imageView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        layout.addView(imageView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    public void clickSticker(View view){
        Integer index = -1;
        if (view.getId() == R.id.btn_sticker1) {
            index = 0;
            //addStickerBox(0,1);
        }
        else if (view.getId() == R.id.btn_sticker2){
            index = 1;
            //addStickerBox(1,1);
        }
        else if (view.getId() == R.id.btn_sticker3){
            index = 2;
            //addStickerBox(2,1);
        }
        else if (view.getId() == R.id.btn_sticker4){
            index = 3;
            //addStickerBox(2,1);
        }
        else if (view.getId() == R.id.btn_sticker5){
            index = 4;
            //addStickerBox(2,1);
        }
        else if (view.getId() == R.id.btn_sticker6){
            index = 5;
            //addStickerBox(2,1);
        }

        Map<String, String> map = new HashMap<>();
        map.put("sticker_index", index.toString());
        map.put("unseen", "true");
        map.put("user", UserDetails.username);
        reference1.push().setValue(map);
        reference2.push().setValue(map);
    }

    public void setNotification(String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myCh")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.shine))
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("You got a new message!")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        notification = builder.build();
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,notification);
    }



}