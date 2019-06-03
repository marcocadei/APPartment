package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.unison.appartment.R;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.fragments.FamilyMemberListFragment.OnFamilyMemberListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di
 * {@link com.unison.appartment.model.HomeUser HomeUser} e che effettua una
 * chiamata al {@link OnFamilyMemberListFragmentInteractionListener listener} specificato.
 */
public class MyFamilyMemberRecyclerViewAdapter extends ListAdapter<HomeUser, MyFamilyMemberRecyclerViewAdapter.ViewHolderMember> {

    private final OnFamilyMemberListFragmentInteractionListener listener;

    public MyFamilyMemberRecyclerViewAdapter(OnFamilyMemberListFragmentInteractionListener listener) {
        super(MyFamilyMemberRecyclerViewAdapter.DIFF_CALLBACK);
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
        final HomeUser member = getItem(position);
        Resources res = holder.itemView.getResources();
        String[] roles = res.getStringArray(R.array.desc_userhomes_uid_homename_role_values);

        holder.textMemberName.setText(member.getNickname());
        holder.textMemberRole.setText(roles[member.getRole()]);
        holder.textStatusUpper.setText(String.valueOf(member.getPoints()));
        holder.textStatusUpper.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.text_extra_large));
        holder.textStatusLower.setText(R.string.general_points_name);
        if (member.getImage() != null) {
            holder.imageMember.setColorFilter(holder.mView.getContext().getResources().getColor(R.color.transparentWhite, null));
            Glide.with(holder.imageMember.getContext()).load(member.getImage()).apply(RequestOptions.circleCropTransform()).into(holder.imageMember);
        }
        else {
            holder.imageMember.setColorFilter(holder.mView.getContext().getResources().getColor(R.color.colorPrimaryDark, null));
            holder.imageMember.setImageDrawable(res.getDrawable(R.drawable.ic_person, null));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFamilyMemberListFragmentOpenMember(member);
                }
            }
        });
    }

    public class ViewHolderMember extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageMember;
        public final TextView textMemberName;
        public final TextView textMemberRole;
        public final TextView textStatusLower;
        public final TextView textStatusUpper;

        public ViewHolderMember(View view) {
            super(view);
            mView = view;
            imageMember = view.findViewById(R.id.fragment_family_member_img_member);
            textMemberName = view.findViewById(R.id.fragment_family_member_text_name);
            textMemberRole = view.findViewById(R.id.fragment_family_member_text_role);
            textStatusUpper = view.findViewById(R.id.fragment_family_member_text_points_value);
            textStatusLower = view.findViewById(R.id.fragment_family_member_text_points_name);
        }
    }

    public static final DiffUtil.ItemCallback<HomeUser> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HomeUser>() {
                @Override
                public boolean areItemsTheSame(@NonNull HomeUser oldItem, @NonNull HomeUser newItem) {
                    return oldItem.getUserId().equals(newItem.getUserId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull HomeUser oldItem, @NonNull HomeUser newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
