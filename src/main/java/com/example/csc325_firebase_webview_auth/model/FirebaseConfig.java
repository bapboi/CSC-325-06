package com.example.csc325_firebase_webview_auth.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FirebaseConfig {

  private static FirebaseConfig instance;

  private final String apiKey;
  private final String projectId;
  private final String storageBucket;

  private FirebaseConfig() {
    Properties props = new Properties();
    try (InputStream in = getClass().getResourceAsStream("/files/firebase_config.properties")) {
      if (in == null) {
        throw new IllegalStateException(
            "Missing src/main/resources/files/firebase_config.properties. "
                + "Copy firebase_config.properties.example to firebase_config.properties "
                + "and fill in your Firebase project's values.");
      }
      props.load(in);
    } catch (IOException ex) {
      throw new IllegalStateException("Could not read firebase_config.properties", ex);
    }

    this.apiKey = props.getProperty("firebase.apiKey", "").trim();
    this.projectId = props.getProperty("firebase.projectId", "").trim();
    this.storageBucket = props.getProperty("firebase.storageBucket", "").trim();
  }

  public static synchronized FirebaseConfig getInstance() {
    if (instance == null) {
      instance = new FirebaseConfig();
    }
    return instance;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getStorageBucket() {
    return storageBucket;
  }
}
