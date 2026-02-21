package com.example.smartcity;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.VH> {

    Context context;
    ArrayList<ComplaintModel> list;

    public ComplaintsAdapter(Context context, ArrayList<ComplaintModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ComplaintModel c = list.get(position);

        h.txtIssue.setText("Issue: " + safe(c.getIssue()));
        h.txtLatLng.setText("Lat: " + c.getLatitude() + "  Lng: " + c.getLongitude());
        h.txtStatus.setText("Status: " + safe(c.getStatus()));

        // ✅ New fields
        h.txtDate.setText("Date: " + safe(c.getDate()));
        h.txtDesc.setText("Desc: " + safe(c.getDescription()));
        h.txtUserId.setText("User: " + safe(c.getUserId()));

        // Status color
        if ("RESOLVED".equalsIgnoreCase(c.getStatus())) {
            h.txtStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            h.txtStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
        }

        // Load local image from path (works on same phone)
        String path = c.getImagePath();
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                h.img.setImageBitmap(BitmapFactory.decodeFile(path));

                // Click image -> full screen view
                h.img.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                    intent.putExtra("imagePath", path);
                    context.startActivity(intent);
                });

            } else {
                h.img.setImageResource(android.R.color.darker_gray);
                h.img.setOnClickListener(null);
            }
        } else {
            h.img.setImageResource(android.R.color.darker_gray);
            h.img.setOnClickListener(null);
        }

        // Click item -> Open Google Maps
        h.itemView.setOnClickListener(v -> {
            double lat = c.getLatitude();
            double lng = c.getLongitude();

            String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + safe(c.getIssue()) + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            try {
                context.startActivity(intent);
            } catch (Exception e) {
                // If Google Maps not installed
                String webUrl = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng;
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
            }
        });
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtIssue, txtLatLng, txtStatus;
        TextView txtDate, txtDesc, txtUserId; // ✅ new

        public VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            txtIssue = itemView.findViewById(R.id.txtIssue);
            txtLatLng = itemView.findViewById(R.id.txtLatLng);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            // ✅ new ids (must exist in item_complaint.xml)
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtUserId = itemView.findViewById(R.id.txtUserId);
        }
    }
}