package com.example.ikshita.travel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v4.content.ContextCompat.startActivity;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    private final Context context;
    private ProgressDialog progbarr;
    private JSONArray mDataset;
    private static final String TAG="rvadp";

      public RVAdapter(Context context, JSONArray myDataset) {
          this.context = context;
        mDataset = myDataset;
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_xml, parent, false);
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
        final ImageView image2;



        public View layout;
        public ViewHolder(View v) {
            super(v);
            layout = v;
           img1 = (ImageView)v.findViewById(R.id.recycle_img1);
          text1 = (TextView) v.findViewById(R.id.recycle_text1);
         text2 = (TextView ) v.findViewById(R.id.recycle_text2);
            image2 = (ImageView) v.findViewById(R.id.recycle_img2);
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

            Picasso.with(holder.itemView.getContext()).load(array.getString("icon")).into(holder.img1);
            holder.text1.setText(array.getString("name"));
            holder.text2.setText(array.getString("vicinity"));
            holder.image2.setImageResource(R.drawable.blackoutlineheart);
            holder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    progbarr=new ProgressDialog(view.getContext());
                    progbarr.setCancelable(true);
                    progbarr.setMessage("Fetching Details");
                    progbarr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progbarr.setProgress(0);
                    progbarr.show();
                     Log.i(TAG,"Get data" +String.valueOf(array));
                    Intent i = new Intent(view.getContext(),Menu_Activity.class);
                    i.putExtra("resultmenu", String.valueOf(array));
                    progbarr.dismiss();
                    context.startActivity(i);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
