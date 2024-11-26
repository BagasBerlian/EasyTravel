package com.bagas.easytravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.DetailsActivity;
import com.bagas.easytravel.Model.ModelKuliner;
import com.bagas.easytravel.Model.ModelWisata;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class KulinerAdapter extends RecyclerView.Adapter<KulinerAdapter.KulinerViewAdapter> {

    private final List<ModelKuliner> kulinerList;

    public KulinerAdapter(List<ModelKuliner> kulinerList) {
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
        ModelKuliner kuliner = kulinerList.get(position);

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
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("nama", kuliner.getNama());
            intent.putExtra("alamat", kuliner.getAlamat());
            intent.putExtra("jam-buka", kuliner.getJamBuka());
            intent.putExtra("koordinat", kuliner.getDistance());
            intent.putExtra("gambar", kuliner.getGambar());
            context.startActivity(intent);
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
