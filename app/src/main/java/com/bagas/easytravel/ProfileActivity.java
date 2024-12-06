package com.bagas.easytravel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    CardView logoutBtn, bookmarkButton, editProfile, btnCommentHistory;
    ImageView backButton;
    TextView namaUser, emailUser;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        initBack();
        initEditProfile();
        initCommentHistory();
        initBookmark();
        initLogoutBtn();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        profileDetail();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initCommentHistory() {
        btnCommentHistory = findViewById(R.id.CommentHistoryButton);
        btnCommentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, CommentHistoryActivity.class));
            }
        });
    }

    private void initEditProfile() {
        editProfile = findViewById(R.id.EditProfileButton);
        editProfile.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });
    }

    private void initBookmark() {
        bookmarkButton = findViewById(R.id.BookmarkButton);
        bookmarkButton.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, ListBookmarkActivity.class));
        });
    }

    private void profileDetail() {
        user = mAuth.getCurrentUser();
        namaUser = findViewById(R.id.namaUser);
        emailUser = findViewById(R.id.emailUser);
        if (user != null) {
            String email = user.getEmail();
            emailUser.setText(email);

            db.collection("users")
                    .whereEqualTo("email", email)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            namaUser.setText("Guest");
                            Log.d("Firestore", "Error getting documents: " + e.getMessage());
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String nama = document.getString("nama");
                            if (nama != null) {
                                namaUser.setText(nama);
                            } else {
                                namaUser.setText("Guest");
                            }
                        } else {
                            namaUser.setText("Guest");
                        }
                    });
        }
    }

    private void initBack() {
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initLogoutBtn() {
        logoutBtn = findViewById(R.id.LogoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getBaseContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                Toast.makeText(getApplicationContext(), "Kehadiran anda akan selalu kami tunggu.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}