package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.model.User;
import com.unison.appartment.FamilyMemberListFragment.OnFamilyMemberListFragmentInteractionListener;

import java.util.List;

public class MyFamilyMemberRecyclerViewAdapter extends RecyclerView.Adapter<MyFamilyMemberRecyclerViewAdapter.ViewHolderMember> {

    private final List<User> userList;
    // TODO: implementare listener

    private final OnFamilyMemberListFragmentInteractionListener listener;

    public MyFamilyMemberRecyclerViewAdapter(List<User> userList, OnFamilyMemberListFragmentInteractionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @Override
    public ViewHolderMember onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_family_member, parent, false);
        return new ViewHolderMember(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderMember holder, int position) {
        ViewHolderMember holderMember = (ViewHolderMember) holder;
        final User user = (User) userList.get(position);
        // TODO risistemare con Member e non User
        holderMember.textMemberName.setText("riccardo");
        holderMember.textMemberPoints.setText("578");
//        holderMember.textMemberName.setText(user.getName());
//        holderMember.textMemberPoints.setText(String.valueOf(user.getPoints()));
//      holderMember.imageMember.setImageURI(memberItem.getImage());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFamilyMemberListFragmentOpenMember(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolderMember extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageMember;
        public final TextView textMemberName;
        public final TextView textMemberPoints;

        public ViewHolderMember(View view) {
            super(view);
            mView = view;
            imageMember = view.findViewById(R.id.fragment_family_member_image_member);
            textMemberName = view.findViewById(R.id.fragment_family_member_text_member_name);
            textMemberPoints = view.findViewById(R.id.fragment_family_member_points_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textMemberName.getText() + "'";
        }
    }
}
