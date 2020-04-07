package ca.cmpt276.magnesium.restaurantmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import ca.cmpt276.magnesium.healthinspectionviewer.R;
import ca.cmpt276.magnesium.healthinspectionviewer.SearchActivity;

public class ReadingCSVFacility {
    private ArrayList<Facility> facilitiesList;
    private static Context context;

    private static ReadingCSVFacility reader;

    private ReadingCSVFacility(Context cont) {
        context = cont;
        setFacilityRes(getFacilityFromTextFile());
    }

    public static ReadingCSVFacility getCSVReader(Context context) {
        // Lazy instantiation, singleton.
        // This way, we don't recreate readers for no reason.
        if (reader == null) {
            reader = new ReadingCSVFacility(context);
        }

        return reader;
    }

    private static ArrayList<Facility> getFacilityFromTextFile(){
        InputStream is = null;
        BufferedReader bufferedReader = null;
        ArrayList<Facility> facilityArrayList = new ArrayList<Facility>();
        SharedPreferences prefs = context.getSharedPreferences(DataUpdater.prefsFile, 0);
        try{
            // Try to use the new file, but fallback to the default if necessary.
            String fileLocation = prefs.getString(DataUpdater.downloadedRestaurantFilenameKey, null);
            if (fileLocation != null) {
                File downloadedRestaurants = new File(fileLocation);
                if (downloadedRestaurants.exists()) {
                    is = new FileInputStream(downloadedRestaurants);
                } else {
                    is = context.getResources().openRawResource(R.raw.restaurants_current);
                }
            } else {
                is = context.getResources().openRawResource(R.raw.restaurants_current);
            }

            bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            // Save line we get from csv file
            String line = null;

            //Save Facility
            String[] strFacility = null;

            bufferedReader.readLine(); // Skip first line since tha is column name
            while (true){
                //read line
                line = bufferedReader.readLine();

                // Check if line exists
                if (line != null){
                    strFacility = line.replace("\"", "").split(",");
                    int index = 0;
                    String trackingNumber = strFacility[index];
                    index++;
                    String name;
                    if (strFacility.length == 7) {
                        name = strFacility[index];
                        index++;
                    } else {
                        name = strFacility[index] + ", " + strFacility[index + 1];
                        index += 2;
                    }
                    String address = strFacility[index];
                    index++;
                    String city = strFacility[index];
                    index++;
                    String facilityTypeString = strFacility[index];
                    // Convert this facility type to enum:
                    FacilityType facilityType = FacilityType.valueOf(facilityTypeString);
                    index++;
                    Double latitude = Double.parseDouble(strFacility[index]);;
                    index++;
                    Double longitude = Double.parseDouble(strFacility[index]);
                    DatabaseReader reader = new DatabaseReader(context);
                    InspectionReport firstReport = reader.getFirstAssociatedInspection(trackingNumber);
                    String inspectionString = "N/A";
                    if (firstReport != null) {
                        inspectionString = firstReport.getInspectionDateString();
                    }

                    // Set the restaurant ID based on a few context clues:
                    int iconID;
                    if(name.toLowerCase().contains("freshslice")
                            && name.toLowerCase().contains("pizza")) {
                        iconID = R.drawable.freshslice;
                    } else if(name.toLowerCase().contains("burger")
                            && name.toLowerCase().contains("king")){
                        iconID = R.drawable.burgerking;
                    } else if (name.toLowerCase().contains("pizza")) {
                        iconID = R.drawable.pizza;
                    } else if (name.toLowerCase().contains("seafood")
                            || name.toLowerCase().contains("sushi")) {
                        iconID = R.drawable.fish;
                    } else if (name.toLowerCase().contains("burger")) {
                        iconID = R.drawable.burger;
                    } else if(name.toLowerCase().contains("a&w")){
                        iconID = R.drawable.aandw;
                    }  else if(name.toLowerCase().contains("donalds")){
                        iconID = R.drawable.mcdonald;
                    } else if(name.toLowerCase().contains("hortons")){
                        iconID = R.drawable.timhortons;
                    } else if(name.toLowerCase().contains("eleven")){
                        iconID = R.drawable.seveneleven;
                    } else if(name.toLowerCase().contains("kfc")){
                        iconID = R.drawable.kfc;
                    } else if(name.toLowerCase().contains("subway")){
                        iconID = R.drawable.subway;
                    } else if(name.toLowerCase().contains("starbucks")){
                        iconID = R.drawable.starbucks;
                    } else if(name.toLowerCase().contains("wendy's")){
                        iconID = R.drawable.wendys;
                    }
                    else {
                        // Default "generic" icon: kitchen
                        iconID = R.drawable.kitchen;
                    }

                    facilityArrayList.add(new Facility(trackingNumber, name,
                            address, city,
                            facilityType,
                            latitude,
                            longitude,
                            iconID,
                            inspectionString));
                } else {
                    break;
                }
            }
        } catch (Exception e){
            Log.d("DEBUG FILE ERROR", "Error Reading File");
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return facilityArrayList;
    }

    public ArrayList<Facility> getFacilityArrayList() {
        ArrayList<Facility> returnList = null;
        SharedPreferences prefs = context.getSharedPreferences(SearchActivity.SEARCH_PREFSFILE, 0);
        String query = prefs.getString(SearchActivity.SEARCH_QUERYSTRING, null);
        if (query != null) {
            // We need to pull this out from the DatabaseHelper!
            DatabaseReader reader = new DatabaseReader(context);
            returnList = reader.getFacilitiesFromDB();
        } else {
            // Return what we already have, it's faster:
            returnList = facilitiesList;
        }
        return returnList;
    }

    public Facility getFacilityAtPos(int i){
        return facilitiesList.get(i);
    }
    public void setFacilityRes(ArrayList<Facility> facilityRes) {
        this.facilitiesList = facilityRes;
    }
}
