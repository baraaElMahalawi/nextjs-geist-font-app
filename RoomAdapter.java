package com.example.hotelroomreservationproject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelroomreservationproject.R;
import com.example.hotelroomreservationproject.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private List<Room> rooms;
    private OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> rooms, OnRoomClickListener listener) {
        this.context = context;
        this.rooms = rooms;
        this.listener = listener;
    }

    public void updateRooms(List<Room> newRooms) {
        this.rooms = newRooms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);

        holder.tvRoomNumber.setText("Room " + room.getRoomNumber());
        holder.tvRoomType.setText(room.getRoomType());
        holder.tvPrice.setText("$" + String.format("%.2f", room.getPricePerNight()) + " per night");

        if (room.getListImage() != null && !room.getListImage().isEmpty()) {
            try {
                holder.ivRoomImage.setImageURI(Uri.parse(room.getListImage()));
            } catch (Exception e) {
                // Remove default image to avoid error, set no image or a color instead
                holder.ivRoomImage.setImageResource(android.R.color.transparent);
            }
        } else {
            // If list image is not available, try using the regular image
            if (room.getImage() != null && !room.getImage().isEmpty()) {
                try {
                    holder.ivRoomImage.setImageURI(Uri.parse(room.getImage()));
                } catch (Exception e) {
                    holder.ivRoomImage.setImageResource(android.R.color.transparent);
                }
            } else {
                holder.ivRoomImage.setImageResource(android.R.color.transparent);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRoomClick(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms != null ? rooms.size() : 0;
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView tvRoomNumber;
        TextView tvRoomType;
        TextView tvPrice;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}