package com.example.hotelroomreservationproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.ManageBookingAdapter;
import com.example.hotelroomreservationproject.DatabaseHelper;
import com.example.hotelroomreservationproject.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManageBookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private DatabaseHelper dbHelper;
    private TextView tvNoBookings;
    private Button btnBack;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_list);

        // Get username from intent
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupListeners();
        loadBookings();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        bookingAdapter = new ManageBookingAdapter(this, bookingList);
        recyclerView.setAdapter(bookingAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBookings() {
        bookingList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // First get the user ID for the current username
        String[] userColumns = {DatabaseHelper.COLUMN_USER_ID};
        String userSelection = DatabaseHelper.COLUMN_USERNAME + "=?";
        String[] userSelectionArgs = {username};

        try {
            Cursor userCursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    userColumns,
                    userSelection,
                    userSelectionArgs,
                    null, null, null
            );

            if (userCursor != null && userCursor.moveToFirst()) {
                int userId = userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
                userCursor.close();

                // Now get bookings for this user
                String[] columns = {
                        DatabaseHelper.COLUMN_BOOKING_ID,
                        DatabaseHelper.COLUMN_BOOKING_USER_ID,
                        DatabaseHelper.COLUMN_BOOKING_ROOM_ID,
                        DatabaseHelper.COLUMN_CHECKIN_DATE,
                        DatabaseHelper.COLUMN_CHECKOUT_DATE,
                        DatabaseHelper.COLUMN_GUESTS,
                        DatabaseHelper.COLUMN_STATUS
                };

                String selection = DatabaseHelper.COLUMN_BOOKING_USER_ID + "=?";
                String[] selectionArgs = {String.valueOf(userId)};

                Cursor cursor = db.query(
                        DatabaseHelper.TABLE_BOOKINGS,
                        columns,
                        selection,
                        selectionArgs,
                        null, null,
                        DatabaseHelper.COLUMN_BOOKING_ID + " DESC" // Show newest bookings first
                );

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Booking booking = new Booking(
                                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_USER_ID)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ROOM_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHECKIN_DATE)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHECKOUT_DATE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GUESTS)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS))
                        );
                        bookingList.add(booking);
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        updateUI();
    }

    private void updateUI() {
        if (bookingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoBookings.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoBookings.setVisibility(View.GONE);
            bookingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings(); // Refresh bookings list when returning to this activity
    }
}