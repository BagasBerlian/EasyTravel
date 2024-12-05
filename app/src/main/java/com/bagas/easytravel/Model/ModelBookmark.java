package com.bagas.easytravel.Model;

import java.io.Serializable;

public class ModelBookmark implements Serializable {
    private String userId;
    private String placeId;
    private Boolean is_bookmark;
    private String image_url;

    public ModelBookmark() {
    }

    public ModelBookmark(String userId, String placeId, Boolean is_bookmark, String image_url) {
        this.userId = userId;
        this.placeId = placeId;
        this.is_bookmark = is_bookmark;
        this.image_url = image_url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Boolean getIs_bookmark() {
        return is_bookmark;
    }

    public void setIs_bookmark(Boolean is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
