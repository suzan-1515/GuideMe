package com.example.ikshita.travel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import static java.lang.Integer.parseInt;

public class Menutab1 extends Fragment {
    private static final String TAG = "MENU1";
    String myDataFromActivity;
    String placeid;
    String url;
    String nametwitter;
    String addresstwitter;
    String websitetwitter;
    String urltwitter;
    String texturl;
    String texturl2;
    String urlweb;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menutab1, container, false);
        Menu_Activity activity = (Menu_Activity) getActivity();

        myDataFromActivity = activity.getMyData();
        Log.i("my respose int", myDataFromActivity);
        Log.i("dd","Ss");
        try {
            JSONObject json = new JSONObject(myDataFromActivity);
            placeid = json.getString("place_id");
            Log.i("Place_id",placeid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        details();
        return view;
    }

   public void details() {


        url="http://csci571-node20.us-east-2.elasticbeanstalk.com/placedetails?placeid="+placeid;

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG,"Mystring : " + response.toString());
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                            Log.i("json", json.toString());
                            JSONObject jsonmenu1 = json.getJSONObject("result");
                            Log.i("jsonmenu1", jsonmenu1.toString());
                            ((Menu_Activity) getActivity())
                                    .setActionBarTitle(jsonmenu1.getString("name"));
                            TextView addtext = (TextView) getView().findViewById(R.id.Address);
                            final TextView number = (TextView) getView().findViewById(R.id.Number);
                            TextView price = (TextView) getView().findViewById(R.id.price);
                            RatingBar rating = (RatingBar) getView().findViewById(R.id.ratingBar);
                            final TextView google1 = (TextView) getView().findViewById(R.id.google);
                            final TextView website = (TextView) getView().findViewById(R.id.website);
                            nametwitter =jsonmenu1.getString("name");
                            addresstwitter = jsonmenu1.getString("vicinity");
                            websitetwitter = jsonmenu1.getString("website");
                            urltwitter = jsonmenu1.getString("url");


                            //  Log.i("Ss",jsonmenu1.getString("rating"));
                            if(jsonmenu1.isNull("vicinity")) {
                                addtext.setText("No Address Available");
                            }else {
                                addtext.setText(jsonmenu1.getString("vicinity"));
                            }
                            if(jsonmenu1.isNull("price_level")) {
                                price.setText("No price available");
                            }
                            else {
                                if(parseInt(jsonmenu1.getString("price_level")) == 1){
                                    price.setText("$");
                                }
                                if(parseInt(jsonmenu1.getString("price_level")) == 2){
                                    price.setText("$$");
                                }
                                if(parseInt(jsonmenu1.getString("price_level")) == 3){
                                    price.setText("$$$");
                                }
                                if(parseInt(jsonmenu1.getString("price_level")) == 4){
                                    price.setText("$$$$");
                                }
                                if(parseInt(jsonmenu1.getString("price_level")) == 5){
                                    price.setText("$$$$$");
                                }
                            }
                            if(jsonmenu1.isNull("formatted_phone_number")) {


                                number.setText("No number available");

                            }
                            else {
                                number.setText(jsonmenu1.getString("formatted_phone_number"));
                                number.setTextColor(Color.parseColor("#ff4081"));
                                number.setPaintFlags(number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                number.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        String phno = number.getText().toString();
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" +phno));
                                        startActivity(callIntent);
                                    }
                                });
                            }
                            if(jsonmenu1.isNull("url")) {

                                google1.setText("No URL found");

                            }
                            else {
                                google1.setText(jsonmenu1.getString("url"));
                                google1.setTextColor(Color.parseColor("#ff4081"));
                                google1.setPaintFlags(number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                google1.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        String url = google1.getText().toString();
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    }
                                });
                            }

                            if(jsonmenu1.isNull("website")) {
                                website.setText("No Website found");
                            }
                            else {
                                website.setText(jsonmenu1.getString("website"));
                                website.setTextColor(Color.parseColor("#ff4081"));
                                website.setPaintFlags(number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                website.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        String url = website.getText().toString();
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    }
                                });

                            }
                            if(jsonmenu1.isNull("rating")) {
                                website.setText("No ratings");
                            } else
                            {

                                rating.setRating(Float.parseFloat(jsonmenu1.getString("rating")));

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                        Log.i(TAG,"Error : " + error.toString());
                    }

                });

        mRequestQueue.add(jsonObjectRequest);


    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



            case R.id.home:

                // Not implemented here
                return false;
            case R.id.add:
                Log.i("1",nametwitter);
                Log.i("2",websitetwitter);
                Log.i("3",urltwitter);
                Log.i("4",addresstwitter);

                texturl= "Check out "+nametwitter+" located at "+addresstwitter+".Website: ";
                if(websitetwitter.length() != 0) {
                urlweb = websitetwitter;
            } else {
                urlweb = urltwitter;
            }
            texturl2 = "https://twitter.com/share";
            texturl2 += "?text=" + URLEncoder.encode(texturl);
            texturl2 += "&url=" + URLEncoder.encode(urlweb);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(texturl2));
                startActivity(browserIntent);
                return true;
            default:
                break;
        }

        return false;
    }
}
