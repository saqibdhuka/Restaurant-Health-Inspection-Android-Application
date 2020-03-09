package ca.cmpt276.magnesium.restaurantmodel;

import androidx.annotation.NonNull;

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
        return violationCode + criticality + violDescription + repeated;
    }
}
