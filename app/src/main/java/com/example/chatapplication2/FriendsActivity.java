package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapplication2.Models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsActivity extends AppCompatActivity {

    private Toolbar actionBar;
    private RecyclerView recyclerView;

    private DatabaseReference userPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewFriendsActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));

        actionBar = (Toolbar) findViewById(R.id.actionBarFriendsActivity);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Arkadaşlarını Bul");

        userPath = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(userPath, Users.class).build();

        FirebaseRecyclerAdapter<Users, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Users, FriendsViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Users model) {
                holder.userName.setText(model.getName());
                holder.userAbout.setText(model.getAbout());
               // holder.profilPhoto.setImageBitmap(model.getPhoto());

                int index = position;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String clickedUserId = getRef(index).getKey();

                        Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                        intent.putExtra("clickedUserId", clickedUserId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_users_layout, parent, false);
                FriendsViewHolder viewHolder = new FriendsViewHolder(view);

                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

}