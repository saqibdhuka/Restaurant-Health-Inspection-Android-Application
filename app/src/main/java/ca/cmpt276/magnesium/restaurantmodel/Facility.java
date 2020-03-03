package ca.cmpt276.magnesium.restaurantmodel;

/**
 * SFU CMPT 276
 * Term Project - Team Magnesium
 *
 * Restaurant Model: Facility class.
 * Created by DatabaseReader's Facility factory.
 *
 * Contains data about an individual Facility.
 * Currently, the only supported facility is a Restaurant.
 *
 * @author dlutz
 *
 */

public class Facility {

    private String trackingNumber;
    private String name;
    private String address;
    private String city;
    private FacilityType facilityType;
    private double latitude;
    private double longitude;

    private Facility(String trackingNumber) {
        // Should only ever be called by DatabaseReader factory
    }

    // Getters and setters provided for use by other classes.
    // Any string representation of this object should be called via this object's toString()
    // method.

    public String toString() {
        // TODO implement unique Facility string generation later
        return super.toString();
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
