package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.model.ImagePost;
import com.unison.appartment.model.Post;
import com.unison.appartment.model.TextPost;

import java.util.List;


public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Post> postList;

    public MyPostRecyclerViewAdapter(List<Post> postList) {
        this.postList = postList;
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
        switch (holder.getItemViewType()){
            case Post.TEXT_POST:
                ViewHolderTextPost holderTextPost = (ViewHolderTextPost) holder;
                TextPost textPostItem = (TextPost) postList.get(position);
                holderTextPost.textPostTxt.setText(textPostItem.getMessage());
                break;
            case Post.IMAGE_POST:
                ViewHolderImagePost holderImagePost = (ViewHolderImagePost) holder;
                ImagePost imagePostItem = (ImagePost) postList.get(position);
                holderImagePost.imagePostImg.setImageURI(imagePostItem.getImage());
                break;
            case Post.AUDIO_POST:
                break;
            default:
                break;
        }
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

        public ViewHolderTextPost(View view) {
            super(view);
            mView = view;
            textPostTxt = view.findViewById(R.id.fragment_text_post_txt);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textPostTxt.getText() + "'";
        }
    }

    public class ViewHolderImagePost extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imagePostImg;

        public ViewHolderImagePost(View view) {
            super(view);
            mView = view;
            imagePostImg = view.findViewById(R.id.fragment_image_post_img);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "immagine" + "'";
        }
    }

    public class ViewHolderAudioPost extends RecyclerView.ViewHolder {
        public final View mView;

        public ViewHolderAudioPost(View view) {
            super(view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "immagine" + "'";
        }
    }
}
