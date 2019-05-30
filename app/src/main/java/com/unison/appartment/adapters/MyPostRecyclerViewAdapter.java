package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unison.appartment.R;
import com.unison.appartment.model.Post;
import com.unison.appartment.fragments.PostListFragment.OnPostListFragmentInteractionListener;

import java.io.IOException;
import java.util.Date;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di {@link Post} e che effettua una
 * chiamata al {@link com.unison.appartment.fragments.PostListFragment.OnPostListFragmentInteractionListener listener} specificato.
 */
public class MyPostRecyclerViewAdapter extends ListAdapter<Post, RecyclerView.ViewHolder> {

    // Player usato per la riproduzione dei file audio
    private MediaPlayer player = null;
    private ViewHolderAudioPost playingTrack = null;

    private final OnPostListFragmentInteractionListener listener;

    public MyPostRecyclerViewAdapter(OnPostListFragmentInteractionListener listener) {
        super(MyPostRecyclerViewAdapter.DIFF_CALLBACK);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(holder.itemView.getContext());
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(holder.itemView.getContext());
        Date timestamp;
        Resources res = holder.itemView.getContext().getResources();

        switch (holder.getItemViewType()){
            case Post.TEXT_POST:
                final ViewHolderTextPost holderTextPost = (ViewHolderTextPost) holder;
                final Post textPostItem = getItem(position);
                holderTextPost.textPostTxt.setText(textPostItem.getContent());
                holderTextPost.textPostSender.setText(textPostItem.getAuthor());
                timestamp = new Date(textPostItem.getTimestamp());
                holderTextPost.textPostDate.setText(res.getString(R.string.fragment_post_datetime_format, dateFormat.format(timestamp), timeFormat.format(timestamp)));
                // Popup menu
                holderTextPost.textPostOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(v.getContext(), holderTextPost.textPostOptions);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.fragment_messages_post_options);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()) {
                                    case R.id.fragment_messages_post_options_delete:
                                        listener.deletePost(textPostItem);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
                break;
            case Post.IMAGE_POST:
                final ViewHolderImagePost holderImagePost = (ViewHolderImagePost) holder;
                final Post imagePostItem = getItem(position);
                // Carico l'immagine con una libreria che effettua il resize dell'immagine in modo
                // efficiente, altrimenti se caricassi l'intera immagine già con poche immagini la
                // recyclerView andrebbe a scatti
                Glide.with(holderImagePost.imagePostImg.getContext())
                        .load(imagePostItem.getContent())
                        .into(holderImagePost.imagePostImg);
                holderImagePost.imagePostSender.setText(imagePostItem.getAuthor());
                timestamp = new Date(imagePostItem.getTimestamp());
                holderImagePost.imagePostDate.setText(res.getString(R.string.fragment_post_datetime_format, dateFormat.format(timestamp), timeFormat.format(timestamp)));
                holderImagePost.imagePostImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onPostListFragmentOpenImage(holderImagePost.imagePostImg, imagePostItem.getContent());
                        }
                    }
                });
                // Popup menu
                holderImagePost.imagePostOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(v.getContext(), holderImagePost.imagePostOptions);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.fragment_messages_post_options);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()) {
                                    case R.id.fragment_messages_post_options_delete:
                                        listener.deletePost(imagePostItem);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
                break;
            case Post.AUDIO_POST:
                final ViewHolderAudioPost holderAudioPost = (ViewHolderAudioPost) holder;
                final Post audioPostItem = getItem(position);
                holderAudioPost.audioPostSender.setText(audioPostItem.getAuthor());
                timestamp = new Date(audioPostItem.getTimestamp());
                holderAudioPost.audioPostDate.setText(res.getString(R.string.fragment_post_datetime_format, dateFormat.format(timestamp), timeFormat.format(timestamp)));
                holderAudioPost.audioPostbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (listener != null) {
                            listener.onListPostFragmentPlayAudio(audioPostItem.getFileName());
                        }*/
                        handleAudioPlay(audioPostItem, holderAudioPost);
                    }
                });
                // Popup menu
                holderAudioPost.audioPostOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(v.getContext(), holderAudioPost.audioPostOptions);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.fragment_messages_post_options);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()) {
                                    case R.id.fragment_messages_post_options_delete:
                                        listener.deletePost(audioPostItem);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    private void handleAudioPlay(Post audioPostItem, ViewHolderAudioPost holderAudioPost) {
        // Se qualcosa era già in riproduzione allora la interrompo
        if (player != null) {
            player.release();
            player = null;
            stopPlay(playingTrack);
        }
        player = new MediaPlayer();
        try {
            playingTrack = holderAudioPost;

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(audioPostItem.getContent());
            player.setOnPreparedListener(audioPrepareListener);
            player.prepareAsync();
        } catch (IOException e) {
        }
        holderAudioPost.audioPostState.setText(holderAudioPost.itemView.getContext().getResources().getString(R.string.fragment_audio_post_state_playing));
    }

    private void stopPlay(final ViewHolderAudioPost holderAudioPost) {
        holderAudioPost.audioPostState.setText(
                holderAudioPost.itemView.getContext().getResources().getString(R.string.fragment_audio_post_state_play)
        );
    }

    MediaPlayer.OnPreparedListener audioPrepareListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlay(playingTrack);
                }
            });
        }
    };

    public class ViewHolderTextPost extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView textPostTxt;
        public final TextView textPostSender;
        public final TextView textPostDate;
        public final ImageView textPostOptions;

        public ViewHolderTextPost(View view) {
            super(view);
            mView = view;
            textPostTxt = view.findViewById(R.id.fragment_text_post_txt);
            textPostSender = view.findViewById(R.id.fragment_text_post_sender);
            textPostDate = view.findViewById(R.id.fragment_text_post_date);
            textPostOptions = view.findViewById(R.id.fragment_text_post_options);
        }
    }

    public class ViewHolderImagePost extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imagePostImg;
        public final TextView imagePostSender;
        public final TextView imagePostDate;
        public final ImageView imagePostOptions;

        public ViewHolderImagePost(View view) {
            super(view);
            mView = view;
            imagePostImg = view.findViewById(R.id.fragment_image_post_img);
            imagePostSender = view.findViewById(R.id.fragment_image_post_sender);
            imagePostDate = view.findViewById(R.id.fragment_image_post_date);
            imagePostOptions = view.findViewById(R.id.fragment_image_post_options);
        }
    }

    public class ViewHolderAudioPost extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageButton audioPostbtn;
        public final TextView audioPostSender;
        public final TextView audioPostState;
        public final TextView audioPostDate;
        public final ImageView audioPostOptions;

        public ViewHolderAudioPost(View view) {
            super(view);
            mView = view;
            audioPostbtn = view.findViewById(R.id.fragment_audio_post_btn);
            audioPostSender = view.findViewById(R.id.fragment_audio_post_sender);
            audioPostState = view.findViewById(R.id.fragment_audio_post_state);
            audioPostDate = view.findViewById(R.id.fragment_audio_post_date);
            audioPostOptions = view.findViewById(R.id.fragment_audio_post_options);
        }
    }

    public static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Post>() {
                @Override
                public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
