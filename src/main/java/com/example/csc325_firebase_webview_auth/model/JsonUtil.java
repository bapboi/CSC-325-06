package com.example.csc325_firebase_webview_auth.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static String getString(String json, String field) {
    if (json == null) {
      return null;
    }
    Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(json);
    return matcher.find() ? matcher.group(1) : null;
  }
}
