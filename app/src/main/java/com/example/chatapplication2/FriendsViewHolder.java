package com.example.chatapplication2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder{

    TextView userName, userAbout;
    CircleImageView profilPhoto;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = (TextView) itemView.findViewById(R.id.userAboutOtherUser);
        userAbout = (TextView)  itemView.findViewById(R.id.userAboutOtherUser);
        profilPhoto = (CircleImageView) itemView.findViewById(R.id.profilPhotoOtherUser);
    }
}
