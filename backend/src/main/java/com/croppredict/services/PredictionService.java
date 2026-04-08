package com.croppredict.services;

import com.croppredict.model.PredictionHistory;
import com.croppredict.model.PredictionRequest;
import com.croppredict.model.PredictionResponse;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class PredictionService {

    @Value("${flask.ai.url}")
    private String flaskBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Field names constants
    private static final String FIELD_CROP = "crop";
    private static final String FIELD_MARKET = "market";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_PREDICTED_PRICE_20KG = "predictedPricePer20kg";
    private static final String FIELD_PREDICTED_PRICE_QUINTAL = "predictedPricePerQuintal";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_ID = "id";

    // ─────────────────────────────────────────
    // PREDICT — calls Flask, saves to Firestore
    // ─────────────────────────────────────────
    public PredictionResponse predict(PredictionRequest req) {

        // 1. Call Flask AI service
        String flaskUrl = flaskBaseUrl + "/predict";

        Map<String, Object> body = new HashMap<>();
        body.put(FIELD_CROP,   req.getCrop());
        body.put(FIELD_MARKET, req.getMarket());
        body.put(FIELD_DATE,   req.getDate());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        var flaskResponse = restTemplate.postForEntity(flaskUrl, entity, Map.class);

        var flaskBody = flaskResponse.getBody();

        if (flaskBody == null || flaskBody.containsKey("error")) {
            String err = flaskBody != null
                    ? (String) flaskBody.get("error")
                    : "No response from AI service";
            throw new IllegalStateException("AI Service Error: " + err);
        }

        double pricePer20kg    = ((Number) flaskBody.get("predicted_price_per_20kg")).doubleValue();
        double pricePerQuintal = ((Number) flaskBody.get("predicted_price_per_quintal")).doubleValue();
        String unit            = (String) flaskBody.getOrDefault("unit", "INR per 20 kg");
        String timestamp       = Instant.now().toString();

        // 2. Save to Firestore — now includes userId
        saveToFirebase(req, pricePer20kg, pricePerQuintal, timestamp);

        // 3. Return to Angular
        return new PredictionResponse(
                req.getCrop(),
                req.getMarket(),
                req.getDate(),
                pricePer20kg,
                pricePerQuintal,
                unit,
                timestamp
        );
    }

    // ─────────────────────────────────────────
    // GET HISTORY — filtered by userId
    // ─────────────────────────────────────────
    public List<PredictionHistory> getHistory(String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<PredictionHistory> list = new ArrayList<>();

            db.collection("predictions")
                .get()
                .get()
                .getDocuments()
                .forEach(doc -> {

                    if (!userId.equals(doc.getString(FIELD_USER_ID))) return;

                    PredictionHistory history = new PredictionHistory();

                    history.setId(doc.getId());
                    history.setCrop(doc.getString(FIELD_CROP));
                    history.setMarket(doc.getString(FIELD_MARKET));
                    history.setDate(doc.getString(FIELD_DATE));

                    history.setPredictedPricePer20kg(
                        doc.getDouble(FIELD_PREDICTED_PRICE_20KG) != null
                            ? doc.getDouble(FIELD_PREDICTED_PRICE_20KG) : 0.0
                    );

                    history.setPredictedPricePerQuintal(
                        doc.getDouble(FIELD_PREDICTED_PRICE_QUINTAL) != null
                            ? doc.getDouble(FIELD_PREDICTED_PRICE_QUINTAL) : 0.0
                    );

                    history.setCreatedAt(doc.getString(FIELD_CREATED_AT));

                    list.add(history);
            });

            list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));


            return list;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while fetching history", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to fetch prediction history from Firestore", e);
        }
    }

    // ─────────────────────────────────────────
    // SAVE TO FIREBASE — now includes userId
    // ─────────────────────────────────────────
    private void saveToFirebase(PredictionRequest req,
                                double per20kg,
                                double perQuintal,
                                String timestamp) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("predictions").document();

            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_ID,                       docRef.getId());
            data.put(FIELD_USER_ID,                  req.getUserId());
            data.put(FIELD_CROP,                     req.getCrop());
            data.put(FIELD_MARKET,                   req.getMarket());
            data.put(FIELD_DATE,                     req.getDate());
            data.put(FIELD_PREDICTED_PRICE_20KG,    per20kg);
            data.put(FIELD_PREDICTED_PRICE_QUINTAL, perQuintal);
            data.put(FIELD_CREATED_AT,               timestamp);

            docRef.set(data).get();

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            System.err.println("Firebase write failed: " + e.getMessage());
        }
    }
}