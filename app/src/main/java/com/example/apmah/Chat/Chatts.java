package com.example.apmah.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apmah.Data.UserData;
import com.example.apmah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chatts extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Register User Images");
    DatabaseReference chatsMessage = FirebaseDatabase.getInstance().getReference().child("Chats");
    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    TextView name,status;
    CircleImageView imageView;
    EditText editText;
    ImageButton send;
    private final List<MessageClass> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatts);

        name = findViewById(R.id.chatts_NameText);
        status = findViewById(R.id.chatts_StatusText);
        imageView = findViewById(R.id.chatts_ProfilePic);
        editText = findViewById(R.id.chatts_EditText);
        send = findViewById(R.id.chatts_SendMessage);

        toolbar = findViewById(R.id.chatts_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView = findViewById(R.id.chatts_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Chatts.this));

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);


        Intent intent = getIntent();
        final String check = intent.getStringExtra("Nishantcheck");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("Female").child(check).exists()){
                    name.setText(snapshot.child("Female").child(check).child("Name").getValue().toString());
                    status.setText(snapshot.child("Female").child(check).child("Status").getValue().toString());
                }
                else if(snapshot.child("Male").child(check).exists()){
                    name.setText(snapshot.child("Male").child(check).child("Name").getValue().toString());
                    status.setText(snapshot.child("Male").child(check).child("Status").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

        storageReference.child(check).getBytes(5024*5024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getMessage = editText.getText().toString();
                if(getMessage.isEmpty()) {
                    Toast.makeText(Chatts.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
                else{
                    final DatabaseReference getPushId = FirebaseDatabase.getInstance().getReference().child("Users").push();
                    final String pushKey = getPushId.getKey();

                    Map messageTextBody = new HashMap();
                    messageTextBody.put("message",getMessage);
                    messageTextBody.put("type","text");
                    messageTextBody.put("from",User.getUid());

                    Map messageBodyDetails = new HashMap();
                    messageBodyDetails.put(User.getUid()+"/"+check+"/"+pushKey,messageTextBody);
                    messageBodyDetails.put(check+"/"+User.getUid()+"/"+pushKey,messageTextBody);

                    chatsMessage.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            editText.setText("");
                        }
                    });
                }
            }
        });

        chatsMessage.child(User.getUid()).child(check).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageClass messageClass = snapshot.getValue(MessageClass.class);
                messageList.add(messageClass);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
