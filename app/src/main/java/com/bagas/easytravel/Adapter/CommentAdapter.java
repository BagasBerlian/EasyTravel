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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<ModelComment> commentList;
    private final String currentUserId;

    public CommentAdapter(List<ModelComment> commentList, String currentUserId) {
        this.commentList = commentList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        ModelComment comment = commentList.get(position);
        String displayName = comment.getUserId().equals(currentUserId) ? "saya" : comment.getUsername();
        holder.userName.setText(displayName);
        holder.description.setText(comment.getComment());
        holder.commentRating.setText(String.valueOf(comment.getRating()));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
        holder.timestamp.setText(sdf.format(comment.getTimestamp().toDate()));

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, description, timestamp, commentRating;
        ImageView imgRatingStar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.UserName);
            description = itemView.findViewById(R.id.CommentDescription);
            timestamp = itemView.findViewById(R.id.timeComment);
            commentRating = itemView.findViewById(R.id.CommentRating);
            imgRatingStar = itemView.findViewById(R.id.imgRatingStar);
        }
    }

}
