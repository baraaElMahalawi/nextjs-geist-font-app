package com.example.hotelroomreservationproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.RoomAdapter;
import com.example.hotelroomreservationproject.DatabaseHelper;
import com.example.hotelroomreservationproject.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity implements RoomAdapter.OnRoomClickListener {

    private RecyclerView rvRooms;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private DatabaseHelper dbHelper;
    private Button btnAddRoom;
    private Button btnBack;
    private TextView tvTotalRooms;
    private TextView tvAvailableRooms;
    private TextView tvBookedRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        initializeViews();
        setupListeners();
        setupRecyclerView();
        loadRooms();
    }

    private void initializeViews() {
        rvRooms = findViewById(R.id.rvRooms);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        btnBack = findViewById(R.id.btnBack);
        tvTotalRooms = findViewById(R.id.tvTotalRooms);
        tvAvailableRooms = findViewById(R.id.tvAvailableRooms);
        tvBookedRooms = findViewById(R.id.tvBookedRooms);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupListeners() {
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RoomListActivity.this, AddRoomActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        roomList = new ArrayList<>();
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        roomAdapter = new RoomAdapter(this, roomList, this);
        rvRooms.setAdapter(roomAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms(); // Refresh the list when returning from EditRoomActivity
    }

    private void loadRooms() {
        roomList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                DatabaseHelper.COLUMN_ROOM_ID,
                DatabaseHelper.COLUMN_ROOM_NUMBER,
                DatabaseHelper.COLUMN_ROOM_TYPE,
                DatabaseHelper.COLUMN_PRICE_PER_NIGHT,
                DatabaseHelper.COLUMN_AVAILABILITY,
                DatabaseHelper.COLUMN_IMAGE,
                DatabaseHelper.COLUMN_LIST_IMAGE,
                DatabaseHelper.COLUMN_ROOM_VIEW,
                DatabaseHelper.COLUMN_POOL_TYPE,
                DatabaseHelper.COLUMN_ROOM_STARS,
                DatabaseHelper.COLUMN_HAS_PARKING,
                DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER,
                DatabaseHelper.COLUMN_HAS_WIFI,
                DatabaseHelper.COLUMN_HAS_COFFEE_MAKER,
                DatabaseHelper.COLUMN_HAS_BAR,
                DatabaseHelper.COLUMN_HAS_BREAKFAST
        };

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ROOMS,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_ROOM_NUMBER + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Room room = new Room();
                    room.setRoomId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_ID)));
                    room.setRoomNumber(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NUMBER)));
                    room.setRoomType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_TYPE)));
                    room.setPricePerNight(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE_PER_NIGHT)));
                    room.setAvailability(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAILABILITY)) == 1);
                    room.setImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE)));
                    room.setListImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_IMAGE)));
                    room.setRoomView(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_VIEW)));
                    room.setPoolType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POOL_TYPE)));
                    room.setRoomStars(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_STARS)));
                    room.setHasParking(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_PARKING)) == 1);
                    room.setHasAirportTransfer(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER)) == 1);
                    room.setHasWifi(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_WIFI)) == 1);
                    room.setHasCoffeeMaker(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_COFFEE_MAKER)) == 1);
                    room.setHasBar(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BAR)) == 1);
                    room.setHasBreakfast(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BREAKFAST)) == 1);

                    roomList.add(room);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading rooms: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        roomAdapter.notifyDataSetChanged();

        // Update room statistics
        updateRoomStatistics();

        if (roomList.isEmpty()) {
            Toast.makeText(this, "No rooms available", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRoomStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get total rooms count
        Cursor totalCursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ROOMS, null);
        int totalRooms = 0;
        if (totalCursor != null && totalCursor.moveToFirst()) {
            totalRooms = totalCursor.getInt(0);
            totalCursor.close();
        }

        // Get available rooms count
        Cursor availableCursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ROOMS +
                        " WHERE " + DatabaseHelper.COLUMN_AVAILABILITY + "=1", null);
        int availableRooms = 0;
        if (availableCursor != null && availableCursor.moveToFirst()) {
            availableRooms = availableCursor.getInt(0);
            availableCursor.close();
        }

        // Calculate booked rooms
        int bookedRooms = totalRooms - availableRooms;

        // Update TextViews
        tvTotalRooms.setText("Total: " + totalRooms);
        tvAvailableRooms.setText("Available: " + availableRooms);
        tvBookedRooms.setText("Booked: " + bookedRooms);
    }

    @Override
    public void onRoomClick(Room room) {
        Intent intent = new Intent(this, EditRoomActivity.class);
        intent.putExtra("roomId", room.getRoomId());
        startActivity(intent);
    }
}