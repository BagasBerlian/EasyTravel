package com.bagas.easytravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

public class DaftarHotelActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerViewHotel;
    HotelAdapter hotelAdapter;
    List<ModelHotel> hotelList;

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
        getHotel();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

                                String alamat = hotelObj.getString("alamat");
                                String[] alamatParts = alamat.split(", ");
                                String jalan = alamatParts[0];
                                String kecamatan = alamatParts[1];
                                String tampilanAlamat = jalan + ", " + kecamatan;
                                hotel.setAlamat(tampilanAlamat);

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
}