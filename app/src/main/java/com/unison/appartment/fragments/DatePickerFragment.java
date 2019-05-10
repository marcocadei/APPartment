package com.unison.appartment.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

// Riferimento sulla struttura della classe --> https://stackoverflow.com/a/28349221

public class DatePickerFragment extends DialogFragment {

    private final static int BUNDLE_CAPACITY = 3;
    private final static String BUNDLE_KEY_YEAR = "y";
    private final static String BUNDLE_KEY_MONTH = "m";
    private final static String BUNDLE_KEY_DAY = "d";

    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public static DatePickerFragment newInstance(int year, int month, int day, DatePickerDialog.OnDateSetListener listener) {
        Bundle bundle = new Bundle(BUNDLE_CAPACITY);
        bundle.putInt(BUNDLE_KEY_YEAR, year);
        bundle.putInt(BUNDLE_KEY_MONTH, month);
        bundle.putInt(BUNDLE_KEY_DAY, day);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), listener,
                getArguments().getInt(BUNDLE_KEY_YEAR),
                getArguments().getInt(BUNDLE_KEY_MONTH),
                getArguments().getInt(BUNDLE_KEY_DAY)
        );
    }

}
