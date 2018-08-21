package com.example.ikshita.travel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class ReviewYelpAdapter extends RecyclerView.Adapter<ReviewYelpAdapter.ViewHolder> {
    private JSONArray mDataset;
    private static final String TAG="rvadp";
    Context context;

    public ReviewYelpAdapter( Context context, JSONArray myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    @Override
    public ReviewYelpAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.menutab4yelp, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemClickListener clickListener;

        final ImageView img1;
        final TextView text1;
        final TextView text2;
        final TextView text3;
        final RatingBar star;


        public View layout;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            img1 = (ImageView)v.findViewById(R.id.imgautyelp);
            text1 = (TextView) v.findViewById(R.id.revtext1yelp);
            text2 = (TextView ) v.findViewById(R.id.revtext3yelp);
            text3 = (TextView) v.findViewById(R.id.revtext4yelp);
            star = (RatingBar) v.findViewById(R.id.revtext2yelp);
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
            final JSONObject array = mDataset.getJSONObject(position);
            String authorname = array.getJSONObject("user").getString("name");
            String rating = array.getString("rating");
            String time = array.getString("time_created");
            String photo =array.getJSONObject("user").getString("image_url");
            final String aurl = array.getString("url");
            String text = array.getString("text");
            Picasso.with(holder.itemView.getContext()).load(photo).into(holder.img1);
            holder.text1.setText(authorname);
            holder.text2.setText(time);
            holder.text3.setText(text);
            holder.star.setRating(Float.parseFloat(rating));
            holder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    String url = aurl.toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
