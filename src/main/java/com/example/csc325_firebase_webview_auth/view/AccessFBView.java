package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.service.StorageService;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class AccessFBView implements Initializable {

  @FXML
  private TextField nameField;
  @FXML
  private TextField majorField;
  @FXML
  private TextField ageField;
  @FXML
  private Button writeButton;
  @FXML
  private Button readButton;
  @FXML
  private Label statusLabel;
  @FXML
  private Label welcomeLabel;
  @FXML
  private TableView<Person> userTable;
  @FXML
  private TableColumn<Person, String> nameColumn;
  @FXML
  private TableColumn<Person, String> majorColumn;
  @FXML
  private TableColumn<Person, Integer> ageColumn;
  @FXML
  private ImageView profileImageView;

  private final ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
  private final StorageService storageService = new StorageService();

  public ObservableList<Person> getListOfUsers() {
    return listOfUsers;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
    nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
    majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
    writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
    userTable.setItems(listOfUsers);

    if (App.currentEmail != null) {
      welcomeLabel.setText("Signed in as " + App.currentEmail);
    }

    try {
      Image defaultImage = new Image(getClass().getResourceAsStream("/files/icon.jpg"));
      profileImageView.setImage(defaultImage);
    } catch (Exception ex) {
      System.out.println("Default profile picture failed to load: " + ex.getMessage());
    }

    loadSavedProfilePhoto();
    readFirebase();
  }

  @FXML
  private void addRecord(ActionEvent event) {
    addData();
  }

  @FXML
  private void readRecord(ActionEvent event) {
    readFirebase();
  }

  @FXML
  private void deleteRecord(ActionEvent event) {
    Person selected = userTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      statusLabel.setText("Select a row in the table first.");
      return;
    }
    DocumentReference docRef = App.fstore.collection("References").document(selected.getDocId());
    docRef.delete();
    listOfUsers.remove(selected);
    statusLabel.setText("Deleted " + selected.getName() + ".");
  }

  @FXML
  private void uploadPhoto(ActionEvent event) {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose a profile picture");
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    File file = chooser.showOpenDialog(profileImageView.getScene().getWindow());
    if (file == null) {
      return;
    }

    statusLabel.setText("Uploading photo...");
    String extension = file.getName().contains(".")
        ? file.getName().substring(file.getName().lastIndexOf('.'))
        : "";
    String destinationPath = "profile_pictures/" + App.currentUid + extension;

    Task<String> uploadTask = new Task<>() {
      @Override
      protected String call() throws Exception {
        return storageService.uploadFile(file, destinationPath);
      }
    };

    uploadTask.setOnSucceeded(e -> {
      String downloadUrl = uploadTask.getValue();
      profileImageView.setImage(new Image(downloadUrl, true));
      statusLabel.setText("Profile photo updated.");
      if (App.currentUid != null) {
        App.fstore.collection("Users").document(App.currentUid)
            .update("PhotoUrl", downloadUrl);
      }
    });

    uploadTask.setOnFailed(e -> {
      Throwable ex = uploadTask.getException();
      statusLabel.setText("Upload failed: " + (ex == null ? "unknown error" : ex.getMessage()));
    });

    new Thread(uploadTask, "photo-upload").start();
  }

  @FXML
  private void logout(ActionEvent event) throws IOException {
    App.clearSession();
    App.setRoot("/files/Login.fxml");
  }

  @FXML
  private void closeApp(ActionEvent event) {
    Platform.exit();
  }

  @FXML
  private void showAbout(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("About");
    alert.setHeaderText("CSC-325 Firebase Demo");
    alert.setContentText("JavaFX + Firebase Auth/Firestore/Storage demo built for CSC-325.");
    alert.showAndWait();
  }

  private void loadSavedProfilePhoto() {
    if (App.currentUid == null) {
      return;
    }
    Task<String> fetchTask = new Task<>() {
      @Override
      protected String call() throws Exception {
        var snapshot = App.fstore.collection("Users").document(App.currentUid).get().get();
        Object photoUrl = snapshot.exists() ? snapshot.get("PhotoUrl") : null;
        return photoUrl == null ? null : photoUrl.toString();
      }
    };
    fetchTask.setOnSucceeded(e -> {
      String url = fetchTask.getValue();
      if (url != null && !url.isBlank()) {
        profileImageView.setImage(new Image(url, true));
      }
    });
    new Thread(fetchTask, "load-profile-photo").start();
  }

  @FXML
  private void onThemeLight(ActionEvent event) {
    App.setTheme(App.LIGHT_THEME);
    statusLabel.setText("Light theme.");
  }

  @FXML
  private void onThemeDark(ActionEvent event) {
    App.setTheme(App.DARK_THEME);
    statusLabel.setText("Dark theme.");
  }

  public void addData() {
    if (ageField.getText() == null || ageField.getText().isBlank()) {
      statusLabel.setText("Age is required.");
      return;
    }
    int age;
    try {
      age = Integer.parseInt(ageField.getText().trim());
    } catch (NumberFormatException ex) {
      statusLabel.setText("Age must be a number.");
      return;
    }

    DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

    Map<String, Object> data = new HashMap<>();
    data.put("Name", nameField.getText());
    data.put("Major", majorField.getText());
    data.put("Age", age);
    ApiFuture<WriteResult> result = docRef.set(data);

    Task<Void> waitTask = new Task<>() {
      @Override
      protected Void call() throws Exception {
        result.get();
        return null;
      }
    };
    waitTask.setOnSucceeded(e -> {
      statusLabel.setText("Saved.");
      nameField.clear();
      majorField.clear();
      ageField.clear();
      readFirebase();
    });
    waitTask.setOnFailed(e -> statusLabel.setText("Save failed."));
    new Thread(waitTask, "write-record").start();
  }

  public void readFirebase() {
    ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();

    Task<List<QueryDocumentSnapshot>> readTask = new Task<>() {
      @Override
      protected List<QueryDocumentSnapshot> call() throws ExecutionException, InterruptedException {
        return future.get().getDocuments();
      }
    };

    readTask.setOnSucceeded(e -> {
      listOfUsers.clear();
      List<QueryDocumentSnapshot> documents = readTask.getValue();
      for (QueryDocumentSnapshot document : documents) {
        String name = String.valueOf(document.getData().get("Name"));
        String major = String.valueOf(document.getData().get("Major"));
        int age = Integer.parseInt(document.getData().get("Age").toString());
        listOfUsers.add(new Person(document.getId(), name, major, age));
      }
      statusLabel.setText(documents.isEmpty() ? "No records yet." : documents.size() + " record(s) loaded.");
    });

    readTask.setOnFailed(e -> statusLabel.setText("Could not load records."));
    new Thread(readTask, "read-records").start();
  }
}
