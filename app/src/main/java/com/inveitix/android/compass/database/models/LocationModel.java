package com.inveitix.android.compass.database.models;

public class LocationModel extends  Model {

    private float northOffset;
    private long timestamp;

    public LocationModel() {
    }

    public LocationModel(float northOffset, long timestamp) {
        this.northOffset = northOffset;
        this.timestamp = timestamp;
    }

    public float getNorthOffset() {
        return northOffset;
    }

    public void setNorthOffset(float northOffset) {
        this.northOffset = northOffset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
