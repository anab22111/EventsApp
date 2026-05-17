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

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvChangePassword;
    private EditText etCurrent, etNew;
    private Button btnSave;
    private String username, newPassword;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        dbHelper helper = new dbHelper(this);
        db = helper.getWritableDatabase();

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

                // check if password is correct
                int userId = checkCurrentPassword(username, currentPsw);

                if(userId != -1){   // if password is correct, change password in database

                    String newHashedPassword = PasswordHasher.hashPassword(newPassword);

                    boolean update = updatePassword(newHashedPassword, userId);

                    if(update){
                        Toast.makeText(PasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(PasswordActivity.this, "Error updating password!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(PasswordActivity.this, "Incorrect current password!", Toast.LENGTH_SHORT).show();
                    etCurrent.setText("");
                    etNew.setText("");
                }
            }else if((currentPsw.isEmpty() && newPassword.isEmpty()) || currentPsw.isEmpty()){
                Toast.makeText(PasswordActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(PasswordActivity.this, "Enter new password!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private int checkCurrentPassword(String username, String currentPsw){
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
            if (PasswordHasher.verifyPassword(currentPsw, hashedPasswordFromDb)) {
                cursor.close();             // close cursor
                return userId;             // return id of user
            }
        }

        if (cursor != null) {     // user not found or password incorrect
            cursor.close();
        }

        return -1; // return -1 if user doesn't exist or password is incorrect
    }

    private boolean updatePassword(String hashedNewPassword, int id){

        ContentValues values = new ContentValues();
        values.put("password", hashedNewPassword);

        // db.update return how many rows have been modified
        int rowsAffected = db.update("users", values, "id = ?", new String[]{String.valueOf(id)});

        return rowsAffected > 0;

    }

}