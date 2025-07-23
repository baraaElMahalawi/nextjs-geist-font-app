package com.example.hotelroomreservationproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // Reset errors
        etUsername.setError(null);
        etPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(username, password)) {
            return;
        }

        // Attempt login
        authenticateUser(username, password);
    }

    private boolean validateInputs(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void authenticateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_ROLE};
        String selection = DatabaseHelper.COLUMN_USERNAME + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE));
                cursor.close();

                if ("ADMIN".equalsIgnoreCase(role)) {
                    // Launch admin home activity
                    Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Launch user home activity
                    Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
            } else {
                showLoginError();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error during login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoginError() {
        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        etPassword.setText("");
        etPassword.requestFocus();
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog before exiting
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity(); // Close all activities
                })
                .setNegativeButton("No", null)
                .show();
    }
}
