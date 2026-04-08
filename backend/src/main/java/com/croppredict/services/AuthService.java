package com.croppredict.services;

import com.croppredict.model.AuthResponse;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 🔥 REGISTER
    public AuthResponse register(String name, String email, String password) {

        try {
            Firestore db = FirestoreClient.getFirestore();

            if (name == null || name.isBlank()) {
                return new AuthResponse(false, "Name required", null, null, null);
            }

            if (email == null || email.isBlank()) {
                return new AuthResponse(false, "Email required", null, null, null);
            }

            if (password == null || password.length() < 6) {
                return new AuthResponse(false, "Password must be at least 6 characters", null, null, null);
            }

            // 🔍 Check duplicate email
            List<QueryDocumentSnapshot> docs = db.collection("users")
                    .whereEqualTo("email", email.toLowerCase())
                    .get()
                    .get()
                    .getDocuments();

            if (!docs.isEmpty()) {
                return new AuthResponse(false, "Email already exists", null, null, null);
            }

            // 🔐 Hash password
            String hashedPassword = encoder.encode(password);

            // 🔥 Save to Firestore
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email.toLowerCase());
            user.put("password", hashedPassword);

            var docRef = db.collection("users").document();
            user.put("id", docRef.getId());

            docRef.set(user);

            return new AuthResponse(true, "Registration successful",
                    docRef.getId(), name, email);

        } catch (Exception e) {
            return new AuthResponse(false, "Error: " + e.getMessage(), null, null, null);
        }
    }

    // 🔥 LOGIN
    public AuthResponse login(String email, String password) {

        try {
            Firestore db = FirestoreClient.getFirestore();

            List<QueryDocumentSnapshot> docs = db.collection("users")
                    .whereEqualTo("email", email.toLowerCase())
                    .get()
                    .get()
                    .getDocuments();

            if (docs.isEmpty()) {
                return new AuthResponse(false, "User not found", null, null, null);
            }

            QueryDocumentSnapshot doc = docs.get(0);

            String storedPassword = doc.getString("password");

            // 🔐 Match hash
            if (!encoder.matches(password, storedPassword)) {
                return new AuthResponse(false, "Invalid password", null, null, null);
            }

            return new AuthResponse(true, "Login successful",
                    doc.getId(),
                    doc.getString("name"),
                    doc.getString("email"));

        } catch (Exception e) {
            return new AuthResponse(false, "Error: " + e.getMessage(), null, null, null);
        }
    }
}