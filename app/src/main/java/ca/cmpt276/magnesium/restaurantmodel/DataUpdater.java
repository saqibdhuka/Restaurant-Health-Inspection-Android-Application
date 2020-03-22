package ca.cmpt276.magnesium.restaurantmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *  DataUpdater class
 *  Facilitates reading of updated data from remote server.
 *  Attempts to operate as atomically as possible to allow cancellation.
 *
 *  We use the Google Volley library to help with async web connections.
 *
 *  March 17, 2020
 *  Devon Lutz, SFU ID 301349016
 *  CMPT 276 - Project - Sprint 2
 *
 */


public class DataUpdater {

    // Useful constants
    private static final String surreyRestaurantGetURL =
            "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String surreyInspectionsGetURL =
            "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    public static final String prefsFile = "healthInspection_dataUpdater_prefs";
    public static final String updateAvailableRestaurantBool = "dataUpdater_updateAvailableRestaurant_bool";
    public static final String updateAvailableInspectionBool = "dataUpdater_updateAvailableInspection_bool";
    public static final String restaurantCheckKey = "dataUpdater_restaurant_lastUpdated";
    public static final String inspectionCheckKey = "dataUpdater_inspection_lastUpdated";
    private static final String resourcesString = "resources";
    private static final String modifiedString = "last_modified";
    private static final String updateIgnoredString = "update_dismissed";


    // Use GET request to query City of Surrey website
    // and compare to SharedPrefs saved dates.
    // If any dates differ, set SharedPrefs to
    public static void checkForAvailableUpdates(Context callerContext) {
        // TODO implement this to check only after 20 hours since last check!
        resetIgnoreBoolean(callerContext);
        // Do this in an async way, so Android lets us continue!
        RequestQueue queue = Volley.newRequestQueue(callerContext);
        // Check the restaurant json file for updates (i.e. new restaurants):
        StringRequest restaurantRequest = new StringRequest(Request.Method.GET, surreyRestaurantGetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Use anonymous class method to compare response with the one in sharedPrefs
                JsonObject obj = new Gson().fromJson(response, JsonObject.class).getAsJsonObject();
                JsonObject result = obj.getAsJsonObject("result");
                JsonArray resources = result.getAsJsonArray(resourcesString);
                JsonObject firstResult = resources.get(0).getAsJsonObject();
                SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0); // 0 means private mode
                String restaurantLastUpdated = prefs.getString(restaurantCheckKey, "");
                String newLastUpdated = firstResult.get(modifiedString).getAsString();
                if (restaurantLastUpdated.equals(newLastUpdated)) {
                    // DO nothing
                } else {
                    // Update sharedPrefs bool to say that an update is available:
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(updateAvailableRestaurantBool, true);
                    edit.apply();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // If an error is caught, cannot determine if an update is available or not.
                SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0); // 0 means private mode
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(updateAvailableRestaurantBool, false);
                edit.apply();
            }
        });

        // Check the inspection json file for updates (i.e. new inspection reports):
        StringRequest inspectionRequest = new StringRequest(Request.Method.GET, surreyInspectionsGetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Use anonymous class method to compare response with the one in sharedPrefs
                JsonObject obj = new Gson().fromJson(response, JsonObject.class).getAsJsonObject();
                JsonObject result = obj.getAsJsonObject("result");
                JsonArray resources = result.getAsJsonArray(resourcesString);
                JsonObject firstResult = resources.get(0).getAsJsonObject();
                SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0); // 0 means private mode
                String inspectionLastUpdated = prefs.getString(inspectionCheckKey, "");
                String newLastUpdated = firstResult.get(modifiedString).getAsString();
                if (inspectionLastUpdated.equals(newLastUpdated)) {
                    // DO nothing
                } else {
                    // Update sharedPrefs bool to say that an update is available:
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(updateAvailableInspectionBool, true);
                    edit.apply();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If an error is caught, cannot determine if an update is available or not.
                SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(updateAvailableInspectionBool, false);
                edit.apply();
            }
        });

        // Add these to the async RequestQueue to be executed in another thread:
        queue.add(restaurantRequest);
        queue.add(inspectionRequest);
    }

    private static void resetIgnoreBoolean(Context callerContext) {
        SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0); // 0 means private mode
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(updateIgnoredString, false);
        edit.apply();
    }


    // notifyIfUpdateAvailable():
    // Creates an AlertDialog asking if the user wants to update,
    // if updates are available.
    public static void notifyIfUpdateAvailable(Context callerContext) {
        SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0);
        boolean ignore = prefs.getBoolean(updateIgnoredString, false);
        if (!ignore) {
            boolean restaurantsNeedUpdate = prefs.getBoolean(updateAvailableRestaurantBool, false);
            boolean inspectionsNeedUpdate = prefs.getBoolean(updateAvailableInspectionBool, false);

            if (restaurantsNeedUpdate || inspectionsNeedUpdate) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(callerContext);
                alertBuilder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO implement the update functionality
                        SharedPreferences.Editor edit = prefs.edit();
                        // Mark that we have successfully updated;
                        edit.putBoolean(updateAvailableInspectionBool, false);
                        edit.putBoolean(updateAvailableRestaurantBool, false);
                        edit.apply();
                    }
                });

                alertBuilder.setNegativeButton("Ignore Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set this as "update ignored" so we only call this on the first time:
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean(updateIgnoredString, true);
                        edit.apply();
                    }
                });
                // TODO: replace these UI strings with resources from strings.xml!
                alertBuilder.setMessage("New content available for restaurant resources - would you like to update now?");
                // Show this alertDialog:
                alertBuilder.create().show();
            }
        }
    }

}
