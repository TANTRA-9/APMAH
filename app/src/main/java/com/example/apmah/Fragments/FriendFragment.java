package com.example.apmah.Fragments;


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
import android.widget.TextView;
import android.widget.Toast;

import com.example.apmah.Data.UserData;
import com.example.apmah.R;
import com.example.apmah.Register.UserDetail;
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


public class FriendFragment extends Fragment {

    private View friendView;
    private RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Register User Images");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        friendView = inflater.inflate(R.layout.fragment_friend, container, false);

        recyclerView = friendView.findViewById(R.id.FrndFragment_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return friendView;
    }

    @Override
    public void onStart() {

        FirebaseRecyclerOptions<UserData> options = new FirebaseRecyclerOptions.Builder<UserData>().setQuery(friendReference.child(user.getUid()),UserData.class).build();

        super.onStart();

        FirebaseRecyclerAdapter<UserData,Holder> adapter = new FirebaseRecyclerAdapter<UserData, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Holder holder, final int position, @NonNull UserData model) {

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
                        }
                        else if(snapshot.child("Female").child(id).exists()){
                            holder.name.setText(snapshot.child("Female").child(id).child("Name").getValue().toString());
                            holder.status.setText(snapshot.child("Female").child(id).child("Status").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = getRef(position).getKey();
                        Toast.makeText(getContext(), ""+name, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.friend_item,parent,false);
                Holder hold = new Holder(view);
                return hold;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView name,status;
        CircleImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.frndItem_Name);
            status = itemView.findViewById(R.id.frndItem_Status);
            imageView = itemView.findViewById(R.id.frndItem_ImageView);
        }
    }
}
