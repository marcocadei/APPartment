package com.unison.appartment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FirebaseErrorDialogFragment extends DialogFragment {

    public final static String TAG_FIREBASE_ERROR_DIALOG = "errorDialog";

    // Riferimento: https://stackoverflow.com/a/13338148
    public interface FirebaseErrorDialogInterface {
        void onDialogFragmentDismiss();
    }

    private FirebaseErrorDialogInterface mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        mListener = (FirebaseErrorDialogInterface) context;
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
        builder.setMessage(R.string.dialog_firebase_error_message)
                .setPositiveButton(R.string.general_ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogFragmentDismiss();
//                        dismiss(); // Già fatto in automatico, superfluo
                    }
                });

        AlertDialog dialog = builder.create();
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
