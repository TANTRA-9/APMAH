package com.example.apmah.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.apmah.Enter;
import com.example.apmah.MainActivity;
import com.example.apmah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText mail,pass;
    private Button button;
    private ProgressDialog dialog;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
    private FirebaseUser user;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.Register_Email);
        pass = findViewById(R.id.Register_Pass);
        button = findViewById(R.id.Register_button);
        radioGroup = findViewById(R.id.Register_RadioGroup);
        dialog = new ProgressDialog(Register.this);

        toolbar = findViewById(R.id.Register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);

                dialog.setMessage("Sign You Up!");
                dialog.show();

                if(pass.getText().toString().isEmpty() || mail.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Fill The Above Fields", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else{
                    auth.createUserWithEmailAndPassword(mail.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user = FirebaseAuth.getInstance().getCurrentUser();

                                        class Data{
                                            public String Gmail;
                                            public String Pass;

                                            public Data( String gmail, String pass) {

                                                Gmail = gmail;
                                                Pass = pass;
                                            }
                                        }

                                        Data data = new Data(mail.getText().toString(),pass.getText().toString());
                                        reference.child(radioButton.getText().toString()).child(user.getUid()).setValue(data);

                                        dialog.dismiss();
                                        Toast.makeText(Register.this, "Email Verification Sent", Toast.LENGTH_SHORT).show();

                                        finish();
                                        startActivity(new Intent(Register.this, MainActivity.class));
                                    }
                                });
                            }
                            else{
                                Toast.makeText(Register.this, "Sign Up Failed!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}
