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

public class DatabaseHelperInspection extends SQLiteOpenHelper {

    public static final String DATABASE_INSP_NAME = "inspection.db";
    public static final String TABLE_INSP_NAME = "inspection_table";

    // Inspection database column names
    public static final String COL_1="TrackingNumber";
    public static final String COL_2 = "InspectionDate";
    public static final String COL_3 = "InspType";
    public static final String COL_4 = "NumCritical";
    public static final String COL_5 = "NumNonCritical";
    public static final String COL_6 = "HazardRating";
    public static final String COL_7 = "ViolLump";

    public static Context contextActivity;
    
    public DatabaseHelperInspection(Context context) {
        super(context, DATABASE_INSP_NAME, null, 1);
        contextActivity = context;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ensureInspectionDBCreation(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ReadingCSVInspection inspection = new ReadingCSVInspection(contextActivity);
        for(int i = 0; i < inspection.getInspectionArrayList().size(); i++){
            contentValues.put(COL_1, inspection.getInspectionReportAtPos(i).getTrackingNumber());
            contentValues.put(COL_2, inspection.getInspectionReportAtPos(i).getInspectionDate());
            contentValues.put(COL_3, inspection.getInspectionReportAtPos(i).getInspectionType().toString());
            contentValues.put(COL_4, inspection.getInspectionReportAtPos(i).getNumCritical());
            contentValues.put(COL_5, inspection.getInspectionReportAtPos(i).getNumNonCritical());
            contentValues.put(COL_6, inspection.getInspectionReportAtPos(i).getHazardRating().toString());
            contentValues.put(COL_7, inspection.getInspectionReportAtPos(i).getViolationStatement());
            long result = db.insertWithOnConflict(TABLE_INSP_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            if(result == -1){
                Log.e("TABLE INSERTION ERROR","Error inserting data at i = " + i + "\n" +
                        inspection.getInspectionReportAtPos(i).toString()); //It had an error inserting data
            }
        }
        db.close();
    }

    public static void ensureInspectionDBCreation(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table " + TABLE_INSP_NAME + " (" +
                COL_1 + " TEXT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " INTEGER, " +
                COL_5 + " INTEGER, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT, " +
                "FOREIGN KEY ("+COL_1+") REFERENCES "+
                DatabaseHelperFacility.TABLE_FACILITY_NAME+"("+DatabaseHelperFacility.COL_1+"));"
        );
    }
}
