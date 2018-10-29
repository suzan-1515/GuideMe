package com.guideme.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guideme.guideme.R;
import com.guideme.guideme.model.Review;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> dataList = new ArrayList<>();

    public ReviewAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Review review = dataList.get(position);

        holder.userName.setText(review.userName);
        holder.ratingBar.setRating(review.rating);
        holder.review.setText(review.review);

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItem(List<Review> items) {
        this.dataList.clear();
        this.dataList.addAll(items);
        notifyDataSetChanged();
    }

    public void addData(Review review) {
        if (review == null) return;

        this.dataList.add(0, review);
        notifyItemInserted(0);
    }

    public void update(Review review) {
        if (review == null) return;

        int changedIndex = 0;
        boolean hasItem = false;
        for (int i = 0; i < dataList.size(); i++) {
            Review review1 = dataList.get(i);
            if (review1.userId.equals(review.userId)) {
                review1.userId = review.userId;
                review1.userName = review.userName;
                review1.placeId = review.placeId;
                review1.rating = review.rating;
                review1.review = review.review;
                changedIndex = i;
                hasItem = true;
                break;
            }
        }
        if (hasItem) {
            notifyItemChanged(changedIndex);
        } else {
            addData(review);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatRatingBar ratingBar;
        AppCompatTextView userName;
        AppCompatTextView review;


        public ViewHolder(View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingbar);
            userName = itemView.findViewById(R.id.client_name);
            review = itemView.findViewById(R.id.review);
        }
    }


}
