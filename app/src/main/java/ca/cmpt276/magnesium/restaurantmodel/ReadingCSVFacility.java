package ca.cmpt276.magnesium.restaurantmodel;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadingCSVFacility {
    private ArrayList<Facility> facilitiesList;
    private static String filePath;

    public ReadingCSVFacility(String fileLocation) {
        filePath = fileLocation;
        setFacilityRes(getFacilityFromTextFile());
    }

    public static ArrayList<Facility> getFacilityFromTextFile(){
        FileInputStream fis = null;
        InputStreamReader is = null;
        BufferedReader bufferedReader = null;
        ArrayList<Facility> facilityArrayList = new ArrayList<Facility>();

        try{
            fis = new FileInputStream(filePath);
            is =  new InputStreamReader(fis);
            bufferedReader = new BufferedReader(is);

            //Save line we get from csv file
            String line = null;

            //Save Facility
            String[] strFacility = null;

            bufferedReader.readLine(); // Skip first line since tha is column name
            while (true){
                //read line
                line = bufferedReader.readLine();

                //Check if line exists
                if(line == null){
                    break;
                }else{
                    strFacility = line.split(",");
                    facilityArrayList.add(new Facility(strFacility[0], strFacility[1],
                                                        strFacility[2], strFacility[3],
                                                        strFacility[4],
                                                        Double.parseDouble(strFacility[5]),
                                                        Double.parseDouble((strFacility[6]))));
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
        return facilityArrayList;
    }

    public ArrayList<Facility> getFacilityArrayList() {
        return facilitiesList;
    }

    public Facility getFacilityAtPos(int i){
        return facilitiesList.get(i);
    }
    public void setFacilityRes(ArrayList<Facility> facilityRes) {
        this.facilitiesList = facilityRes;
    }
}
