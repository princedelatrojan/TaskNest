
package com.example.loginpage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Array;

public class Register extends AppCompatActivity {
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        MyDbHelper dbHelper = new MyDbHelper(this, "login.db", null, 1);
        db = dbHelper.getWritableDatabase();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
        });
        Spinner genderSpinner = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            EditText fullName = findViewById(R.id.input_fullName);
            String fullNameText = fullName.getText().toString();

            Spinner gender = findViewById(R.id.gender);
            String genderText = gender.getSelectedItem().toString();

            EditText userEmail = findViewById(R.id.input_userEmail);
            String userEmailText = userEmail.getText().toString();

            EditText mobile = findViewById(R.id.inputMobile);
            String mobileText = mobile.getText().toString();

            EditText address = findViewById(R.id.inputAddress);
            String addressText = address.getText().toString();

            EditText password = findViewById(R.id.input_password);
            String passwordText = password.getText().toString();

            EditText confirmPassword = findViewById(R.id.confirm_password);
            String confirmPasswordText = confirmPassword.getText().toString();

            if(fullNameText.isEmpty() && genderText.isEmpty() && userEmailText.isEmpty() && mobileText.isEmpty() && addressText.isEmpty() && passwordText.isEmpty() && confirmPasswordText.isEmpty()) {
                Toast.makeText(this, "Please Fill in all Fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!passwordText.equals(confirmPasswordText)) {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();
            values.put("fullName", fullNameText);
            values.put("gender", genderText);
            values.put("userEmail", userEmailText);
            values.put("mobile", mobileText);
            values.put("address", addressText);
            values.put("password", passwordText);

            long newRowId = db.insert("users", null, values);

            if (newRowId == -1){
                Toast.makeText(this, "Error inserting data", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User Created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();

        }

    }
}
