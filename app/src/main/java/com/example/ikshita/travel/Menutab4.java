package com.example.ikshita.travel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ikshita.travel.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class Menutab4 extends Fragment {
    private static final String TAG = "menutab2";
    String myDataFromActivity;
    String placeid;
    String url;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String val;
    String timesort;
    String valmain;
    String urlyelp;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.revrec, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Menu_Activity activity = (Menu_Activity) getActivity();

        myDataFromActivity = activity.getMyData();
        Log.i("my respose int", myDataFromActivity);
        Log.i("dd", "Ss");
        try {
            JSONObject json = new JSONObject(myDataFromActivity);
            placeid = json.getString("place_id");
            Log.i("Place_id", placeid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinnercat2);
        final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinnercat1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected( AdapterView <?> arg0, View arg1,
                                        int arg2, long arg3 ) {
                valmain = spinner1.getSelectedItem().toString();
                Log.i("val", valmain);
                if (spinner1.getSelectedItemPosition() == 0) {
                    spinner2.setSelection(0);
                    url = "http://csci571-node20.us-east-2.elasticbeanstalk.com/placedetails?placeid=" + placeid;

                    final RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
                    final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener <JSONObject>() {

                                @Override
                                public void onResponse( JSONObject response ) {
                                    Log.i(TAG, "Mystring : " + response.toString());
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(response.toString());
                                        Log.i("json", json.toString());
                                        final JSONObject jsonmenu1 = json.getJSONObject("result");
                                        Log.i("jsonmenu1", jsonmenu1.toString());
                                        final JSONArray reviews = jsonmenu1.getJSONArray("reviews");
                                        Log.i("rev", reviews.toString());

                                        if (spinner2.getSelectedItemPosition() == 0) {

                                            mAdapter = new ReviewAdapter(getContext(),reviews);
                                            mRecyclerView.setAdapter(mAdapter);

                                        }


                                        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                            @Override
                                            public void onItemSelected( AdapterView <?> arg0, View arg1,
                                                                        int arg2, long arg3 ) {
                                                val = spinner2.getSelectedItem().toString();
                                                Log.i("val", val);

                                                if (val.equals("Default Order") && (valmain.equals("Google"))) {
                                                    mAdapter = new ReviewAdapter(getContext(),reviews);
                                                    mRecyclerView.setAdapter(mAdapter);

                                                }
                                                if (val.equals("Highest Rating") && (valmain.equals("Google"))) {

                                                    ArrayList <JSONObject> array = new ArrayList <>();
                                                    JSONArray rev = reviews;
                                                    for (int i = 0; i < rev.length(); i++) {
                                                        try {
                                                            array.add(rev.getJSONObject(i));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    Collections.sort(array, new Comparator <JSONObject>() {

                                                        @Override
                                                        public int compare( JSONObject lhs, JSONObject rhs ) {
                                                            // TODO Auto-generated method stub

                                                            try {
                                                                if (lhs.getDouble("rating") < rhs.getDouble("rating")) {
                                                                    return 1;
                                                                } else {
                                                                    return -1;
                                                                }
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                                return 0;
                                                            }
                                                        }
                                                    });
                                                    JSONArray jsArray = new JSONArray(array);
                                                    mAdapter = new ReviewAdapter(getContext(),jsArray);
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }
                                                if (val.equals("Lowest Rating") && (valmain.equals("Google"))) {

                                                    ArrayList <JSONObject> array = new ArrayList <>();
                                                    JSONArray rev = reviews;
                                                    for (int i = 0; i < rev.length(); i++) {
                                                        try {
                                                            array.add(rev.getJSONObject(i));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    Collections.sort(array, new Comparator <JSONObject>() {

                                                        @Override
                                                        public int compare( JSONObject lhs, JSONObject rhs ) {
                                                            // TODO Auto-generated method stub

                                                            try {
                                                                if (lhs.getDouble("rating") > rhs.getDouble("rating")) {
                                                                    return 1;
                                                                } else {
                                                                    return -1;
                                                                }
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                                return 0;
                                                            }
                                                        }
                                                    });
                                                    JSONArray jsArray = new JSONArray(array);
                                                    mAdapter = new ReviewAdapter(getContext(),jsArray);
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }


                                                if (val.equals("Most recent") && (valmain.equals("Google"))) {

                                                    ArrayList <JSONObject> array = new ArrayList <>();
                                                    JSONArray rev = reviews;
                                                    for (int i = 0; i < rev.length(); i++) {
                                                        try {
                                                            array.add(rev.getJSONObject(i));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    Collections.sort(array, new Comparator <JSONObject>() {

                                                        @Override
                                                        public int compare( JSONObject lhs, JSONObject rhs ) {
                                                            // TODO Auto-generated method stub

                                                            try {
                                                                if (lhs.getDouble("time") < rhs.getDouble("time")) {
                                                                    return 1;
                                                                } else {
                                                                    return -1;
                                                                }
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                                return 0;
                                                            }
                                                        }
                                                    });
                                                    JSONArray jsArray = new JSONArray(array);
                                                    mAdapter = new ReviewAdapter(getContext(),jsArray);
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }


                                                if (val.equals("Least recent") && (valmain.equals("Google"))) {

                                                    ArrayList <JSONObject> array = new ArrayList <>();
                                                    JSONArray rev = reviews;
                                                    for (int i = 0; i < rev.length(); i++) {
                                                        try {
                                                            array.add(rev.getJSONObject(i));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    Collections.sort(array, new Comparator <JSONObject>() {

                                                        @Override
                                                        public int compare( JSONObject lhs, JSONObject rhs ) {
                                                            // TODO Auto-generated method stub

                                                            try {
                                                                if (lhs.getDouble("rating") > rhs.getDouble("rating")) {
                                                                    return 1;
                                                                } else {
                                                                    return -1;
                                                                }
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                                return 0;
                                                            }
                                                        }
                                                    });
                                                    JSONArray jsArray = new JSONArray(array);
                                                    mAdapter = new ReviewAdapter(getContext(),jsArray);
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected( AdapterView <?> parent ) {

                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse( VolleyError error ) {

                                    Log.i(TAG, "Error : " + error.toString());
                                }

                            });

                    mRequestQueue.add(jsonObjectRequest);

                }


                if (valmain.equals("Yelp")) {
                    spinner2.setSelection(0);
                    url = "http://csci571-node20.us-east-2.elasticbeanstalk.com/placedetails?placeid=" + placeid;

                    final RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
                    final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener <JSONObject>() {

                                @Override
                                public void onResponse( JSONObject response ) {
                                    Log.i(TAG, "Mystring : " + response.toString());
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(response.toString());
                                        Log.i("json", json.toString());
                                        final JSONObject jsonmenu1 = json.getJSONObject("result");
                                        Log.i("jsonmenu1", jsonmenu1.toString());
                                        final JSONArray reviews = jsonmenu1.getJSONArray("reviews");
                                        Log.i("rev", reviews.toString());

                                        String add = jsonmenu1.getString("formatted_address");
                                        String pname = jsonmenu1.getString("name");
                                        String[] addsplit = add.split(",");
                                        int arrLen = addsplit.length;
                                        String displaycountry = addsplit[arrLen - 1].substring(1,3);
                                        String add1 = addsplit[0];
                                        String[] statecode = addsplit[arrLen - 2].split(",");
                                        String state = statecode[0];
                                        state = state.substring(1,3);
                                        String city = addsplit[arrLen - 3];
                                        String add2 = addsplit[arrLen - 3] + ',' + addsplit[arrLen - 2];
                                        Log.i("msg", add + "" + pname + "" + addsplit + "" + arrLen + "" + displaycountry + ""
                                                + "" + add1 + "" + statecode + "" + state + "" + city + "" + add2);

                                       String plname = pname.replaceAll("\\s+","+");
                                        String addr1 = add1.replaceAll("\\s+","+");
                                        String addr2 = add2.replaceAll("\\s+","+");
                                        String plcity = city.replaceAll("\\s+","+");
                                        String plstate = state.replaceAll("\\s+","+");
                                        String plcount = displaycountry.replaceAll("\\s+","+");

                                        urlyelp = "http://csci571-node20.us-east-2.elasticbeanstalk.com/yelpurl?plname=" +plname+ "&addr1=" +addr1+ "&addr2=" +addr2+ "&plcity=" +plcity+"&plstate=" +plstate+ "&plcount=" +plcount;

                                        final RequestQueue mRequestQueue1 = Volley.newRequestQueue(getActivity());
                                        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest
                                                (Request.Method.GET, urlyelp, null, new Response.Listener <JSONObject>() {

                                                    @Override
                                                    public void onResponse( JSONObject response ) {
                                                        Log.i(TAG, "Mystringyelpppp : " + response.toString());
                                                        JSONObject json = null;
                                                        try {
                                                            json = new JSONObject(response.toString());
                                                            final JSONArray reviewsyelp = json.getJSONArray("reviews");
                                                            Log.i("reviewsyelp", reviewsyelp.toString());
                                                            if (spinner2.getSelectedItemPosition() == 0) {
                                                                mAdapter = new ReviewYelpAdapter(getContext(),reviewsyelp);
                                                                mRecyclerView.setAdapter(mAdapter);
                                                            }


                                                            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                @Override
                                                                public void onItemSelected( AdapterView <?> arg0, View arg1,
                                                                                            int arg2, long arg3 ) {
                                                                    val = spinner2.getSelectedItem().toString();
                                                                    Log.i("val", val);

                                                                    if (val.equals("Default Order") && (valmain.equals("Yelp"))) {
                                                                        mAdapter = new ReviewYelpAdapter(getContext(),reviewsyelp);
                                                                        mRecyclerView.setAdapter(mAdapter);

                                                                    }
                                                                    if (val.equals("Highest Rating") && (valmain.equals("Yelp"))) {

                                                                        ArrayList <JSONObject> arrayyelp = new ArrayList <>();
                                                                        JSONArray revyelp = reviewsyelp;
                                                                        for (int i = 0; i < revyelp.length(); i++) {
                                                                            try {
                                                                                arrayyelp.add(revyelp.getJSONObject(i));
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }

                                                                        Collections.sort(arrayyelp, new Comparator <JSONObject>() {

                                                                            @Override
                                                                            public int compare( JSONObject lhs, JSONObject rhs ) {
                                                                                // TODO Auto-generated method stub

                                                                                try {
                                                                                    if (lhs.getDouble("rating") < rhs.getDouble("rating")) {
                                                                                        return 1;
                                                                                    } else {
                                                                                        return -1;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    // TODO Auto-generated catch block
                                                                                    e.printStackTrace();
                                                                                    return 0;
                                                                                }
                                                                            }
                                                                        });
                                                                        JSONArray jsArrayyelp = new JSONArray(arrayyelp);
                                                                        mAdapter = new ReviewYelpAdapter(getContext(),jsArrayyelp);
                                                                        mRecyclerView.setAdapter(mAdapter);
                                                                    }
                                                                    if (val.equals("Lowest Rating") && (valmain.equals("Yelp"))) {

                                                                        ArrayList <JSONObject> arrayyelp = new ArrayList <>();
                                                                        JSONArray revyelp = reviewsyelp;
                                                                        for (int i = 0; i < revyelp.length(); i++) {
                                                                            try {
                                                                                arrayyelp.add(revyelp.getJSONObject(i));
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }

                                                                        Collections.sort(arrayyelp, new Comparator <JSONObject>() {

                                                                            @Override
                                                                            public int compare( JSONObject lhs, JSONObject rhs ) {
                                                                                // TODO Auto-generated method stub

                                                                                try {
                                                                                    if (lhs.getDouble("rating") > rhs.getDouble("rating")) {
                                                                                        return 1;
                                                                                    } else {
                                                                                        return -1;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    // TODO Auto-generated catch block
                                                                                    e.printStackTrace();
                                                                                    return 0;
                                                                                }
                                                                            }
                                                                        });
                                                                        JSONArray jsArrayyelp = new JSONArray(arrayyelp);
                                                                        mAdapter = new ReviewYelpAdapter(getContext(),jsArrayyelp);
                                                                        mRecyclerView.setAdapter(mAdapter);               }


                                                                    if (val.equals("Most recent") && (valmain.equals("Yelp"))) {

                                                                        ArrayList <JSONObject> arrayyelp = new ArrayList <>();
                                                                        JSONArray revyelp = reviewsyelp;
                                                                        for (int i = 0; i < revyelp.length(); i++) {
                                                                            try {
                                                                                arrayyelp.add(revyelp.getJSONObject(i));
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                        Collections.sort(arrayyelp, new Comparator <JSONObject>() {

                                                                            @Override
                                                                            public int compare( JSONObject lhs, JSONObject rhs ) {
                                                                                // TODO Auto-generated method stub

                                                                                try {
                                                                                    if (lhs.getDouble("time_created") < rhs.getDouble("time_created")) {
                                                                                        return 1;
                                                                                    } else {
                                                                                        return -1;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    // TODO Auto-generated catch block
                                                                                    e.printStackTrace();
                                                                                    return 0;
                                                                                }
                                                                            }
                                                                        });
                                                                        JSONArray jsArrayyelp = new JSONArray(arrayyelp);
                                                                        mAdapter = new ReviewYelpAdapter(getContext(),jsArrayyelp);
                                                                        mRecyclerView.setAdapter(mAdapter);                   }

                                                                    if (val.equals("Least recent")&& (valmain.equals("Yelp"))) {

                                                                        ArrayList <JSONObject> arrayyelp = new ArrayList <>();
                                                                        JSONArray revyelp = reviewsyelp;
                                                                        for (int i = 0; i < revyelp.length(); i++) {
                                                                            try {
                                                                                arrayyelp.add(revyelp.getJSONObject(i));
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                        Collections.sort(arrayyelp, new Comparator <JSONObject>() {

                                                                            @Override
                                                                            public int compare( JSONObject lhs, JSONObject rhs ) {
                                                                                // TODO Auto-generated method stub

                                                                                try {
                                                                                    if (lhs.getDouble("time_created") > rhs.getDouble("time_created")) {
                                                                                        return 1;
                                                                                    } else {
                                                                                        return -1;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    // TODO Auto-generated catch block
                                                                                    e.printStackTrace();
                                                                                    return 0;
                                                                                }
                                                                            }
                                                                        });
                                                                        JSONArray jsArrayyelp = new JSONArray(arrayyelp);
                                                                        mAdapter = new ReviewYelpAdapter(getContext(),jsArrayyelp);
                                                                        mRecyclerView.setAdapter(mAdapter);                   }
                                                                }



                                                                @Override
                                                                public void onNothingSelected( AdapterView <?> parent ) {

                                                                }
                                                            });

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse( VolleyError error ) {

                                                        Log.i(TAG, "Error : " + error.toString());
                                                    }

                                                });

                                        mRequestQueue1.add(jsonObjectRequest1);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse( VolleyError error ) {

                                    Log.i(TAG, "Error : " + error.toString());
                                }
                            });
                    mRequestQueue.add(jsonObjectRequest);
                }
            }
            @Override
            public void onNothingSelected( AdapterView <?> parent ) {

            }
        });
        return view;
    }
}
