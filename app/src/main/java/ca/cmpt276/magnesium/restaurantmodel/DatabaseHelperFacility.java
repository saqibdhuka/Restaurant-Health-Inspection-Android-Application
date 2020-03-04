package ca.cmpt276.magnesium.restaurantmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DatabaseHelperFacility extends SQLiteOpenHelper {

    public static final String DATABASE_FACILITY_NAME = "factility.db";
    public static final String TABLE_FACILITY_NAME = "factility_table";

    public static final String filePath = "C:\\Users\\saqib\\Documents\\CMPT 276\\Magnesium Group project\\prj\\restaurants_itr1.csv";


    //Facility database column names
    public static final String COL_1 ="TrackingNumber";
    public static final String COL_2 ="Name";
    public static final String COL_3 ="PhysicalAddress";
    public static final String COL_4 ="PhysicalCity";
    public static final String COL_5 ="FacilityType";
    public static final String COL_6 ="Latitude";
    public static final String COL_7 ="Longitude";

    public DatabaseHelperFacility(Context context) {
        //Pass in 'f' to make facility or 'i' to make inspection type
        super(context, DATABASE_FACILITY_NAME, null, 1);
        insertData();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_FACILITY_NAME +
                "(" + COL_1 + " TEXT PRIMARY KEY," + COL_2 + " TEXT," +
                COL_3 + " TEXT," + COL_4 + " TEXT," + COL_5 + " TEXT," +
                COL_6 + " DOUBLE," + COL_7 + " DOUBLE" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FACILITY_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ReadingCSVFacility facility = new ReadingCSVFacility(filePath);
        for(int i =0; i < facility.getFacilityArrayList().size(); i++){
            contentValues.put(COL_1, facility.getFacilityAtPos(i).getTrackingNumber());
            contentValues.put(COL_2, facility.getFacilityAtPos(i).getName());
            contentValues.put(COL_3, facility.getFacilityAtPos(i).getAddress());
            contentValues.put(COL_4, facility.getFacilityAtPos(i).getCity());
            contentValues.put(COL_5, facility.getFacilityAtPos(i).getFacilityType().toString());
            contentValues.put(COL_6, facility.getFacilityAtPos(i).getLatitude());
            contentValues.put(COL_7, facility.getFacilityAtPos(i).getLongitude());
            long result = db.insert(DATABASE_FACILITY_NAME, null, contentValues);
            if(result == -1){
                Log.e("TABLE INSERTION ERROR","Error inserting data at i = " + i + "\n" +
                                                    facility.getFacilityAtPos(i).toString()); //It had an error inserting data
            }
        }
    }


}
