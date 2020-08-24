package com.example.apmah.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apmah.Chat.Chatts;
import com.example.apmah.Data.UserData;
import com.example.apmah.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    private View ChatFragment;
    private DatabaseReference getChats = FirebaseDatabase.getInstance().getReference().child("Chats");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Register User Images");
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ChatFragment = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = ChatFragment.findViewById(R.id.Chat_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return ChatFragment;
    }

    @Override
    public void onStart() {

        FirebaseRecyclerOptions<UserData> options = new FirebaseRecyclerOptions.Builder<UserData>().setQuery(getChats.child(User.getUid()),UserData.class).build();
        super.onStart();

        FirebaseRecyclerAdapter<UserData,RecyclerHolder> adapter = new FirebaseRecyclerAdapter<UserData, RecyclerHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position, @NonNull UserData model) {

                final String id = getRef(position).getKey();

                storageReference.child(id).getBytes(5024*5024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        holder.imageView.setImageBitmap(bitmap);
                    }
                });
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("Male").child(id).exists()){
                            holder.name.setText(snapshot.child("Male").child(id).child("Name").getValue().toString());
                            holder.status.setText(snapshot.child("Male").child(id).child("Status").getValue().toString());
                            if(snapshot.child("Male").child(User.getUid()).child("Online").equals("True")){
                                holder.online.setVisibility(View.VISIBLE);
                            }
                        }
                        else if(snapshot.child("Female").child(id).exists()){
                            holder.name.setText(snapshot.child("Female").child(id).child("Name").getValue().toString());
                            holder.status.setText(snapshot.child("Female").child(id).child("Status").getValue().toString());
                            if(snapshot.child("Female").child(User.getUid()).child("Online").equals("True")){
                                holder.online.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String getUserId = getRef(position).getKey();
                        Intent intent  = new Intent(getContext(),Chatts.class);
                        intent.putExtra("Nishantcheck",getUserId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.friend_item,parent,false);
                RecyclerHolder hold = new RecyclerHolder(view);
                return hold;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView name,status;
        CircleImageView imageView;
        ImageView online;

        public RecyclerHolder(@NonNull View itemView) {

            super(itemView);

            name = itemView.findViewById(R.id.frndItem_Name);
            status = itemView.findViewById(R.id.frndItem_Status);
            imageView = itemView.findViewById(R.id.frndItem_ImageView);
            online = itemView.findViewById(R.id.fnrdItem_OnlineStatus);

        }
    }
}
