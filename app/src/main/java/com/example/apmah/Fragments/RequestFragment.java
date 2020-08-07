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
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {

    private View viewRequest;
    private RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Requests").child(user.getUid());
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Register User Images");
    private DatabaseReference reqReference = FirebaseDatabase.getInstance().getReference().child("Requests");
    private DatabaseReference frndRefrence = FirebaseDatabase.getInstance().getReference().child("Friends");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        viewRequest = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = viewRequest.findViewById(R.id.RequestFrag_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return viewRequest;
    }

    @Override
    public void onStart() {

        FirebaseRecyclerOptions<UserData> options = new FirebaseRecyclerOptions.Builder<UserData>().setQuery(database.child("Receive"),UserData.class).build();

        super.onStart();

        FirebaseRecyclerAdapter<UserData,RecyclerHolder> adapter = new FirebaseRecyclerAdapter<UserData, RecyclerHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecyclerHolder holder, int position, @NonNull UserData model) {

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

                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reqReference.child(id).child(user.getUid()).child("Type").removeValue();
                        reqReference.child(user.getUid()).child("Receive").child(id).child("Type").removeValue();

                        frndRefrence.child(user.getUid()).child(id).child("Status").setValue("Friends");
                        frndRefrence.child(id).child(user.getUid()).child("Status").setValue("Friends");
                    }
                });

                holder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reqReference.child(id).child(user.getUid()).child("Type").removeValue();
                        reqReference.child(user.getUid()).child("Receive").child(id).child("Type").removeValue();
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.messages_item,parent,false);
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
        Button accept,decline;

        public RecyclerHolder(@NonNull View itemView) {

            super(itemView);

            status = itemView.findViewById(R.id.msgItem_Status);
            name = itemView.findViewById(R.id.msgItem_Name);
            imageView = itemView.findViewById(R.id.msgItem_ImageView);
            accept = itemView.findViewById(R.id.msgItem_AcceptButton);
            decline = itemView.findViewById(R.id.msgItem_DeclineButton);

        }
    }
}
