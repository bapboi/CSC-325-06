package com.example.csc325_firebase_webview_auth.model;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;

// key loading 
public class CredentialsProvider {

  private static final String[] SCOPES = {
      "https://www.googleapis.com/auth/cloud-platform"
  };

  private static GoogleCredentials credentials;

  public static synchronized GoogleCredentials getCredentials() {
    if (credentials == null) {
      try (InputStream in = CredentialsProvider.class.getResourceAsStream("/files/key.json")) {
        if (in == null) {
          throw new IllegalStateException(
              "Missing src/main/resources/files/key.json. Download your service "
                  + "account key from Firebase Console > Project settings > Service "
                  + "accounts and place it there (it is gitignored).");
        }
        credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);
      } catch (IOException ex) {
        throw new IllegalStateException("Could not load key.json", ex);
      }
    }
    return credentials;
  }

  public static String getAccessToken() {
    try {
      GoogleCredentials creds = getCredentials();
      AccessToken token = creds.refreshAccessToken();
      return token.getTokenValue();
    } catch (IOException ex) {
      throw new IllegalStateException("Could not obtain access token", ex);
    }
  }
}
