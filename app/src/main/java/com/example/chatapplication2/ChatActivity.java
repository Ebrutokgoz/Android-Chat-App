package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.chatapplication2.Models.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView receiverUsername, receiverLastSeen;
    private EditText messageInput;
    private CircleImageView receiverPhoto;
    private ImageView arrowBack;
    private ImageButton sendMessageButton;

    private Toolbar actionBar;

    private String messageReceiverId, messageReceiverName, messageReceiverPhoto, messageSenderId;

    private FirebaseAuth auth;
    private DatabaseReference messagePath, userPath;

    private final List<Messages> messagesList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    
    private String messageTime, messageDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverUsername = (TextView) findViewById(R.id.usernameChatActivity);
        receiverLastSeen = (TextView) findViewById(R.id.lastSeenChatActivity);
        messageInput = (EditText) findViewById(R.id.messageInput);
        receiverPhoto = (CircleImageView) findViewById(R.id.profilPhotoChatActivity);
        arrowBack = (ImageView) findViewById(R.id.backChatActivity);
        sendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);

        
        //ChatsFragment'den gelen intent ile
        messageReceiverId = getIntent().getExtras().get("receiverId").toString();
        messageReceiverName = getIntent().getExtras().get("receiverName").toString();
        messageReceiverPhoto = getIntent().getExtras().get("receiverPhoto").toString();
        
        auth = FirebaseAuth.getInstance();
        messagePath = FirebaseDatabase.getInstance().getReference();
        userPath = FirebaseDatabase.getInstance().getReference();
        messageSenderId = auth.getCurrentUser().getUid();
        
        receiverUsername.setText(messageReceiverName);

        messageAdapter=new MessageAdapter(messagesList);
        recyclerView=findViewById(R.id.chatsList);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);
        

        //TAKVİM
        Calendar calendar = Calendar.getInstance();
        //Tarih formatı
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        messageDate=currentDate.format(calendar.getTime());
        //Saat formatı
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        messageTime=currentTime.format(calendar.getTime());
        
        arrowBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        

    }
    @Override
    protected void onStart() {
        super.onStart();

        //Veri tabanından verileri çekme
        messagePath.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();

                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        
        String messageContent = messageInput.getText().toString();
        
        if(TextUtils.isEmpty(messageContent)){
            Toast.makeText(this, "Bir şeyler yazın", Toast.LENGTH_SHORT).show();
        }
        else{
            String senderPath = "Messages/"+messageSenderId+"/"+messageReceiverId;
            String receiverPath = "Messages/"+messageReceiverId+"/"+messageSenderId;
            
            DatabaseReference messageKeyPath = messagePath.child("Messages").child(messageSenderId).child(messageReceiverId).push();
            
            String addMessageId = messageKeyPath.getKey();
            
            Map messageRoot = new HashMap();
            messageRoot.put("message",messageContent);
            messageRoot.put("kind","Metin");
            messageRoot.put("Sender",messageSenderId);
            messageRoot.put("Receiver",messageReceiverId);
            messageRoot.put("messageId",addMessageId);
            messageRoot.put("time", messageTime);
            messageRoot.put("date", messageDate);
            
            Map message = new HashMap();
            message.put(senderPath+"/"+addMessageId, messageRoot);
            message.put(receiverPath+"/"+addMessageId, messageRoot);
            
            messagePath.updateChildren(message).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Mesaj Gönderildi!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Mesaj gönderme hatalı!!!", Toast.LENGTH_SHORT).show();
                    }

                    messageInput.setText("");
                } 
                
            });
        }
    }


}