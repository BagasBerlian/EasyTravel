package com.bagas.easytravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bagas.easytravel.API.Api;
import com.bagas.easytravel.Adapter.KulinerAdapter;
import com.bagas.easytravel.Model.ModelKuliner;
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

public class DaftarKulinerActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    ProgressDialog progressDialog;
    RecyclerView recyclerViewKuliner;
    KulinerAdapter kulinerAdapter;
    List<ModelKuliner> kulinerList;

    FusedLocationProviderClient fusedLocationClient;
    double userLatitude;
    double userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_kuliner);
        initBackBtn();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon ditunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memuat data...");

        recyclerViewKuliner = findViewById(R.id.recyclerViewKuliner);
        recyclerViewKuliner.setLayoutManager(new GridLayoutManager(this, 2));
        kulinerList = new ArrayList<>();
        kulinerAdapter = new KulinerAdapter(kulinerList);
        recyclerViewKuliner.setAdapter(kulinerAdapter);

        AndroidNetworking.initialize(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initBackBtn() {
        ImageView btnBack = findViewById(R.id.btnBackKuliner);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        getKuliner();
                    } else {
                        requestLocationUpdates();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Error mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(DaftarKulinerActivity.this, "Gagal mendapatkan lokasi real-time", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                    getKuliner();
                    fusedLocationClient.removeLocationUpdates(this);
                }
            }
        }, getMainLooper());
    }

    private void getKuliner() {
        progressDialog.show();
        AndroidNetworking.get(Api.Kuliner)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("API Response", response.toString());
                        try {
                            JSONArray kuliners = response.getJSONArray("kuliner");
                            for (int i = 0; i < kuliners.length(); i++) {
                                JSONObject kulinerObj = kuliners.getJSONObject(i);
                                ModelKuliner kuliner = new ModelKuliner();

                                String koordinat = kulinerObj.optString("kordinat", "");
                                if (koordinat.contains(",")) {
                                    String[] koor = koordinat.split(",");
                                    try {
                                        kuliner.setLatitude(Double.parseDouble(koor[0].trim()));
                                        kuliner.setLongitude(Double.parseDouble(koor[1].trim()));
                                    } catch (NumberFormatException e) {
                                        kuliner.setLatitude(-6.537805);
                                        kuliner.setLongitude(107.440644);
                                    }
                                } else {
                                    kuliner.setLatitude(-6.5378051);
                                    kuliner.setLongitude(107.440644);
                                }

                                float[] results = new float[1];
                                Location.distanceBetween(
                                        userLatitude,
                                        userLongitude,
                                        kuliner.getLatitude(),
                                        kuliner.getLongitude(),
                                        results
                                );
                                Log.d("Jarak Dihitung", "Jarak: " + results[0] + " meter");
                                float hasil = (float) (results[0] / 100000.0);

                                kuliner.setDistance(hasil);
                                kuliner.setId(kulinerObj.getString("id"));
                                kuliner.setAlamat(kulinerObj.getString("alamat"));
                                kuliner.setJamBuka(kulinerObj.optString("jam_buka_tutup", "Tidak tersedia"));
                                kuliner.setNama(kulinerObj.optString("nama", "Tidak diketahui"));
                                kuliner.setGambar(kulinerObj.optString("gambar_url", ""));

                                kulinerList.add(kuliner);
                            }
                            kulinerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DaftarKulinerActivity.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(DaftarKulinerActivity.this, "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan untuk melanjutkan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}