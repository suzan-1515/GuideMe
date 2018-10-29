package com.guideme.guideme.home;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.guideme.guideme.R;
import com.guideme.guideme.api.DataParser;
import com.guideme.guideme.api.DownloadUrl;
import com.guideme.guideme.model.ActiveUser;
import com.guideme.guideme.model.Category;
import com.guideme.guideme.model.User;
import com.guideme.guideme.nearest.NearestPlaceActivity;
import com.guideme.guideme.utils.ToastUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class HomeActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = HomeActivity.class.getName();
    private GoogleMap mMap;
    private int radius = 500;
    private boolean mLocationPermissionGranted;
    private Circle circle;
    private LocationCallback mLocationCallback;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private User user;
    private CollectionReference mActiveUserCollectionRef;
    private List<HashMap<String, String>> nearByPlacesList;
    private Category category = Category.TEMPLE;
    private HashMap<String, Marker> currentMarkers = new HashMap<>();
    private Polyline shortestPath;

    private AppCompatSpinner mCategorySpinner;
    private OnLocationUpdatedListener locationUpdatedListener = new OnLocationUpdatedListener() {
        @Override
        public void onLocationUpdated(Location location) {
            if (location == null) return;
            updateMap(location);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (User) extras.getSerializable("user");
            getSupportActionBar().setSubtitle("Welcome, " + user.getName());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadiusFragment radiusFragment = RadiusFragment.newInstance();
                radiusFragment.registerActionCallback(new RadiusFragment.OnActionCallBack() {
                    @Override
                    public void onPostButtonClicked(View v, int r) {
                        Log.d(TAG, "Radius set to: " + radius);
                        radius = r;
                        updateRadius();
                    }
                });
                radiusFragment.show(getSupportFragmentManager(), "Radius Fragment");
            }
        });
        mCategorySpinner = findViewById(R.id.category_spinner);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) mCategorySpinner.getSelectedItem();
                Log.d(TAG, "Category selected: " + category);
                if (lastLocation != null)
                    fetchNearbyPlaces(lastLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mActiveUserCollectionRef = FirebaseFirestore.getInstance().collection("active_users");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initCategorySpinner();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (user != null)
            outState.putSerializable("user", user);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Serializable user = savedInstanceState.getSerializable("user");
        if (user != null) {
            this.user = (User) user;
            getSupportActionBar().setSubtitle("Welcome, " + this.user.getName());
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initCategorySpinner() {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.TEMPLE);
        categories.add(Category.ZOO);
        categories.add(Category.STADIUM);
        categories.add(Category.CHURCH);
        categories.add(Category.MUSEUM);
        categories.add(Category.MOSQUE);
        categories.add(Category.NIGHTCLUB);
        categories.add(Category.SYNAGOGUE);

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mCategorySpinner.setAdapter(dataAdapter);
        mCategorySpinner.setSelection(Category.TEMPLE.ordinal());

    }

    private void updateMap(Location location) {
        Log.d(TAG, "Location lat:" + location.getLatitude() + " lon:" + location.getLongitude());
        if (lastLocation == null || lastLocation.distanceTo(location) > 20.0) {
            lastLocation = location;
            updateCircle(location);
            updateCurrentLocationMarker(location);
            fetchNearbyPlaces(location);
        }
    }

    private void fetchNearbyPlaces(Location location) {
        Log.d(TAG, "fetchNearbyPlaces: lat-" + location.getLatitude() + " lon-" + location.getLongitude());
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        String url = getUrl(location.getLatitude(), location.getLongitude(), category.getValue());
        getNearbyPlaces.execute(url);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                HashMap<String, String> markerPlace = (HashMap<String, String>) marker.getTag();
                addActiveUserInfo(markerPlace, marker);
                Intent placeDetailIntent = getPlaceDetailIntent(markerPlace);
                startActivity(placeDetailIntent);
            }
        });
    }

    private void updateCurrentLocationMarker(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        currentUserLocationMarker = mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
        mMap.animateCamera(zoom);
    }

    private void updateCircle(Location location) {
        Log.d(TAG, "update circle");
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        if (circle == null) {
            Log.d(TAG, "Adding circle");
            circle = mMap.addCircle(new CircleOptions()
                    .center(current)
                    .radius(radius)
                    .strokeColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary))
                    .fillColor(ContextCompat.getColor(HomeActivity.this, R.color.radiusColor)));
        } else {
            circle.setCenter(current);
        }

    }

    private void updateRadius() {
        if (circle != null) {
            circle.setRadius(radius);
            fetchNearbyPlaces(lastLocation);
        }
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            mLocationPermissionGranted = true;
                            startLocationUpdates();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        SmartLocation.with(this).location().stop();
    }

    private void startLocationUpdates() {
        if (SmartLocation.with(this).location().state().isNetworkAvailable()) {
            SmartLocation.with(this)
                    .location()
                    .config(new LocationParams.Builder()
                            .setAccuracy(LocationAccuracy.HIGH)
                            .setDistance(10)
                            .setInterval(1000)
                            .build())
                    .start(locationUpdatedListener);
        } else {
            ToastUtils.show(this, "Please enable location service for Internet");
        }
    }

    private String getUrl(double latitide, double longitude, String type) {
        //去 https://developers.google.com/places/web-service/search 看文件
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + radius);
        googleURL.append("&type=" + type);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyC_bxHoTmjKPhmiiIdSRmYVQ_l5x2Froyw"); //AIzaSyC_bxHoTmjKPhmiiIdSRmYVQ_l5x2Froyw

        Log.d(TAG, "url = " + googleURL.toString());

        return googleURL.toString();
    }

    private void resetCurrentMarkers() {
        Set<String> keys = currentMarkers.keySet();
        for (String key : keys) {
            currentMarkers.get(key).remove();
        }
    }

    private void addActiveUserInfo(final HashMap<String, String> shortestPlace, final Marker marker) {
        String place_id = shortestPlace.get("id");
        mActiveUserCollectionRef
                .document(place_id)
                .collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        int size = snapshot.getDocuments().size();
                        int activeUsers = snapshot.getDocuments().size();
                        shortestPlace.put("active_user", String.valueOf(activeUsers));
                        marker.setSnippet(marker.getSnippet() + "," + "Active User: " + String.valueOf(activeUsers));
                    }
                });
    }

    private void removeFromActiveUser(HashMap<String, String> shortestPlace) {
        String place_id = shortestPlace.get("id");
        ActiveUser activeUser = new ActiveUser();
        activeUser.placeID = place_id;
        activeUser.userID = user.getId();
        mActiveUserCollectionRef.document(place_id)
                .collection("users")
                .document(user.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure", e);
                    }
                });
    }

    private void addActiveUser(HashMap<String, String> shortestPlace) {
        String place_id = shortestPlace.get("id");
        ActiveUser activeUser = new ActiveUser();
        activeUser.placeID = place_id;
        activeUser.userID = user.getId();
        mActiveUserCollectionRef
                .document(place_id)
                .collection("users")
                .document(user.getId())
                .set(activeUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Log.d(TAG, "onSuccess");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure", e);

                    }
                });
    }

    private void drawPathToNearestPlace(HashMap<String, String> shortestPlace) {
        final double toLat = Double.parseDouble(shortestPlace.get("lat"));
        final double toLan = Double.parseDouble(shortestPlace.get("lng"));

        if (shortestPath != null) shortestPath.remove();

        shortestPath = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                        new LatLng(toLat, toLan))
                .width(5)
                .color(Color.RED));
    }

    private void sendNotification(HashMap<String, String> shortestPlace) {
        Log.d(TAG, "Shortest path: " + shortestPlace.get("place_name"));

        final String notification_title = shortestPlace.get("place_name");
        final String activeUser = shortestPlace.get("active_user");
        final String address = shortestPlace.get("vicinity");
        final String notification_msg = "Shortest place: " + address;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = getPlaceDetailIntent(shortestPlace);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notification_title)
                .setContentText(notification_msg)
                .setContentInfo("Info")
                .setContentIntent(pendingIntent);
        int mNotificationId = (int) System.currentTimeMillis();
        notificationManager.notify(mNotificationId, notificationBuilder.build());

    }


    private Intent getPlaceDetailIntent(HashMap<String, String> markerPlace) {
        Intent intent = new Intent(this, NearestPlaceActivity.class);
        intent.putExtra("place_id", markerPlace.get("id"));
        intent.putExtra("place_title", markerPlace.get("place_name"));
        intent.putExtra("address", markerPlace.get("vicinity"));
        intent.putExtra("icon", markerPlace.get("icon"));
        intent.putExtra("rating", markerPlace.get("rating"));
        intent.putExtra("place_lat", Double.parseDouble(markerPlace.get("lat")));
        intent.putExtra("place_lng", Double.parseDouble(markerPlace.get("lng")));
        intent.putExtra("current_lat", lastLocation.getLatitude());
        intent.putExtra("current_lan", lastLocation.getLongitude());
        intent.putExtra("user", user);
        intent.putExtra("active_user", markerPlace.get("active_user"));

        return intent;
    }

    class GetNearbyPlaces extends AsyncTask<Object, String, String> {

        private String googleplaceData, url;

        @Override
        protected String doInBackground(Object... objects) {
            Log.d(TAG, "fetching data");
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
            nearByPlacesList = dataParser.parse(s);
            if (nearByPlacesList != null && nearByPlacesList.size() > 0) {
                resetCurrentMarkers();
                displayNearbyPlaces(nearByPlacesList);
            }
        }

        private void displayNearbyPlaces(List<HashMap<String, String>> nearByPlacesList) {
            Log.d(TAG, "displayNearbyPlaces: size-" + nearByPlacesList.size());
            float shortestDistance = 0;
            int shortDistanceIndex = -1;
            for (int i = 0; i < nearByPlacesList.size(); i++) {
                HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);
                String placeId = googleNearbyPlace.get("id");
                String nameOfPlace = googleNearbyPlace.get("place_name");
                String vicinity = googleNearbyPlace.get("vicinity");
                double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
                double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

                LatLng latLng = new LatLng(lat, lng);

                Location location = new Location(nameOfPlace);
                location.setLatitude(lat);
                location.setLongitude(lng);
                final int distance = (int) location.distanceTo(lastLocation);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(nameOfPlace + ": " + vicinity)
                        .snippet("Distance: " + distance + "M")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.setTag(googleNearbyPlace);

                if (distance < 200) {
                    addActiveUser(googleNearbyPlace);
                } else {
                    removeFromActiveUser(googleNearbyPlace);
                }

                if (i == 0) {
                    shortestDistance = distance;
                    shortDistanceIndex = i;
                } else {
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        shortDistanceIndex = i;
                    }
                }
                addActiveUserInfo(googleNearbyPlace, marker);
                currentMarkers.put(placeId, marker);

                Log.d(TAG, "place = " + nameOfPlace);
            }
            if (shortDistanceIndex != -1) {
                drawPathToNearestPlace(nearByPlacesList.get(shortDistanceIndex));
                sendNotification(nearByPlacesList.get(shortDistanceIndex));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mMap.animateCamera(zoom);
        }
    }

}
