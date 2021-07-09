package com.example.instagram.adapters;
import android.content.Context;
import android.content.Intent;
import android.os.ConditionVariable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.ParcelableObject;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.example.instagram.activities.DetailActivity;
import com.example.instagram.activities.MainActivity;
import com.example.instagram.activities.ProfileActivity;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String TAG = "PostsAdapter";
    private final int REQUEST_CODE_FAVORITE = 7;

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
        private ImageView ivHeart;
        private TextView tvLikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            // switch the heart to opposite icon depending on whether or not the user is liking or unliking a post
            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = posts.get(getAdapterPosition());
                    likeOrUnlike(post);
                }
            });
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

        public void bind(Post post) {
            tvDescription.setText(Html.fromHtml("<b>" + post.getUser().getUsername() + "</b> " + post.getDescription()));
            tvUsername.setText(post.getUser().getUsername());
            tvRelativeTime.setText(Post.calculateTimeAgo(post.getCreatedAt()));
            tvLikeCount.setText("" + post.getLikeCount() + " Likes");
            queryLikes(post);
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            }
            ParseFile profilePicture = (ParseFile) post.getUser().get("profilePicture");
            if (profilePicture != null) {
                Glide.with(context).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
            } else {
                Glide.with(context).load(R.drawable.ic_baseline_account_circle_24).circleCrop().into(ivProfilePicture);
            }

        }

        public void likeOrUnlike(Post post) {
            int oldLikeCount = post.getLikeCount();
            if (post.getLikeStatus()) {
                // delete a like object to the parse server
                Like.destroyLike(post);
                post.dislike();
                ivHeart.setImageResource(R.drawable.ufi_heart);
                post.put("likes", oldLikeCount - 1);
                tvLikeCount.setText("" + (oldLikeCount - 1) + " Likes");
            } else {
                // create and save a like object from the parse server
                Like like = new Like();
                like.setLikeParentPost(post);
                like.setLiker(ParseUser.getCurrentUser());
                like.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!= null) {
                            Log.e(TAG, "Error saving like object", e);
                            Toast.makeText(context, "Error saving like", Toast.LENGTH_LONG).show();
                        }
                        Log.i(TAG, "Like was a success");
                    }
                });
                post.like();
                ivHeart.setImageResource(R.drawable.ufi_heart_active);
                post.put("likes", oldLikeCount + 1);
                tvLikeCount.setText("" + (oldLikeCount + 1) + " Likes");
            }
            // set new like count in parse server
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error setting new like count", e);
                    }
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
                intent.putExtra("postPosition", position);
            ((MainActivity) context).startActivityForResult(intent, REQUEST_CODE_FAVORITE);
            }
        }
    }

    public static void queryLikes(Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.include("objectId");
        query.whereEqualTo(Like.KEY_POST, post);
        query.whereEqualTo(Like.KEY_LIKER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                Log.d(TAG, "done: at query likes");
                if (e != null) {
                    Log.e(TAG, "Issue with getting likes", e);
                    return;
                } else {
                    if (objects.size() == 1) {
                        post.like();
                    } else {
                        post.dislike();
                    }
                }
            }
        });
    }
}



