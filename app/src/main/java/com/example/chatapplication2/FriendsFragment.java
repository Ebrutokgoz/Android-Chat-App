package com.example.chatapplication2;

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
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    private View friendsView;
    private RecyclerView friendsList;

    private DatabaseReference chatsPath, usersPath;
    private FirebaseAuth auth;

    private String currentUserId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        friendsView = inflater.inflate(R.layout.fragment_friends, container,false);
        friendsList=friendsView.findViewById(R.id.frinedsList);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        chatsPath = FirebaseDatabase.getInstance().getReference().child("Chats").child(currentUserId);
        usersPath = FirebaseDatabase.getInstance().getReference().child("Users");
        return friendsView;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(chatsPath,Users.class).build();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                String clickedLineUserId = getRef(position).getKey();
                usersPath.child(clickedLineUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("Photo")){
                                String userPhoto = snapshot.child("Photo").getValue().toString();
                                String userName = snapshot.child("Name").getValue().toString();
                                String userAbout = snapshot.child("About").getValue().toString();

                                holder.username.setText(userName);
                                holder.about.setText(userAbout);
                            }
                            else{
                                String userName = snapshot.child("Name").getValue().toString();
                                String userAbout = snapshot.child("About").getValue().toString();

                                holder.username.setText(userName);
                                holder.about.setText(userAbout);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_users_layout,parent,false);

                UsersViewHolder viewHolder = new UsersViewHolder(view);

                return  viewHolder;
            }
        };
        friendsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        TextView username, about;
        CircleImageView photo;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userNameOtherUser);
            about = itemView.findViewById(R.id.userAboutOtherUser);
            photo = itemView.findViewById(R.id.profilPhotoOtherUser);
        }
    }
}