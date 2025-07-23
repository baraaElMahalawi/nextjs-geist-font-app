package com.example.hotelroomreservationproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.DatabaseHelper;
import com.example.hotelroomreservationproject.Room;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class RoomDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvRoomType, tvPrice, tvRoomNumber, tvDescription;
    private ImageView ivRoomImage;
    private Button btnBook;
    private DatabaseHelper dbHelper;
    private int roomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        tvRoomType = findViewById(R.id.tvRoomType);
        tvPrice = findViewById(R.id.tvPrice);
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvDescription = findViewById(R.id.tvDescription);
        ivRoomImage = findViewById(R.id.ivRoomImage);
        btnBook = findViewById(R.id.btnBook);

        // Accessibility content descriptions
        ivRoomImage.setContentDescription("Room image");

        dbHelper = new DatabaseHelper(this);

        roomId = getIntent().getIntExtra("roomId", -1);
        if (roomId == -1) {
            Toast.makeText(this, "Invalid room", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRoomDetails();

        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetailsActivity.this, BookingActivity.class);
            intent.putExtra("roomId", roomId);
            // Pass username if available
            String username = getIntent().getStringExtra("username");
            if (username != null) {
                intent.putExtra("username", username);
            }
            startActivity(intent);
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRoomDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_ROOM_ID,
                DatabaseHelper.COLUMN_ROOM_NUMBER,
                DatabaseHelper.COLUMN_ROOM_TYPE,
                DatabaseHelper.COLUMN_PRICE_PER_NIGHT,
                DatabaseHelper.COLUMN_AVAILABILITY,
                DatabaseHelper.COLUMN_IMAGE,
                DatabaseHelper.COLUMN_ROOM_VIEW,
                DatabaseHelper.COLUMN_POOL_TYPE,
                DatabaseHelper.COLUMN_HAS_PARKING,
                DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER,
                DatabaseHelper.COLUMN_HAS_WIFI,
                DatabaseHelper.COLUMN_HAS_COFFEE_MAKER,
                DatabaseHelper.COLUMN_HAS_BAR,
                DatabaseHelper.COLUMN_HAS_BREAKFAST
        };
        String selection = DatabaseHelper.COLUMN_ROOM_ID + "=?";
        String[] selectionArgs = {String.valueOf(roomId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_ROOMS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String roomNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NUMBER));
            String roomType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_TYPE));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE_PER_NIGHT));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
            String roomView = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_VIEW));
            String poolType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POOL_TYPE));
            int hasParkingInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_PARKING));
            int hasAirportTransferInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER));
            int hasWifiInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_WIFI));
            int hasCoffeeMakerInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_COFFEE_MAKER));
            int hasBarInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BAR));
            int hasBreakfastInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BREAKFAST));

            tvRoomNumber.setText("Room: " + roomNumber);
            tvRoomType.setText(roomType);
            tvPrice.setText("$" + String.format("%.2f", price) + " / night");
            String description = "Room " + roomNumber + " is a " + roomType + " with all amenities included.";
            tvDescription.setText(description);

            if (image != null && !image.isEmpty()) {
                ivRoomImage.setImageURI(Uri.parse(image));
            } else {
                ivRoomImage.setImageResource(R.drawable.ic_room_placeholder);
            }

            // Set feature texts
            TextView tvRoomView = findViewById(R.id.tvRoomView);
            TextView tvPoolType = findViewById(R.id.tvPoolType);
            TextView tvParking = findViewById(R.id.tvParking);
            TextView tvAirportTransfer = findViewById(R.id.tvAirportTransfer);
            TextView tvWifi = findViewById(R.id.tvWifi);
            TextView tvCoffeeMaker = findViewById(R.id.tvCoffeeMaker);
            TextView tvBar = findViewById(R.id.tvBar);
            TextView tvBreakfast = findViewById(R.id.tvBreakfast);

            tvRoomView.setText(roomView);
            tvPoolType.setText(poolType);
            tvParking.setText(hasParkingInt == 1 ? "Parking" : "Not Available Parking");
            tvAirportTransfer.setText(hasAirportTransferInt == 1 ? "Airport Transfer" : "Not Available Airport Transfer");
            tvWifi.setText(hasWifiInt == 1 ? "Free WiFi" : "Not Available Free WiFi");
            tvCoffeeMaker.setText(hasCoffeeMakerInt == 1 ? "Coffee Maker" : "Not Available Coffee Maker");
            tvBar.setText(hasBarInt == 1 ? "Bar" : "Not Available Bar");
            tvBreakfast.setText(hasBreakfastInt == 1 ? "Breakfast" : "Not Available Breakfast");

            cursor.close();
        } else {
            Toast.makeText(this, "Room not found", Toast.LENGTH_SHORT).show();
        }
    }
}