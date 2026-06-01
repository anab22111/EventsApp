package com.example.eventsapp;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
public class PasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvChangePassword;
    private EditText etCurrent, etNew;
    private Button btnSave;
    private String username, newPassword;
    private SQLiteDatabase db;
    private dbHelper helper;
    private HttpHelper httpHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        helper = new dbHelper(this);
        db = helper.getWritableDatabase();

        httpHelper= new HttpHelper();

        //taking data that was transferred from MyProfileActivity
        Bundle bundle = getIntent().getExtras();    //taking a reference of the bundle that was forwarded

        username = bundle.getString("username");

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
            newPassword = etNew.getText().toString();

            if(!currentPsw.isEmpty() && !newPassword.isEmpty()){  // if all fields are filled

                // make JSON to send request to server
                JSONObject passwordData = new JSONObject();
                try {
                    passwordData.put("username", username);
                    passwordData.put("oldPassword", currentPsw);
                    passwordData.put("newPassword", newPassword);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // make thread to send request to server
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // make url for /password path
                        String url = "http://10.194.239.97:3000/password";
                        //String url = "http://10.0.2.2:3000/password";
                        JSONObject serverResponse = null;
                        String errorText = null;

                        try{            // try to change password - server
                            serverResponse = httpHelper.postJSONObjectFromURL(url, passwordData, "PUT");
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
                                // check if there was an error with server
                                if(finalErrorText != null){
                                    Toast.makeText(PasswordActivity.this, finalErrorText, Toast.LENGTH_SHORT).show();
                                    return;     // print error and exit
                                }

                                // if server returned a non empty response
                                if (response != null) {
                                    int response_code = response.optInt("http_status_code",0);

                                    // get status code to check if PUT request was successful
                                    switch(response_code) {

                                        case 200:   // password changed successfully
                                            Toast.makeText(PasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();

                                            // hash new password
                                            String hashedNewPassword = PasswordHasher.hashPassword(newPassword);
                                            ContentValues cv = new ContentValues();
                                            cv.put("password",hashedNewPassword);

                                            // update local database with new user password
                                            db.update("users",cv, "username = ?", new String[]{username});

                                            finish();    // close activity
                                            break;

                                        case 401:
                                            Toast.makeText(PasswordActivity.this, "Invalid current password.", Toast.LENGTH_SHORT).show();
                                            etCurrent.setText("");
                                            etNew.setText("");
                                            break;

                                        default:
                                            Toast.makeText(PasswordActivity.this, "Error while changing password. Status: " + response_code, Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(PasswordActivity.this, "Unknown error from server", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                thread.start();
            }else if((currentPsw.isEmpty() && newPassword.isEmpty()) || currentPsw.isEmpty()){
                Toast.makeText(PasswordActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(PasswordActivity.this, "Enter new password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private boolean updatePassword(String hashedNewPassword, int id){
//
//        ContentValues values = new ContentValues();
//        values.put("password", hashedNewPassword);
//
//        // db.update returns how many rows have been modified
//        int rowsAffected = db.update("users", values, "id = ?", new String[]{String.valueOf(id)});
//
//        return rowsAffected > 0;
//
//    }

}