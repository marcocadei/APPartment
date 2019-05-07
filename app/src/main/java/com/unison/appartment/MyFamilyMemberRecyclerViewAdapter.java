package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.model.Member;
import com.unison.appartment.FamilyMemberListFragment.OnFamilyMemberListFragmentInteractionListener;

import java.util.List;

public class MyFamilyMemberRecyclerViewAdapter extends RecyclerView.Adapter<MyFamilyMemberRecyclerViewAdapter.ViewHolderMember> {

    private final List<Member> memberList;
    // TODO: implementare listener

    private final OnFamilyMemberListFragmentInteractionListener listener;

    public MyFamilyMemberRecyclerViewAdapter(List<Member> memberList, OnFamilyMemberListFragmentInteractionListener listener) {
        this.memberList = memberList;
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
        final Member member = (Member) memberList.get(position);
        holderMember.textMemberName.setText(member.getName());
        holderMember.textMemberPoints.setText(String.valueOf(member.getPoints()));
        holderMember.textMemberRole.setText(String.valueOf(member.getRole()));
//      holderMember.imageMember.setImageURI(memberItem.getImage());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFamilyMemberListFragmentOpenMember(member);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class ViewHolderMember extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageMember;
        public final TextView textMemberName;
        public final TextView textMemberPoints;
        public final TextView textMemberRole;

        public ViewHolderMember(View view) {
            super(view);
            mView = view;
            imageMember = view.findViewById(R.id.fragment_family_member_img_member);
            textMemberName = view.findViewById(R.id.fragment_family_member_text_name);
            textMemberPoints = view.findViewById(R.id.fragment_family_member_text_points_value);
            textMemberRole = view.findViewById(R.id.fragment_family_member_text_role);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textMemberName.getText() + "'";
        }
    }
}
