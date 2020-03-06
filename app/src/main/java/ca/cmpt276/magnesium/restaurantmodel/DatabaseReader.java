package ca.cmpt276.magnesium.restaurantmodel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * SFU CMPT 276
 * Term Project - Team Magnesium
 *
 * Restaurant Model: Database Reader Class
 * Reads from the SQLite database and creates objects
 * for use elsewhere in the restaurant model.
 * Mostly contains static factory methods.
 *
 * @author dlutz
 *
 */

public class DatabaseReader {

    private Context activityContext; // Needed for calling DBHelperFacility

    public DatabaseReader(Context activityContext) {
        this.activityContext = activityContext;
    }

    /**
     * Create a Facility object from records in the database.
     * @param trackingNumber the tracking number of the restaurant
     * @return Facility if found in db, null if not in db
     */
    public static Facility facilityFactory(String trackingNumber) {
        // TODO - implement this stub
        Facility returnFacility = null;

        return returnFacility;
    }

    /**
     * Create an InspectionReport object from records in the database.
     * @param trackingNumber the tracking number of the report
     * @return InspectionReport if found in db, null if not in db
     */
    public static InspectionReport inspectionReportFactory(String trackingNumber) {
        // TODO - implement this stub
        InspectionReport returnReport = null;

        return returnReport;
    }

    public ArrayList<String> getAllTrackingNums() {
        ArrayList<String> returnArray = new ArrayList<>();

        // Get the dbHelper instance:
        DatabaseHelperFacility dbHelper = new DatabaseHelperFacility(activityContext);
        // Make sure the table has been created properly:
        dbHelper.ensureFacilityDBCreation(dbHelper.getWritableDatabase());
        // Get database to read from:
        SQLiteDatabase facilityDB = dbHelper.getReadableDatabase();

        String allTrackingNumQuery = "SELECT " + DatabaseHelperFacility.COL_1 + " FROM " +
                DatabaseHelperFacility.TABLE_FACILITY_NAME;

        // Read all values from the SQLite database!
        // No "where" clause here:
        Cursor queryResults = facilityDB.rawQuery(allTrackingNumQuery, null);

        // Now, go through the Cursor and add all resulting values to the ArrayList:
        // But only do it if the result is not empty.
        if (queryResults.moveToFirst()) {
            while (!queryResults.isAfterLast()) {
                String trackingNum = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_1)
                );
                returnArray.add(trackingNum);
                // Advance the Cursor:
                queryResults.moveToNext();
            }
        }

        // All values should have been added.
        return returnArray;
    }

    public ArrayList<Facility> getAllFacilities() {
        ArrayList<Facility> returnArray = new ArrayList<>();

        // Get the dbHelper instance:
        DatabaseHelperFacility dbHelper = new DatabaseHelperFacility(activityContext);
        // Make sure the table has been created properly:
        dbHelper.ensureFacilityDBCreation(dbHelper.getWritableDatabase());
        // Get database to read from:
        SQLiteDatabase facilityDB = dbHelper.getReadableDatabase();

        String allTrackingNumQuery = "SELECT * FROM " +
                DatabaseHelperFacility.TABLE_FACILITY_NAME;

        // Cursor to peruse all results:
        Cursor queryResults = facilityDB.rawQuery(allTrackingNumQuery, null);

        // Now, go through the Cursor and add all resulting values to the ArrayList:
        // But only do it if the result is not empty.
        if (queryResults.moveToFirst()) {
            while (!queryResults.isAfterLast()) {
                String trackingNum = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_1)
                );
                String name = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_2)
                );
                String physicalAddr = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_3)
                );
                String city = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_4)
                );
                String facilityType = queryResults.getString(
                            queryResults.getColumnIndex(
                                    DatabaseHelperFacility.COL_5));
                Double latitude = Double.parseDouble(queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_6))
                );
                Double longitude = Double.parseDouble(queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperFacility.COL_7))
                );

                Facility currentFacility = new Facility( trackingNum, name, physicalAddr,
                        city, facilityType, latitude, longitude);

                returnArray.add(currentFacility);
                // Advance the Cursor:
                queryResults.moveToNext();
            }
        }

        // All values should have been added.
        return returnArray;
    }


}
