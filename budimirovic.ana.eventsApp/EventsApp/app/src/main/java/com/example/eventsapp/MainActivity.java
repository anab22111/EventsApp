package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button firstLogin, secondLogin, firstRegister, secondRegister;
    private EditText loginUsername, loginPassword;
    private EditText registerUsername, registerPassword, registerEmail;
    private TextView tvUsername, tvEmail, tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstLogin = findViewById(R.id.firstLogin);
        secondLogin = findViewById(R.id.secondLogin);
        firstRegister = findViewById(R.id.firstRegister);
        secondRegister = findViewById(R.id.secondRegister);
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        registerEmail = findViewById(R.id.registerEmail);
        tvUsername = findViewById(R.id.textViewUsername);
        tvPassword = findViewById(R.id.textViewPassword);
        tvEmail = findViewById(R.id.textViewEmail);

        firstLogin.setOnClickListener(this);
        secondLogin.setOnClickListener(this);
        firstRegister.setOnClickListener(this);
        secondRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.firstLogin){
            firstRegister.setVisibility(View.GONE);
            firstLogin.setVisibility(View.GONE);
            loginUsername.setVisibility(View.VISIBLE);
            loginPassword.setVisibility(View.VISIBLE);
            secondLogin.setVisibility(View.VISIBLE);
        }

        else if(view.getId() == R.id.firstRegister){
            firstRegister.setVisibility(View.GONE);
            firstLogin.setVisibility(View.GONE);
            secondRegister.setVisibility(View.VISIBLE);
            registerEmail.setVisibility(View.VISIBLE);
            registerPassword.setVisibility(View.VISIBLE);
            registerUsername.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);
        }
        else if(view.getId() == R.id.secondLogin){
            String username = loginUsername.getText().toString();
            String password = loginPassword.getText().toString();

            if(username.equals("admin") && password.equals("admin")){
                //intent za naredni activity
                Intent intent = new Intent(MainActivity.this,
                        EventsActivity.class);

                //bundle za prenos podataka
                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                //povezivanje bundle sa intentom
                intent.putExtras(bundle);

                //prelazak na sledeci activity
                startActivity(intent);
            }
        }
        else if(view.getId() == R.id.secondRegister){
            String username = registerUsername.getText().toString();
            String password = registerPassword.getText().toString();
            String email = registerEmail.getText().toString();

            if(username.equals("admin") && password.equals("admin") && email.equals("admin@gmail.com")){
                //prelazak na sledeci activity
                Intent intent = new Intent(MainActivity.this,
                        EventsActivity.class);

                //bundle za prenos podataka
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("email", email);

                intent.putExtras(bundle);

                startActivity(intent);
            }
        }
    }
}