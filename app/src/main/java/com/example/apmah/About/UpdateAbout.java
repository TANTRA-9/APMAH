package com.example.apmah.About;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.apmah.R;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAbout extends AppCompatActivity {

    TextInputLayout name,status;
    CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_about);

        name = findViewById(R.id.UpdateAbout_Name);
        status = findViewById(R.id.UpdateAbout_Status);
        imageView = findViewById(R.id.UpdateAbout_profile);

    }
}
