package ca.cmpt276.magnesium.restaurantmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelperFacility extends SQLiteOpenHelper {

    public static final String DATABASE_FACILITY_NAME = "facility.db";
    public static final String TABLE_FACILITY_NAME = "facility_table";

    // Facility database column names
    public static final String COL_1 ="TrackingNumber";
    public static final String COL_2 ="Name";
    public static final String COL_3 ="PhysicalAddress";
    public static final String COL_4 ="PhysicalCity";
    public static final String COL_5 ="FacilityType";
    public static final String COL_6 ="Latitude";
    public static final String COL_7 ="Longitude";

    public Context contextActivity;

    public DatabaseHelperFacility(Context context) {
        super(context, DATABASE_FACILITY_NAME, null, 1);
        contextActivity = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ensureFacilityDBCreation(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FACILITY_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertData(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Ensure DB is starting from scratch with the csv as our base:
        String dropDB = "DROP TABLE IF EXISTS " + TABLE_FACILITY_NAME;
        db.execSQL(dropDB);
        ensureFacilityDBCreation(db);
        // Now add back values:
        ContentValues contentValues = new ContentValues();
        ReadingCSVFacility facility = new ReadingCSVFacility(contextActivity);
        for(int i =0; i < facility.getFacilityArrayList().size(); i++){
            contentValues.put(COL_1, facility.getFacilityAtPos(i).getTrackingNumber());
            contentValues.put(COL_2, facility.getFacilityAtPos(i).getName());
            contentValues.put(COL_3, facility.getFacilityAtPos(i).getAddress());
            contentValues.put(COL_4, facility.getFacilityAtPos(i).getCity());
            contentValues.put(COL_5, facility.getFacilityAtPos(i).getFacilityType().toString());
            contentValues.put(COL_6, facility.getFacilityAtPos(i).getLatitude());
            contentValues.put(COL_7, facility.getFacilityAtPos(i).getLongitude());
            long result = db.insert(TABLE_FACILITY_NAME, null, contentValues);
            if(result == -1){
                // It had an error inserting data
                Log.e("TABLE INSERTION ERROR","Error inserting data at i = " + i + "\n" +
                                                    facility.getFacilityAtPos(i).toString());

            }
        }
        db.close();
    }

    public void ensureFacilityDBCreation(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FACILITY_NAME + " (" +
                COL_1 + " TEXT PRIMARY KEY," +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " DOUBLE, " +
                COL_7 + " DOUBLE);"
        );
    }


}
