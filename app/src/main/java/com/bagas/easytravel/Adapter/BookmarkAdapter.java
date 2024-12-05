package com.bagas.easytravel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Model.ModelBookmark;
import com.bagas.easytravel.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context context;
    private List<ModelBookmark> bookmarks;
    private Map<String, Float> averageRating;

    public void removeItem(int position) {
        bookmarks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bookmarks.size());
    }

    public BookmarkAdapter(Context context, List<ModelBookmark> bookmarks, Map<String, Float> averageRating) {
        this.context = context;
        this.bookmarks = bookmarks;
        this.averageRating = averageRating;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        ModelBookmark bookmark = bookmarks.get(position);
        holder.tvBookmarkName.setText(bookmark.getPlaceId());
        Glide.with(context)
                .load(bookmark.getImage_url())
                .placeholder(R.drawable.iconn)
                .error(R.drawable.iconn)
                .into(holder.imgBookmark);

        Float rataRating = averageRating.get(bookmark.getPlaceId());
        if (rataRating != null) {
            holder.ratingBar.setRating(rataRating);
            holder.tvRating.setText(String.format("%.1f/5", rataRating));
        }

        holder.btnDeleteBookmark.setOnClickListener(v -> {
            String bookmarkId = bookmarks.get(position).getPlaceId();
            String userId = bookmarks.get(position).getUserId();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("bookmarks")
                    .whereEqualTo("placeId", bookmarkId)
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                document.getReference().update("is_bookmark", false)
                                        .addOnSuccessListener(aVoid -> {
                                            removeItem(position);
                                            Toast.makeText(context, "Bookmark dihapus", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Gagal menghapus bookmark", Toast.LENGTH_SHORT).show()
                                        );
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Gagal mengambil data bookmark", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }


    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookmarkName, tvRating;
        ImageView imgBookmark, btnDeleteBookmark;
        RatingBar ratingBar;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookmarkName = itemView.findViewById(R.id.tvBookmarkName);
            tvRating = itemView.findViewById(R.id.tvRatingBookmark);
            imgBookmark = itemView.findViewById(R.id.imgBookmark);
            ratingBar = itemView.findViewById(R.id.ratingBarBookmark);
            btnDeleteBookmark = itemView.findViewById(R.id.btnDeleteBookmark);
        }
    }
}
