package com.croppredict.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount =
                    getClass().getClassLoader().getResourceAsStream("firebase-key.json");

            if (serviceAccount == null) {
                throw new RuntimeException("firebase-key.json not found in resources folder");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://crop-price-prediction-f44b1-default-rtdb.firebaseio.com/")
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("🔥 Firebase initialized successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}