package com.example.hotelroomreservationproject;



import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.DatabaseHelper;
import com.example.hotelroomreservationproject.Booking;

import java.util.List;

public class ManageBookingAdapter extends RecyclerView.Adapter<ManageBookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private DatabaseHelper dbHelper;

    public ManageBookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvBookingInfo.setText("Booking ID: " + booking.getBookingId() +
                "\nUser ID: " + booking.getUserId() +
                "\nRoom ID: " + booking.getRoomId() +
                "\nCheck-in: " + booking.getCheckinDate() +
                "\nCheck-out: " + booking.getCheckoutDate() +
                "\nGuests: " + booking.getGuests() +
                "\nStatus: " + booking.getStatus());

        holder.btnConfirm.setOnClickListener(v -> updateBookingStatus(booking, "CONFIRMED", position));
        holder.btnDelete.setOnClickListener(v -> deleteBooking(booking, position));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private void updateBookingStatus(Booking booking, String status, int position) {
        booking.setStatus(status);
        boolean success = updateBookingInDb(booking);
        if (success) {
            notifyItemChanged(position);
            Toast.makeText(context, "Booking status updated to " + status, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update booking status", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBooking(Booking booking, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_BOOKINGS, DatabaseHelper.COLUMN_BOOKING_ID + "=?", new String[]{String.valueOf(booking.getBookingId())});
        if (rowsDeleted > 0) {
            bookingList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Booking deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete booking", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean updateBookingInDb(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STATUS, booking.getStatus());
        int rowsAffected = db.update(DatabaseHelper.TABLE_BOOKINGS, values, DatabaseHelper.COLUMN_BOOKING_ID + "=?", new String[]{String.valueOf(booking.getBookingId())});
        return rowsAffected > 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingInfo;
        Button btnConfirm, btnCancel, btnDelete;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingInfo = itemView.findViewById(R.id.tvBookingInfo);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}