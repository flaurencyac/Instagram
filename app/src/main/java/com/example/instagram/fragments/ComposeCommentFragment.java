package com.example.instagram.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.instagram.R;
import com.example.instagram.activities.DetailActivity;

public class ComposeCommentFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText etCommentBody;
    private ImageButton ibClose;
    private Button btnSubmit;

    public ComposeCommentFragment() {}

    public static ComposeCommentFragment newInstance(String title) {
        ComposeCommentFragment frag = new ComposeCommentFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_comment, container);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            ComposeCommentDialogListener listener = (ComposeCommentDialogListener) getActivity();
            listener.onFinishComposeDialog(etCommentBody.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }

    public interface ComposeCommentDialogListener {
        void onFinishComposeDialog(String inputText);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCommentBody = view.findViewById(R.id.etCommentBody);
        ibClose = view.findViewById(R.id.ibClose);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        String title = getArguments().getString("title", "Write a comment");
        getDialog().setTitle(title);
        etCommentBody.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        etCommentBody.setOnEditorActionListener(this);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return input text back to activity through the implemented listener
                ComposeCommentDialogListener listener = (ComposeCommentDialogListener) getActivity();
                listener.onFinishComposeDialog(etCommentBody.getText().toString());
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });

    }



}
