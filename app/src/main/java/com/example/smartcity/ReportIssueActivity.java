package com.example.smartcity;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportIssueActivity extends AppCompatActivity {

    private static final String TAG = "ReportIssueActivity";

    private ImageView imgPreview;
    private Button btnCamera, btnLocation, btnSubmit;
    private Spinner spIssue;
    private TextView txtLocation, txtUserId;
    private EditText edtDescription;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private double latitude = 0, longitude = 0;

    private Uri photoUri = null;
    private String photoLocalPath = null;
    private boolean photoCaptured = false;

    private String userId;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        imgPreview = findViewById(R.id.imgPreview);
        btnCamera = findViewById(R.id.btnCamera);
        btnLocation = findViewById(R.id.btnLocation);
        btnSubmit = findViewById(R.id.btnSubmit);
        spIssue = findViewById(R.id.spIssue);
        txtLocation = findViewById(R.id.txtLocation);

        edtDescription = findViewById(R.id.edtDescription);
        txtUserId = findViewById(R.id.txtUserId);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // ✅ userId
        userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (txtUserId != null) {
            txtUserId.setText("UserId: " + userId);
        }

        String[] issues = {"Pothole", "Garbage", "Street Light", "Water Leakage"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, issues
        );
        spIssue.setAdapter(adapter);

        // Camera result
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        photoCaptured = true;
                        imgPreview.setImageURI(photoUri);
                    } else {
                        photoCaptured = false;
                        Toast.makeText(this, "Photo capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Camera permission
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) openCamera();
                    else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
        );

        // Location permission
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean fine = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                    boolean coarse = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                    if (fine || coarse) fetchLocationNow();
                    else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
        );

        btnCamera.setOnClickListener(v -> askCameraThenOpen());
        btnLocation.setOnClickListener(v -> askLocationThenFetch());
        btnSubmit.setOnClickListener(v -> submitComplaint());
    }

    // ---------------- CAMERA ----------------

    private void askCameraThenOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File photoFile = createImageFile();
            photoLocalPath = photoFile.getAbsolutePath();

            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );

            takePictureLauncher.launch(photoUri);

        } catch (Exception e) {
            photoCaptured = false;
            Toast.makeText(this, "Camera error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) storageDir.mkdirs();
        String fileName = "complaint_" + System.currentTimeMillis();
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    // ---------------- LOCATION ----------------

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void askLocationThenFetch() {
        if (hasLocationPermission()) {
            fetchLocationNow();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchLocationNow() {
        if (!hasLocationPermission()) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        txtLocation.setText("Lat: " + latitude + "  Lng: " + longitude);
                    } else {
                        txtLocation.setText("Location is null. Try again.");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ---------------- SUBMIT ----------------

    private void submitComplaint() {

        String description = edtDescription.getText().toString().trim();

        if (!photoCaptured || photoLocalPath == null) {
            Toast.makeText(this, "Please capture photo first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitude == 0 || longitude == 0) {
            Toast.makeText(this, "Please get location first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return;
        }

        String issueType = spIssue.getSelectedItem().toString();
        String dateText = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
                .format(new Date());

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        Map<String, Object> data = new HashMap<>();
        data.put("issue", issueType);
        data.put("description", description);
        data.put("userId", userId);
        data.put("date", dateText);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        data.put("imagePath", photoLocalPath);
        data.put("status", "PENDING");

        // ✅ Better: server timestamp (real time + correct ordering)
        data.put("createdAt", FieldValue.serverTimestamp());

        // ✅ Debug log (so you know what is going to Firestore)
        Log.d(TAG, "Submitting: " + data.toString());

        db.collection("complaints")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    // ✅ show document ID so you can open exact record in Firestore
                    Toast.makeText(this,
                            "Saved ✅ ID: " + documentReference.getId(),
                            Toast.LENGTH_LONG
                    ).show();

                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit Complaint");
                    resetForm();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore error", e);
                    Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit Complaint");
                });
    }

    private void resetForm() {
        photoCaptured = false;
        photoUri = null;
        photoLocalPath = null;
        imgPreview.setImageDrawable(null);

        edtDescription.setText("");
        txtLocation.setText("Location not fetched");
        latitude = 0;
        longitude = 0;
    }
}