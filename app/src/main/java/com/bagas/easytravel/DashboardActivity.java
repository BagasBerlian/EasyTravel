package com.bagas.easytravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    CardView hotelCard, wisataCard, kulinerCard;
    LinearLayout profileCard;
    TextView namaUser, tanggal;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale.setDefault(new Locale("id", "ID"));
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        initDate();
        initListCard();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        initProfileCard();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initListCard() {
        hotelCard = findViewById(R.id.hotelcard);
        wisataCard = findViewById(R.id.wisatacard);
        kulinerCard = findViewById(R.id.kulinercard);

        hotelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DaftarHotelActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

//        wisataCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), DaftarWisataActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//            }
//        });
//
//        kulinerCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), DaftarKulinerActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//            }
//        });
    }

    private void initDate() {
        tanggal = findViewById(R.id.teksTanggal);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        String currentDate = sdf.format(new Date());
        tanggal.setText(currentDate);
    }

    private void initProfileCard() {
        profileCard = findViewById(R.id.profileCard);
        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        namaUser = findViewById(R.id.namaUser);
        if (user != null) {
            String email = user.getEmail();
            String username = email != null ? email.split("@")[0] : "Guest";
            namaUser.setText(username);
        }
    }
}