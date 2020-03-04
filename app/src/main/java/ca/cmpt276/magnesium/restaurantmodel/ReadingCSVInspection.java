package ca.cmpt276.magnesium.restaurantmodel;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import ca.cmpt276.magnesium.healthinspectionviewer.R;

public class ReadingCSVInspection {
    private ArrayList<InspectionReport> inspection;
//    private static String filePath;
    private static Context context;

    public ReadingCSVInspection(Context cont) {
        context = cont;
        setInspectionList(getInspectionFromTextFile());
    }

    public static ArrayList<InspectionReport> getInspectionFromTextFile(){
        InputStream is = null;
        BufferedReader bufferedReader = null;
        ArrayList<InspectionReport> inspectionArrayList = new ArrayList<InspectionReport>();

        try{
            is =  context.getResources().openRawResource(R.raw.inspectionreports_itr1);
            bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            //Save line we get from csv file
            String line = null;

            //Save InspectionReport
            String[] strInspection = null;

            bufferedReader.readLine(); // Skip first line since tha is column name

            while (true){
                //read line
                line = bufferedReader.readLine();

                if(line == null){
                    break;
                }

                strInspection = line.trim().split(",");
                Log.d("STRING_INPECTION_LENGTH", "" + strInspection.length);

                //InspectionReport(String track, String date, String inspecType, int critical,
                //                            int nonCritical, String hazardRate, String statement)
                String violation;
                if(strInspection.length == 6){
                    violation = " ";
                }else {
                    violation = strInspection[6];
                    for (int i = 7; i < strInspection.length; i++) {
                        violation += "," + strInspection[i];
                    }
                }

                inspectionArrayList.add(new InspectionReport(strInspection[0],strInspection[1], strInspection[2],
                        Integer.parseInt(strInspection[3]), Integer.parseInt(strInspection[4]),
                        strInspection[5], violation));



            }
        }catch (Exception e){
            Log.d("DEBUG FILE ERROR", "Error Reading File");
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inspectionArrayList;
    }

    public ArrayList<InspectionReport> getInspectionArrayList() {
        return inspection;
    }

    public InspectionReport getInspectionReportAtPos(int i){
        return inspection.get(i);
    }
    public void setInspectionList(ArrayList<InspectionReport> inspRep) {
        this.inspection = inspRep;
    }
}
