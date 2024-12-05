package com.bagas.easytravel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Adapter.BookmarkAdapter;
import com.bagas.easytravel.Model.ModelBookmark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListBookmarkActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BookmarkAdapter adapter;
    List<ModelBookmark> bookmarkList;
    Map<String, Float> averageRatings;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_bookmark);
        initBtnBack();

        recyclerView = findViewById(R.id.recyclerViewBookmark);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookmarkList = new ArrayList<>();
        averageRatings = new HashMap<>();
        adapter = new BookmarkAdapter(this, bookmarkList, averageRatings);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadBookmarks();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initBtnBack() {
        btnBack = findViewById(R.id.btnBackBookmark);
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void loadBookmarks() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("bookmarks")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ModelBookmark bookmark = doc.toObject(ModelBookmark.class);
                        bookmarkList.add(bookmark);

                        db.collection("comments")
                                .whereEqualTo("placeId", bookmark.getPlaceId())
                                .get()
                                .addOnSuccessListener(ratingSnapshots -> {
                                    float totalRating = 0;
                                    int count = 0;

                                    for (QueryDocumentSnapshot ratingDoc : ratingSnapshots) {
                                        Number rating = ratingDoc.getDouble("rating");
                                        if (rating != null) {
                                            totalRating += rating.floatValue();
                                            count++;
                                        }
                                    }

                                    if (count > 0) {
                                        averageRatings.put(bookmark.getPlaceId(), totalRating / count);
                                    }

                                    adapter.notifyDataSetChanged();
                                });
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load bookmarks", Toast.LENGTH_SHORT).show();
                });
    }
}