package ca.cmpt276.magnesium.restaurantmodel;

/**
 * SFU CMPT 276
 * Term Project - Team Magnesium
 *
 * Restaurant Model: Database Reader Class
 * Reads from the SQLite database and creates objects
 * for use elsewhere in the restaurant model.
 * Mostly contains static factory methods.
 *
 * @author dlutz
 *
 */

public class DatabaseReader {

    private DatabaseReader() {
        // Currently do nothing.
    }

    /**
     * Create a Facility object from records in the database.
     * @param trackingNumber the tracking number of the restaurant
     * @return Facility if found in db, null if not in db
     */
    public static Facility facilityFactory(String trackingNumber) {
        // TODO - implement this stub
        Facility returnFacility = null;

        return returnFacility;
    }

    /**
     * Create an InspectionReport object from records in the database.
     * @param trackingNumber the tracking number of the report
     * @return InspectionReport if found in db, null if not in db
     */
    public static InspectionReport inspectionReportFactory(String trackingNumber) {
        // TODO - implement this stub
        InspectionReport returnReport = null;

        return returnReport;
    }


}
