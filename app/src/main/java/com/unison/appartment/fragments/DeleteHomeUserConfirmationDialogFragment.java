package com.unison.appartment.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unison.appartment.R;

public class DeleteHomeUserConfirmationDialogFragment extends DialogFragment {

    public final static String TAG_CONFIRMATION_DIALOG = "confirmationDialog";

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_home_user_confirmation_message)
                .setPositiveButton(R.string.general_continue_button, new DialogInterface.OnClickListener() {
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
