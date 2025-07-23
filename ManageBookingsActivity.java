package com.example.hotelroomreservationproject;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
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

public class ManageBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManageBookingAdapter manageBookingAdapter;
    private List<Booking> bookingList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewManageBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        bookingList = new ArrayList<>();

        loadBookings();

        manageBookingAdapter = new ManageBookingAdapter(this, bookingList);
        recyclerView.setAdapter(manageBookingAdapter);
    }

    private void loadBookings() {
        bookingList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_BOOKING_ID,
                DatabaseHelper.COLUMN_BOOKING_USER_ID,
                DatabaseHelper.COLUMN_BOOKING_ROOM_ID,
                DatabaseHelper.COLUMN_CHECKIN_DATE,
                DatabaseHelper.COLUMN_CHECKOUT_DATE,
                DatabaseHelper.COLUMN_GUESTS,
                DatabaseHelper.COLUMN_STATUS
        };
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, columns, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_USER_ID));
                int roomId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ROOM_ID));
                String checkinDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHECKIN_DATE));
                String checkoutDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHECKOUT_DATE));
                int guests = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GUESTS));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));

                Booking booking = new Booking(bookingId, userId, roomId, checkinDate, checkoutDate, guests, status);
                bookingList.add(booking);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }
}