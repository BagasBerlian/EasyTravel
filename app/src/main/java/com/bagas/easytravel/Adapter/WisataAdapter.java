package com.bagas.easytravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bagas.easytravel.API.Api;
import com.bagas.easytravel.DetailsActivity;
import com.bagas.easytravel.Model.ModelHotel;
import com.bagas.easytravel.Model.ModelWisata;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.WisataViewHolder> {

    private final List<ModelWisata> wisataList;

    public WisataAdapter(List<ModelWisata> wisataList) {
        this.wisataList = wisataList;
    }

    @NonNull
    @Override
    public WisataAdapter.WisataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wisata, parent, false);
        return new WisataAdapter.WisataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WisataAdapter.WisataViewHolder holder, int position) {
        ModelWisata wisata = wisataList.get(position);
        holder.tvWisataName.setText(wisata.getNama());
        holder.tvWisataKategori.setText(wisata.getKategori());

        Glide.with(holder.itemView.getContext())
                .load(wisata.getGambar())
                .placeholder(R.drawable.iconn)
                .error(R.drawable.iconn)
                .into(holder.tvWisataGambar);

        holder.itemView.setOnClickListener(view -> {
            Context context = holder.itemView.getContext();
            String wisataId = wisata.getId();
            Intent intent = new Intent(context, DetailsActivity.class);

            AndroidNetworking.get(Api.DetailWisata + wisataId)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String nama = response.getString("nama");
                                String deskripsi = response.optString("deskripsi", "Deskripsi tidak ada");
                                String gambar = response.getString("gambar_url");
                                double latitude = response.getDouble("latitude");
                                double longitude = response.getDouble("longitude");

                                float[] results = new float[1];
                                Location.distanceBetween(
                                        wisata.getLatitude(),
                                        wisata.getLongitude(),
                                        latitude,
                                        longitude,
                                        results
                                );
                                Log.d("Jarak Dihitung", "Jarak: " + results[0] + " meter");
                                float hasil = (float) (results[0] / 100000.0);

                                intent.putExtra("type", "wisata");
                                intent.putExtra("nama", nama);
                                intent.putExtra("deskripsi", deskripsi);
                                intent.putExtra("gambar", gambar);
                                intent.putExtra("koordinat", hasil);
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);

                                context.startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Error parsing detail data!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(context, "Error fetching detail data!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return wisataList.size();
    }

    public class WisataViewHolder extends RecyclerView.ViewHolder {
        TextView tvWisataName, tvWisataKategori;
        ImageView tvWisataGambar;

        public WisataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWisataName = itemView.findViewById(R.id.tvWisataName);
            tvWisataKategori = itemView.findViewById(R.id.tvWisataKategori);
            tvWisataGambar = itemView.findViewById(R.id.imgWisata);
        }
    }
}
