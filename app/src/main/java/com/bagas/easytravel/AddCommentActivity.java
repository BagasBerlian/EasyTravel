package com.bagas.easytravel;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCommentActivity extends AppCompatActivity {

    private EditText commentInput;
    private RatingBar ratingBar;
    private Button postButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String placeId, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_comment);
        initBackBtn();

        commentInput = findViewById(R.id.commentInput);
        ratingBar = findViewById(R.id.ratingBar);
        postButton = findViewById(R.id.postButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        placeId = getIntent().getStringExtra("placeId");
        type = getIntent().getStringExtra("type");

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void postComment() {
        Date currentDate = new Date();

        String comment = commentInput.getText().toString();
        float rating = ratingBar.getRating();

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(this, "Pastikan Komentar & Rating terisi", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("userId", currentUser.getUid());
        commentData.put("placeId", placeId);
        commentData.put("type", type);
        commentData.put("comment", comment);
        commentData.put("rating", rating);
        commentData.put("timestamp", currentDate);

        db.collection("comments")
                .add(commentData)
                .addOnSuccessListener(documentReference -> {
                    Intent intent = new Intent(AddCommentActivity.this, DetailsActivity.class);
                    intent.putExtra("placeId", placeId);
                    intent.putExtra("isNewCommentAdded", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(AddCommentActivity.this, "Komentar berhasil dikirim", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to add comment", e);
                    Toast.makeText(AddCommentActivity.this, "Gagal mengirim komentar", Toast.LENGTH_SHORT).show();
                });
    }


    private void initBackBtn() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}