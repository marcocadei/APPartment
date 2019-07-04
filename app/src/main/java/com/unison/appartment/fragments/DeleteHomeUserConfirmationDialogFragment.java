package com.unison.appartment.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.unison.appartment.R;

public class DeleteHomeUserConfirmationDialogFragment extends DialogFragment {

    public final static String TAG_CONFIRMATION_DIALOG = "confirmationDialog";

    private final static int BUNDLE_CAPACITY = 2;
    private final static String BUNDLE_KEY_MESSAGE = "message";
    private final static String BUNDLE_KEY_POSITIVE_BUTTON_STRING = "positiveButtonString";

    public interface ConfirmationDialogInterface {
        void onConfirm();
    }

    private ConfirmationDialogInterface mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        mListener = (ConfirmationDialogInterface) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public static DeleteHomeUserConfirmationDialogFragment newInstance(@StringRes int message, @StringRes int positiveButtonString) {
        Bundle args = new Bundle(BUNDLE_CAPACITY);
        args.putInt(BUNDLE_KEY_MESSAGE, message);
        args.putInt(BUNDLE_KEY_POSITIVE_BUTTON_STRING, positiveButtonString);

        DeleteHomeUserConfirmationDialogFragment fragment = new DeleteHomeUserConfirmationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomErrorAlertDialogTheme);
        builder.setMessage(getArguments().getInt(BUNDLE_KEY_MESSAGE))
                .setPositiveButton(getArguments().getInt(BUNDLE_KEY_POSITIVE_BUTTON_STRING), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onConfirm();
                    }
                })
                .setNegativeButton(R.string.general_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        dismiss(); // Già fatto in automatico, superfluo
                    }
                });
        return builder.create();
    }

}
