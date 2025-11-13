// Added Firebase imports
package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// REMOVED: All SQLite imports

public class ForgotPasswordActivity extends AppCompatActivity {

    // --- REMOVED: SQLiteDatabase variable ---

    // --- Firebase Member Variable ---
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // REMOVED: EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // --- Firebase Initialization ---
        mAuth = FirebaseAuth.getInstance();

        // --- REMOVED: dbHelper and db initialization ---

        TextView backToLogin = findViewById(R.id.back_to_login);
        backToLogin.setOnClickListener(v -> {
            finish(); // Just close this activity
        });

        EditText email = findViewById(R.id.input_email);
        Button resetPassword = findViewById(R.id.button_reset_password);

        resetPassword.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            if (TextUtils.isEmpty(emailText)) {
                Toast.makeText(this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- CHANGED: Firebase Password Reset ---
            // This one simple command replaces all your old logic
            mAuth.sendPasswordResetEmail(emailText)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                                finish(); // Close this activity
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }

    // --- REMOVED: resetPassword() and checkIfUserExists() methods ---
    // --- REMOVED: onDestroy() no longer needed for db.close() ---
}