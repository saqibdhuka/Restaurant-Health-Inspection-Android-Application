package ca.cmpt276.magnesium.restaurantmodel;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private String inspectionDateString;
    private LocalDate inspectionDate;
    private InspectionType inspectionType;
    private int numCritical;
    private int numNonCritical;
    private HazardRating hazardRating;
    private ArrayList<Violation> violations;
    private String violationStatement;

    public InspectionReport(String track, LocalDate date, String inspecType, int critical,
                            int nonCritical, String hazardRate, String statement){
        trackingNumber = track;
        inspectionDate = date;
        numCritical = critical;
        numNonCritical = nonCritical;
        violationStatement = statement;
        violations = getViolationsFromString(statement);

        if (inspecType.toLowerCase().equals("followup")
                || inspecType.toLowerCase().equals("follow-up")) {
            inspectionType = InspectionType.FollowUp;
        } else if(inspecType.toLowerCase().equals("routine")){
            inspectionType = InspectionType.Routine;
        } else{
            inspectionType = InspectionType.None;
        }

        String lowercaseRating = hazardRate.toLowerCase();
        if (lowercaseRating.equals("low")) {
            hazardRating = HazardRating.Low;

        } else if (lowercaseRating.equals("moderate")) {
            hazardRating = HazardRating.Moderate;
        } else {
            hazardRating = HazardRating.High;
        }

    }

    private ArrayList<Violation> getViolationsFromString(String violationStatement) {
        ArrayList<Violation> returnArray = new ArrayList<>();
        List<String> violationArray = Arrays.asList(violationStatement.split("\\|"));
        // Do some string processing on violation statements:
        int positionOfFirstComma = violationStatement.indexOf(',');
        if (positionOfFirstComma >= 0) {
            violationStatement = violationStatement.substring(positionOfFirstComma + 1);
        }
        // We know that in cases of multiple violations, they are seperated by "pipe" or '|'

        // Process each possible "violation" in the report:
        for (String violation : violationArray) {
            // Components of a violation are split up by commas
            ArrayList<String> violationParams = new ArrayList<String>();
            Collections.addAll(violationParams, violation.split(","));
            // Figure out if we need to remove a hazard rating:
            if (violationParams.size() > 0) {
                if (violationParams.get(0).equalsIgnoreCase("low")
                        || (violationParams.get(0).equalsIgnoreCase("moderate")
                        || (violationParams.get(0).equalsIgnoreCase("high")))) {
                    // We need to remove this one.
                    violationParams.remove(0);
                }
                // Also check the last.
                int lastIndex = violationParams.size() - 1;
                if (violationParams.get(lastIndex).equalsIgnoreCase("low")
                        || (violationParams.get(lastIndex).equalsIgnoreCase("moderate")
                        || (violationParams.get(lastIndex).equalsIgnoreCase("high")))) {
                    // We need to remove this one.
                    violationParams.remove(lastIndex);
                }
            }
            // Only try to pull values with a correct number of terms!
            if (violationParams.size() == 4) {
                Violation tmpViol = new Violation(violationParams.get(0),
                                                violationParams.get(1),
                                                violationParams.get(2),
                                                violationParams.get(3));
                returnArray.add(tmpViol);
            }
        }
        return returnArray;
    }

    public String getInspectionDateString() {
        String returnString;

        Days daysBetween = Days.daysBetween(inspectionDate, LocalDate.now());
        // Pretty printing for the user:
        if (daysBetween.getDays() <= 30) {
            returnString = daysBetween.getDays() + " days ago";
        } else if (daysBetween.getDays() <= 365) {
            returnString = inspectionDate.toString("MMMM d");
        } else {
            returnString = inspectionDate.toString("MMMM yyyy");
        }

        return returnString;
    }



    // Getters and setters provided for use by other classes.
    // Any string representation of this object should be called via this object's toString()
    // method.


    public ArrayList<Violation> getViolations() {
        return violations;
    }

    public String toString() {
        // TODO implement unique InspectionReport string generation later
        return super.toString();
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public InspectionType getInspectionType() {
        return inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public String getViolationStatement() {
        return violationStatement;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public HazardRating getHazardRating() {
        return hazardRating;
    }



}
