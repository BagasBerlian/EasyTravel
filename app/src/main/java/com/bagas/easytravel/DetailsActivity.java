package com.bagas.easytravel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    TextView tvNama, tvAlamat, tvJamBuka, tvDistance, tvDeskripsi;
    TextView txtAboutPlace;
    ImageView tvGambar, iconMap;
    ImageButton buttonMaps;
    Button commentBtn;

    double latitude;
    double longitude;

    float distance;
    String type, nama, deskripsi, alamat,
            jamBuka, gambar,tampilanDistance,
            formattedDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        initBackBtn();

        tvNama = findViewById(R.id.NamaTempat);
        tvDeskripsi = findViewById(R.id.DeskripsiTempat);
        tvAlamat = findViewById(R.id.AlamatTempat);
        tvJamBuka = findViewById(R.id.jamBuka);
        tvGambar = findViewById(R.id.bgTempat);
        tvDistance = findViewById(R.id.distance);
        iconMap = findViewById(R.id.iconMap);
        buttonMaps = findViewById(R.id.ButtonMaps);
        txtAboutPlace = findViewById(R.id.txtAboutPlace);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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