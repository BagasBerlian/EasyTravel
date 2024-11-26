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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bagas.easytravel.API.Api;
import com.bagas.easytravel.Adapter.HotelAdapter;
import com.bagas.easytravel.Model.ModelHotel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class DaftarHotelActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    ProgressDialog progressDialog;
    RecyclerView recyclerViewHotel;
    HotelAdapter hotelAdapter;
    List<ModelHotel> hotelList;

    FusedLocationProviderClient fusedLocationClient;
    double userLatitude;
    double userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_hotel);
        initBtnBack();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon ditunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memuat data...");

        recyclerViewHotel = findViewById(R.id.recyclerViewHotel);
        recyclerViewHotel.setLayoutManager(new LinearLayoutManager(this));
        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(hotelList);
        recyclerViewHotel.setAdapter(hotelAdapter);

        AndroidNetworking.initialize(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        getHotel();
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
                    Toast.makeText(DaftarHotelActivity.this, "Gagal mendapatkan lokasi real-time", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                    fusedLocationClient.removeLocationUpdates(this);
                    getHotel();
                }
            }
        }, getMainLooper());
    }

    private void initBtnBack() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getHotel() {
        progressDialog.show();
        AndroidNetworking.get(Api.Hotel)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("API Response", response.toString());
                        try {
                            JSONArray hotels = response.getJSONArray("hotel");
                            for (int i = 0; i < hotels.length(); i++) {
                                JSONObject hotelObj = hotels.getJSONObject(i);
                                ModelHotel hotel = new ModelHotel();

                                hotel.setNama(hotelObj.getString("nama"));
                                hotel.setAlamat(hotelObj.getString("alamat"));

                                String koordinat = hotelObj.optString("kordinat", "");
                                if (koordinat.contains(",")) {
                                    String[] koor = koordinat.split(",");
                                    try {
                                        hotel.setLatitude(Double.parseDouble(koor[0].trim()));
                                        hotel.setLongitude(Double.parseDouble(koor[1].trim()));
                                    } catch (NumberFormatException e) {
                                        hotel.setLatitude(0.0);
                                        hotel.setLongitude(0.0);
                                    }
                                } else {
                                    hotel.setLatitude(-6.5378051);
                                    hotel.setLongitude(107.440644);
                                }

                                float[] results = new float[1];
                                Location.distanceBetween(
                                        hotel.getLatitude(),
                                        hotel.getLongitude(),
                                        userLatitude,
                                        userLongitude,
                                        results
                                );
                                float hasil = (float) (results[0] / 1000.0);

                                hotel.setDistance(hasil);
                                hotel.setGambar(hotelObj.getString("gambar_url"));
                                hotelList.add(hotel);
                            }
                            hotelAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DaftarHotelActivity.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(DaftarHotelActivity.this, "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
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

