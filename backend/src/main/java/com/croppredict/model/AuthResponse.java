package com.croppredict.model;

public class AuthResponse {

    private boolean success;
    private String message;
    private String userId;
    private String name;
    private String email;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, String userId, String name, String email) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}