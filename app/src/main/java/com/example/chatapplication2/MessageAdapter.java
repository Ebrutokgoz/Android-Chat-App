package com.example.chatapplication2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication2.Models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagesViewHolder> {

    private List<Messages> messagesList;
    
    private FirebaseAuth auth;
    private DatabaseReference usersPath;
    
    public MessageAdapter(List<Messages> messagesList){
        this.messagesList = messagesList;
    }
    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.textViewSenderMessage);
            receiverMessageText = (TextView) itemView.findViewById(R.id.textViewReceiverMessage);

        }


    }
    
    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout,parent,false);

        auth = FirebaseAuth.getInstance();

        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        
        String senderId = auth.getCurrentUser().getUid();
        Messages messages = messagesList.get(position);
        String senderUserId = messages.getSender();
        String messageKind = messages.getKind();
        
        usersPath= FirebaseDatabase.getInstance().getReference().child("Users").child(senderUserId);

        if(messageKind.equals("Metin")){
            //Görünmez yapma
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

            if(senderUserId.equals(senderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_design);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessageText());
            }

            else{


                //Görünür yapma
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_design);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessageText());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
