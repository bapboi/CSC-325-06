package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.AuthResult;
import com.example.csc325_firebase_webview_auth.service.AuthService;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void signIn(ActionEvent event) {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password.");
            return;
        }

        signInButton.setDisable(true);
        statusLabel.setText("Signing in...");

        Task<AuthResult> signInTask = new Task<>() {
            @Override
            protected AuthResult call() {
                return authService.signIn(email, password);
            }
        };

        signInTask.setOnSucceeded(e -> {
            signInButton.setDisable(false);
            AuthResult result = signInTask.getValue();
            if (result.isSuccess()) {
                App.currentUid = result.getUid();
                App.currentEmail = result.getEmail();
                goToDashboard();
            } else {
                statusLabel.setText(result.getErrorMessage());
            }
        });

        signInTask.setOnFailed(e -> {
            signInButton.setDisable(false);
            statusLabel.setText("Something went wrong. Please try again.");
        });

        new Thread(signInTask, "sign-in").start();
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        App.setRoot("/files/Register.fxml");
    }

    private void goToDashboard() {
        try {
            App.setRoot("/files/AccessFBView.fxml");
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Could not open dashboard: " + ex.getMessage()).showAndWait();
        }
    }
}
