package com.example.hotelroomreservationproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.DatabaseHelper;

public class EditRoomActivity extends AppCompatActivity {

    private EditText etRoomNumber, etPrice, etRoomView;
    private Spinner spRoomType, spPoolType, spRoomStars;
    private CheckBox cbParking, cbAirportTransfer, cbWifi, cbCoffeeMaker, cbBar, cbBreakfast;
    private Button btnUpdateRoom, btnDeleteRoom, btnBack;
    private Button btnSelectImage, btnSelectListImage;
    private ImageView ivRoomPreview, ivListRoomPreview;
    private DatabaseHelper dbHelper;
    private int roomId;
    private Uri selectedImageUri;
    private Uri selectedListImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_LIST_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        dbHelper = new DatabaseHelper(this);

        etRoomNumber = findViewById(R.id.etRoomNumber);
        etPrice = findViewById(R.id.etPrice);
        etRoomView = findViewById(R.id.etRoomView);
        spRoomType = findViewById(R.id.spRoomType);
        spPoolType = findViewById(R.id.spPoolType);
        spRoomStars = findViewById(R.id.spRoomStars);
        cbParking = findViewById(R.id.cbParking);
        cbAirportTransfer = findViewById(R.id.cbAirportTransfer);
        cbWifi = findViewById(R.id.cbWifi);
        cbCoffeeMaker = findViewById(R.id.cbCoffeeMaker);
        cbBar = findViewById(R.id.cbBar);
        cbBreakfast = findViewById(R.id.cbBreakfast);
        btnUpdateRoom = findViewById(R.id.btnUpdateRoom);
        btnDeleteRoom = findViewById(R.id.btnDeleteRoom);
        btnBack = findViewById(R.id.btnBack);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectListImage = findViewById(R.id.btnSelectListImage);
        ivRoomPreview = findViewById(R.id.ivRoomPreview);
        ivListRoomPreview = findViewById(R.id.ivListRoomPreview);

        setupSpinners();
        loadRoomDetails();

        btnSelectImage.setOnClickListener(v -> openImagePicker(PICK_IMAGE_REQUEST));
        btnSelectListImage.setOnClickListener(v -> openImagePicker(PICK_LIST_IMAGE_REQUEST));

        btnUpdateRoom.setOnClickListener(v -> updateRoom());

        btnDeleteRoom.setOnClickListener(v -> confirmDeleteRoom());

        btnBack.setOnClickListener(v -> finish());

        roomId = getIntent().getIntExtra("roomId", -1);
        if (roomId == -1) {
            Toast.makeText(this, "Invalid room ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> roomTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_type_array, android.R.layout.simple_spinner_item);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomType.setAdapter(roomTypeAdapter);

        ArrayAdapter<CharSequence> poolTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.pool_type_array, android.R.layout.simple_spinner_item);
        poolTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPoolType.setAdapter(poolTypeAdapter);

        ArrayAdapter<CharSequence> starsAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_stars_array, android.R.layout.simple_spinner_item);
        starsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomStars.setAdapter(starsAdapter);
    }

    private void loadRoomDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_ROOM_NUMBER,
                DatabaseHelper.COLUMN_ROOM_TYPE,
                DatabaseHelper.COLUMN_PRICE_PER_NIGHT,
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

        Cursor cursor = db.query(DatabaseHelper.TABLE_ROOMS, columns,
                DatabaseHelper.COLUMN_ROOM_ID + "=?", new String[]{String.valueOf(roomId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            etRoomNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NUMBER)));
            etPrice.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE_PER_NIGHT))));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));
            String listImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_IMAGE));

            if (image != null && !image.isEmpty()) {
                selectedImageUri = Uri.parse(image);
                ivRoomPreview.setImageURI(selectedImageUri);
            }

            if (listImage != null && !listImage.isEmpty()) {
                selectedListImageUri = Uri.parse(listImage);
                ivListRoomPreview.setImageURI(selectedListImageUri);
            }
            etRoomView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_VIEW)));

            setSpinnerSelection(spRoomType, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_TYPE)));
            setSpinnerSelection(spPoolType, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POOL_TYPE)));
            setSpinnerSelection(spRoomStars, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_STARS)));

            cbParking.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_PARKING)) == 1);
            cbAirportTransfer.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER)) == 1);
            cbWifi.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_WIFI)) == 1);
            cbCoffeeMaker.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_COFFEE_MAKER)) == 1);
            cbBar.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BAR)) == 1);
            cbBreakfast.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_BREAKFAST)) == 1);

            cursor.close();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private void openImagePicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_REQUEST || requestCode == PICK_LIST_IMAGE_REQUEST) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            getContentResolver().takePersistableUriPermission(selectedUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = selectedUri;
                ivRoomPreview.setImageURI(selectedImageUri);
            } else if (requestCode == PICK_LIST_IMAGE_REQUEST) {
                selectedListImageUri = selectedUri;
                ivListRoomPreview.setImageURI(selectedListImageUri);
            }
        }
    }

    private void updateRoom() {
        String roomNumber = etRoomNumber.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String image = selectedImageUri != null ? selectedImageUri.toString() : null;
        String listImage = selectedListImageUri != null ? selectedListImageUri.toString() : null;
        String roomView = etRoomView.getText().toString().trim();
        String roomType = spRoomType.getSelectedItem().toString();
        String poolType = spPoolType.getSelectedItem().toString();
        String roomStars = spRoomStars.getSelectedItem().toString();

        if (roomNumber.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_NUMBER, roomNumber);
        values.put(DatabaseHelper.COLUMN_ROOM_TYPE, roomType);
        values.put(DatabaseHelper.COLUMN_PRICE_PER_NIGHT, price);
        values.put(DatabaseHelper.COLUMN_IMAGE, image);
        values.put(DatabaseHelper.COLUMN_LIST_IMAGE, listImage);
        values.put(DatabaseHelper.COLUMN_ROOM_VIEW, roomView);
        values.put(DatabaseHelper.COLUMN_POOL_TYPE, poolType);
        values.put(DatabaseHelper.COLUMN_ROOM_STARS, roomStars);
        values.put(DatabaseHelper.COLUMN_HAS_PARKING, cbParking.isChecked() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER, cbAirportTransfer.isChecked() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_WIFI, cbWifi.isChecked() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_COFFEE_MAKER, cbCoffeeMaker.isChecked() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_BAR, cbBar.isChecked() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_BREAKFAST, cbBreakfast.isChecked() ? 1 : 0);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.update(DatabaseHelper.TABLE_ROOMS, values,
                DatabaseHelper.COLUMN_ROOM_ID + "=?", new String[]{String.valueOf(roomId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Room updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update room", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteRoom() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to delete this room?")
                .setPositiveButton("Yes", (dialog, which) -> deleteRoom())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteRoom() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_ROOMS,
                DatabaseHelper.COLUMN_ROOM_ID + "=?", new String[]{String.valueOf(roomId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Room deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to delete room", Toast.LENGTH_SHORT).show();
        }
    }
}