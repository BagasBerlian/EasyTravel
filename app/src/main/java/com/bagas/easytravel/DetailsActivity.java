package com.bagas.easytravel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    TextView tvNama, tvAlamat, tvJamBuka, tvDistance;
    ImageView tvGambar, iconMap;
    LinearLayout linearRating;

    float distance;
    String nama, alamat, jamBuka, gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        initBackBtn();

        tvNama = findViewById(R.id.NamaTempat);
        tvAlamat = findViewById(R.id.AlamatTempat);
        tvJamBuka = findViewById(R.id.jamBuka);
        tvGambar = findViewById(R.id.bgTempat);
        tvDistance = findViewById(R.id.distance);
        iconMap = findViewById(R.id.iconMap);
        linearRating = findViewById(R.id.linearRating);

        gambar = getIntent().getStringExtra("gambar");
        nama = getIntent().getStringExtra("nama");

        distance = getIntent().getFloatExtra("koordinat", 0);
        String formattedDistance = String.format("%.2f", distance);
        String tampilanDistance = formattedDistance + " KM dari tempat anda";

        if (getIntent().getStringExtra("alamat") != null) {
            alamat = getIntent().getStringExtra("alamat");
        } else {
            linearRating.setVisibility(View.GONE);
            iconMap.setVisibility(View.GONE);
            alamat = getIntent().getStringExtra("kategori");
        }

        if (getIntent().getStringExtra("jam-buka") != null) {
            jamBuka = getIntent().getStringExtra("jam-buka");
        } else {
            tvJamBuka.setVisibility(View.GONE);
        }
        tvNama.setText(nama);
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