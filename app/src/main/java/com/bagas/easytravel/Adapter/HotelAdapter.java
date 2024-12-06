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
import com.bagas.easytravel.Model.ModelPlace;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.BreakIterator;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private final List<ModelPlace> hotelList;

    public HotelAdapter(List<ModelPlace> hotelList) {
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelAdapter.HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelAdapter.HotelViewHolder holder, int position) {
        ModelPlace hotel = hotelList.get(position);
        holder.tvHotelName.setText(hotel.getNama());

        String alamat = hotel.getAlamat();
        String[] alamatParts = alamat.split(", ");
        String jalan = alamatParts[0];
        String kecamatan = alamatParts[1];
        String tampilanAlamat = jalan + ", " + kecamatan;
        holder.tvHotelAlamat.setText(tampilanAlamat);

        Glide.with(holder.itemView.getContext())
                .load(hotel.getGambar())
                .placeholder(R.drawable.iconn)
                .error(R.drawable.iconn)
                .into(holder.tvHotelGambar);

        holder.itemView.setOnClickListener(view -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("type", "hotel");
            intent.putExtra("nama", hotel.getNama());
            intent.putExtra("alamat", alamat);
            intent.putExtra("gambar", hotel.getGambar());
            intent.putExtra("koordinat", hotel.getDistance());
            intent.putExtra("latitude", hotel.getLatitude());
            intent.putExtra("longitude", hotel.getLongitude());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView tvHotelName, tvHotelAlamat;
        ImageView tvHotelGambar;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvHotelAlamat = itemView.findViewById(R.id.tvHotelAlamat);
            tvHotelGambar = itemView.findViewById(R.id.imgHotel);
        }
    }
}
