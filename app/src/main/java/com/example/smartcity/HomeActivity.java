package com.example.smartcity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnReport, btnMap, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnReport = findViewById(R.id.btnReport);
        btnMap = findViewById(R.id.btnMap);
        btnAdmin = findViewById(R.id.btnAdmin);

        btnReport.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ReportIssueActivity.class)));

        btnMap.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, LiveMapActivity.class)));

        btnAdmin.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AdminComplaintsActivity.class)));
    }
}