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
import com.bagas.easytravel.Model.ModelHotel;
import com.bagas.easytravel.Model.ModelWisata;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;

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
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("nama", wisata.getNama());
            intent.putExtra("kategori", wisata.getKategori());
            intent.putExtra("gambar", wisata.getGambar());
            context.startActivity(intent);
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
