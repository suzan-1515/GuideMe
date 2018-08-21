package com.example.ikshita.travel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.constraint.solver.widgets.Helper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ikshita.travel.Menu_Activity;
import com.example.ikshita.travel.R;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Menutab2 extends Fragment {

    private static final String TAG = "menutab2";
    String myDataFromActivity;
    String placeid;
    String url;
    GeoDataClient mGeoDataClient;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Bitmap> bitmapList;



    Context context;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.recycphotos, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_photo);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        context=getActivity();
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

        mGeoDataClient = Places.getGeoDataClient(context,null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeid);
        Log.i("placeid",placeid);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                bitmapList = new ArrayList<Bitmap>();
                mAdapter = new Recyclerphotos(bitmapList);
                mRecyclerView.setAdapter(mAdapter);
                int count = photoMetadataBuffer.getCount();
                if(count != 0 ) {
                    for (int x = 0; x < count; x++) {
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(x);
                        Log.i("ss", photoMetadata.toString());
                        // Get the attribution text.
                        CharSequence attribution = photoMetadata.getAttributions();
                        // Get a full-size bitmap for the photo.
                        Task <PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener <PlacePhotoResponse>() {
                            @Override
                            public void onComplete( @NonNull Task <PlacePhotoResponse> task ) {
                                PlacePhotoResponse photo = task.getResult();
                                Bitmap bitmap = photo.getBitmap();
                                bitmapList.add(bitmap);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
                else {

                    TextView txt = (TextView) view.findViewById(R.id.errimg);
                    txt.setVisibility(View.VISIBLE);

                }

            }

        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



}
