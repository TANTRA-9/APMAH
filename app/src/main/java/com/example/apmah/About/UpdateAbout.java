package com.example.apmah.About;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apmah.Data.UserData;
import com.example.apmah.Enter;
import com.example.apmah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAbout extends AppCompatActivity {

    EditText name,status;
    CircleImageView imageView;
    Button btn;
    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Register User Images");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_about);

        name = findViewById(R.id.UpdateAbout_Name);
        status = findViewById(R.id.UpdateAbout_Status);
        imageView = findViewById(R.id.UpdateAbout_profile);
        btn = findViewById(R.id.UpdateAbout_Save);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(UpdateAbout.this);
            }
        });

        storageReference.child(User.getUid()).getBytes(5024*5024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String getName = name.getText().toString();
                final String getStatus = status.getText().toString();

                if(getName.isEmpty() || getStatus.isEmpty()){
                    Toast.makeText(UpdateAbout.this, "Upper field is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("Male").child(User.getUid()).exists()){
                                databaseReference.child("Male").child(User.getUid()).child("Name").setValue(getName);
                                databaseReference.child("Male").child(User.getUid()).child("Status").setValue(getStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                        startActivity(new Intent(UpdateAbout.this, Enter.class));
                                        Toast.makeText(UpdateAbout.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if(snapshot.child("Female").child(User.getUid()).exists()){
                                databaseReference.child("Female").child(User.getUid()).child("Name").setValue(getName);
                                databaseReference.child("Female").child(User.getUid()).child("Status").setValue(getStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                        startActivity(new Intent(UpdateAbout.this, Enter.class));
                                        Toast.makeText(UpdateAbout.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                uri = activityResult.getUri();
                imageView.setImageURI(uri);
                storageReference.child(User.getUid()).putFile(uri);
            }
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = activityResult.getError();
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
