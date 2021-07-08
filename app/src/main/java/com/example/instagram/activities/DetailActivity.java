package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.instagram.fragments.ComposeCommentFragment;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
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
    private RecyclerView rvComments;
    private List<Comment> comments;
    private CommentsAdapter commentsAdapter;
    private FloatingActionButton fab;

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
        comments = new ArrayList<>();
        fab = findViewById(R.id.fabComment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO start a fragment for composing the comment
                showComposeDialog();
//                Intent intent = new Intent(context, ComposeActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_COMMENT);
            }
        });

        // Create adapter passing in the sample user data
        commentsAdapter = new CommentsAdapter(context, comments);
        // Attach the adapter to the recyclerview to populate items
        rvComments.setAdapter(commentsAdapter);
        // Set layout manager to position the items
        rvComments.setLayoutManager(new LinearLayoutManager(this));

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

        queryComments();
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
        // make a comment out of this inputText
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
                // update the recycler view
                queryComments();
            }
        });
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
