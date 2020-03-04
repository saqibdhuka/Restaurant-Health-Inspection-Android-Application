package ca.cmpt276.magnesium.restaurantmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelperInspection extends SQLiteOpenHelper {

    public static final String DATABASE_INSP_NAME = "inspection.db";
    public static final String TABLE_INSP_NAME = "inspection_table";

    public static String filepath = "C:\\Users\\saqib\\Documents\\CMPT 276\\Magnesium Group project\\prj\\inspectionreports_itr1.csv";
    //Inspection database column names
    public static final String COL_1="TrackingNumber";
    public static final String COL_2 = "InspectionDate";
    public static final String COL_3 = "InspType";
    public static final String COL_4 = "NumCritical";
    public static final String COL_5 = "NumNonCritical";
    public static final String COL_6 = "HazardRating";
    public static final String COL_7 = "ViolLump";

    public DatabaseHelperInspection(Context context) {
        super(context, DATABASE_INSP_NAME, null, 1);
        insertData();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_INSP_NAME +
                "(" + COL_1 + " TEXT PRIMARY KEY," + COL_2 + " TEXT," +
                COL_3 + " TEXT," + COL_4 + " TEXT," + COL_5 + " TEXT," +
                COL_6 + " DOUBLE," + COL_7 + " DOUBLE" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INSP_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ReadingCSVInspection inspection = new ReadingCSVInspection(filepath);
        for(int i = 0; i < inspection.getInspectionArrayList().size(); i++){
            contentValues.put(COL_1, inspection.getInspectionReportAtPos(i).getTrackingNumber());
            contentValues.put(COL_2, inspection.getInspectionReportAtPos(i).getInspectionDate());
            contentValues.put(COL_3, inspection.getInspectionReportAtPos(i).getInspectionType().toString());
            contentValues.put(COL_4, inspection.getInspectionReportAtPos(i).getNumCritical());
            contentValues.put(COL_5, inspection.getInspectionReportAtPos(i).getNumNonCritical());
            contentValues.put(COL_6, inspection.getInspectionReportAtPos(i).getHazardRating().toString());
            contentValues.put(COL_7, inspection.getInspectionReportAtPos(i).getViolationStatement());
        }
    }
}
