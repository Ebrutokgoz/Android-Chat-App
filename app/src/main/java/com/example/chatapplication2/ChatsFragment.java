package com.example.chatapplication2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatapplication2.Models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private View chatsView;
    private RecyclerView chatsList;
    private DatabaseReference chatPath, userPath;
    private FirebaseAuth auth;
    private String currentUserId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        chatPath = FirebaseDatabase.getInstance().getReference().child("Chats").child(currentUserId);
        userPath = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = chatsView.findViewById(R.id.chatsListChatsFragment);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return chatsView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(chatPath, Users.class).build();
        FirebaseRecyclerAdapter<Users, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Users, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Users model) {
                final String userIds = getRef(position).getKey();
                final String[] getPhoto = {"Default Photo"};

                //Veri tabanından veri çağırma
                userPath.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("Photo")){
                                getPhoto[0] = snapshot.child("Photo").getValue().toString();
                                //Veri tabanından gelen resmi kontrole aktarma

                            }
                            final String getName = snapshot.child("Name").getValue().toString();
                            final String getAbout = snapshot.child("About").getValue().toString();

                            holder.username.setText(getName);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("userId_visited",userIds);
                                    intent.putExtra("username_visited",getName);
                                    intent.putExtra("photo_visited",getPhoto[0]);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_users_layout,parent,false);
                return new ChatsViewHolder(view);
            }
        };
        chatsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profilePhoto;
        TextView username, about;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilPhotoOtherUser);
            username = itemView.findViewById(R.id.userNameOtherUser);
            about = itemView.findViewById(R.id.userAboutOtherUser);
        }
    }
}