package com.bagas.easytravel;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bagas.easytravel.API.Api;
import com.bagas.easytravel.Adapter.HotelAdapter;
import com.bagas.easytravel.Adapter.WisataAdapter;
import com.bagas.easytravel.Model.ModelHotel;
import com.bagas.easytravel.Model.ModelWisata;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DaftarWisataActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerViewWisata;
    WisataAdapter wisataAdapter;
    List<ModelWisata> wisataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_wisata);
        initBckBtn();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon ditunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memuat data...");

        recyclerViewWisata = findViewById(R.id.recyclerViewWisata);
        recyclerViewWisata.setLayoutManager(new LinearLayoutManager(this));
        wisataList = new ArrayList<>();
        wisataAdapter = new WisataAdapter(wisataList);
        recyclerViewWisata.setAdapter(wisataAdapter);

        AndroidNetworking.initialize(getApplicationContext());
        getWisata();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void initBckBtn() {
        ImageView backButton = findViewById(R.id.btnBackWisata);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getWisata() {
        progressDialog.show();
        AndroidNetworking.get(Api.Wisata)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("API Response", response.toString());
                        try {
                            JSONArray wisatas = response.getJSONArray("wisata");
                            for (int i = 0; i < wisatas.length(); i++) {
                                JSONObject wisataObj = wisatas.getJSONObject(i);
                                ModelWisata wisata = new ModelWisata();
                                wisata.setNama(wisataObj.getString("nama"));
                                wisata.setKategori("Kategori: " + wisataObj.getString("kategori"));
                                wisata.setGambar(wisataObj.getString("gambar_url"));
                                wisataList.add(wisata);
                            }
                            wisataAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DaftarWisataActivity.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(DaftarWisataActivity.this, "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}