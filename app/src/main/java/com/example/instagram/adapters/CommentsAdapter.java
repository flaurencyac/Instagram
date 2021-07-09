package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.models.Comment;
import com.parse.Parse;
import com.parse.ParseFile;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivProfilePicture;
        private TextView tvCommentary;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvCommentary = itemView.findViewById(R.id.tvCommentary);
        }

        public void bind(Comment comment) {
            tvCommentary.setText(comment.getCommentary());
            tvUsername.setText(comment.getCommenter().getUsername());
            ParseFile profilePicture = (ParseFile) comment.getCommenter().getParseFile("profilePicture");
            if (profilePicture != null) {
                Glide.with(context).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
            } else {
                Glide.with(context).load(R.drawable.ic_baseline_account_circle_24).circleCrop().into(ivProfilePicture);
            }
        }
    }
}
