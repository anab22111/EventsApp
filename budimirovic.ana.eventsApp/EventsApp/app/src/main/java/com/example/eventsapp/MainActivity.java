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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnFirstLogin, btnSecondLogin, btnFirstRegister, btnSecondRegister;
    private EditText etLoginUsername, etLoginPassword, etRegisterUsername, etRegisterPassword, etRegisterEmail;
    private TextView tvUsername, tvEmail, tvPassword;
    private CheckBox cbAdmin;

    private SQLiteDatabase db;

    private boolean isAdmin = false;

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
            if(isChecked){
                isAdmin = true;
            }else{
                isAdmin = false;
            }
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
            cbAdmin.setVisibility(View.VISIBLE);
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

            String hashedPassword = PasswordHasher.hashPassword(password);

            if(!username.isEmpty() && !password.isEmpty() && !email.isEmpty()){

                // check if email is in valid format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if email is in valid format try to register user
                // create JSON to send
                JSONObject data  = new JSONObject();
                try {
                    data.put("username", username);
                    data.put("password", hashedPassword);
                    data.put("email", email);
                    data.put("isAdmin", Boolean.valueOf(isAdmin));
                } catch (JSONException e) {
                    Toast.makeText(this, "Error packing data", Toast.LENGTH_SHORT).show();
                    return;
                }

                // make new thread to send message to server
                Thread thread = new Thread(new Runnable() {
                    @Override public void run() {
                        // code to run in background thread
                        HttpHelper httpHelper = new HttpHelper();
                        String url = "http://192.168.0.7:3000/users";    // url for users table - computer ip address:port/table

                        JSONObject serverResponse = null;
                        String errorText = null;

                        // try to register user on server
                        try {
                            serverResponse = httpHelper.postJSONObjectFromURL(url, data);
                        } catch (IOException e) {
                            e.printStackTrace();    // if there is no internet or server is off
                            errorText = "Server unreachable.";
                        } catch (JSONException e) {
                            e.printStackTrace();      // if server returns something that's not JSON
                            errorText = "Server didn't return JSON.";
                        }catch (Exception e) {
                            e.printStackTrace();
                            errorText = "Error " + e.getMessage();
                        }

                        final JSONObject response = serverResponse;
                        final String finalErrorText = errorText;
                        // UI thread - what needs to be done when thread finishes
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalErrorText != null) {
                                    Toast.makeText(MainActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // server is alive and sent a response
                                if(response != null){
                                    // get status code
                                    int statusCode = response.optInt("http_status_code",0);

                                    if(statusCode == 200){
                                        // user is registered successfully
                                        String hashedPassword = PasswordHasher.hashPassword(password);

                                        // get id from servers response
                                        String serverId = response.optString("_id", "");

                                        long result = registerUser(username, hashedPassword, email, serverId);

                                        if (result != -1) {
                                            Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            goToNextActivity(username);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Error when saving to local database.", Toast.LENGTH_SHORT).show();
                                        }
                                    }else if(statusCode == 409){
                                        Toast.makeText(MainActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "Error while registering.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(MainActivity.this, "Unknown error from server.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                thread.start();
            }else{
                Toast.makeText(MainActivity.this, "You need to fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long registerUser(String username, String password, String email, String serverId){

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("server_id", serverId);
        if(isAdmin) values.put("admin", 1);  // 1 is true, 0 is false


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