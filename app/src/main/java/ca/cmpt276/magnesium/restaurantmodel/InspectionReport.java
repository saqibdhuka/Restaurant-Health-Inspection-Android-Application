package ca.cmpt276.magnesium.restaurantmodel;

import java.util.ArrayList;

/**
 * SFU CMPT 276
 * Term Project - Team Magnesium
 *
 * Restaurant Model: InspectionReport class.
 * Created by DatabaseReader's InspectionReport factory.
 *
 * Contains data about an inspection report.
 *
 * @author dlutz
 *
 */

public class InspectionReport {

    private String trackingNumber;
    private String inspectionDate;
    private InspectionType inspectionType;
    private int numCritical;
    private int numNonCritical;
    private HazardRating hazardRating;
    private ArrayList<Integer> violations;

    private InspectionReport(String trackingNumber) {
        // Should only ever be called by InspectionReport factory
        // TODO implement constructor
    }



//--------------------------------------------------
    // add for testing UI

    public InspectionReport(String trackingNumber, String inspectionDate, InspectionType inspectionType, int numCritical, int numNonCritical, HazardRating hazardRating, ArrayList<Integer> violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violations = violations;
    }

//--------------------------------------------------------------------------------------------



    // Getters and setters provided for use by other classes.
    // Any string representation of this object should be called via this object's toString()
    // method.

    public String toString() {
        // TODO implement unique InspectionReport string generation later
        return super.toString();
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public InspectionType getInspectionType() {
        return inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public HazardRating getHazardRating() {
        return hazardRating;
    }



}
