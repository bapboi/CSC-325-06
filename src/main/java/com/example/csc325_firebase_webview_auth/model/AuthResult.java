package com.example.csc325_firebase_webview_auth.model;


public class AuthResult {
    private final boolean success;
    private final String uid;
    private final String email;
    private final String idToken;
    private final String errorMessage;

    private AuthResult(boolean success, String uid, String email, String idToken, String errorMessage) {
        this.success = success;
        this.uid = uid;
        this.email = email;
        this.idToken = idToken;
        this.errorMessage = errorMessage;
    }

    public static AuthResult success(String uid, String email, String idToken) {
        return new AuthResult(true, uid, email, idToken, null);
    }

    public static AuthResult failure(String errorMessage) {
        return new AuthResult(false, null, null, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
