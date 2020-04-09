package ca.cmpt276.magnesium.restaurantmodel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.sql.PreparedStatement;
import java.util.HashSet;

/**
 * FavoritesHelper.java
 * Some basic static functions to help with managing Favorites.
 */

public class FavoritesHelper {

    public final static String FAVORITES_COL = "TrackingNo";

    private static void ensureFavoritesCreation(Context context) {
        DatabaseHelperFacility dbHelper = new DatabaseHelperFacility(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String creationStatement = "CREATE TABLE IF NOT EXISTS Favorites(TrackingNo TEXT PRIMARY KEY);";
        db.execSQL(creationStatement);
        db.close();
        dbHelper.close();
    }

    public static boolean isTrackingNoFavorite(String trackingNo, Context context) {
        HashSet<String> allFavorites = getSetOfFavoriteTrackingNums(context);
        boolean returnBool = false;
        if (allFavorites.contains(trackingNo)) {
            returnBool = true;
        }

        return returnBool;
    }

    public static void addTrackingNumToFavorites(String newTrackingNum, Context context) {
        if (!isTrackingNoFavorite(newTrackingNum, context)) {
            // Go ahead and add this one.
            DatabaseHelperFacility dbHelper = new DatabaseHelperFacility(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            SQLiteStatement insertStatement = db.compileStatement("INSERT INTO Favorites(TrackingNo) VALUES (?);");
            insertStatement.bindString(0, newTrackingNum);
            insertStatement.executeInsert();
            db.close();
            dbHelper.close();
        }
    }

    public static void removeTrackingNoFromFavorites(String trackingToDelete, Context context) {
        if (isTrackingNoFavorite(trackingToDelete, context)) {
            // Go ahead and remove this one.
            DatabaseHelperFacility dbHelper = new DatabaseHelperFacility(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            SQLiteStatement deleteStatement = db.compileStatement("DELETE FROM Favorites WHERE TrackingNo == ?;");
            deleteStatement.bindString(0, trackingToDelete);
            deleteStatement.executeInsert();
            db.close();
            dbHelper.close();
        }
    }

    private static HashSet<String> getSetOfFavoriteTrackingNums(Context context) {
        ensureFavoritesCreation(context);
        String selectString = "SELECT * FROM Favorites;";

        DatabaseHelperFacility dbHelper = new DatabaseHelperFacility(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.rawQuery(selectString, null);
        HashSet<String> returnTrackingNums = new HashSet<>();
        while (results.moveToNext()) {
            returnTrackingNums.add(results.getString(results.getColumnIndex(FAVORITES_COL)));
        }
        db.close();
        dbHelper.close();
        return returnTrackingNums;
    }


}
