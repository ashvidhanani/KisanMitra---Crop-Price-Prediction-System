package com.croppredict.model;

public class PredictionResponse {
    private String crop;
    private String market;
    private String date;
    private double predictedPricePer20kg;
    private double predictedPricePerQuintal;
    private String unit;
    private String timestamp;

    public PredictionResponse() {}

    public PredictionResponse(String crop, String market, String date, double predictedPricePer20kg, double predictedPricePerQuintal, String unit, String timestamp) {
        this.crop = crop;
        this.market = market;
        this.date = date;
        this.predictedPricePer20kg = predictedPricePer20kg;
        this.predictedPricePerQuintal = predictedPricePerQuintal;
        this.unit = unit;
        this.timestamp = timestamp;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}