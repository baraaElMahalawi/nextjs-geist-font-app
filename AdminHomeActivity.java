package com.example.hotelroomreservationproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelroomreservationproject.R;

public class AdminHomeActivity extends AppCompatActivity {

    private Button btnManageRooms, btnEditRooms, btnManageBookings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnManageRooms = findViewById(R.id.btnManageRooms);
        btnEditRooms = findViewById(R.id.btnEditRooms);
        btnManageBookings = findViewById(R.id.btnManageBookings);
    }

    private void setupClickListeners() {
        // Add Room Button
        btnManageRooms.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, AddRoomActivity.class);
            startActivity(intent);
        });

        // Edit Rooms Button
        btnEditRooms.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RoomListActivity.class);
            startActivity(intent);
        });

        // Manage Bookings Button
        btnManageBookings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ManageBookingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog before exiting
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
