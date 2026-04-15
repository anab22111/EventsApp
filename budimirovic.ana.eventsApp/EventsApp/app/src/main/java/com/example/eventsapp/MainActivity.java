package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnFirstLogin, btnSecondLogin, btnFirstRegister, btnSecondRegister;
    private EditText etLoginUsername, etLoginPassword, etRegisterUsername, etRegisterPassword, etRegisterEmail;
    private TextView tvUsername, tvEmail, tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFirstLogin = findViewById(R.id.btnFirstLogin);
        btnSecondLogin = findViewById(R.id.btnSecondLogin);
        btnFirstRegister = findViewById(R.id.btnFirstRegister);
        btnSecondRegister = findViewById(R.id.btnSecondRegister);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        tvUsername = findViewById(R.id.textViewUsername);
        tvPassword = findViewById(R.id.textViewPassword);
        tvEmail = findViewById(R.id.textViewEmail);

        btnFirstLogin.setOnClickListener(this);
        btnSecondLogin.setOnClickListener(this);
        btnFirstRegister.setOnClickListener(this);
        btnSecondRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnFirstLogin){
            btnFirstRegister.setVisibility(View.GONE);
            btnFirstLogin.setVisibility(View.GONE);
            etLoginUsername.setVisibility(View.VISIBLE);
            etLoginPassword.setVisibility(View.VISIBLE);
            btnSecondLogin.setVisibility(View.VISIBLE);
        }

        else if(view.getId() == R.id.btnFirstRegister){
            btnFirstRegister.setVisibility(View.GONE);
            btnFirstLogin.setVisibility(View.GONE);
            btnSecondRegister.setVisibility(View.VISIBLE);
            etRegisterEmail.setVisibility(View.VISIBLE);
            etRegisterPassword.setVisibility(View.VISIBLE);
            etRegisterUsername.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);
        }
        else if(view.getId() == R.id.btnSecondLogin){
            String username = etLoginUsername.getText().toString();
            String password = etLoginPassword.getText().toString();

            if(username.equals("admin") && password.equals("admin")){
                //intent for next activity
                Intent intent = new Intent(MainActivity.this,
                        EventsActivity.class);

                //bundle to transfer data
                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                //connecting bundle to intent
                intent.putExtras(bundle);

                //go to next activity
                startActivity(intent);
            }
        }
        else if(view.getId() == R.id.btnSecondRegister){
            String username = etRegisterUsername.getText().toString();
            String password = etRegisterPassword.getText().toString();
            String email = etRegisterEmail.getText().toString();

            if(username.equals("admin") && password.equals("admin") && email.equals("admin@gmail.com")){
                Intent intent1 = new Intent(MainActivity.this,
                        EventsActivity.class);

                //create bundle to transfer data
                Bundle bundle1 = new Bundle();
                bundle1.putString("username", username);
                bundle1.putString("email", email);

                //connecting bundle to intent
                intent1.putExtras(bundle1);

                //go to next activity
                startActivity(intent1);
            }
        }
    }
}