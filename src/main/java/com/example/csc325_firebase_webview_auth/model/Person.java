package com.example.csc325_firebase_webview_auth.model;

/**
 * Model:
 * Plain data holder for a "References" document in Firestore. docId is not
 * shown in the UI but is kept so the Delete menu action knows which
 * Firestore document to remove.
 */
public class Person {
    private String docId;
    private String name;
    private String major;
    private int age;

    public Person(String docId, String name, String major, int age) {
        this.docId = docId;
        this.name = name;
        this.major = major;
        this.age = age;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
