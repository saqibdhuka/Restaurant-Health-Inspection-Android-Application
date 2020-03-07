package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;
import ca.cmpt276.magnesium.restaurantmodel.Violation;

import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.*;
import static ca.cmpt276.magnesium.restaurantmodel.InspectionType.*;

public class InspectionActivity extends AppCompatActivity {

    private final static String EXTRA_INSPECT_ID = "InspectionActivity_InspectIDExtra";
    private final static String EXTRA_FACILITY_ID = "InspectionActivity_FacilityIDExtra";

    private InspectionReport inspection;
    private BaseAdapter adapter;

    public static Intent makeInspectionIntent(Context context, int inspectionID, int facilityID) {
        Intent intent = new Intent(context, InspectionActivity.class);
        intent.putExtra(EXTRA_INSPECT_ID, inspectionID);
        intent.putExtra(EXTRA_FACILITY_ID, facilityID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseReader reader = new DatabaseReader(getApplicationContext());

        ArrayList<Facility> facilities = reader.getAllFacilities();
        int facilityIndex = getIntent().getIntExtra(EXTRA_FACILITY_ID, 0);
        Facility currentFacility = facilities.get(facilityIndex);

        String trackingNo = currentFacility.getTrackingNumber();
        ArrayList<InspectionReport> reports = reader.getAssociatedInspections(trackingNo);
        int inspectionIndex = getIntent().getIntExtra(EXTRA_INSPECT_ID, 0);
        inspection = reports.get(inspectionIndex);

        populateListView();
    }

    private void populateListView() {
        ListView lv = findViewById(R.id.inspection_violation_listView);
        ArrayList<Violation> violationList = inspection.getViolations();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return violationList.size();
            }

            @Override
            public Violation getItem(int position) {
                return violationList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.violation_list_view, parent, false);
                }

                // Need connect violation number to violation data set


                return convertView;
            }
        };

        lv.setAdapter(adapter);
    }


}
