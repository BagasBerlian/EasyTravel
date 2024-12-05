package com.bagas.easytravel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Adapter.CommentAdapter;
import com.bagas.easytravel.Model.ModelBookmark;
import com.bagas.easytravel.Model.ModelComment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    TextView tvNama, tvAlamat, tvJamBuka, tvDistance, tvDeskripsi;
    TextView txtRatingValue;
    TextView txtAboutPlace;
    TextView txtMoreComment;
    ImageView tvGambar, iconMap;
    ImageButton buttonMaps, buttonBookmark;
    Button commentBtn;

    float totalRating;
    int commentCount;

    double latitude;
    double longitude;

    float distance;
    String type, nama, deskripsi, alamat,
            jamBuka, gambar,tampilanDistance,
            formattedDistance;

    RecyclerView rvComment;
    CommentAdapter commentAdapter;
    List<ModelComment> commentList;

    String placeId, getUserId;
    Integer a = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        initBackBtn();
        initMoreCommentBtn();

        tvNama = findViewById(R.id.NamaTempat);
        tvDeskripsi = findViewById(R.id.DeskripsiTempat);
        tvAlamat = findViewById(R.id.AlamatTempat);
        tvJamBuka = findViewById(R.id.jamBuka);
        tvGambar = findViewById(R.id.bgTempat);
        tvDistance = findViewById(R.id.distance);
        iconMap = findViewById(R.id.iconMap);
        buttonMaps = findViewById(R.id.ButtonMaps);
        txtAboutPlace = findViewById(R.id.txtAboutPlace);
        txtRatingValue = findViewById(R.id.RatingValue);
        buttonBookmark = findViewById(R.id.BookmarkButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        getUserId = user.getUid();

        if (user == null) {
            startActivity(new Intent(DetailsActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        type = getIntent().getStringExtra("type");
        initCommentBtn(type);

        nama = getIntent().getStringExtra("nama");
        if (nama == null || nama.isEmpty()) {
            nama = "Nama tidak ditemukan";
        }

        if (getIntent().getStringExtra("alamat") != null) {
            alamat = getIntent().getStringExtra("alamat");
        } else {
            // Kondisi khusus Wisata
            iconMap.setVisibility(View.GONE);
            alamat = getIntent().getStringExtra("kategori");
        }

        gambar = getIntent().getStringExtra("gambar");

        distance = getIntent().getFloatExtra("koordinat", 0);
        formattedDistance = String.format("%.2f", distance);
        tampilanDistance = formattedDistance + " KM dari tempat anda";

        if (getIntent().getStringExtra("deskripsi") != null) {
            deskripsi = getIntent().getStringExtra("deskripsi");
        } else {
            txtAboutPlace.setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("jam-buka") != null) {
            jamBuka = getIntent().getStringExtra("jam-buka");
        } else {
            tvJamBuka.setVisibility(View.GONE);
        }

        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        handleMaps();

        tvNama.setText(nama);
        tvDeskripsi.setText(deskripsi);
        tvAlamat.setText(alamat);
        tvDistance.setText(tampilanDistance);
        tvJamBuka.setText(jamBuka);
        Glide.with(this)
                .load(gambar)
                .placeholder(R.drawable.grama_tirta_jatiluhur)
                .error(R.drawable.grama_tirta_jatiluhur)
                .into(tvGambar);

        initComment();
        checkBookmark();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkBookmark() {
        db.collection("bookmarks")
                .whereEqualTo("placeId", placeId)
                .whereEqualTo("userId", getUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Boolean isBookmarked = task.getResult().getDocuments().get(0).getBoolean("is_bookmark");
                        if (isBookmarked != null && isBookmarked) {
                            buttonBookmark.setImageResource(R.drawable.bookmark_solid);
                        } else {
                            buttonBookmark.setImageResource(R.drawable.bookmark);
                        }
                    } else {
                        buttonBookmark.setImageResource(R.drawable.bookmark); // default
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("checkBookmark", "Error checking bookmark", e);
                });

        toogleBookmark();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK ) {
            loadComment(placeId);
        }
    }

    private void initComment() {
        placeId = nama;

        rvComment = findViewById(R.id.rvComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, user.toString());
        rvComment.setAdapter(commentAdapter);

        loadComment(placeId);
    }

    private void loadComment(String placeId) {
        db.collection("comments")
                .whereEqualTo("placeId", placeId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.d("load_comment", "Error getting comments: ", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        commentList.clear();
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        String currentUserId = user.getUid();
                        totalRating = 0;
                        commentCount = 0;

                        for (int i = 0; i < documents.size(); i++) {
                            ModelComment comment = documents.get(i).toObject(ModelComment.class)    ;
                            String userId = comment.getUserId();
                            totalRating += comment.getRating();
                            commentCount++;

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
                                            if (commentList.size() == documents.size()) {
                                                Float averageRating = totalRating / commentCount;
                                                txtRatingValue.setText(String.format("%.1f", averageRating));
                                                commentAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(err -> {
                                        Log.d("load_comment", "Gagal mengambil data user", err);
                                    });
                        }
                    } else {
                        Log.d("load_comment", "Tidak ada dokumen yang ditemukan");
                    }
                });
    }

    private void toogleBookmark() {
        buttonBookmark.setOnClickListener(view -> {
            db.collection("bookmarks")
                    .whereEqualTo("placeId", placeId)
                    .whereEqualTo("userId", getUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Jika dokumen ditemukan, toggle nilai `is_bookmark`
                            String documentId = task.getResult().getDocuments().get(0).getId();
                            Boolean isBookmarked = task.getResult().getDocuments().get(0).getBoolean("is_bookmark");
                            boolean newStatus = !(isBookmarked != null && isBookmarked);

                            // Update Firestore
                            db.collection("bookmarks").document(documentId)
                                    .update("is_bookmark", newStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        if (newStatus) {
                                            buttonBookmark.setImageResource(R.drawable.bookmark_solid);
                                            Toast.makeText(DetailsActivity.this, "Bookmark ditambahkan", Toast.LENGTH_SHORT).show();
                                        } else {
                                            buttonBookmark.setImageResource(R.drawable.bookmark);
                                            Toast.makeText(DetailsActivity.this, "Bookmark dihapus", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("buttonBookmark", "Gagal memperbarui bookmark", e);
                                    });

                        } else {
                            // Jika dokumen tidak ditemukan, tambahkan dokumen baru dengan `is_bookmark = true`
                            ModelBookmark newBookmark = new ModelBookmark(getUserId, placeId, true);
                            db.collection("bookmarks").add(newBookmark)
                                    .addOnSuccessListener(documentReference -> {
                                        buttonBookmark.setImageResource(R.drawable.bookmark_solid);
                                        Toast.makeText(DetailsActivity.this, "Bookmark ditambahkan", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("buttonBookmark", "Gagal menambahkan bookmark", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("buttonBookmark", "Error checking bookmark", e);
                    });
        });

    }

    private void initCommentBtn(String type) {
        commentBtn = findViewById(R.id.commentButton);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);
                intent.putExtra("placeId", nama);
                intent.putExtra("type", type);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void handleMaps() {
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)));
                }
            }
        });
    }

    private void initMoreCommentBtn() {
        txtMoreComment = findViewById(R.id.BukaKomentar);
        txtMoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("placeId", placeId);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    private void initBackBtn() {
        ImageButton backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}