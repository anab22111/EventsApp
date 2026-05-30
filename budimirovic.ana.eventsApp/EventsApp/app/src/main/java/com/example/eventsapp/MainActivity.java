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
    private HttpHelper httpHelper;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // make database
        dbHelper helper = new dbHelper(this);
        db = helper.getWritableDatabase();

        httpHelper = new HttpHelper();

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
                try {
                    checkUserLogin(username, password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }else{
                Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == R.id.btnSecondRegister){
            
            String username = etRegisterUsername.getText().toString();
            String password = etRegisterPassword.getText().toString();
            String email = etRegisterEmail.getText().toString();

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
                    data.put("password", password);
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
                        String url = "http://192.168.0.7:3000/users";    // url for users table - computer ip address:port/table
                        //String url = "http://10.0.2.2:3000/password";

                        JSONObject serverResponse = null;
                        String errorText = null;

                        // try to register user on server
                        try {
                            serverResponse = httpHelper.postJSONObjectFromURL(url, data, "POST");
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

                                    if(statusCode == 200){                  // user is registered successfully

                                        String hashPassword = PasswordHasher.hashPassword(password);

                                        // get id from servers response
                                        String serverId = response.optString("_id", "");

                                        long result = registerUser(username, hashPassword, email, serverId);

                                        if (result != -1) {
                                            Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            goToNextActivity(username, isAdmin);
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
    private void checkUserLogin(String username, String password) throws JSONException {

        // make json for server
        JSONObject loginData = new JSONObject();
        loginData.put("username", username);
        loginData.put("password", password);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // url for users login /login
                String url = "http://192.168.0.7:3000/login";
//                String url = "http://10.0.2.2:3000/password";

                JSONObject serverResponse = null;
                String errorText = null;

                // check if user exists and if password is correct
                try {
                    serverResponse = httpHelper.postJSONObjectFromURL(url, loginData, "POST");
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // check if there was an error with the server
                        if (finalErrorText != null) {
                            Toast.makeText(MainActivity.this, finalErrorText, Toast.LENGTH_LONG).show();
                            return;        // print error and exit
                        }

                        if(response != null){         // if there is a response form server
                            // get status code
                            int statusCode = response.optInt("http_status_code",0);
                            if(statusCode == 200){          // valid username and password
                                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                // get user object from response
                                JSONObject userObj = response.optJSONObject("user");
                                boolean isAdminServer = false;

                                if (userObj != null) {
//                                    String serverId = userObj.optString("_id", "");
//                                    String email = userObj.optString("email", "");
                                    isAdminServer = userObj.optBoolean("isAdmin", false);
                                }

                                goToNextActivity(username, isAdminServer);     // go to next Activity

                            }else if(statusCode == 401){
                                Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainActivity.this, "Login failed. Status: " + statusCode, Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "Unknown error from server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread.start();   // start thread
    }

    private void goToNextActivity(String username, boolean isAdmin){
        //intent for next activity
        Intent intent = new Intent(MainActivity.this,
                EventsActivity.class);

        //bundle to transfer data
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putBoolean("isAdmin", isAdmin);

        //connecting bundle to intent
        intent.putExtras(bundle);

        //go to next activity
        startActivity(intent);
    }

}