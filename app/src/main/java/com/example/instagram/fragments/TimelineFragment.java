package com.example.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.models.Post;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class TimelineFragment extends Fragment {
    public static final String TAG = "TimelineFragment";
    public static final Integer CODE_QUERY_POSTS_NORMALLY = 0;
    public static final Integer CODE_QUERY_MORE_POSTS = 1;

    private RecyclerView rvPosts;
    private List<Post> allPosts;
    private PostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Date oldestPostDate;

    public TimelineFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        llm.setReverseLayout(true);
//        llm.setStackFromEnd(true);
        rvPosts.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPosts.getContext(), DividerItemDecoration.VERTICAL);
        rvPosts.addItemDecoration(dividerItemDecoration);
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(CODE_QUERY_MORE_POSTS);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryPosts(CODE_QUERY_POSTS_NORMALLY);
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        queryPosts(CODE_QUERY_POSTS_NORMALLY);
    }

    protected void queryPosts(Integer codedRequestType) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        if (codedRequestType == CODE_QUERY_MORE_POSTS) {
            query.setLimit(3);
            query.whereLessThan("createdAt", oldestPostDate);
        }
        else if (codedRequestType == CODE_QUERY_POSTS_NORMALLY) {
            query.setLimit(5);
        }
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
                int numOldPosts = adapter.getItemCount();
                allPosts.addAll(posts);
                if (codedRequestType == CODE_QUERY_MORE_POSTS) {
                    adapter.notifyItemRangeInserted(numOldPosts-1, allPosts.size()-numOldPosts);
                    scrollListener.resetState();
                } else {
                    adapter.notifyDataSetChanged();
                }
                setOldestDate();
            }
        });
    }

    private void setOldestDate() {
        int lastPostIndex = adapter.getItemCount()-1;
        oldestPostDate = allPosts.get(lastPostIndex).getCreatedAt();
    }
}
