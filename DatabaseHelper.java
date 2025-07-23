package com.example.hotelroomreservationproject;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotel_booking.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_BOOKINGS = "bookings";

    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    public static final String COLUMN_ROOM_ID = "room_id";
    public static final String COLUMN_ROOM_NUMBER = "room_number";
    public static final String COLUMN_ROOM_TYPE = "room_type";
    public static final String COLUMN_PRICE_PER_NIGHT = "price_per_night";
    public static final String COLUMN_AVAILABILITY = "availability";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LIST_IMAGE = "list_image";
    public static final String COLUMN_ROOM_VIEW = "room_view";
    public static final String COLUMN_POOL_TYPE = "pool_type";
    public static final String COLUMN_ROOM_STARS = "room_stars";
    public static final String COLUMN_HAS_PARKING = "has_parking";
    public static final String COLUMN_HAS_AIRPORT_TRANSFER = "has_airport_transfer";
    public static final String COLUMN_HAS_WIFI = "has_wifi";
    public static final String COLUMN_HAS_COFFEE_MAKER = "has_coffee_maker";
    public static final String COLUMN_HAS_BAR = "has_bar";
    public static final String COLUMN_HAS_BREAKFAST = "has_breakfast";

    public static final String COLUMN_BOOKING_ID = "booking_id";
    public static final String COLUMN_BOOKING_USER_ID = "user_id";
    public static final String COLUMN_BOOKING_ROOM_ID = "room_id";
    public static final String COLUMN_CHECKIN_DATE = "checkin_date";
    public static final String COLUMN_CHECKOUT_DATE = "checkout_date";
    public static final String COLUMN_GUESTS = "guests";
    public static final String COLUMN_STATUS = "status";

    public static final String TABLE_USER_PERSONAL_ROOMS = "user_personal_rooms";
    public static final String COLUMN_UPR_ID = "upr_id";
    public static final String COLUMN_UPR_USER_ID = "user_id";
    public static final String COLUMN_UPR_ROOM_ID = "room_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
                + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ROOM_NUMBER + " TEXT,"
                + COLUMN_ROOM_TYPE + " TEXT,"
                + COLUMN_PRICE_PER_NIGHT + " REAL,"
                + COLUMN_AVAILABILITY + " INTEGER,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_LIST_IMAGE + " TEXT,"
                + COLUMN_ROOM_VIEW + " TEXT,"
                + COLUMN_POOL_TYPE + " TEXT,"
                + COLUMN_ROOM_STARS + " TEXT,"
                + COLUMN_HAS_PARKING + " INTEGER,"
                + COLUMN_HAS_AIRPORT_TRANSFER + " INTEGER,"
                + COLUMN_HAS_WIFI + " INTEGER,"
                + COLUMN_HAS_COFFEE_MAKER + " INTEGER,"
                + COLUMN_HAS_BAR + " INTEGER,"
                + COLUMN_HAS_BREAKFAST + " INTEGER"
                + ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BOOKING_USER_ID + " INTEGER,"
                + COLUMN_BOOKING_ROOM_ID + " INTEGER,"
                + COLUMN_CHECKIN_DATE + " TEXT,"
                + COLUMN_CHECKOUT_DATE + " TEXT,"
                + COLUMN_GUESTS + " INTEGER,"
                + COLUMN_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_BOOKING_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COLUMN_ROOM_ID + ")"
                + ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        String CREATE_USER_PERSONAL_ROOMS_TABLE = "CREATE TABLE " + TABLE_USER_PERSONAL_ROOMS + "("
                + COLUMN_UPR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UPR_USER_ID + " INTEGER,"
                + COLUMN_UPR_ROOM_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_UPR_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_UPR_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COLUMN_ROOM_ID + ")"
                + ")";
        db.execSQL(CREATE_USER_PERSONAL_ROOMS_TABLE);

        String INSERT_ADMIN = "INSERT INTO " + TABLE_USERS + "(" + COLUMN_USERNAME + "," + COLUMN_PASSWORD + "," + COLUMN_ROLE + ") VALUES('ADMIN','admin123','ADMIN')";
        db.execSQL(INSERT_ADMIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }
}
