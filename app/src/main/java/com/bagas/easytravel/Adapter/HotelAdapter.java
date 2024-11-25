package com.bagas.easytravel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Model.ModelHotel;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.BreakIterator;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private final List<ModelHotel> hotelList;

    public HotelAdapter(List<ModelHotel> hotelList) {
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
        ModelHotel hotel = hotelList.get(position);
        holder.tvHotelName.setText(hotel.getNama());
        holder.tvHotelAlamat.setText(hotel.getAlamat());
        Glide.with(holder.itemView.getContext())
                .load(hotel.getGambar())
                .error(R.drawable.iconn)
                .into(holder.tvHotelGambar);
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
