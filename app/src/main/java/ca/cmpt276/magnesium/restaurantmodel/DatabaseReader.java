package ca.cmpt276.magnesium.restaurantmodel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import ca.cmpt276.magnesium.healthinspectionviewer.R;

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
                DatabaseHelperFacility.TABLE_FACILITY_NAME
                + " ORDER BY " + DatabaseHelperFacility.COL_2 + " ASC";

        // Cursor to peruse all results:
        Cursor queryResults = facilityDB.rawQuery(allTrackingNumQuery, null);

        // Randomly pick an icon for the listView:
        ArrayList<Integer> iconsList = new ArrayList<Integer>();
        iconsList.add(R.drawable.burger);
        iconsList.add(R.drawable.fish);
        iconsList.add(R.drawable.pizza);
        iconsList.add(R.drawable.kitchen);

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
                String facilityTypeStr = queryResults.getString(
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

                // Attempt to convert facilityType string to enum:
                FacilityType facilityType;
                try {
                    facilityType = FacilityType.valueOf(facilityTypeStr);
                } catch (IllegalArgumentException e) {
                    // Failed to convert, let it be null.
                    facilityType = null;
                }


                Integer iconIndex = (int)Math.round((Math.random() * (iconsList.size() - 1)));
                Integer iconID = iconsList.get(iconIndex);

                Facility currentFacility = new Facility( trackingNum, name, physicalAddr,
                        city, facilityType, latitude, longitude, iconID);

                returnArray.add(currentFacility);
                // Advance the Cursor:
                queryResults.moveToNext();
            }
        }

        // All values should have been added.
        return returnArray;
    }

    public ArrayList<InspectionReport> getAssociatedInspections(String trackingNo) {
        ArrayList<InspectionReport> returnArray = new ArrayList<>();

        // Get the dbHelper instance:
        DatabaseHelperInspection dbHelper = new DatabaseHelperInspection(activityContext);
        // Make sure the table has been created properly:
        dbHelper.ensureInspectionDBCreation(dbHelper.getWritableDatabase());
        // Get database to read from:
        SQLiteDatabase inspectionDB = dbHelper.getReadableDatabase();

        String allRelatedInspectionQuery = "SELECT * FROM " + DatabaseHelperInspection.TABLE_INSP_NAME
                + " WHERE " + DatabaseHelperInspection.COL_1 + " == ?"
                + " ORDER BY " + DatabaseHelperInspection.COL_2 + " DESC";

        // Cursor to peruse all results:
        Cursor queryResults = inspectionDB.rawQuery(allRelatedInspectionQuery,
                                                    new String[] {trackingNo});

        // Now, go through the Cursor and add all resulting values to the ArrayList:
        // But only do it if the result is not empty.
        if (queryResults.moveToFirst()) {
            while (!queryResults.isAfterLast()) {
                String inspectionTrackingNum = queryResults.getString(
                                queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_1));
                String inspectionDate = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_2));
                String inspectionType = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_3));
                String inspectionNumCritical = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_4));
                String inspectionNumNonCritical = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_5));
                String inspectionHazardRating = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_6));
                String inspectionViolations = queryResults.getString(
                        queryResults.getColumnIndex(
                                DatabaseHelperInspection.COL_7));

                InspectionReport report = new InspectionReport(inspectionTrackingNum,
                                                        inspectionDate,
                                                        inspectionType,
                                                        Integer.valueOf(inspectionNumCritical),
                                                        Integer.valueOf(inspectionNumNonCritical),
                                                        inspectionHazardRating,
                                                        inspectionViolations);

                returnArray.add(report);

                // Advance the Cursor:
                queryResults.moveToNext();
            }
        }

        // All reports should have been added.
        return returnArray;
    }


}
