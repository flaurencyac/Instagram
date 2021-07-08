package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.ParcelableObject;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.example.instagram.adapters.PictureAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    public static final Integer NUMBEROFCOLUMNS = 3;

    private Post post;
    private Context context;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private RecyclerView rvPictures;
    private List<Post> userPosts;
    private PictureAdapter picsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ParcelableObject receivedParcel = Parcels.unwrap(getIntent().getParcelableExtra("postObject"));
        post = receivedParcel.getPost();

        tvUsername = findViewById(R.id.tvUsername);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        rvPictures = findViewById(R.id.rvPictures);
        tvUsername.setText(post.getUser().getUsername());
        ParseFile profilePicture = (ParseFile) post.getUser().get("profilePicture");
        if (profilePicture != null) {
            Glide.with(context).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
        }
        userPosts = new ArrayList<>();
        picsAdapter = new PictureAdapter(context, userPosts);
        rvPictures.setAdapter(picsAdapter);
        rvPictures.setLayoutManager(new GridLayoutManager(context, NUMBEROFCOLUMNS));
        // get all posts that the user created
        queryPosts();

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, post.getUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                userPosts.addAll(posts);
                picsAdapter.notifyDataSetChanged();
            }
        });
    }

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