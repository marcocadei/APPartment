package com.unison.appartment.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.unison.appartment.R;
import com.unison.appartment.fragments.InsertPostFragment.OnInsertPostFragmentListener;
import com.unison.appartment.fragments.PostListFragment.OnPostListFragmentInteractionListener;
import com.unison.appartment.activities.ImageDetailActivity;
import com.unison.appartment.model.Post;
import com.unison.appartment.utils.ImageUtils;

/**
 * Fragment che rappresenta l'intera bacheca
 */
public class MessagesFragment extends Fragment implements OnInsertPostFragmentListener, OnPostListFragmentInteractionListener {

    private ProgressBar progressBar;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public MessagesFragment() {
    }

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);

        progressBar = view.findViewById(R.id.fragment_messages_progress);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
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

    @Override
    public void onHomeListElementsLoaded(int elements) {
        if (!loading) {
            progressBar.setVisibility(View.GONE);
            // Disabilito i bottoni di inserimento di un post durante un caricamento
            InsertPostFragment pf = (InsertPostFragment)getChildFragmentManager()
                    .findFragmentById(R.id.fragment_messages_fragment_insert_post);
            pf.loaded();
        }

        View emptyListLayout = getView().findViewById(R.id.fragment_messages_layout_empty_list);
        // Se gli elementi sono 0 allora mostro un testo che indichi all'utente l'assenza di case
        if (elements == 0) {
            emptyListLayout.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
        }
    }

    // Variabile ausiliaria usata per indicare che è in corso un caricamento
    private boolean loading;
    @Override
    public void loading(boolean loading) {
        this.loading = loading;
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void deletePost(Post post) {
        PostListFragment pf = (PostListFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.deletePost(post);
    }

    public void onDowngrade() {
        View snackbarView = getActivity().findViewById(R.id.fragment_messages);
        Snackbar.make(snackbarView, getString(R.string.snackbar_downgrade_error_message),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostListError(boolean error) {
        if (error) {
            View snackbarView = getActivity().findViewById(R.id.fragment_messages);
            Snackbar.make(snackbarView, getString(R.string.snackbar_messages_error_message),
                    Snackbar.LENGTH_LONG).show();
        }
    }
}
