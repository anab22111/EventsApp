package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUsername, tvEmail;
    private Button btnPassword, btnEndSession;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get email and username
        Bundle bundle = getIntent().getExtras();

        String username = bundle.getString("username");
        String email = bundle.getString("email");
        password = bundle.getString("password");


        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnEndSession = findViewById(R.id.btnEndSession);
        btnPassword = findViewById(R.id.btnPassword);

        tvUsername.setText(username);
        tvEmail.setText(email);

        btnPassword.setOnClickListener(this);
        btnEndSession.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnEndSession){    //go to MainActivity
            Intent intent = new Intent(ProfileActivity.this,
                    MainActivity.class);

            // start new task and delete previous activities from history
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        }else if(view.getId() == R.id.btnPassword){   //go to PasswordActivity
            Intent intent = new Intent(ProfileActivity.this,
                    PasswordActivity.class);

            // send password to next activity
            Bundle bundle = new Bundle();
            bundle.putString("password", password);

            intent.putExtras(bundle);

            startActivity(intent);
        }
    }
}