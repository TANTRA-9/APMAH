package com.example.apmah;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apmah.About.UpdateAbout;
import com.example.apmah.About_Application.about_application;
import com.example.apmah.Data.UserData;
import com.example.apmah.Messages.MainMessages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Iterator;


public class Enter extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Register User Images");
    private RecyclerView recyclerView;
    private DatabaseReference reqReference = FirebaseDatabase.getInstance().getReference().child("Requests");
    private DatabaseReference frndRefrence = FirebaseDatabase.getInstance().getReference().child("Friends");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        toolbar = findViewById(R.id.Enter_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>APMAH</font>"));

        recyclerView = findViewById(R.id.Enter_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Enter.this));


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("Male").child(user.getUid()).exists()){
                    setGender("Female");
                }
                else if(snapshot.child("Female").child(user.getUid()).exists()) {
                    setGender("Male");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem item = menu.findItem(R.id.Search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Enter Proper Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.Logout){
            auth.signOut();
            finish();
            startActivity(new Intent(Enter.this,MainActivity.class));
        }

        else if(item.getItemId() == R.id.Messages){
            startActivity(new Intent(Enter.this, MainMessages.class));
        }
        else if(item.getItemId() == R.id.About_Application){
            startActivity(new Intent(Enter.this, about_application.class));
        }
        else if(item.getItemId() == R.id.Update_Profile){
            startActivity(new Intent(Enter.this, UpdateAbout.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void setGender(final String Text){

        String uid = user.getUid();
        Log.d("CheckUid",""+uid.length());


        FirebaseRecyclerOptions<UserData> options = new FirebaseRecyclerOptions.Builder<UserData>().setQuery(reference.child(Text),UserData.class).build();

        final FirebaseRecyclerAdapter<UserData,Holder> adapter = new FirebaseRecyclerAdapter<UserData, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Holder holder, int position, @NonNull UserData model) {

                final String id = getRef(position).getKey();

                storageReference.child(id).getBytes(5024*5024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        holder.imageView.setImageBitmap(bitmap);
                    }
                });

                holder.name.setText(model.getName());

                frndRefrence.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(id).child(user.getUid()).exists()){
                            holder.btnAccept.setText("UnFriend");
                            holder.btnDecline.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                reqReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(user.getUid()).child(id).exists()){
                            holder.btnAccept.setText("Cancel Request");
                        }
                        else if(snapshot.child(user.getUid()).child("Receive").child(id).exists()){
                            holder.btnAccept.setText("Accept Request");
                            holder.btnDecline.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reqReference.child(user.getUid()).child("Receive").child(id).child("Type").removeValue();
                        reqReference.child(id).child(user.getUid()).removeValue();
                        holder.btnAccept.setText("Send Message");
                        holder.btnDecline.setVisibility(View.INVISIBLE);

                    }
                });

                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.btnAccept.getText().equals("Send Message")){

                            reqReference.child(user.getUid()).child(id).child("Type").setValue("Sent");
                            reqReference.child(id).child("Receive").child(user.getUid()).child("Type").setValue("Receive");
                            holder.btnAccept.setText("Cancel Request");

                        }
                        else if(holder.btnAccept.getText().equals("Cancel Request")){

                            reqReference.child(user.getUid()).child(id).child("Type").removeValue();
                            reqReference.child(id).child("Receive").child(user.getUid()).child("Type").removeValue();
                            holder.btnAccept.setText("Send Message");

                        }
                        else if(holder.btnAccept.getText().equals("Accept Request")){

                            reqReference.child(user.getUid()).child("Receive").child(id).child("Type").removeValue();
                            reqReference.child(id).child(user.getUid()).removeValue();

                            frndRefrence.child(user.getUid()).child(id).child("Status").setValue("Friends");
                            frndRefrence.child(id).child(user.getUid()).child("Status").setValue("Friends");
                            holder.btnAccept.setText("UnFriend");

                        }
                        else if(holder.btnAccept.getText().equals("UnFriend")){

                            holder.btnAccept.setText("Send Message");
                            frndRefrence.child(user.getUid()).child(id).child("Status").removeValue();
                            frndRefrence.child(id).child(user.getUid()).child("Status").removeValue();
                        }
                    }
                });

            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.item,parent,false);
                Holder hold = new Holder(view);
                return hold;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class Holder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        Button btnAccept,btnDecline;

        public Holder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_ImageView);
            name = itemView.findViewById(R.id.item_TextView);
            btnAccept = itemView.findViewById(R.id.item_SendRequest);
            btnDecline = itemView.findViewById(R.id.item_AcceptRequest);

        }
    }

    private void firebaseSearch(final String searchText){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("Male").child(user.getUid()).exists()){
                    Query firebasequery = reference.child("Female").orderByChild("Name").startAt(searchText).endAt(searchText+"\uf8ff");

                    FirebaseRecyclerOptions<UserData> options = new FirebaseRecyclerOptions.Builder<UserData>().setQuery(firebasequery,UserData.class).build();

                    final FirebaseRecyclerAdapter<UserData,Holder> adapter = new FirebaseRecyclerAdapter<UserData, Holder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final Holder holder, int position, @NonNull UserData model) {

                            final String id = getRef(position).getKey();

                            storageReference.child(id).getBytes(5024*5024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    holder.imageView.setImageBitmap(bitmap);
                                }
                            });

                            holder.name.setText(model.getName());

                            frndRefrence.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child(id).child(user.getUid()).exists()){
                                        holder.btnAccept.setText("UnFriend");
                                        holder.btnDecline.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            reqReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child(user.getUid()).child(id).exists()){
                                        holder.btnAccept.setText("Cancel Request");
                                    }
                                    else if(snapshot.child(user.getUid()).child("Receive").child(id).exists()){
                                        holder.btnAccept.setText("Accept Request");
                                        holder.btnDecline.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    reqReference.child(id).child(user.getUid()).child("Type").removeValue();
                                    reqReference.child(user.getUid()).child("Receive").child(user.getUid()).child("Type").removeValue();
                                    holder.btnAccept.setText("Send Message");

                                }
                            });

                            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(holder.btnAccept.getText().equals("Send Message")){

                                        reqReference.child(user.getUid()).child(id).child("Type").setValue("Sent");
                                        reqReference.child(id).child("Receive").child(user.getUid()).child("Type").setValue("Receive");
                                        holder.btnAccept.setText("Cancel Request");

                                    }
                                    else if(holder.btnAccept.getText().equals("Cancel Request")){

                                        reqReference.child(user.getUid()).child(id).child("Type").removeValue();
                                        reqReference.child(id).child("Receive").child(user.getUid()).child("Type").removeValue();
                                        holder.btnAccept.setText("Send Message");

                                    }
                                    else if(holder.btnAccept.getText().equals("Accept Request")){

                                        frndRefrence.child(user.getUid()).child(id).child("Status").setValue("Friends");
                                        frndRefrence.child(id).child(user.getUid()).child("Status").setValue("Friends");
                                        holder.btnAccept.setText("UnFriend");

                                    }
                                    else if(holder.btnAccept.getText().equals("UnFriend")){

                                        frndRefrence.child(user.getUid()).child(id).child("Status").removeValue();
                                        frndRefrence.child(id).child(user.getUid()).child("Status").removeValue();
                                        holder.btnAccept.setText("Send Message");
                                    }
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                            View view = inflater.inflate(R.layout.item,parent,false);
                            Holder hold = new Holder(view);
                            return hold;
                        }
                    };

                    recyclerView.setAdapter(adapter);
                    adapter.startListening();

                }

                if(snapshot.child("Female").child(user.getUid()).exists()){
                    Query firebasequery = reference.child("Male").orderByChild("Name").startAt(searchText).endAt(searchText+"\uf8ff");

                    FirebaseRecyclerOptions<UserData> options = new FirebaseRecyclerOptions.Builder<UserData>().setQuery(firebasequery,UserData.class).build();

                    final FirebaseRecyclerAdapter<UserData,Holder> adapter = new FirebaseRecyclerAdapter<UserData, Holder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final Holder holder, int position, @NonNull UserData model) {

                            final String id = getRef(position).getKey();

                            storageReference.child(id).getBytes(5024*5024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    holder.imageView.setImageBitmap(bitmap);
                                }
                            });

                            holder.name.setText(model.getName());

                            frndRefrence.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child(id).child(user.getUid()).exists()){
                                        holder.btnAccept.setText("UnFriend");
                                        holder.btnDecline.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            reqReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child(user.getUid()).child(id).exists()){
                                        holder.btnAccept.setText("Cancel Request");
                                    }
                                    else if(snapshot.child(user.getUid()).child("Receive").child(id).exists()){
                                        holder.btnAccept.setText("Accept Request");
                                        holder.btnDecline.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    reqReference.child(id).child(user.getUid()).child("Type").removeValue();
                                    reqReference.child(user.getUid()).child("Receive").child(user.getUid()).child("Type").removeValue();
                                    holder.btnAccept.setText("Send Message");

                                }
                            });

                            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(holder.btnAccept.getText().equals("Send Message")){

                                        reqReference.child(user.getUid()).child(id).child("Type").setValue("Sent");
                                        reqReference.child(id).child("Receive").child(user.getUid()).child("Type").setValue("Receive");
                                        holder.btnAccept.setText("Cancel Request");

                                    }
                                    else if(holder.btnAccept.getText().equals("Cancel Request")){

                                        reqReference.child(user.getUid()).child(id).child("Type").removeValue();
                                        reqReference.child(id).child("Receive").child(user.getUid()).child("Type").removeValue();
                                        holder.btnAccept.setText("Send Message");

                                    }
                                    else if(holder.btnAccept.getText().equals("Accept Request")){

                                        frndRefrence.child(user.getUid()).child(id).child("Status").setValue("Friends");
                                        frndRefrence.child(id).child(user.getUid()).child("Status").setValue("Friends");
                                        holder.btnAccept.setText("UnFriend");

                                    }
                                    else if(holder.btnAccept.getText().equals("UnFriend")){

                                        frndRefrence.child(user.getUid()).child(id).child("Status").removeValue();
                                        frndRefrence.child(id).child(user.getUid()).child("Status").removeValue();
                                        holder.btnAccept.setText("Send Message");
                                    }
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                            View view = inflater.inflate(R.layout.item,parent,false);
                            Holder hold = new Holder(view);
                            return hold;
                        }
                    };

                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
