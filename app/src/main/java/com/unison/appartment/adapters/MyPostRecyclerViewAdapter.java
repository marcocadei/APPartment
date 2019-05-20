package com.unison.appartment.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unison.appartment.R;
import com.unison.appartment.model.Post;
import com.unison.appartment.fragments.PostListFragment.OnPostListFragmentInteractionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di {@link Post} e che effettua una
 * chiamata al {@link com.unison.appartment.fragments.PostListFragment.OnPostListFragmentInteractionListener listener} specificato.
 */
public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Post> postList;
    // private final OnListPostFragmentListener listener;

    // Player usato per la riproduzione dei file audio
    private MediaPlayer player = null;
    private ViewHolderAudioPost playingTrack = null;

    private final OnPostListFragmentInteractionListener listener;

    public MyPostRecyclerViewAdapter(List<Post> postList, OnPostListFragmentInteractionListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Post.TEXT_POST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_text_post, parent, false);
                return new ViewHolderTextPost(view);
            case Post.IMAGE_POST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_image_post, parent, false);
                return new ViewHolderImagePost(view);
            case Post.AUDIO_POST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_audio_post, parent, false);
                return new ViewHolderAudioPost(view);
            default:
                // TODO gestione errore
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm");
        switch (holder.getItemViewType()){
            case Post.TEXT_POST:
                ViewHolderTextPost holderTextPost = (ViewHolderTextPost) holder;
                Post textPostItem = postList.get(position);
                holderTextPost.textPostTxt.setText(textPostItem.getContent());
                holderTextPost.textPostSender.setText(textPostItem.getAuthor());
                holderTextPost.textPostDate.setText(format.format(textPostItem.getTimestamp()));
                break;
            case Post.IMAGE_POST:
                final ViewHolderImagePost holderImagePost = (ViewHolderImagePost) holder;
                final Post imagePostItem = postList.get(position);
                // Carico l'immagine con una libreria che effettua il resize dell'immagine in modo
                // efficiente, altrimenti se caricassi l'intera immagine già con poche immagini la
                // recyclerView andrebbe a scatti
                Glide.with(holderImagePost.imagePostImg.getContext()).load(imagePostItem.getContent()).into(holderImagePost.imagePostImg);
                holderImagePost.imagePostSender.setText(imagePostItem.getAuthor());
                holderImagePost.imagePostDate.setText(format.format(imagePostItem.getTimestamp()));
                holderImagePost.imagePostImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onPostListFragmentOpenImage(holderImagePost.imagePostImg, imagePostItem.getContent());
                        }
                    }
                });
                break;
            case Post.AUDIO_POST:
                final ViewHolderAudioPost holderAudioPost = (ViewHolderAudioPost) holder;
                final Post audioPostItem = postList.get(position);
                holderAudioPost.audioPostSender.setText(audioPostItem.getAuthor());
                holderAudioPost.audioPostDate.setText(format.format(audioPostItem.getTimestamp()));
                holderAudioPost.audioPostbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (listener != null) {
                            listener.onListPostFragmentPlayAudio(audioPostItem.getFileName());
                        }*/
                        handleAudioPlay(audioPostItem, holderAudioPost);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void handleAudioPlay(Post audioPostItem, final ViewHolderAudioPost holderAudioPost) {
        // Se qualcosa era già in riproduzione allora la interrompo
        if (player != null) {
            player.release();
            player = null;
            stopPlay(playingTrack);
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(audioPostItem.getContent());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlay(holderAudioPost);
                }
            });
            playingTrack = holderAudioPost;
        } catch (IOException e) {
        }
        holderAudioPost.audioPostState.setText(holderAudioPost.itemView.getContext().getResources().getString(R.string.fragment_audio_post_state_playing));
    }

    private void stopPlay(final ViewHolderAudioPost holderAudioPost) {
        holderAudioPost.audioPostState.setText(
                holderAudioPost.itemView.getContext().getResources().getString(R.string.fragment_audio_post_state_play)
        );
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position).getType();
    }

    public class ViewHolderTextPost extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView textPostTxt;
        public final TextView textPostSender;
        public final TextView textPostDate;

        public ViewHolderTextPost(View view) {
            super(view);
            mView = view;
            textPostTxt = view.findViewById(R.id.fragment_text_post_txt);
            textPostSender = view.findViewById(R.id.fragment_text_post_sender);
            textPostDate = view.findViewById(R.id.fragment_text_post_date);
        }
    }

    public class ViewHolderImagePost extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imagePostImg;
        public final TextView imagePostSender;
        public final TextView imagePostDate;

        public ViewHolderImagePost(View view) {
            super(view);
            mView = view;
            imagePostImg = view.findViewById(R.id.fragment_image_post_img);
            imagePostSender = view.findViewById(R.id.fragment_image_post_sender);
            imagePostDate = view.findViewById(R.id.fragment_image_post_date);
        }
    }

    public class ViewHolderAudioPost extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageButton audioPostbtn;
        public final TextView audioPostSender;
        public final TextView audioPostState;
        public final TextView audioPostDate;

        public ViewHolderAudioPost(View view) {
            super(view);
            mView = view;
            audioPostbtn = view.findViewById(R.id.fragment_audio_post_btn);
            audioPostSender = view.findViewById(R.id.fragment_audio_post_sender);
            audioPostState = view.findViewById(R.id.fragment_audio_post_state);
            audioPostDate = view.findViewById(R.id.fragment_audio_post_date);
        }
    }
}
