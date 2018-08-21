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


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private JSONArray mDataset;
    private static final String TAG="rvadp";
    Context context;

    public ReviewAdapter( Context context, JSONArray myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.menutab4, parent, false);
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
            img1 = (ImageView)v.findViewById(R.id.imgaut);
            text1 = (TextView) v.findViewById(R.id.revtext1);
            text2 = (TextView ) v.findViewById(R.id.revtext3);
            text3 = (TextView) v.findViewById(R.id.revtext4);
            star = (RatingBar) v.findViewById(R.id.revtext2);
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
            String authorname = array.getString("author_name");
            String rating = array.getString("rating");
            String time = array.getString("time");
            int epochTime = array.getInt("time");
            Date date = new Date(epochTime * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formatted = format.format(date);
            String photo = array.getString("profile_photo_url");
            final String aurl = array.getString("author_url");
            String text = array.getString("text");
            Picasso.with(holder.itemView.getContext()).load(photo).into(holder.img1);
            holder.text1.setText(authorname);
            holder.text2.setText(formatted);
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
