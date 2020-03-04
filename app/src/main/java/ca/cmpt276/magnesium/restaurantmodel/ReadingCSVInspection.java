package ca.cmpt276.magnesium.restaurantmodel;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadingCSVInspection {
    private ArrayList<InspectionReport> inspection;
    private static String filePath;

    public ReadingCSVInspection(String fileLocation) {
        filePath = fileLocation;
        setInspectionList(getInspectionFromTextFile());
    }

    public static ArrayList<InspectionReport> getInspectionFromTextFile(){
        FileInputStream fis = null;
        InputStreamReader is = null;
        BufferedReader bufferedReader = null;
        ArrayList<InspectionReport> inspectionArrayList = new ArrayList<InspectionReport>();

        try{
            fis = new FileInputStream(filePath);
            is =  new InputStreamReader(fis);
            bufferedReader = new BufferedReader(is);

            //Save line we get from csv file
            String line = null;

            //Save InspectionReport
            String[] strInspection = null;

            bufferedReader.readLine(); // Skip first line since tha is column name

            while (true){
                //read line
                line = bufferedReader.readLine();

                //Check if line exists
                if(line == null){
                    break;
                }else{
                    strInspection = line.split(",");

                    //InspectionReport(String track, String date, String inspecType, int critical,
                    //                            int nonCritical, String hazardRate, String statement)

                    inspectionArrayList.add(new InspectionReport(strInspection[0],strInspection[1], strInspection[2],
                                                    Integer.parseInt(strInspection[3]), Integer.parseInt(strInspection[4]),
                                                    strInspection[5], strInspection[6]));
                }
            }
        }catch (Exception e){
            Log.d("DEBUG FILE ERROR", "Error Reading File");
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
                is.close();
                fis.close();
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
