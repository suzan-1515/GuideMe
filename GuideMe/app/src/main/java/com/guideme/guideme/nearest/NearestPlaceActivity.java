package com.guideme.guideme.nearest;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.guideme.guideme.R;
import com.guideme.guideme.adapter.ReviewAdapter;
import com.guideme.guideme.api.DataParser;
import com.guideme.guideme.api.DownloadUrl;
import com.guideme.guideme.model.Review;
import com.guideme.guideme.model.User;
import com.guideme.guideme.review.ReviewFragment;
import com.guideme.guideme.utils.ToastUtils;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NearestPlaceActivity extends AppCompatActivity {

    private static final String TAG = NearestPlaceActivity.class.getName();
    private Location currentLocation;

    private HashMap<String, String> placeDetailMap = null;
    private User user;
    private CollectionReference mReviewCollectionRef;
    private ReviewAdapter reviewAdapter;
    private ImageView placeImageView;
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView ratingBar;
    private ProgressBar progressBar;
    private TextView activeUserTextView;
    private int activeUsers;
    private CollectionReference mActiveUserCollectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        final String place_id = extras.getString("place_id");
        String place_title = extras.getString("place_title");
        String place_address = extras.getString("address");
        final String place_icon = extras.getString("icon");
        final String place_rating = extras.getString("rating");
        final double placeLat = extras.getDouble("place_lat");
        final double placelon = extras.getDouble("place_lng");
        double currentLat = extras.getDouble("current_lat");
        double currentLon = extras.getDouble("current_lng");
        user = (User) extras.getSerializable("user");
        if (place_id == null) return;

        getSupportActionBar().setTitle(place_title);

        currentLocation = new Location("Current");
        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLon);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewFragment reviewFragment = ReviewFragment.newInstance(user);
                reviewFragment.registerActionCallback(new ReviewFragment.OnActionCallBack() {
                    @Override
                    public void onPostButtonClicked(View v, String review, int rating) {
                        Log.d(TAG, "Rating " + rating);
                        postReview(review, rating, place_id, user);

                    }
                });
                reviewFragment.show(getSupportFragmentManager(), "Review Fragment");
            }
        });
        FloatingActionButton fabDirection = findViewById(R.id.fab_direction);
        fabDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://maps.google.com/maps?daddr=" + placeLat + "," + placelon;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        placeImageView = findViewById(R.id.image);
        nameTextView = findViewById(R.id.name);
        addressTextView = findViewById(R.id.address);
        ratingBar = findViewById(R.id.rating);
        activeUserTextView = findViewById(R.id.active_user);
        progressBar = findViewById(R.id.progress);

        RecyclerView recyclerView = findViewById(R.id.review_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        recyclerView.setAdapter(reviewAdapter);

        mActiveUserCollectionRef = FirebaseFirestore.getInstance().collection("active_users");
        fetchActiveUsers(place_id, user.getId());
        mReviewCollectionRef = FirebaseFirestore.getInstance().collection(place_id);
        onDataReceived(place_id, place_title, place_address, place_icon, place_rating, placeLat, placelon);
        fetchReviews(place_id);
    }

    private void fetchActiveUsers(String place_id, String id) {
        mActiveUserCollectionRef
                .document(place_id)
                .collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while fetching active user", e);
                            return;
                        }

                        activeUsers = snapshot.getDocuments().size();
                        updateActiveUser();
                    }
                });
    }

    private void updateActiveUser() {
        activeUserTextView.setText(String.format(getString(R.string.prefix_active_user), String.valueOf(activeUsers)));
    }

    private void fetchReviews(String place_id) {
        mReviewCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    QueryDocumentSnapshot document = dc.getDocument();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "Data loaded: " + document.getData());
                        Review review = document.toObject(Review.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "Review added");
                                reviewAdapter.addData(review);
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Review modified");
                                reviewAdapter.update(review);
                                break;
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            }
        });
    }

    private void postReview(String review, int rating, String place_id, User user) {
        Review reviewData = new Review();
        reviewData.id = System.currentTimeMillis();
        reviewData.rating = rating;
        reviewData.placeId = place_id;
        reviewData.userName = user.getName();
        reviewData.userId = user.getId();
        reviewData.review = review;
        mReviewCollectionRef.document(user.getId())
                .set(reviewData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "review posted success");
                        ToastUtils.show(NearestPlaceActivity.this, "Review posted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "error posting review", e);
                        ToastUtils.show(NearestPlaceActivity.this, "Error posting review!");
                    }
                });
    }


    private void fetchPlaceDetail(String placeId) {
        Log.d(TAG, "fetchPlaceDetail: placeId-" + placeId);
        if (placeDetailMap == null) {
            progressBar.setVisibility(View.VISIBLE);
            GetPlaceDetails getPlaceDetails = new GetPlaceDetails();
            String url = getUrl(placeId);
            getPlaceDetails.execute(url);
        }
    }

    private String getUrl(String placeId) {
        //去 https://developers.google.com/places/web-service/search 看文件
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googleURL.append("placeid=" + placeId);
        googleURL.append("&fields=" + "name,rating,formatted_phone_number,geometry,icon,id,name,place_id,vicinity,website");
        googleURL.append("&key=" + "AIzaSyD3b64Gst28K9U_v56hzt3KZV9pw4oEkI0");

        Log.d(TAG, "url = " + googleURL.toString());

        return googleURL.toString();

    }

    private void onDataReceived(HashMap<String, String> placeDetailMap) {
        Log.d(TAG, "onDataReceived");
        progressBar.setVisibility(View.GONE);
        nameTextView.setText(String.format(getString(R.string.prefix_place_name), placeDetailMap.get("place_name")));
        addressTextView.setText(String.format(getString(R.string.prefix_place_address), placeDetailMap.get("vicinity")));
        ratingBar.setText(String.format(getString(R.string.prefix_place_rating), placeDetailMap.get("rating")));
        activeUserTextView.setText(String.format(getString(R.string.prefix_active_user), String.valueOf(activeUsers)));
        String icon = placeDetailMap.get("icon");
        if (TextUtils.isEmpty(icon)) {
            placeImageView.setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(icon)
                    .into(placeImageView);
        }
    }

    private void onDataReceived(String place_id, String place_title, String place_address,
                                String place_icon, String place_rating, double placeLat, double placelon) {
        Log.d(TAG, "onDataReceived");
        progressBar.setVisibility(View.GONE);
        nameTextView.setText(String.format(getString(R.string.prefix_place_name), place_title));
        addressTextView.setText(String.format(getString(R.string.prefix_place_address), place_address));
        ratingBar.setText(String.format(getString(R.string.prefix_place_rating), place_rating));
        activeUserTextView.setText(String.format(getString(R.string.prefix_active_user), String.valueOf(activeUsers)));
        if (TextUtils.isEmpty(place_icon)) {
            placeImageView.setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(place_icon)
                    .into(placeImageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetPlaceDetails extends AsyncTask<Object, String, String> {

        private String googleplaceData, url;

        @Override
        protected String doInBackground(Object... objects) {
            Log.d(TAG, "getching data");
            url = (String) objects[0];
            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googleplaceData = downloadUrl.ReadTheURL(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googleplaceData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "Data received");
            DataParser dataParser = new DataParser();
            placeDetailMap = dataParser.parsePlaceDetails(s);
//            onDataRe
// ceived(placeDetailMap);
        }


    }
}
