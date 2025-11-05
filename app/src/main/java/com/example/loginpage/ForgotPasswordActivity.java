
package com.example.loginpage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        MyDbHelper dbHelper = new MyDbHelper(this, "login.db", null, 1);
        db = dbHelper.getWritableDatabase();

        TextView backToLogin = findViewById(R.id.back_to_login);
        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
        });
        EditText email = findViewById(R.id.input_email);
        Button resetPassword = findViewById(R.id.button_reset_password);
        resetPassword.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            if(emailText.isEmpty()) {
                Toast.makeText(this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkIfUserExists(emailText)) {
                resetPassword(emailText);
            } else {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void resetPassword(String emailText) {
        if (checkIfUserExists(emailText)){
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra("userEmail", emailText);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfUserExists(String emailText) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE userEmail = ?", new String[]{emailText});
        boolean exists = cursor.getCount()>0;
        cursor.close();

        return exists;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
