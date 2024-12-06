package com.bagas.easytravel.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.easytravel.Model.ModelComment;
import com.bagas.easytravel.R;

import java.util.List;

public class CommentHistoryAdapter extends RecyclerView.Adapter<CommentHistoryAdapter.CommentHistoryViewHolder> {

    private List<ModelComment> comments;

    public CommentHistoryAdapter(List<ModelComment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentHistoryAdapter.CommentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_history, parent, false);
        return new CommentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHistoryAdapter.CommentHistoryViewHolder holder, int position) {
        ModelComment comment = comments.get(position);
        holder.tvCommentName.setText(comment.getPlaceId());
        holder.tvCommentDescription.setText(comment.getComment());
        holder.tvCommentRating.setText(String.valueOf(comment.getRating()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentName, tvCommentDescription, tvCommentRating;
        ImageView imgRatingStar;

        public CommentHistoryViewHolder(View itemView) {
            super(itemView);
            tvCommentName = itemView.findViewById(R.id.tvCommentName);
            tvCommentDescription = itemView.findViewById(R.id.tvCommentDescription);
            tvCommentRating = itemView.findViewById(R.id.tvCommentRating);
            imgRatingStar = itemView.findViewById(R.id.imgRatingStar);
        }
    }
}
