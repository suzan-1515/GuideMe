package com.guideme.guideme.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = " -N/A- ";
        String vicinity = " -N/A- ";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String icon = "";
        String id = "";
        String rating = "";

        try {
            if (!googlePlaceJSON.isNull("name")) {
                NameOfPlace = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            if (!googlePlaceJSON.isNull("icon")) {
                icon = googlePlaceJSON.getString("icon");
            }
            if (!googlePlaceJSON.isNull("rating")) {
                rating = googlePlaceJSON.getString("rating");
            }

            id = googlePlaceJSON.getString("place_id");
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("id", id);
            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("icon", icon);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("rating", rating);
            googlePlaceMap.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();
        List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
        HashMap<String, String> nearbyPlaceMap = null;
        for (int i = 0; i < counter; i++) {
            try {
                nearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                nearbyPlacesList.add(nearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearbyPlacesList;
    }

    public List<HashMap<String, String>> parse(String jSONData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jSONData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray);
    }

    public HashMap<String, String> parsePlaceDetails(String jSONData) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jSONData);
            jsonObject = jsonObject.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaceDetail(jsonObject);
    }


    private HashMap<String, String> getPlaceDetail(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = " -N/A- ";
        String vicinity = " -N/A- ";
        String latitude = "";
        String longitude = "";
        String icon = "";
        String id = "";
        String rating = "";
        String website = "";

        try {
            if (!googlePlaceJSON.isNull("name")) {
                NameOfPlace = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            if (!googlePlaceJSON.isNull("icon")) {
                icon = googlePlaceJSON.getString("icon");
            }
            if (!googlePlaceJSON.isNull("website")) {
                website = googlePlaceJSON.getString("website");
            }

            id = googlePlaceJSON.getString("place_id");
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            rating = googlePlaceJSON.getString("rating");

            googlePlaceMap.put("id", id);
            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("icon", icon);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("rating", rating);
            googlePlaceMap.put("website", website);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }
}
