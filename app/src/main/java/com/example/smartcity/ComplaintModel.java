package com.example.smartcity;

import com.google.firebase.Timestamp;

public class ComplaintModel {
    private String docId;
    private String issue;
    private double latitude;
    private double longitude;
    private String status;
    private String imagePath;

    private String userId;
    private String description;
    private String date;

    private Timestamp createdAt;   // ✅ change long -> Timestamp

    public ComplaintModel() {}

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}