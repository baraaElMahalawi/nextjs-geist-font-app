package com.example.hotelroomreservationproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.RoomAdapter;
import com.example.hotelroomreservationproject.DatabaseHelper;
import com.example.hotelroomreservationproject.Room;

import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private DatabaseHelper dbHelper;

    private Spinner spinnerRoomType;
    private Spinner spinnerStars;
    private Spinner spinnerPoolType;
    private CheckBox checkboxWifi;
    private CheckBox checkboxParking;
    private CheckBox checkboxBreakfast;
    private Button btnMyBookings;
    private TextView tvNoRooms;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // Get username from intent
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupSpinners();
        setupListeners();
        loadRooms();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewRooms);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        spinnerStars = findViewById(R.id.spinnerStars);
        spinnerPoolType = findViewById(R.id.spinnerPoolType);
        checkboxWifi = findViewById(R.id.checkboxWifi);
        checkboxParking = findViewById(R.id.checkboxParking);
        checkboxBreakfast = findViewById(R.id.checkboxBreakfast);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        tvNoRooms = findViewById(R.id.tvNoRooms);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList, room -> {
            Intent intent = new Intent(UserHomeActivity.this, RoomDetailsActivity.class);
            intent.putExtra("roomId", room.getRoomId());
            intent.putExtra("username", username);
            startActivity(intent);
        });
        recyclerView.setAdapter(roomAdapter);
    }

    private void setupSpinners() {
        // Room Type Spinner with "All" option added
        ArrayAdapter<CharSequence> roomTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_type_array, android.R.layout.simple_spinner_item);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(roomTypeAdapter);
        addAllOptionToSpinner(spinnerRoomType, roomTypeAdapter);

        // Stars Spinner with "All" option added
        ArrayAdapter<CharSequence> starsAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_stars_array, android.R.layout.simple_spinner_item);
        starsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStars.setAdapter(starsAdapter);
        addAllOptionToSpinner(spinnerStars, starsAdapter);

        // Pool Type Spinner with "All" option added
        ArrayAdapter<CharSequence> poolTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.pool_type_array, android.R.layout.simple_spinner_item);
        poolTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPoolType.setAdapter(poolTypeAdapter);
        addAllOptionToSpinner(spinnerPoolType, poolTypeAdapter);
    }

    private void addAllOptionToSpinner(Spinner spinner, ArrayAdapter<CharSequence> adapter) {
        // Create a new adapter with "All" option at the top
        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newAdapter.add("All");
        for (int i = 0; i < adapter.getCount(); i++) {
            newAdapter.add(adapter.getItem(i).toString());
        }
        spinner.setAdapter(newAdapter);
    }

    private void setupListeners() {
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, BookingsListActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Add filter change listeners
        android.widget.AdapterView.OnItemSelectedListener filterListener = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadRooms();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        };

        spinnerRoomType.setOnItemSelectedListener(filterListener);
        spinnerStars.setOnItemSelectedListener(filterListener);
        spinnerPoolType.setOnItemSelectedListener(filterListener);

        android.widget.CompoundButton.OnCheckedChangeListener checkListener = (buttonView, isChecked) -> loadRooms();
        checkboxWifi.setOnCheckedChangeListener(checkListener);
        checkboxParking.setOnCheckedChangeListener(checkListener);
        checkboxBreakfast.setOnCheckedChangeListener(checkListener);
    }

    private void loadRooms() {
        roomList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        ArrayList<String> selectionArgs = new ArrayList<>();

        // Base condition for available rooms
        selection.append(DatabaseHelper.COLUMN_AVAILABILITY + "=?");
        selectionArgs.add("1");

        // Add filter conditions
        String selectedRoomType = spinnerRoomType.getSelectedItem().toString();
        if (!selectedRoomType.equals("All")) {
            selection.append(" AND " + DatabaseHelper.COLUMN_ROOM_TYPE + "=?");
            selectionArgs.add(selectedRoomType);
        }

        String selectedStars = spinnerStars.getSelectedItem().toString();
        if (!selectedStars.equals("All")) {
            selection.append(" AND " + DatabaseHelper.COLUMN_ROOM_STARS + "=?");
            selectionArgs.add(selectedStars);
        }

        String selectedPoolType = spinnerPoolType.getSelectedItem().toString();
        if (!selectedPoolType.equals("All")) {
            selection.append(" AND " + DatabaseHelper.COLUMN_POOL_TYPE + "=?");
            selectionArgs.add(selectedPoolType);
        }

        if (checkboxWifi.isChecked()) {
            selection.append(" AND " + DatabaseHelper.COLUMN_HAS_WIFI + "=1");
        }
        if (checkboxParking.isChecked()) {
            selection.append(" AND " + DatabaseHelper.COLUMN_HAS_PARKING + "=1");
        }
        if (checkboxBreakfast.isChecked()) {
            selection.append(" AND " + DatabaseHelper.COLUMN_HAS_BREAKFAST + "=1");
        }

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
                    selection.toString(),
                    selectionArgs.toArray(new String[0]),
                    null, null, null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Room room = new Room(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NUMBER)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_TYPE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE_PER_NIGHT)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAILABILITY)) == 1,
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_IMAGE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_VIEW)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POOL_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_STARS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_PARKING)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_WIFI)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_COFFEE_MAKER)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BAR)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BREAKFAST)) == 1
                    );
                    roomList.add(room);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading rooms: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        updateUI();
    }

    private void updateUI() {
        if (roomList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoRooms.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoRooms.setVisibility(View.GONE);
            roomAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms(); // Refresh rooms list when returning to this activity
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(UserHomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

}
