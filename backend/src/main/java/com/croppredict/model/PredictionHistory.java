package com.croppredict.model;

public class PredictionHistory {
    private String id;
    private String crop;
    private String market;
    private String date;
    private double predictedPricePer20kg;
    private double predictedPricePerQuintal;
    private String createdAt;

    public PredictionHistory() {}

    public PredictionHistory(String id, String crop, String market, String date, double predictedPricePer20kg, double predictedPricePerQuintal, String createdAt) {
        this.id = id;
        this.crop = crop;
        this.market = market;
        this.date = date;
        this.predictedPricePer20kg = predictedPricePer20kg;
        this.predictedPricePerQuintal = predictedPricePerQuintal;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPredictedPricePer20kg() {
        return predictedPricePer20kg;
    }

    public void setPredictedPricePer20kg(double predictedPricePer20kg) {
        this.predictedPricePer20kg = predictedPricePer20kg;
    }

    public double getPredictedPricePerQuintal() {
        return predictedPricePerQuintal;
    }

    public void setPredictedPricePerQuintal(double predictedPricePerQuintal) {
        this.predictedPricePerQuintal = predictedPricePerQuintal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}