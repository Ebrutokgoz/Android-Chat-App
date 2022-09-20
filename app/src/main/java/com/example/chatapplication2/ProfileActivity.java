package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String visitedUserId, currentUserId, currentState;
    private TextView visitedUserName, visitedUserAbout;
    private CircleImageView visitedUserPhoto;
    private Button sendRequestButton, cancelRequestButton;

    private FirebaseAuth auth;
    private DatabaseReference userPath, requestPath, chatsPath, notificationPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        visitedUserId = getIntent().getExtras().get("visited_user_id").toString();



        visitedUserName =(TextView) findViewById(R.id.visitedUser_usernameProfileActivity);
        visitedUserAbout = (TextView) findViewById(R.id.visitedUser_aboutProfileActivity);
        visitedUserPhoto = (CircleImageView) findViewById(R.id.visitedUser_photoProfileActivity);
        sendRequestButton = (Button) findViewById(R.id.sendRequestButton);
        cancelRequestButton =(Button) findViewById(R.id.cancelRequestButton);

        auth = FirebaseAuth.getInstance();
        userPath = FirebaseDatabase.getInstance().getReference().child("Users");
        requestPath = FirebaseDatabase.getInstance().getReference().child("Requests");
        chatsPath = FirebaseDatabase.getInstance().getReference().child("Chats");
        notificationPath = FirebaseDatabase.getInstance().getReference().child("Notifications");

        currentUserId = auth.getCurrentUser().getUid();
        currentState = "New";

        getVisitedUserInformation();
    }

    private void getVisitedUserInformation() {
        userPath.child(visitedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists()) && (snapshot.hasChild("Photo"))){
                    String profilePhoto = snapshot.child("Photo").getValue().toString();
                    String username = snapshot.child("Username").getValue().toString();
                    String about = snapshot.child("About").getValue().toString();

                    visitedUserName.setText(username);
                    visitedUserAbout.setText(about);

                    manageRequest();
                }
                else{
                    String username = snapshot.child("Username").getValue().toString();
                    String about = snapshot.child("About").getValue().toString();
                    visitedUserName.setText(username);
                    visitedUserAbout.setText(about);

                    manageRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void manageRequest() {
        //Talep varsa buton iptali göstersin
        requestPath.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(visitedUserId)){
                    String requestKind =snapshot.child(visitedUserId).child("RequestKind").getValue().toString();
                    if(requestKind.equals("Sended")) {
                        currentState = "Request Sended";
                        sendRequestButton.setText("Mesaj talebi iptal");
                    }
                    else{
                        currentState = "Request Received";
                        sendRequestButton.setText("Mesaj Talebi Kabul");
                        cancelRequestButton.setVisibility(View.VISIBLE);
                        cancelRequestButton.setEnabled(true);

                        cancelRequestButton.setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                cancelRequest();
                            }
                        });
                    }
                }
                else{
                    chatsPath.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(visitedUserId)){
                                currentState = "Friends";
                                sendRequestButton.setText("Sohbeti Sil");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(currentUserId.equals(visitedUserId)){
            sendRequestButton.setVisibility(View.INVISIBLE);
        }
        else{
            sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentState.equals("New")){
                        sendRequest();
                    }
                    if(currentState.equals("Request Sended")){
                        cancelRequest();
                    }
                    if(currentState.equals("Request Received")){
                        acceptRequest();
                    }
                    if(currentState.equals("Friends")){
                        deleteChat();
                    }
                }
            });
        }
    }

    private void sendRequest() {
        requestPath.child(currentUserId).child(visitedUserId).child("RequestKind").setValue("Sended").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Veri tabanına veri gönderme
                    requestPath.child(visitedUserId).child(currentUserId).child("RequestKind").setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Bildirim
                                HashMap<String,String> chatNotificationMap = new HashMap<>();
                                chatNotificationMap.put("Sender", currentUserId);
                                chatNotificationMap.put("Kind", "Request");

                                notificationPath.child(visitedUserId).push().setValue(chatNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            sendRequestButton.setEnabled(true);
                                            currentState = "Reguest Sended";
                                            sendRequestButton.setText("Mesaj Talebi İptal");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    private void cancelRequest() {
        //Talebi gönderenden sil
        requestPath.child(currentUserId).child(visitedUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Talebi alandan sil
                    requestPath.child(visitedUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendRequestButton.setEnabled(true);
                                currentState = "New";
                                sendRequestButton.setText("Mesaj Talebi Gönder");

                                cancelRequestButton.setVisibility(View.INVISIBLE);
                                cancelRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptRequest(){
        chatsPath.child(currentUserId).child(visitedUserId).child("Chats").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatsPath.child(visitedUserId).child(currentUserId).child("Chats").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                requestPath.child(currentUserId).child(visitedUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            requestPath.child(visitedUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sendRequestButton.setEnabled((true));
                                                    currentState = "Arkadaşlar";
                                                    sendRequestButton.setText("Sohbeti Sil");
                                                    cancelRequestButton.setVisibility(View.INVISIBLE);
                                                    cancelRequestButton.setEnabled(false);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    private void deleteChat(){
        chatsPath.child(currentUserId).child(visitedUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Talebi alandan sil
                    chatsPath.child(visitedUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendRequestButton.setEnabled(true);
                                currentState = "New";
                                sendRequestButton.setText("Mesaj Talebi Gönder");
                                cancelRequestButton.setVisibility(View.INVISIBLE);
                                cancelRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }
}