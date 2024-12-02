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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Adapter.CommentAdapter;
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
    ImageButton buttonMaps;
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

    String placeId;

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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(DetailsActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }


        type = getIntent().getStringExtra("type");
        initCommentBtn(type);

        nama = getIntent().getStringExtra("nama");

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("isNewCommentAdded", false)) {
            loadComment(getIntent().getStringExtra("placeId"));
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
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        commentList.clear();
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
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
                                    .addOnFailureListener(e -> {
                                        Log.d("load_comment", "Gagal mengambil data user", e);
                                    });
                        }
                    } else {
                        Log.d("load_comment", "Tidak ada dokumen yang ditemukan");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("load_comment", "Gagal load comment", e);
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
                startActivity(intent);
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
                startActivity(new Intent(getApplicationContext(), CommentActivity.class));
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