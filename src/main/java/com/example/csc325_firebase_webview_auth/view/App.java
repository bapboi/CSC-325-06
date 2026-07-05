package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.FirestoreContext;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

  public static Firestore fstore;
  public static FirebaseAuth fauth;
  public static Scene scene;

  public static String currentUid;
  public static String currentEmail;
  public static String currentDisplayName;

  public static final String LIGHT_THEME = "/files/styles.css";
  public static final String DARK_THEME = "/files/styles-dark.css";
  private static String currentTheme = LIGHT_THEME;

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("CSC325 Firebase App");
    scene = new Scene(loadFXML("/files/Splash.fxml"), 960, 620);
    applyStylesheet();
    primaryStage.setScene(scene);
    primaryStage.show();

  }

  public static void finishStartup() {
    FirestoreContext contxtFirebase = new FirestoreContext();
    fstore = contxtFirebase.firebase();
    fauth = FirebaseAuth.getInstance();
  }

  public static void clearSession() {
    currentUid = null;
    currentEmail = null;
    currentDisplayName = null;
  }

  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
    applyStylesheet();
  }

  public static void setTheme(String stylesheetPath) {
    currentTheme = stylesheetPath;
    applyStylesheet();
  }

  public static String getCurrentTheme() {
    return currentTheme;
  }

  private static void applyStylesheet() {
    String css = Objects.requireNonNull(App.class.getResource(currentTheme)).toExternalForm();
    scene.getStylesheets().clear();
    scene.getStylesheets().add(css);
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
    return fxmlLoader.load();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
