package com.croppredict.controller;

import com.croppredict.model.PredictionHistory;
import com.croppredict.model.PredictionRequest;
import com.croppredict.model.PredictionResponse;
import com.croppredict.services.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    // POST /api/predictPrice
    @PostMapping("/predictPrice")
    public ResponseEntity<?> predictPrice(@Valid @RequestBody PredictionRequest request) {
        try {
            PredictionResponse response = predictionService.predict(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/history?userId=abc123
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam(required = false) String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "userId request parameter is required"));
        }
        try {
            List<PredictionHistory> history = predictionService.getHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/health
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "Backend running"));
    }
}