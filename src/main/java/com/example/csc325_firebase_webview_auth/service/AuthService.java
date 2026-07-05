package com.example.csc325_firebase_webview_auth.service;

import com.example.csc325_firebase_webview_auth.model.AuthResult;
import com.example.csc325_firebase_webview_auth.model.FirebaseConfig;
import com.example.csc325_firebase_webview_auth.model.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthService {

  private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=";
  private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build();

  public AuthResult signUp(String email, String password) {
    String apiKey = FirebaseConfig.getInstance().getApiKey();
    String body = "{\"email\":\"" + escape(email) + "\",\"password\":\"" + escape(password)
        + "\",\"returnSecureToken\":true}";
    return callIdentityToolkit(SIGN_UP_URL + apiKey, body);
  }

  public AuthResult signIn(String email, String password) {
    String apiKey = FirebaseConfig.getInstance().getApiKey();
    String body = "{\"email\":\"" + escape(email) + "\",\"password\":\"" + escape(password)
        + "\",\"returnSecureToken\":true}";
    return callIdentityToolkit(SIGN_IN_URL + apiKey, body);
  }

  private AuthResult callIdentityToolkit(String url, String jsonBody) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      String responseBody = response.body();

      if (response.statusCode() == 200) {
        String uid = JsonUtil.getString(responseBody, "localId");
        String email = JsonUtil.getString(responseBody, "email");
        String idToken = JsonUtil.getString(responseBody, "idToken");
        return AuthResult.success(uid, email, idToken);
      } else {
        String message = JsonUtil.getString(responseBody, "message");
        return AuthResult.failure(friendlyError(message));
      }
    } catch (IOException | InterruptedException ex) {
      return AuthResult.failure("Network error contacting Firebase: " + ex.getMessage());
    }
  }

  private String friendlyError(String code) {
    if (code == null) {
      return "Unknown error. Please try again.";
    }
    switch (code) {
      case "EMAIL_EXISTS":
        return "An account with that email already exists.";
      case "EMAIL_NOT_FOUND":
      case "INVALID_LOGIN_CREDENTIALS":
      case "INVALID_PASSWORD":
        return "Incorrect email or password.";
      case "WEAK_PASSWORD : Password should be at least 6 characters":
        return "Password should be at least 6 characters.";
      case "INVALID_EMAIL":
        return "That email address looks invalid.";
      default:
        return code.replace("_", " ");
    }
  }

  private String escape(String s) {
    return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
