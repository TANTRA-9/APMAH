package com.example.apmah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apmah.Register.Register;
import com.example.apmah.Register.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button btn,register;
    private FirebaseAuth firebaseAuth;
    private EditText name,pass;
    private FirebaseUser user;
    private ProgressDialog dialog;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        btn = findViewById(R.id.Main_LoginButton);
        name = findViewById(R.id.Login_Name);
        pass = findViewById(R.id.Login_Pass);
        register = findViewById(R.id.Main_RegisterButton);
        user = firebaseAuth.getCurrentUser();
        dialog = new ProgressDialog(MainActivity.this);

        if(user!=null){
            finish();
            startActivity(new Intent(MainActivity.this,Enter.class));
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Authentication");
                dialog.show();
                if(name.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Fill The Above Fields", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(name.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                dialog.dismiss();
                                finish();
                                startActivity(new Intent(MainActivity.this, Enter.class));
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Log In Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

}
