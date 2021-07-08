package com.example.instagram.adapters;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.ParcelableObject;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.example.instagram.activities.DetailActivity;
import com.example.instagram.activities.MainActivity;
import com.example.instagram.activities.ProfileActivity;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvDescription;
        private ImageView ivImage;
        private TextView tvRelativeTime;
        private ImageView ivProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToDetailActivity();
                }
            });
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfileActivity();
                }
            });
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfileActivity();
                }
            });
        }

        public void goToProfileActivity() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                ParcelableObject parcel = new ParcelableObject();
                parcel.setPost(post);
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("postObject", Parcels.wrap(parcel));
                ((MainActivity) context).startActivity(intent);
            }
        }

        public void goToDetailActivity() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                ParcelableObject parcel = new ParcelableObject();
                parcel.setPost(post);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("postObject", Parcels.wrap(parcel));
                //intent.putExtra("postPosition", position);
                ((MainActivity) context).startActivity(intent);
            }
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(Html.fromHtml("<b>" + post.getUser().getUsername() + "</b> " + post.getDescription()));
            tvUsername.setText(post.getUser().getUsername());
            tvRelativeTime.setText(Post.calculateTimeAgo(post.getCreatedAt()));
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            }
            ParseFile profilePicture = (ParseFile) post.getUser().get("profilePicture");
            if (profilePicture != null) {
                Glide.with(context).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
            }
        }
    }
}



