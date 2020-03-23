package ca.cmpt276.magnesium.restaurantmodel;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static android.content.Context.DOWNLOAD_SERVICE;

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
    private static final String restaurantCsvUrlString = "url";
    private static final String restaurantCsvUrlKey = "dataUpdater_updateAvailableRestaurant_url";
    private static final String inspectionCsvUrlString = "url";
    private static final String inspectionCsvUrlKey = "dataUpdater_updateAvailableInspection_url";
    private static final String updateIgnoredString = "update_dismissed";
    private static final String lastCheckDateString = "dataUpdater_last_checked";

    private static final String downloadedRestaurantFilenameKey = "dataUpdater_restaurant_filename";
    private static final String downloadedInspectionFilenameKey = "dataUpdater_inspection_filename";


    // Use GET request to query City of Surrey website
    // and compare to SharedPrefs saved dates.
    // If any dates differ, set SharedPrefs to
    public static void checkForAvailableUpdates(Context callerContext) {
        resetIgnoreBoolean(callerContext);
        Hours timeSinceLastUpdated = numHoursSinceUpdate(callerContext);
        // TODO change -1 to 20, this is for debug only
        if (timeSinceLastUpdated.isGreaterThan(Hours.hours(-1))) {
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
                    // Ensure we use HTTPS as Android disallows cleartext traffic:
                    String newRestaurantURL = firstResult.get(restaurantCsvUrlString).getAsString().replace("http://", "https://");
                    if (restaurantLastUpdated.equals(newLastUpdated)) {
                        // DO nothing
                    } else {
                        // Update sharedPrefs bool to say that an update is available:
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean(updateAvailableRestaurantBool, true);
                        edit.putString(restaurantCsvUrlKey, newRestaurantURL);
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
                    // Wipe out the old URL:
                    edit.putString(restaurantCsvUrlKey, null);
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
                    // Ensure we use HTTPS as Android disallows cleartext traffic
                    String newInspectionURL = firstResult.get(inspectionCsvUrlString).getAsString().replace("http://", "https://");
                    if (inspectionLastUpdated.equals(newLastUpdated)) {
                        // DO nothing
                    } else {
                        // Update sharedPrefs bool to say that an update is available:
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean(updateAvailableInspectionBool, true);
                        edit.putString(inspectionCsvUrlKey, newInspectionURL);
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
                    // Wipe out old URL
                    edit.putString(inspectionCsvUrlKey, null);
                    edit.apply();
                }
            });

            // Add these to the async RequestQueue to be executed in another thread:
            queue.add(restaurantRequest);
            queue.add(inspectionRequest);
            // Save now as the last checked time:
            saveCurrentUpdateTime(callerContext);
        }
    }

    // Helper function for determining the number of hours between last check for updates:
    private static Hours numHoursSinceUpdate(Context callerContext) {
        Hours returnHours;
        SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0);
        String lastCheckedString = prefs.getString(lastCheckDateString, null);
        if (lastCheckedString == null) {
            // If there is no stored value, assume we've never checked.
            returnHours = Hours.hours(24);
        } else {
            // Something is stored here.
            Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
            // Deserialize last DateTime with Gson:
            DateTime lastUpdatedDate = gson.fromJson(lastCheckedString, DateTime.class);
            // Also get current time:
            DateTime now = DateTime.now();
            // Find the difference:
            returnHours = Hours.hoursBetween(lastUpdatedDate, now);
        }

        return returnHours;
    }

    // Helper function: After checking for update, set stored DateTime to now.
    private static void saveCurrentUpdateTime(Context callerContext) {
        SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0);
        Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
        DateTime now = DateTime.now();
        String serializedNow = gson.toJson(now);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(lastCheckDateString, serializedNow);
        edit.apply();
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
                        downloadFiles(callerContext);
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

    // Helper function: downloadFiles()
    // Uses DownloadManager to download the newly-updated files.
    // Saves to local, internal (read: private to this application) directory.
    private static void downloadFiles(Context callerContext) {
        SharedPreferences prefs = callerContext.getSharedPreferences(prefsFile, 0);
        boolean restaurantsNeedUpdate = prefs.getBoolean(updateAvailableRestaurantBool, false);
        boolean inspectionsNeedUpdate = prefs.getBoolean(updateAvailableInspectionBool, false);
        DownloadManager manager = (DownloadManager)callerContext.getSystemService(DOWNLOAD_SERVICE);

        if (restaurantsNeedUpdate) {
            String restaurantURL = prefs.getString(restaurantCsvUrlKey, null);
            if (restaurantURL != null) {
                String downloadFilename = "restaurants_" + DateTime.now().toString()
                        .replace(" ", "_")
                        .replace(":", "_")
                        .replace("-", "_")
                        + ".csv";
                File downloadedRestaurant = new File(callerContext.getExternalFilesDirs(null)[0], downloadFilename);
                DownloadManager.Request restaurantRequest =
                        new DownloadManager.Request(Uri.parse(restaurantURL))
                                .setTitle("Restaurant Data")
                                .setDescription("Downloading")
                                .setDestinationUri(Uri.fromFile(downloadedRestaurant));
                if (manager != null) {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(downloadedRestaurantFilenameKey, downloadedRestaurant.getAbsolutePath());
                    edit.apply();
                    manager.enqueue(restaurantRequest);
                }
            }
        }

        if (inspectionsNeedUpdate) {
            String inspectionURL = prefs.getString(inspectionCsvUrlKey, null);
            if (inspectionURL != null) {
                String downloadFilename = "inspections_" + DateTime.now().toString()
                        .replace(" ", "_")
                        .replace(":", "_")
                        .replace("-", "_")
                        + ".csv";
                File downloadedInspections = new File(callerContext.getExternalFilesDirs(null)[0], downloadFilename);
                DownloadManager.Request inspectionRequest =
                        new DownloadManager.Request(Uri.parse(inspectionURL))
                                .setTitle("Inspection Data")
                                .setDescription("Downloading")
                                .setDestinationUri(Uri.fromFile(downloadedInspections));
                if (manager != null) {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(downloadedInspectionFilenameKey, downloadedInspections.getAbsolutePath());
                    edit.apply();
                    manager.enqueue(inspectionRequest);
                }
            }
        }
        // Debug files. TODO remove these.
//        File downloadedInspection = new File(prefs.getString(downloadedInspectionFilenameKey, ""));
//        File downloadedRestaurants = new File(prefs.getString(downloadedRestaurantFilenameKey, ""));
    }
}