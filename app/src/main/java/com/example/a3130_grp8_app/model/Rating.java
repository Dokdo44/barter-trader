package com.example.a3130_grp8_app.model;

public class Rating {
    private String receiverId;
    private String providerId;
    private int ratingValue;
    private String comment;

    public Rating() {
    }

    public Rating(String receiverId, String providerId, int ratingValue, String comment) {
        this.receiverId = receiverId;
        this.providerId = providerId;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}