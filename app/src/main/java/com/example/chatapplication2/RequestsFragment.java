package com.example.chatapplication2;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication2.Models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    private View requestsView;

    private RecyclerView requestsList;
    //firebase
    private DatabaseReference requestsPath,usersPath,chatsPath;
    private FirebaseAuth auth;

    private String currentUserId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
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
        requestsView= inflater.inflate(R.layout.fragment_request, container, false);

        //Firebase
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        requestsPath= FirebaseDatabase.getInstance().getReference().child("Sohbet Talebi");
        usersPath= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        chatsPath= FirebaseDatabase.getInstance().getReference().child("Sohbetler");

        //Recyler
        requestsList= requestsView.findViewById(R.id.requestList);
        requestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestsView;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(requestsPath.child(currentUserId),Users.class)
                .build();
        FirebaseRecyclerAdapter<Users, RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Users, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Users model) {
                holder.itemView.findViewById(R.id.acceptButton).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.denyButton).setVisibility(View.VISIBLE);

                final String userIdList = getRef(position).getKey();
                DatabaseReference getrequestType = getRef(position).child("RequestKind").getRef();
                getrequestType.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String type = snapshot.getValue().toString();
                            if(type.equals("Received")){
                                usersPath.child(userIdList).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("Photo"))
                                        {
                                            //Veri tabanından resmi alıp değişkenlere aktarma
                                            final String requestProfilePhoto = snapshot.child("Photo").getValue().toString();

                                            ;

                                        }
                                        //Veri tabanından verileri alıp değişkenlere aktarma
                                        final String requestUsername = snapshot.child("Name").getValue().toString();
                                        final String requestAbout = snapshot.child("About").getValue().toString();

                                        holder.username.setText(requestUsername);
                                        // holder.about.setText("kullanıcı senle iletişim kurmak istiyor");
                                        holder.about.setText(requestAbout);

                                        //her satıra tıklandığında
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Kabul et",
                                                        "Reddet"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestUsername+" adlı kullanıcıdan bir yeni istek" );

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0){
                                                            chatsPath.child(currentUserId).child(userIdList).child("Chats").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        chatsPath.child(userIdList).child(currentUserId).child("Chats").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    requestsPath.child(currentUserId).child(userIdList).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                requestsPath.child(userIdList).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        Toast.makeText(getContext(), "İstek kabul edildi", Toast.LENGTH_LONG).show();

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
                                                        if ( i== 1){
                                                            requestsPath.child(currentUserId).child(userIdList).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        requestsPath.child(userIdList).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(getContext(), "İstek reddedildi", Toast.LENGTH_LONG).show();

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else if (type.equals("Sended")){
                                Button sendRequest = holder.itemView.findViewById(R.id.acceptButton);
                                sendRequest.setText("Talep Gönderildi");
                                holder.itemView.findViewById(R.id.denyButton).setVisibility(View.INVISIBLE);
                                usersPath.child(userIdList).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("Photo")){
                                            final String requestProfilePhoto = snapshot.child("Photo").getValue().toString();

                                        }
                                        final String requestUserName = snapshot.child("Name").getValue().toString();
                                        final String requestUserAbout = snapshot.child("About").getValue().toString();

                                        holder.username.setText(requestUserName);
                                        holder.about.setText(requestUserName+" adlı kullanıcıya istek gönderildi");
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[] =
                                                        new CharSequence[]
                                                                {
                                                                        "Talebi İptal Et"
                                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Bir istek var");


                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if(i == 0){
                                                            requestsPath.child(currentUserId).child(userIdList).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        requestsPath.child(userIdList).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(getContext(), "Talebiniz silindi..", Toast.LENGTH_LONG).show();

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_users_layout,parent,false);

                RequestsViewHolder holder = new RequestsViewHolder(view);

                return holder;
            }
        };
    }
    public static class RequestsViewHolder extends RecyclerView.ViewHolder{
        TextView username, about;
        CircleImageView photo;
        Button acceptButton, denyButton;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userNameOtherUser);
            about = itemView.findViewById(R.id.userAboutOtherUser);
            photo = itemView.findViewById(R.id.profilPhotoOtherUser);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            denyButton = itemView.findViewById(R.id.denyButton);
        }
    }
}