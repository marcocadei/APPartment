package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.unison.appartment.model.AudioPost;
import com.unison.appartment.model.ImagePost;
import com.unison.appartment.model.Post;
import com.unison.appartment.model.TextPost;
// import com.unison.appartment.ListPostFragment.OnListPostFragmentListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Post> postList;
    // private final OnListPostFragmentListener listener;
    // Player usato per la riproduzione dei file audio
    private MediaPlayer player = null;
    private ViewHolderAudioPost playingTrack = null;

    public MyPostRecyclerViewAdapter(List<Post> postList/*, OnListPostFragmentListener listener*/) {
        this.postList = postList;
        // this.listener = listener;
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
                TextPost textPostItem = (TextPost) postList.get(position);
                holderTextPost.textPostTxt.setText(textPostItem.getMessage());
                holderTextPost.textPostSender.setText(textPostItem.getSender());
                holderTextPost.textPostDate.setText(format.format(textPostItem.getDate()));
                break;
            case Post.IMAGE_POST:
                ViewHolderImagePost holderImagePost = (ViewHolderImagePost) holder;
                ImagePost imagePostItem = (ImagePost) postList.get(position);
                // Carico l'immagine con una libreria che effettua il resize dell'immagine in modo
                // efficiente, altrimenti se caricassi l'intera immagine già con poche immagini la
                // recyclerView andrebbe a scatti
                Picasso.get().load(imagePostItem.getImage()).fit().centerCrop().into(holderImagePost.imagePostImg);
                holderImagePost.imagePostSender.setText(imagePostItem.getSender());
                holderImagePost.imagePostDate.setText(format.format(imagePostItem.getDate()));
                break;
            case Post.AUDIO_POST:
                final ViewHolderAudioPost holderAudioPost = (ViewHolderAudioPost) holder;
                final AudioPost audioPostItem = (AudioPost) postList.get(position);
                holderAudioPost.audioPostSender.setText(audioPostItem.getSender());
                holderAudioPost.audioPostDate.setText(format.format(audioPostItem.getDate()));
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

    public void handleAudioPlay(AudioPost audioPostItem, final ViewHolderAudioPost holderAudioPost) {
        // Se qualcosa era già in riproduzione allora la interrompo
        if (player != null) {
            player.release();
            player = null;
            stopPlay(playingTrack);
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(audioPostItem.getFileName());
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

    public void stopPlay(final ViewHolderAudioPost holderAudioPost) {
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

        @Override
        public String toString() {
            return super.toString() + " '" + textPostTxt.getText() + "'";
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

        @Override
        public String toString() {
            return super.toString() + " '" + "immagine" + "'";
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

        @Override
        public String toString() {
            return super.toString() + " '" + "immagine" + "'";
        }
    }
}
