package com.example.ikshita.travel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AutocompleteResultType;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class tab1 extends Fragment {
    String lat;
    String lng;
    String enterlat;
    String enterlon;
    private int pstatus = 0;
    private ProgressDialog progbar;
    private FusedLocationProviderClient client_location_provider;
    private static final String TAG="tab1";
    //private static final String TAG =MainActivity.class.getName() ;
    private static final String REQUESTTAG = "string request first";

    private RequestQueue mRequestQueue;
    private String url ;
    private Handler handler_p = new Handler();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab1_xml,container,false);

        return view;
    }



    public void onViewCreated(View v, Bundle savedInstanceState) {


        super.onViewCreated(v, savedInstanceState);
        final RadioButton radio1 = (RadioButton) getView().findViewById(R.id.radio1);
        final RadioButton radio2 = (RadioButton) getView().findViewById(R.id.radio2);
        final PlacesAutocompleteTextView location = (PlacesAutocompleteTextView) getView().findViewById(R.id.location);
        final Button submit = (Button) getView().findViewById(R.id.submit);
        final Button clear = (Button) getView().findViewById(R.id.clear);
        final TextView ErrorTextTitle = (TextView) getView().findViewById(R.id.ErrorTextTitle);
        final EditText keyword = (EditText) getView().findViewById(R.id.keyword);
        final TextView ErrorTextLoc = (TextView) getView().findViewById(R.id.ErrorTextLoc);
        final Spinner spinnercat = (Spinner) getView().findViewById(R.id.spinnercat);
        final EditText editTextDist = (EditText) getView().findViewById(R.id.editTextDist);
        final RadioGroup radiogroup = (RadioGroup) getView().findViewById(R.id.radiogroup);

        location.setLocationBiasEnabled(true);
        location.setResultType(AutocompleteResultType.GEOCODE);
        location.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull com.seatgeek.placesautocomplete.model.Place place) {
                location.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(PlaceDetails placeDetails) {
                        enterlat = String.valueOf(placeDetails.geometry.location.lat);
                        enterlon= String.valueOf(placeDetails.geometry.location.lng);
                        Log.d("test", enterlat);
                        Log.d("test1", enterlon);
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                });

            }
        });

        location.setEnabled(false);
        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setEnabled(false);
                location.setText("");
                ErrorTextLoc.setText("");
            }
        });
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setEnabled(true);

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String  input =  keyword.getText().toString().trim();
                if(input.length() == 0 ){

                    ErrorTextTitle.setText("Please enter mandatory field");
                    ErrorTextTitle.setTextColor(Color.RED);
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) getActivity().findViewById(R.id.root));
                    //get the TextView from the custom_toast layout
                    TextView text = (TextView) layout.findViewById(R.id.toastText);
                    text.setText("Please fix all fields with error");
                    //create the toast object, set display duration,
                    //set the view as layout that's inflated above and then call show()
                    Toast t = new Toast(getActivity().getApplicationContext());
                    t.setDuration(Toast.LENGTH_LONG);
                    t.setView(layout);
                    t.show();
                }
                if(input.length() != 0 ){
                    ErrorTextTitle.setText("");
                }

                String  inputloc =  location.getText().toString().trim();
                if((inputloc.length() == 0) && (radio2.isChecked()) ){

                    ErrorTextLoc.setText("Please enter mandatory field");
                    ErrorTextLoc.setTextColor(Color.RED);
                    //get the LayoutInflater and inflate the custom_toast layout
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) getActivity().findViewById(R.id.root));

                    //get the TextView from the custom_toast layout
                    TextView text = (TextView) layout.findViewById(R.id.toastText);
                    text.setText("Please fix all fields with error");

                    //create the toast object, set display duration,
                    //set the view as layout that's inflated above and then call show()
                    Toast t = new Toast(getActivity().getApplicationContext());
                    t.setDuration(Toast.LENGTH_LONG);
                    t.setView(layout);
                    t.show();
                }
                if((inputloc.length() != 0 ) && (radio2.isChecked())){
                    ErrorTextLoc.setText("");
                }

                if((input.length() != 0 && (inputloc.length() != 0)) || (input.length() != 0 && (radio1.isChecked())) )  {
                    sendRequestAndPrintResponse();
                    progressbar(v);
                }


            }
            });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyword.setText(null);
                ErrorTextLoc.setText(null);
                editTextDist.setText(null);
                ErrorTextTitle.setText(null);
                radio1.setChecked(true);
                radio2.setChecked(false);
                spinnercat.setSelection(0);
                location.setEnabled(false);
                location.setText(null);

            }

        });

        location();



    }
    private void location(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION},1);
        client_location_provider = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(),ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){return;}
        client_location_provider.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                if (loc != null) {
                     lat = String.valueOf(loc.getLatitude());
                     lng = String.valueOf(loc.getLongitude());
                    Log.i("Lat",lat);
                    Log.i("Lng",lng);

                }
            }
        });
    }
    private void sendRequestAndPrintResponse() {
        final RadioButton radio1 = (RadioButton) getView().findViewById(R.id.radio1);
        final RadioButton radio2 = (RadioButton) getView().findViewById(R.id.radio2);
        final AutoCompleteTextView location = (AutoCompleteTextView ) getView().findViewById(R.id.location);
        final Button submit = (Button) getView().findViewById(R.id.submit);
        final Button clear = (Button) getView().findViewById(R.id.clear);
        final EditText keyword = (EditText) getView().findViewById(R.id.keyword);
        final Spinner spinnercat = (Spinner) getView().findViewById(R.id.spinnercat);
        final EditText editTextDist = (EditText) getView().findViewById(R.id.editTextDist);
        final RadioGroup radiogroup = (RadioGroup) getView().findViewById(R.id.radiogroup);
        String key = keyword.getText().toString();

        String loc = location.getText().toString();
        String spinner = spinnercat.getSelectedItem().toString();
        String dist;

        if(editTextDist.getText().toString().length() == 0 ){
            dist = "10";
        }
        else {
             dist = editTextDist.getText().toString();
        }


        int radioID = radiogroup.getCheckedRadioButtonId();
        View radioButton = radiogroup.findViewById(radioID);
        int index = radiogroup.indexOfChild(radioButton);
        RadioButton checkedradio = (RadioButton)  radiogroup.getChildAt(index);
        String checkedtext = checkedradio.getText().toString();


        Log.d(TAG, "Key: " + key.toString().trim().replaceAll("\\s+","+"));
        Log.d(TAG, "Dist: " +dist );
        Log.d(TAG, "loc: " + loc.toString());
        Log.d(TAG, "spinner: " + spinner.toString());
        Log.d(TAG, "CheckedRadio: " + checkedtext.toString());

        if(radio1.isChecked()) {
            url = "http://csci571-node20.us-east-2.elasticbeanstalk.com/heredetails?keyword=" + key + "&cat=" + spinner + "&distance=" + dist + "&currentlat=" + lat + "&currentlon=" + lng;
        }
        if(radio2.isChecked()) {
            url = "http://csci571-node20.us-east-2.elasticbeanstalk.com/heredetails?keyword=" + key + "&cat=" + spinner + "&distance=" + dist + "&currentlat=" + enterlat + "&currentlon=" + enterlon;
        }
       /* if(location.length() == 0){
            url = "http://10.0.2.2:3000/heredetails?keyword=" + key + "&cat=" + spinner + "&distance=" + dist + "&currentlat=" + enterlat + "&currentlon=" + enterlon;

        }
        */
        mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    Log.i(TAG,"Response : " + response.toString());
                    Intent intent = new Intent(getActivity(), Search_activity.class);
                    intent.putExtra("result", response.toString());
                    progbar.dismiss();
                    startActivity(intent);
                    /*JSONArray array = json.getJSONArray("results");
                    JSONObject jsonObj = (JSONObject) array.get(0);
                    JSONObject geometry = jsonObj.getJSONObject("geometry");
                    String latitude = geometry.getJSONObject("location").getString("lat");
                    System.out.println("");
                    */
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

                Log.i(TAG,"Error : " + error.toString());
            }

        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public void progressbar(View v){
        progbar=new ProgressDialog(v.getContext());
        progbar.setCancelable(true);
        progbar.setMessage("Fetching Details");
        progbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progbar.setProgress(0);
        progbar.show();

    }

}


