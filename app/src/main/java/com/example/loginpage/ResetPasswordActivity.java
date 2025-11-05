
package com.example.loginpage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResetPasswordActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        MyDbHelper dbHelper = new MyDbHelper(this, "login.db", null, 1);
        db = dbHelper.getWritableDatabase();

        EditText newPassword = findViewById(R.id.input_new_password);

        EditText confirmNewPassword = findViewById(R.id.input_confirm_new_password);

        String emailText = getIntent().getStringExtra("userEmail");


        Button resetPassword = findViewById(R.id.button_reset_password);
        resetPassword.setOnClickListener(v -> {
            String nPassword = newPassword.getText().toString();
            String cPassword = confirmNewPassword.getText().toString();

            if(nPassword.isEmpty() && cPassword.isEmpty()) {
                Toast.makeText(this, "Please Enter and confirm your Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!nPassword.equals(cPassword)) {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if(updatePasswordInDatabase(emailText, nPassword)) {
                Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "Error Updating Password", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean updatePasswordInDatabase(String emailText, String nPassword) {
        if (db == null ) {
            Log.e("Database Error", "Database is null");
        }

        ContentValues values = new ContentValues();
        values.put("password", nPassword);

        int rowsAffected = db.update(
                "users",
                values,
                "userEmail = ?",
                new String[]{emailText});
        return rowsAffected>0;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }

    }
}
