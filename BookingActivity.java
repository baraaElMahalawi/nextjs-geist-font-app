package com.example.hotelroomreservationproject;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private Button btnCheckinDate, btnCheckoutDate;
    private TextView tvCheckinDate, tvCheckoutDate;
    private NumberPicker npGuests;
    private Button btnConfirmBooking;

    private DatabaseHelper dbHelper;
    private int roomId;
    private Calendar checkinDate, checkoutDate;
    private SimpleDateFormat dateFormat;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViews();
        setupDatePicker();
        setupConfirmButton();


        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

    }

    private void initializeViews() {
        btnCheckinDate = findViewById(R.id.btnCheckinDate);
        btnCheckoutDate = findViewById(R.id.btnCheckoutDate);
        tvCheckinDate = findViewById(R.id.tvCheckinDate);
        tvCheckoutDate = findViewById(R.id.tvCheckoutDate);
        npGuests = findViewById(R.id.npGuests);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);



        dbHelper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        checkinDate = Calendar.getInstance();
        checkoutDate = Calendar.getInstance();

        roomId = getIntent().getIntExtra("roomId", -1);
        username = getIntent().getStringExtra("username");

        if (roomId == -1 || username == null) {
            Toast.makeText(this, "Invalid room or user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        npGuests.setMinValue(1);
        npGuests.setMaxValue(10);
    }

    private void setupDatePicker() {
        btnCheckinDate.setOnClickListener(v -> showCheckinDatePicker());
        btnCheckoutDate.setOnClickListener(v -> showCheckoutDatePicker());
    }

    private void showCheckinDatePicker() {
        Calendar minDate = Calendar.getInstance();
        DatePickerDialog checkinDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    checkinDate.set(year, month, dayOfMonth);
                    tvCheckinDate.setText("Check-in: " + dateFormat.format(checkinDate.getTime()));
                    // If checkout date is before checkin, reset checkout date
                    if (checkoutDate.before(checkinDate)) {
                        checkoutDate = (Calendar) checkinDate.clone();
                        checkoutDate.add(Calendar.DAY_OF_MONTH, 1);
                        tvCheckoutDate.setText("Check-out: Not selected");
                    }
                },
                checkinDate.get(Calendar.YEAR),
                checkinDate.get(Calendar.MONTH),
                checkinDate.get(Calendar.DAY_OF_MONTH));

        checkinDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        checkinDialog.show();
    }

    private void showCheckoutDatePicker() {
        Calendar minDate = (Calendar) checkinDate.clone();
        minDate.add(Calendar.DAY_OF_MONTH, 1);

        DatePickerDialog checkoutDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    checkoutDate.set(year, month, dayOfMonth);
                    tvCheckoutDate.setText("Check-out: " + dateFormat.format(checkoutDate.getTime()));
                },
                checkoutDate.get(Calendar.YEAR),
                checkoutDate.get(Calendar.MONTH),
                checkoutDate.get(Calendar.DAY_OF_MONTH));

        checkoutDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        checkoutDialog.show();
    }

    private void setupConfirmButton() {
        btnConfirmBooking.setOnClickListener(v -> {
            if (checkinDate == null || checkoutDate == null) {
                Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkoutDate.after(checkinDate)) {
                Toast.makeText(this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
                return;
            }

            saveBooking(
                    dateFormat.format(checkinDate.getTime()),
                    dateFormat.format(checkoutDate.getTime()),
                    npGuests.getValue()
            );
        });
    }

    private void saveBooking(String checkinDate, String checkoutDate, int guests) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_BOOKING_USER_ID, username);
        values.put(DatabaseHelper.COLUMN_BOOKING_ROOM_ID, roomId);
        values.put(DatabaseHelper.COLUMN_CHECKIN_DATE, checkinDate);
        values.put(DatabaseHelper.COLUMN_CHECKOUT_DATE, checkoutDate);
        values.put(DatabaseHelper.COLUMN_GUESTS, guests);
        values.put(DatabaseHelper.COLUMN_STATUS, "CONFIRMED");

        long newRowId = db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Booking failed", Toast.LENGTH_SHORT).show();
        } else {
            // Update room availability
            ContentValues roomValues = new ContentValues();
            roomValues.put(DatabaseHelper.COLUMN_AVAILABILITY, 0); // Set room as unavailable
            db.update(DatabaseHelper.TABLE_ROOMS, roomValues,
                    DatabaseHelper.COLUMN_ROOM_ID + "=?",
                    new String[]{String.valueOf(roomId)});

            Toast.makeText(this, "Booking successful", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

