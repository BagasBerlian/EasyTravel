package com.bagas.easytravel.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Model.ModelBookmark;
import com.bagas.easytravel.R;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<ModelBookmark> bookmarks;

    public BookmarkAdapter(List<ModelBookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, int position) {
        ModelBookmark bookmark = bookmarks.get(position);
        holder.tvBookmarkName.setText(bookmark.getPlaceId());
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    public void setBookmarks(List<ModelBookmark> bookmarks) {
        this.bookmarks = bookmarks;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookmarkName;

        public  ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookmarkName = itemView.findViewById(R.id.tvBookmarkName);
        }
    }
}
