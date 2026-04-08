package com.croppredict.model;

public class PredictionRequest {

    private String crop;
    private String market;
    private String date;
    private String userId;

    // 🔥 GETTERS

    public String getCrop() {
        return crop;
    }

    public String getMarket() {
        return market;
    }

    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    // 🔥 SETTERS

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}