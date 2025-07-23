package com.example.hotelroomreservationproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.DatabaseHelper;

public class AddRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_LIST_IMAGE_REQUEST = 2;

    private EditText etRoomNumber, etPrice, etRoomView;
    private Spinner spPoolType, spRoomType, spRoomStars;
    private CheckBox cbParking, cbAirportTransfer, cbWifi, cbCoffeeMaker, cbBar, cbBreakfast;
    private Button btnAddRoom, btnSelectImage, btnSelectListImage;
    private ImageView ivRoomPreview, ivListRoomPreview;
    private DatabaseHelper dbHelper;
    private Uri selectedImageUri;
    private Uri selectedListImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        etRoomNumber = findViewById(R.id.etRoomNumber);
        etPrice = findViewById(R.id.etPrice);
        etRoomView = findViewById(R.id.etRoomView);
        spPoolType = findViewById(R.id.spPoolType);
        spRoomType = findViewById(R.id.spRoomType);
        spRoomStars = findViewById(R.id.spRoomStars);
        cbParking = findViewById(R.id.cbParking);
        cbAirportTransfer = findViewById(R.id.cbAirportTransfer);
        cbWifi = findViewById(R.id.cbWifi);
        cbCoffeeMaker = findViewById(R.id.cbCoffeeMaker);
        cbBar = findViewById(R.id.cbBar);
        cbBreakfast = findViewById(R.id.cbBreakfast);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectListImage = findViewById(R.id.btnSelectListImage);
        ivRoomPreview = findViewById(R.id.ivRoomPreview);
        ivListRoomPreview = findViewById(R.id.ivListRoomPreview);

        dbHelper = new DatabaseHelper(this);

        btnSelectImage.setOnClickListener(v -> openImagePicker(PICK_IMAGE_REQUEST));
        btnSelectListImage.setOnClickListener(v -> openImagePicker(PICK_LIST_IMAGE_REQUEST));

        // Setup Pool Type Spinner
        ArrayAdapter<CharSequence> poolAdapter = ArrayAdapter.createFromResource(this,
                R.array.pool_type_array, android.R.layout.simple_spinner_item);
        poolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPoolType.setAdapter(poolAdapter);

        // Setup Room Type Spinner
        ArrayAdapter<CharSequence> roomTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_type_array, android.R.layout.simple_spinner_item);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomType.setAdapter(roomTypeAdapter);

        // Setup Room Stars Spinner
        ArrayAdapter<CharSequence> starsAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_stars_array, android.R.layout.simple_spinner_item);
        starsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomStars.setAdapter(starsAdapter);

        btnAddRoom.setOnClickListener(v -> addRoom());
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

    private void addRoom() {
        String roomNumber = etRoomNumber.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String image = selectedImageUri != null ? selectedImageUri.toString() : "";
        String listImage = selectedListImageUri != null ? selectedListImageUri.toString() : "";
        String roomView = etRoomView.getText().toString().trim();
        String roomType = spRoomType.getSelectedItem().toString();
        String poolType = spPoolType.getSelectedItem().toString();
        String roomStars = spRoomStars.getSelectedItem().toString();
        boolean hasParking = cbParking.isChecked();
        boolean hasAirportTransfer = cbAirportTransfer.isChecked();
        boolean hasWifi = cbWifi.isChecked();
        boolean hasCoffeeMaker = cbCoffeeMaker.isChecked();
        boolean hasBar = cbBar.isChecked();
        boolean hasBreakfast = cbBreakfast.isChecked();

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

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_NUMBER, roomNumber);
        values.put(DatabaseHelper.COLUMN_ROOM_TYPE, roomType);
        values.put(DatabaseHelper.COLUMN_PRICE_PER_NIGHT, price);
        values.put(DatabaseHelper.COLUMN_AVAILABILITY, 1);
        values.put(DatabaseHelper.COLUMN_IMAGE, image);
        values.put(DatabaseHelper.COLUMN_LIST_IMAGE, listImage);
        values.put(DatabaseHelper.COLUMN_ROOM_VIEW, roomView);
        values.put(DatabaseHelper.COLUMN_POOL_TYPE, poolType);
        values.put(DatabaseHelper.COLUMN_ROOM_STARS, roomStars);
        values.put(DatabaseHelper.COLUMN_HAS_PARKING, hasParking ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_AIRPORT_TRANSFER, hasAirportTransfer ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_WIFI, hasWifi ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_COFFEE_MAKER, hasCoffeeMaker ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_BAR, hasBar ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HAS_BREAKFAST, hasBreakfast ? 1 : 0);

        long newRowId = db.insert(DatabaseHelper.TABLE_ROOMS, null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}