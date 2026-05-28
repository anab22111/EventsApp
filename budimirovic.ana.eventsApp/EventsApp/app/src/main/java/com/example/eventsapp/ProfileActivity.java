package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUsername, tvEmail;
    private Button btnPassword, btnEndSession;
    private String password;
    private String username, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get email and username
        Bundle bundle = getIntent().getExtras();

        username = bundle.getString("username");

        // get email
        dbHelper helper = new dbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE username = ?", new String[]{username});

        if(cursor.moveToFirst()){
            email = cursor.getString(0);    // only email column selected, hence getString(0)
        }

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

            // send username to next activity
            Bundle bundle = new Bundle();
            bundle.putString("username", username);

            intent.putExtras(bundle);

            startActivity(intent);
        }
    }
}