package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.R;
import com.unison.appartment.adapters.MyUserPickerRecyclerViewAdapter;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;

public class UserPickerFragment extends DialogFragment {

    public final static String TAG_USER_PICKER = "userPicker";

    private OnUserPickerFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserPickerFragment() {
    }

    @SuppressWarnings("unused")
    public static UserPickerFragment newInstance() {
        return new UserPickerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userpicker_list, container, false);
        getDialog().setTitle(R.string.fragment_userpicker_text_title);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyUserPickerRecyclerViewAdapter(Appartment.getInstance().getHomeUsersList(), mListener));
        }
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnUserPickerFragmentInteractionListener) {
            mListener = (OnUserPickerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserPickerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUserPickerFragmentInteractionListener {
        void onListFragmentInteraction(HomeUser item);
    }
}
