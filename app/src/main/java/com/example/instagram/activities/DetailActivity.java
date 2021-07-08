package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.ParcelableObject;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    private Post post;
    private Context context;
    private TextView tvUsername;
    private ImageView ivImage;
    private TextView tvDateCreated;
    private TextView tvDescription;
    private ImageView ivProfilePicture;
    private RecyclerView rvComments;
    private List<Comment> comments;
    private CommentsAdapter commentsAdapter;

    /*
    Sring commentHere =etComment.getText()sdkfa;
    comment.put(jsdlf)

    Intent sendBack = new Intent();
    sendBack.putExtra("comment", commentHere);

     */

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
        rvComments = findViewById(R.id.rvComments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ParcelableObject receivedParcel = Parcels.unwrap(getIntent().getParcelableExtra("postObject"));
        post = receivedParcel.getPost();

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvDateCreated.setText(Post.dateToString(post.getCreatedAt()));
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
        }
        ParseFile profilePicture = (ParseFile) post.getUser().get("profilePicture");
        if (profilePicture != null) {
            Glide.with(context).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
        }
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
