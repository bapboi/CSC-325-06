package com.example.csc325_firebase_webview_auth.service;

import com.example.csc325_firebase_webview_auth.model.CredentialsProvider;
import com.example.csc325_firebase_webview_auth.model.FirebaseConfig;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;

public class StorageService {

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(15))
      .build();

  public String uploadFile(File file, String destinationPath) throws IOException, InterruptedException {
    String bucket = FirebaseConfig.getInstance().getStorageBucket();
    String accessToken = CredentialsProvider.getAccessToken();
    String contentType = Files.probeContentType(file.toPath());
    if (contentType == null) {
      contentType = "application/octet-stream";
    }

    String encodedName = URLEncoder.encode(destinationPath, StandardCharsets.UTF_8);
    String url = "https://storage.googleapis.com/upload/storage/v1/b/" + bucket
        + "/o?uploadType=media&name=" + encodedName + "&predefinedAcl=publicRead";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + accessToken)
        .header("Content-Type", contentType)
        .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new IOException("Upload failed (HTTP " + response.statusCode() + "): " + response.body());
    }

    return "https://storage.googleapis.com/" + bucket + "/" + encodedName;
  }
}
