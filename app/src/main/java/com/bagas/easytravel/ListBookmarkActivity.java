package com.bagas.easytravel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

public class ListBookmarkActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    List<ModelBookmark> bookmarks;
    BookmarkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_bookmark);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(ListBookmarkActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewBookmark);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookmarks = new ArrayList<>();
        adapter = new BookmarkAdapter(bookmarks);
        recyclerView.setAdapter(adapter);

        loadBookmarks();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadBookmarks() {
        String userId = user.getUid();

        db.collection("bookmarks")
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookmark", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ModelBookmark> bookmarks = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ModelBookmark bookmark = document.toObject(ModelBookmark.class);
                        if (bookmark != null) {
                            bookmarks.add(bookmark);
                        }
                    }
                    adapter.setBookmarks(bookmarks);
                })
                .addOnFailureListener(e -> Log.e("ListBookmark", "Gagal memuat bookmarks.", e));
    }
}