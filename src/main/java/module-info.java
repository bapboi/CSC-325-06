module com.example.csc325_firebase_webview_auth {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires java.xml;
  requires java.logging;
  requires java.net.http;
  requires com.google.auth.oauth2;
  requires google.cloud.firestore;
  requires firebase.admin;
  requires com.google.api.apicommon;

  requires google.cloud.core;
  requires com.google.auth;

  opens com.example.csc325_firebase_webview_auth.viewmodel;
  opens com.example.csc325_firebase_webview_auth.view;

  exports com.example.csc325_firebase_webview_auth.view;
  exports com.example.csc325_firebase_webview_auth.model;

  opens com.example.csc325_firebase_webview_auth.model;

  exports com.example.csc325_firebase_webview_auth.service;

  opens com.example.csc325_firebase_webview_auth.service;
}
