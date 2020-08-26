package com.example.apmah.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apmah.Enter;
import com.example.apmah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetail extends AppCompatActivity {

    private EditText name,status;
    private CircleImageView imageView;
    private Button btn;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Uri uriImage;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Register User Images");
    private ProgressDialog dialog;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        name = findViewById(R.id.userDetail_Name);
        status = findViewById(R.id.userDetail_Status);
        imageView = findViewById(R.id.userDetail_ImageView);
        btn = findViewById(R.id.userDetail_button);

        dialog = new ProgressDialog(UserDetail.this);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Male").child(user.getUid()).child("Status").exists() && auth.getCurrentUser().isEmailVerified()){
                    finish();
                    startActivity(new Intent(UserDetail.this,Enter.class));
                }
                if(snapshot.child("Female").child(user.getUid()).child("Status").exists() && auth.getCurrentUser().isEmailVerified()){
                    finish();
                    startActivity(new Intent(UserDetail.this,Enter.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(UserDetail.this);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setMessage("Updating Data");
                dialog.show();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child("Male").child(user.getUid()).exists()){
                            setUserdata("Male");
                            dialog.dismiss();

                        }
                        if(snapshot.child("Female").child(user.getUid()).exists()){
                            setUserdata("Female");
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                uriImage = result.getUri();
                imageView.setImageURI(uriImage);
                setUserImage(uriImage);

            }
            if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setUserdata(String getGender){
        reference.child(getGender).child(user.getUid()).child("Name").setValue(name.getText().toString());
        reference.child(getGender).child(user.getUid()).child("Status").setValue(status.getText().toString());
        finish();
        startActivity(new Intent(UserDetail.this, Enter.class));
    }

    public void setUserImage(Uri userProImage){

        storageReference.child(user.getUid()).putFile(userProImage);

    }
}
