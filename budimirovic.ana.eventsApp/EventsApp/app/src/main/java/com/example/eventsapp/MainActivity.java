package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnFirstLogin, btnSecondLogin, btnFirstRegister, btnSecondRegister;
    private EditText etLoginUsername, etLoginPassword, etRegisterUsername, etRegisterPassword, etRegisterEmail;
    private TextView tvUsername, tvEmail, tvPassword;
    private CheckBox cbAdmin;

    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // make database
        dbHelper helper = new dbHelper(this);
        db = helper.getWritableDatabase();

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
        cbAdmin = findViewById(R.id.checkBoxAdmin);

        btnFirstLogin.setOnClickListener(this);
        btnSecondLogin.setOnClickListener(this);
        btnFirstRegister.setOnClickListener(this);
        btnSecondRegister.setOnClickListener(this);


        cbAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // if user is admin then 
        });

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

            if(!username.isEmpty() && !password.isEmpty()){
                // check if user exists
                int userId = checkUserLogin(username, password);

                if(userId != -1){          // if user exists
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    goToNextActivity(username);
                }else{
                    Toast.makeText(this, "Incorrect password or username.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == R.id.btnSecondRegister){
            
            String username = etRegisterUsername.getText().toString();
            String password = etRegisterPassword.getText().toString();
            String email = etRegisterEmail.getText().toString();

            // CHECK EMAIL

            // get hashed password
            String hashedPassword = PasswordHasher.hashPassword(password);


            if(!username.isEmpty() && !password.isEmpty() && !email.isEmpty()){

                // check if email is in valid format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // try to register user,  if not successful db.insert returns -1
                long result = registerUser(username, hashedPassword, email);

                if(result != -1){           // registration successful

                    Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    goToNextActivity(username);
                }else{
                    Toast.makeText(MainActivity.this, "Username or email already exists!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MainActivity.this, "You need to fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long registerUser(String username, String password, String email){

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("users", null, values);

        return result;
    }
    private int checkUserLogin(String username, String password){

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {

            // get position of columns
            int idColumnIndex = cursor.getColumnIndex("id");
            int passwordColumnIndex = cursor.getColumnIndex("password");

            // get the id
            int userId = cursor.getInt(idColumnIndex);

            // get password
            String hashedPasswordFromDb = cursor.getString(passwordColumnIndex);

            // check if the password is correct
            if (PasswordHasher.verifyPassword(password, hashedPasswordFromDb)) {
                cursor.close();             // close cursor
                return userId;             // return id of user
            }
        }

        if (cursor != null) {     // user not found or password incorrect
            cursor.close();
        }

        return -1; // return -1 if user doesn't exist or password is incorrect
    }

    private void goToNextActivity(String username){
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