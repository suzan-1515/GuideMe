package com.example.ikshita.travel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;


public class Recyclerphotos extends RecyclerView.Adapter<Recyclerphotos.ViewHolder> {
    private List<Bitmap> mDataset;
    private static final String TAG="rvadp";

    public Recyclerphotos(List<Bitmap> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public Recyclerphotos.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.menutab2, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemClickListener clickListener;

        final ImageView img1;


        public View layout;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            img1 = (ImageView)v.findViewById(R.id.imagerec);

            itemView.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getLayoutPosition());
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            holder.img1.setImageBitmap(mDataset.get(position));
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

}
