package com.example.ikshita.travel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AutocompleteResultType;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menutab3 extends Fragment    implements OnMapReadyCallback {
    private static final String TAG = "menutab2";
    String myDataFromActivity;
    String placeid;
    String url;
    String address;
    String enterlatmap;
    String enterlonmap;
    String latitude;
    String longitude;
    String name;
    GoogleMap google;
    String val;
    Double latorg;
    Double lonorg;
    Double latdest;
    Double londest;
    String placename;

    @Nullable
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.menutab3, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated( @NonNull final View view, @Nullable Bundle savedInstanceState ) {

        Menu_Activity activity = (Menu_Activity) getActivity();
        myDataFromActivity = activity.getMyData();
        Log.i("my respose int", myDataFromActivity);
        Log.i("dd", myDataFromActivity);
        try {
            JSONObject json = new JSONObject(myDataFromActivity);
            name = json.getString("name");
            JSONObject geometry = json.getJSONObject("geometry");
            latitude = geometry.getJSONObject("location").getString("lat");
            latorg = Double.parseDouble(latitude);
            Log.i("Ss","dd" +latorg);
            longitude = geometry.getJSONObject("location").getString("lng");
            lonorg = Double.parseDouble(longitude);
            Log.i("Ss","dd" +lonorg);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final PlacesAutocompleteTextView maploc = (PlacesAutocompleteTextView) getView().findViewById(R.id.maplocation);
        maploc.setLocationBiasEnabled(true);
        maploc.setResultType(AutocompleteResultType.GEOCODE);
        maploc.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected( @NonNull com.seatgeek.placesautocomplete.model.Place place ) {
                maploc.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess( PlaceDetails placeDetails ) {
                        placename = String.valueOf(placeDetails.name);
                        enterlatmap = String.valueOf(placeDetails.geometry.location.lat);
                        enterlonmap = String.valueOf(placeDetails.geometry.location.lng);
                        Log.d("test", enterlatmap);
                        Log.d("test1", enterlonmap);
                        latdest = Double.parseDouble(enterlatmap);
                        Log.i("Ss","dddest" +latdest);
                        londest = Double.parseDouble(enterlonmap);
                        Log.i("Ss","dddest" +londest);
                        Log.i("Ss","dd" +lonorg);
                        Log.i("Ss","dd" +latorg);
                        Log.i("Ss","dd" +placename);
                        Log.i("Ss","dd" +name);

                        final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinnercatmap);
                        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

                        {
                            @Override
                            public void onItemSelected (AdapterView < ? > arg0, View arg1,int arg2, long arg3 ){
                                val = spinner2.getSelectedItem().toString();
                                Log.i("val", val);
                                if (val.equals("Driving")) {
                                    GoogleDirection.withServerKey("AIzaSyB_80UHaL3Lg0Z681PxVXPPTr_Tcg4FzIs")
                                            .from(new LatLng(latdest,londest))
                                            .to(new LatLng(latorg,lonorg))
                                            .alternativeRoute(true)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess( Direction direction, String rawBody ) {
                                                    if (direction.isOK()) {
                                                        google.clear();
                                                        String status = direction.getStatus();
                                                        Route route = direction.getRouteList().get(0);
                                                        Leg leg = route.getLegList().get(0);
                                                        ArrayList <LatLng> directionPositionList = leg.getDirectionPoint();
                                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latorg, lonorg))
                                                                .title(placename));
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latdest, londest))
                                                                .title(name));
                                                        google.addPolyline(polylineOptions);
                                                        LatLng northeastercordinate = route.getBound().getNortheastCoordination().getCoordination();
                                                        LatLng southwesterncordinate = route.getBound().getSouthwestCoordination().getCoordination();
                                                        LatLngBounds camerabounds = new LatLngBounds(southwesterncordinate,northeastercordinate);
                                                        google.animateCamera(CameraUpdateFactory.newLatLngBounds(camerabounds,100));
                                                    }
                                                }

                                                @Override
                                                public void onDirectionFailure( Throwable t ) {

                                                }


                                            });
                                }
                                if (val.equals("Bicycling")) {
                                    GoogleDirection.withServerKey("AIzaSyB_80UHaL3Lg0Z681PxVXPPTr_Tcg4FzIs")
                                            .from(new LatLng(latdest,londest))
                                            .to(new LatLng(latorg,lonorg))
                                            .alternativeRoute(true)
                                            .transportMode(TransportMode.BICYCLING)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess( Direction direction, String rawBody ) {
                                                    if (direction.isOK()) {
                                                        google.clear();
                                                        String status = direction.getStatus();
                                                        Route route = direction.getRouteList().get(0);
                                                        Leg leg = route.getLegList().get(0);
                                                        ArrayList <LatLng> directionPositionList = leg.getDirectionPoint();
                                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latorg, lonorg))
                                                                .title(placename));
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latdest, londest))
                                                                .title(name));
                                                        google.addPolyline(polylineOptions);
                                                        LatLng northeastercordinate = route.getBound().getNortheastCoordination().getCoordination();
                                                        LatLng southwesterncordinate = route.getBound().getSouthwestCoordination().getCoordination();
                                                        LatLngBounds camerabounds = new LatLngBounds(southwesterncordinate,northeastercordinate);
                                                        google.animateCamera(CameraUpdateFactory.newLatLngBounds(camerabounds,100));

                                                    }
                                                }

                                                @Override
                                                public void onDirectionFailure( Throwable t ) {

                                                }


                                            });
                                }
                                if (val.equals("Transit")) {
                                    GoogleDirection.withServerKey("AIzaSyB_80UHaL3Lg0Z681PxVXPPTr_Tcg4FzIs")
                                            .from(new LatLng(latdest,londest))
                                            .to(new LatLng(latorg,lonorg))
                                            .alternativeRoute(true)
                                            .transportMode(TransportMode.TRANSIT)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess( Direction direction, String rawBody ) {
                                                    if (direction.isOK()) {
                                                        google.clear();
                                                        String status = direction.getStatus();
                                                        Route route = direction.getRouteList().get(0);
                                                        Leg leg = route.getLegList().get(0);
                                                        ArrayList <LatLng> directionPositionList = leg.getDirectionPoint();
                                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latorg, lonorg))
                                                                .title(placename));
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latdest, londest))
                                                                .title(name));
                                                        google.addPolyline(polylineOptions);
                                                        LatLng northeastercordinate = route.getBound().getNortheastCoordination().getCoordination();
                                                        LatLng southwesterncordinate = route.getBound().getSouthwestCoordination().getCoordination();
                                                        LatLngBounds camerabounds = new LatLngBounds(southwesterncordinate,northeastercordinate);
                                                        google.animateCamera(CameraUpdateFactory.newLatLngBounds(camerabounds,100));

                                                    }
                                                }

                                                @Override
                                                public void onDirectionFailure( Throwable t ) {

                                                }


                                            });
                                }
                                if (val.equals("Walking")) {
                                    GoogleDirection.withServerKey("AIzaSyB_80UHaL3Lg0Z681PxVXPPTr_Tcg4FzIs")
                                            .from(new LatLng(latdest,londest))
                                            .to(new LatLng(latorg,lonorg))
                                            .alternativeRoute(true)
                                            .transportMode(TransportMode.WALKING)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess( Direction direction, String rawBody ) {
                                                    if (direction.isOK()) {
                                                        google.clear();
                                                        String status = direction.getStatus();
                                                        Route route = direction.getRouteList().get(0);
                                                        Leg leg = route.getLegList().get(0);
                                                        ArrayList <LatLng> directionPositionList = leg.getDirectionPoint();
                                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latorg, lonorg))
                                                                .title(placename));
                                                        google.addMarker(new MarkerOptions().position(new LatLng(latdest, londest))
                                                                .title(name));
                                                        google.addPolyline(polylineOptions);
                                                        LatLng northeastercordinate = route.getBound().getNortheastCoordination().getCoordination();
                                                        LatLng southwesterncordinate = route.getBound().getSouthwestCoordination().getCoordination();
                                                        LatLngBounds camerabounds = new LatLngBounds(southwesterncordinate,northeastercordinate);
                                                        google.animateCamera(CameraUpdateFactory.newLatLngBounds(camerabounds,100));

                                                    }
                                                }

                                                @Override
                                                public void onDirectionFailure( Throwable t ) {

                                                }


                                            });
                                }
                            }

                            @Override
                            public void onNothingSelected (AdapterView < ? > parent ){

                            }
                        });
                    }

                    @Override
                    public void onFailure( Throwable throwable ) {
                    }
                });

            }
        });


    }

    @Override
    public void onMapReady( GoogleMap googleMap ) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng place = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        googleMap.addMarker(new MarkerOptions().position(place)
                .title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 16));
        this.google = googleMap;

    }



}






              /*

        origin =  new LatLng(Double.parseDouble(enterlatmap), Double.parseDouble(enterlonmap));
        destination = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        Log.i("orignnn",origin +""+ destination);



*/