package com.example.hotelroomreservationproject;

public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean availability;
    private String image;
    private String listImage;
    private String roomView;
    private String poolType;
    private String roomStars;
    private boolean hasParking;
    private boolean hasAirportTransfer;
    private boolean hasWifi;
    private boolean hasCoffeeMaker;
    private boolean hasBar;
    private boolean hasBreakfast;

    public Room() {
        // Default constructor
    }

    public Room(int roomId, String roomNumber, String roomType, double pricePerNight,
                boolean availability, String image, String listImage, String roomView, String poolType, String roomStars,
                boolean hasParking, boolean hasAirportTransfer, boolean hasWifi,
                boolean hasCoffeeMaker, boolean hasBar, boolean hasBreakfast) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.availability = availability;
        this.image = image;
        this.listImage = listImage;
        this.roomView = roomView;
        this.poolType = poolType;
        this.roomStars = roomStars;
        this.hasParking = hasParking;
        this.hasAirportTransfer = hasAirportTransfer;
        this.hasWifi = hasWifi;
        this.hasCoffeeMaker = hasCoffeeMaker;
        this.hasBar = hasBar;
        this.hasBreakfast = hasBreakfast;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getListImage() {
        return listImage;
    }

    public void setListImage(String listImage) {
        this.listImage = listImage;
    }

    public String getRoomView() {
        return roomView;
    }

    public void setRoomView(String roomView) {
        this.roomView = roomView;
    }

    public String getPoolType() {
        return poolType;
    }

    public void setPoolType(String poolType) {
        this.poolType = poolType;
    }

    public String getRoomStars() {
        return roomStars;
    }

    public void setRoomStars(String roomStars) {
        this.roomStars = roomStars;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }

    public boolean isHasAirportTransfer() {
        return hasAirportTransfer;
    }

    public void setHasAirportTransfer(boolean hasAirportTransfer) {
        this.hasAirportTransfer = hasAirportTransfer;
    }

    public boolean isHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public boolean isHasCoffeeMaker() {
        return hasCoffeeMaker;
    }

    public void setHasCoffeeMaker(boolean hasCoffeeMaker) {
        this.hasCoffeeMaker = hasCoffeeMaker;
    }

    public boolean isHasBar() {
        return hasBar;
    }

    public void setHasBar(boolean hasBar) {
        this.hasBar = hasBar;
    }

    public boolean isHasBreakfast() {
        return hasBreakfast;
    }

    public void setHasBreakfast(boolean hasBreakfast) {
        this.hasBreakfast = hasBreakfast;
    }
}