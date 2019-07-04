package com.unison.appartment.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FirebaseProgressDialogFragment extends DialogFragment {

    public final static String TAG_FIREBASE_PROGRESS_DIALOG = "progressDialog";

    private final static int BUNDLE_CAPACITY = 2;
    private final static String BUNDLE_KEY_TITLE = "title";
    private final static String BUNDLE_KEY_DESCRIPTION = "description";

    public static FirebaseProgressDialogFragment newInstance(String title, String description) {
        Bundle args = new Bundle(BUNDLE_CAPACITY);
        args.putString(BUNDLE_KEY_TITLE, title);
        args.putString(BUNDLE_KEY_DESCRIPTION, description);
        
        FirebaseProgressDialogFragment fragment = new FirebaseProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final ProgressDialog progress = ProgressDialog.show(
                getActivity(),
                getArguments().getString(BUNDLE_KEY_TITLE),
                getArguments().getString(BUNDLE_KEY_DESCRIPTION),
                true);
        setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        return progress;
    }
}
