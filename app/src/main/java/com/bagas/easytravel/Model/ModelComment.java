package com.bagas.easytravel.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;


public class ModelComment implements Serializable {
    private String comment;
    private String placeId;
    private Float rating;
    private Timestamp timestamp;
    private String userId;
    private String username;
    private String type;

    public ModelComment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ModelComment{" +
                "userId='" + userId + '\'' +
                ", placeId='" + placeId + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                ", rating=" + rating +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
