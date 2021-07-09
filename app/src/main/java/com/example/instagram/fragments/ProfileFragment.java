package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.example.instagram.adapters.PictureAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public static final Integer NUMBEROFCOLUMNS = 3;
    public final static int PICK_PHOTO_CODE = 1000;
    private TextView tvUsername;
    private RecyclerView rvPictures;
    private List<Post> userPosts;
    private PictureAdapter picsAdapter;
    private ImageView ivProfilePicture;
    private Button btnUpdateProfilePic;
    private Bitmap bitmap;
    //private File photoFile;
    //private String photoFileName = "photo.jpg";

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsername);
        rvPictures = view.findViewById(R.id.rvPictures);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        btnUpdateProfilePic = view.findViewById(R.id.btnUpdateProfilePic);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());

        ParseFile profilePicture = (ParseFile) ParseUser.getCurrentUser().get("profilePicture");
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
        }

        btnUpdateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        userPosts = new ArrayList<>();
        picsAdapter = new PictureAdapter(getContext(), userPosts);
        rvPictures.setAdapter(picsAdapter);
        rvPictures.setLayoutManager(new GridLayoutManager(getActivity(), NUMBEROFCOLUMNS));
        // get all posts that the user created
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_LIKES);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            bitmap = loadFromUri(photoUri);
            ivProfilePicture.setImageBitmap(bitmap);
            //Glide.with(getContext()).asBitmap().load(bitmap).circleCrop().into(ivProfilePicture);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile newProfilePicture = new ParseFile("profile.png", byteArray);
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profilePicture", newProfilePicture);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.i(TAG, "Successfully uploaded image");
                        Toast.makeText(getContext(), "Profile pic uploaded!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
