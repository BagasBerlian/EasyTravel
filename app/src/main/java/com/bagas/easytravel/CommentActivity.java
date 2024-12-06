package com.bagas.easytravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Adapter.CommentAdapter;
import com.bagas.easytravel.Model.ModelComment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser user;

    ProgressDialog progressDialog;
    RecyclerView rvComment;
    CommentAdapter commentAdapter;
    List<ModelComment> commentList;

    ImageButton backBtn;
    Button addCommentBtn;

    String placeId, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);
        initBackBtn();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon ditunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memuat komentar...");

        placeId = getIntent().getStringExtra("placeId");
        type = getIntent().getStringExtra("type");

        rvComment = findViewById(R.id.recyclerViewComments);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, user != null ? user.getUid() : null);
        rvComment.setAdapter(commentAdapter);

        loadComments(placeId);
        initCommentBtn();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initCommentBtn() {
        addCommentBtn = findViewById(R.id.AddCommentButton);
        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);
                intent.putExtra("placeId", placeId);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    private void loadComments(String placeId) {
        progressDialog.show();
        db.collection("comments")
                .whereEqualTo("placeId", placeId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("CommentActivity", "Error saat mengambil data komentar", e);
                        progressDialog.dismiss();
                        return;
                    }
                    progressDialog.dismiss();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("CommentActivity", "Jumlah komentar: " + querySnapshot.size());
                        commentList.clear();
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        String currentUserId = user.getUid();
                        for (DocumentSnapshot document : documents) {
                            Log.d("CommentActivity", "Data Komentar: " + document.getData());

                            ModelComment comment = document.toObject(ModelComment.class);
                            if (comment != null) {
                                String userId = comment.getUserId();

                                db.collection("users")
                                        .whereEqualTo("uuid", userId)
                                        .get()
                                        .addOnSuccessListener(userQuery -> {
                                            if (!userQuery.isEmpty()) {
                                                String username = userQuery.getDocuments().get(0).getString("nama");
                                                if (userId.equals(currentUserId)) {
                                                    username = "saya";
                                                }
                                                comment.setUsername(username);

                                                commentList.add(comment);

                                                Log.d("CommentActivity", "Komentar Setelah Update: " + comment.toString());

                                                commentAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(err -> {
                                            Log.e("CommentActivity", "Gagal mengambil data user", err);
                                        });
                            } else {
                                Log.e("CommentActivity", "Gagal mengonversi dokumen ke ModelComment");
                            }
                        }
                    } else {
                        Log.d("CommentActivity", "Tidak ada komentar ditemukan");
                    }
                });
    }


    private void initBackBtn() {
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}