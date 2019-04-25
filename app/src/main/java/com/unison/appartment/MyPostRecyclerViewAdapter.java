package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.dummy.DummyContent.DummyItem;

import java.util.List;


public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DummyItem> mValues;
    // private final OnListFragmentInteractionListener mListener;

    public MyPostRecyclerViewAdapter(List<DummyItem> items/*, OnListFragmentInteractionListener listener*/) {
        mValues = items;
        // mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_post, parent, false);
            return new ViewHolder1(view);
        }else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_post2, parent, false);
            return new ViewHolder2(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                ViewHolder1 holder1 = (ViewHolder1)holder;
                holder1.mItem = mValues.get(position);
                holder1.mIdView.setText(mValues.get(position).id);
                holder1.mContentView.setText(mValues.get(position).content);
                break;
            case 1:
                ViewHolder2 holder2 = (ViewHolder2)holder;
                holder2.mItem = mValues.get(position);
                holder2.mContentView.setText(mValues.get(position).content);
                break;
            default:
                break;
        }


       /* holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        int id = Integer.valueOf(mValues.get(position).id);
        return id%2;
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder1(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder2(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
