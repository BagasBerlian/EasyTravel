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
import com.bagas.easytravel.Model.ModelPlace;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class KulinerAdapter extends RecyclerView.Adapter<KulinerAdapter.KulinerViewAdapter> {

    private final List<ModelPlace> kulinerList;

    public KulinerAdapter(List<ModelPlace> kulinerList) {
        this.kulinerList = kulinerList;
    }

    @NonNull
    @Override
    public KulinerAdapter.KulinerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kuliner, parent, false);
        return new KulinerAdapter.KulinerViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KulinerAdapter.KulinerViewAdapter holder, int position) {
        ModelPlace kuliner = kulinerList.get(position);

        String kulinerName = kuliner.getNama();
        String[] words = kulinerName.split(" ");

        if (words.length > 3) {
            kulinerName = words[0] + " " + words[1] + " " + words[2] + "...";
        }

        holder.tvKulinerName.setText(kulinerName);

        String distanceText = String.format("%.2f km", kuliner.getDistance());
        holder.tvKulinerDistance.setText(distanceText);

        if (kuliner.getGambar() != null && !kuliner.getGambar().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(kuliner.getGambar())
                    .placeholder(R.drawable.iconn)
                    .error(R.drawable.iconn)
                    .into(holder.tvKulinerGambar);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.iconn)
                    .into(holder.tvKulinerGambar);
        }

        holder.itemView.setOnClickListener(view -> {
            Context context = holder.itemView.getContext();
            String kulinerId = kuliner.getId();
            Float distance = kuliner.getDistance();
            Double latitude = kuliner.getLatitude();
            Double longitude = kuliner.getLongitude();
            Intent intent = new Intent(context, DetailsActivity.class);

            AndroidNetworking.get(Api.DetailKuliner + kulinerId)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String nama = response.getString("nama");
                                String alamat = response.getString("alamat");
                                String jamBuka = response.getString("jam_buka_tutup");
                                String deskripsi = response.optString("deskripsi", "Deskripsi tidak ada");
                                String gambar = response.getString("gambar_url");

                                intent.putExtra("type", "kuliner");
                                intent.putExtra("nama", nama);
                                intent.putExtra("alamat", alamat);
                                intent.putExtra("jam-buka", jamBuka);
                                intent.putExtra("deskripsi", deskripsi);
                                intent.putExtra("gambar", gambar);
                                intent.putExtra("koordinat", distance);
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
        return kulinerList.size();
    }


    public class KulinerViewAdapter extends RecyclerView.ViewHolder {
        TextView tvKulinerName, tvKulinerDistance;
        ImageView tvKulinerGambar;

        public KulinerViewAdapter(@NonNull View itemView) {
            super(itemView);
            tvKulinerName = itemView.findViewById(R.id.tvKulinerName);
            tvKulinerDistance = itemView.findViewById(R.id.tvKulinerDistance);
            tvKulinerGambar = itemView.findViewById(R.id.imgKuliner);
        }
    }
}
