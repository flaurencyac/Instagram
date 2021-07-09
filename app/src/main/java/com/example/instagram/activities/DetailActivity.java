package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.ParcelableObject;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.fragments.ComposeCommentFragment;
import com.example.instagram.fragments.TimelineFragment;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity  implements ComposeCommentFragment.ComposeCommentDialogListener {
    private static final String TAG = "DetailActivity";

    private Post post;
    private Context context;
    private TextView tvUsername;
    private ImageView ivImage;
    private TextView tvDateCreated;
    private TextView tvDescription;
    private ImageView ivProfilePicture;
    private ImageView ivHeart;
    private TextView tvLikeCount;
    private RecyclerView rvComments;
    private List<Comment> comments;
    private CommentsAdapter commentsAdapter;
    private FloatingActionButton fab;
    private Integer postPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        this.context = this;
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvDateCreated = findViewById(R.id.tvDateCreated);
        ivImage = findViewById(R.id.ivImage);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        ivHeart = findViewById(R.id.ivHeart);
        rvComments = findViewById(R.id.rvComments);
        comments = new ArrayList<>();
        fab = findViewById(R.id.fabComment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start a dialog fragment for composing the comment
                showComposeDialog();
            }
        });

        // set up recycler view, adapter, and linear layout manager
        commentsAdapter = new CommentsAdapter(context, comments);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Unwrap parcel to get post and post position in the adapter
        ParcelableObject receivedParcel = Parcels.unwrap(getIntent().getParcelableExtra("postObject"));
        post = receivedParcel.getPost();
        postPosition = getIntent().getExtras().getInt("postPosition");

        // Define vars
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(Html.fromHtml("<b>" + post.getUser().getUsername() + "</b> " + post.getDescription()));
        tvDateCreated.setText(Post.dateToString(post.getCreatedAt()));
        tvLikeCount.setText(""+ post.getLikeCount()+ " Likes");
        PostsAdapter.queryLikes(post);
        if (post.getLikeStatus()) {
            ivHeart.setImageResource(R.drawable.ufi_heart_active);
        } else {
            ivHeart.setImageResource(R.drawable.ufi_heart);
        }
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
        // set any on click listener
        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeOrUnlike(post);
            }
        });

        queryComments();
    }

    private void likeOrUnlike(Post post) {
        int oldLikeCount = post.getLikeCount();
        if (post.getLikeStatus()) {
            //delete the like object from the parse server
            Like.destroyLike(post);
            post.dislike();
            ivHeart.setImageResource(R.drawable.ufi_heart);
            post.put("likes", oldLikeCount - 1);
            tvLikeCount.setText("" + (oldLikeCount - 1) + " Likes");
            post.setLikeCount(oldLikeCount-1);
        }
        else { // create and save a like object to the parse server
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
            post.setLikeCount(oldLikeCount+1);
        }
        // set new like count in parse server
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "Error setting new like count", e);
                }
            }
        });
    }

    // get list of comments that belong to a post
    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_COMMENTER);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                Log.i(TAG, "Comment: "+objects.toString());
                comments.clear();
                comments.addAll(objects);
                commentsAdapter.notifyDataSetChanged();
            }
        });

    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeCommentFragment frag = ComposeCommentFragment.newInstance("Write a comment...");
        frag.show(fm, "fragment_compose_comment");
    }

    @Override
    public void onFinishComposeDialog(String inputText) {
        Comment comment = new Comment();
        comment.setCommentary(inputText);
        comment.setCommenter(ParseUser.getCurrentUser());
        comment.setCommentsParentPost(post);
        // save the comment into parse server
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                }
                Log.i(TAG, "Saving comment to Parse was a success");
                queryComments();
            }
        });
    }

    // send an intent with the post position to be updated in the recycler view
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailActivity.this, TimelineFragment.class);
        ParcelableObject parcel = new ParcelableObject();
        parcel.setPost(post);
        intent.putExtra("post", post);
        intent.putExtra("postPosition", postPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    //------------TOOLBAR METHODS-------------------------------------------------------------//

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
    // so long as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // if the user taps "logout"
        if (id == R.id.action_logout) {
            Log.i(TAG, "logout");
            ParseUser.logOut();
            //this should be null: ParseUser currentUser = ParseUser.getCurrentUser();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
