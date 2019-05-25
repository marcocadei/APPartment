package com.unison.appartment.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.unison.appartment.R;
import com.unison.appartment.fragments.InsertPostFragment.OnInsertPostFragmentListener;
import com.unison.appartment.fragments.PostListFragment.OnPostListFragmentInteractionListener;
import com.unison.appartment.activities.ImageDetailActivity;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.ImageUtils;


/**
 * Fragment che rappresenta l'intera bacheca
 */
public class MessagesFragment extends Fragment implements OnInsertPostFragmentListener, OnPostListFragmentInteractionListener {

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public MessagesFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onInsertPostFragmentSendPost(String content, int postType) {
        PostListFragment pf = (PostListFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addPost(content, postType);
    }

    @Override
    public void onPostListFragmentOpenImage(ImageView image, String imageUri) {
        Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), image, ViewCompat.getTransitionName(image));
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE_URI, imageUri);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE_TYPE, ImageUtils.IMAGE_TYPE_SQUARE);
        startActivity(i, options.toBundle());
    }
}
