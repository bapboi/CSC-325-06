package com.example.csc325_firebase_webview_auth.view;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

  @FXML
  private Label statusLabel;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    Task<Void> initTask = new Task<>() {
      @Override
      protected Void call() {
        updateMessage("Connecting to Firebase...");
        try {
          App.finishStartup();
        } catch (Exception ex) {
          updateMessage("Startup failed: " + ex.getMessage());
          throw ex;
        }
        return null;
      }
    };

    statusLabel.textProperty().bind(initTask.messageProperty());

    initTask.setOnSucceeded(e -> {
      statusLabel.textProperty().unbind();
      statusLabel.setText("Ready!");
      goToLogin();
    });

    initTask.setOnFailed(e -> {
      statusLabel.textProperty().unbind();
      Throwable ex = initTask.getException();
      statusLabel.setText("Startup failed. Check key.json / firebase_config.properties.");
      if (ex != null) {
        ex.printStackTrace();
      }
    });

    Thread thread = new Thread(initTask, "firebase-init");
    thread.setDaemon(true);
    thread.start();
  }

  private void goToLogin() {
    PauseTransition pause = new PauseTransition(Duration.millis(400));
    pause.setOnFinished(e -> {
      try {
        App.setRoot("/files/Login.fxml");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
    pause.play();
  }
}
