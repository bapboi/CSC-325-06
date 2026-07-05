package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.AuthResult;
import com.example.csc325_firebase_webview_auth.service.AuthService;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {

  @FXML
  private TextField nameField;
  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
  @FXML
  private Button registerButton;
  @FXML
  private Label statusLabel;

  private final AuthService authService = new AuthService();

  @FXML
  private void register(ActionEvent event) {
    String name = nameField.getText() == null ? "" : nameField.getText().trim();
    String email = emailField.getText() == null ? "" : emailField.getText().trim();
    String password = passwordField.getText() == null ? "" : passwordField.getText();
    String confirm = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
      statusLabel.setText("Please fill in all fields.");
      return;
    }
    if (!password.equals(confirm)) {
      statusLabel.setText("Passwords do not match.");
      return;
    }
    if (password.length() < 6) {
      statusLabel.setText("Password should be at least 6 characters.");
      return;
    }

    registerButton.setDisable(true);
    statusLabel.setText("Creating your account...");

    Task<AuthResult> registerTask = new Task<>() {
      @Override
      protected AuthResult call() {
        return authService.signUp(email, password);
      }
    };

    registerTask.setOnSucceeded(e -> {
      AuthResult result = registerTask.getValue();
      if (result.isSuccess()) {
        saveProfile(result.getUid(), name, email);
        statusLabel.setText("Account created! Redirecting to sign in...");
        goToLogin();
      } else {
        registerButton.setDisable(false);
        statusLabel.setText(result.getErrorMessage());
      }
    });

    registerTask.setOnFailed(e -> {
      registerButton.setDisable(false);
      Throwable ex = registerTask.getException();
      statusLabel.setText(ex == null ? "Something went wrong. Please try again."
          : "Error: " + ex.getMessage());
    });

    new Thread(registerTask, "register").start();
  }

  private void saveProfile(String uid, String name, String email) {
    Map<String, Object> data = new HashMap<>();
    data.put("Name", name);
    data.put("Email", email);
    data.put("PhotoUrl", "");
    App.fstore.collection("Users").document(uid).set(data);
  }

  private void goToLogin() {
    try {
      App.setRoot("/files/Login.fxml");
    } catch (IOException ex) {
      statusLabel.setText("Account created, but could not return to login: " + ex.getMessage());
    }
  }

  @FXML
  private void backToLogin(ActionEvent event) throws IOException {
    App.setRoot("/files/Login.fxml");
  }
}
