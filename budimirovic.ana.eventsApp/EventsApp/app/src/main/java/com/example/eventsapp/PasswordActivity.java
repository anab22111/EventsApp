package com.example.eventsapp;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvChangePassword;
    private EditText etCurrent, etNew;
    private Button btnSave;

    private String currentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        //taking data that was transferred from MyProfileActivity
        Bundle bundle = getIntent().getExtras();    //taking a reference of the bundle that was forwarded

        currentPassword = bundle.getString("password");

        tvChangePassword = findViewById(R.id.tvChangePassword);
        etCurrent = findViewById(R.id.etCurrentPassword);
        etNew = findViewById(R.id.etNewPassword);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnSave){
            String currentPsw = etCurrent.getText().toString();
            String newPsw = etNew.getText().toString();
            if(!currentPsw.isEmpty() && !newPsw.isEmpty()){
                if(currentPsw.equals(currentPassword)){
                    //show that the password was changed successfully
                    Toast.makeText(PasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(PasswordActivity.this, "Incorrect current password!", Toast.LENGTH_SHORT).show();
                    etCurrent.setText("");
                    etNew.setText("");
                }
            }else if((currentPsw.isEmpty() && newPsw.isEmpty()) || currentPsw.isEmpty()){
                Toast.makeText(PasswordActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(PasswordActivity.this, "Enter new password!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}