package com.unison.appartment.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unison.appartment.R;

public class DismissableWarningDialogFragment extends DialogFragment {

    public final static String TAG_DISMISSABLE_WARNING_DIALOG = "dismissableWarningDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_user_deletion_not_allowed)
                .setPositiveButton(R.string.general_ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        dismiss(); // Già fatto in automatico, superfluo
                    }
                });

        AlertDialog dialog = builder.create();
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

}
