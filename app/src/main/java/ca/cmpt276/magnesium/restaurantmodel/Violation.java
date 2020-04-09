package ca.cmpt276.magnesium.restaurantmodel;

import android.content.Context;

import androidx.annotation.NonNull;

import ca.cmpt276.magnesium.healthinspectionviewer.R;

/**
 * SFU CMPT 276
 * Term Project - Team Magnesium
 *
 * Restaurant Model: Violation class.
 * Created by InspectionReport on creation.
 *
 * Contains data about an individual violation.
 *
 * @author dlutz
 *
 */

public class Violation {
    private int violationCode;
    private String criticality;
    private String violDescription;
    private String repeated;

    public Violation(String violCode, String criticality, String violDescription, String repeated) {
        // Try parsing the violation code as an int - stripping all non-numeric chars:
        violationCode = Integer.parseInt(violCode.replaceAll("[^0-9.]", ""));
        this.criticality = criticality;
        this.violDescription = violDescription;
        this.repeated = repeated;
    }

    public int getViolationCode() {
        return violationCode;
    }

    public String getCriticality() {
        return criticality;
    }

    public String getViolDescription() {
        return violDescription;
    }

    public String getRepeated() {
        return repeated;
    }

    @NonNull
    @Override
    public String toString() {
        int endIndex = 15;
        if (violDescription.length() < 16) {
            endIndex = violDescription.length();
        }
        String returnString = String.format("%s: Violation #%d, %s...",
                criticality, violationCode, violDescription.substring(0, endIndex));

        return returnString;
    }


    public String getViolationNature(Context context) {
        String returnString = context.getResources().getString(R.string.vioNature_NA);
        if ((violationCode > 100) && (violationCode < 105)) {
            returnString = context.getResources().getString(R.string.vioNature_regulatory);;
        } else if ((violationCode > 200) && (violationCode < 212)) {
            returnString = context.getResources().getString(R.string.vioNature_food);;
        } else if ((violationCode > 300) && (violationCode < 304)) {
            returnString = context.getResources().getString(R.string.vioNature_sanitization);;
        } else if ((violationCode > 303) && (violationCode < 306)) {
            returnString = context.getResources().getString(R.string.vioNature_pests);;
        } else if ((violationCode > 305) && (violationCode < 311)) {
            returnString = context.getResources().getString(R.string.vioNature_sanitization);
        } else if ((violationCode > 310) && (violationCode < 316)) {
            returnString = context.getResources().getString(R.string.vioNature_facility);
        } else if ((violationCode > 400) && (violationCode < 405)) {
            returnString = context.getResources().getString(R.string.vioNature_employee);
        } else if ((violationCode > 500) && (violationCode < 503)) {
            returnString = context.getResources().getString(R.string.vioNature_operator);
        }

        return returnString;
    }
}

