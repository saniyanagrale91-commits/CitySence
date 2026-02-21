package com.example.smartcity;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AdminComplaintsActivity extends AppCompatActivity {

    private static final String TAG = "AdminComplaintsActivity";

    RecyclerView recyclerView;
    ComplaintsAdapter adapter;
    ArrayList<ComplaintModel> list = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        recyclerView = findViewById(R.id.recyclerComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ComplaintsAdapter(this, list);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadComplaints();
    }

    private void loadComplaints() {
        db.collection("complaints")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e(TAG, "Firestore listen error", error);
                        return;
                    }
                    if (value == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        try {
                            ComplaintModel c = doc.toObject(ComplaintModel.class);

                            if (c != null) {
                                c.setDocId(doc.getId());

                                // ✅ SAFETY: handle createdAt type (Timestamp or Long)
                                Object created = doc.get("createdAt");

                                if (created instanceof Timestamp) {
                                    // if your ComplaintModel has Timestamp createdAt, it will already be set
                                    // if it has long createdAt, DO NOT crash; ignore here
                                } else if (created instanceof Long) {
                                    // old docs may have long
                                }

                                list.add(c);
                            }
                        } catch (Exception ex) {
                            // ✅ If one document is wrong type, skip it instead of crashing
                            Log.e(TAG, "Skipping bad doc: " + doc.getId(), ex);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}