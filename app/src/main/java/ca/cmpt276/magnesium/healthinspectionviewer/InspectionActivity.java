package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.HazardRating;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;
import ca.cmpt276.magnesium.restaurantmodel.Violation;

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
        setupToolbar();

        DatabaseReader reader = new DatabaseReader(getApplicationContext());

        ArrayList<Facility> facilities = reader.getAllFacilities();
        int facilityIndex = getIntent().getIntExtra(EXTRA_FACILITY_ID, 0);
        Facility currentFacility = facilities.get(facilityIndex);

        String trackingNo = currentFacility.getTrackingNumber();
        ArrayList<InspectionReport> reports = reader.getAssociatedInspections(trackingNo);
        int inspectionIndex = getIntent().getIntExtra(EXTRA_INSPECT_ID, 0);
        inspection = reports.get(inspectionIndex);

        TextView name = findViewById(R.id.inspection_restaurant_name);
        name.setText(currentFacility.getName());

        TextView type = findViewById(R.id.inspection_type);
        type.setText(inspection.getInspectionType().toString());

        TextView hazardLevel = findViewById(R.id.inspection_hazard_lv);
        hazardLevel.setText(inspection.getHazardRating().toString());

        View hazardColor = findViewById(R.id.inspection_hazard_color);
        HazardRating hazardRating = inspection.getHazardRating();
        switch (hazardRating) {
            case High: {
                hazardColor.setBackgroundColor(Color.RED);
                break;
            }
            case Moderate: {
                hazardColor.setBackgroundColor(Color.YELLOW);
                break;
            }
            case Low: {
                hazardColor.setBackgroundColor(Color.GREEN);
                break;
            }
        }

        String dateString = inspection.getInspectionDateString();
        TextView date = findViewById(R.id.inspection_date);
        date.setText(dateString);



        TextView empty = findViewById(R.id.inspection_violation_empty);
        ListView list = findViewById(R.id.inspection_violation_listView);
        if(inspection.getViolations().isEmpty()){
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
        }else {
            populateListView();
            empty.setVisibility(View.INVISIBLE);
            list.setVisibility(View.VISIBLE);
        }

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.inspection_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                Violation currentViolation = violationList.get(position);

                TextView criticality = (TextView) convertView.findViewById(
                                                R.id.violationArrayList_issue_lv);
                criticality.setText(currentViolation.getCriticality());

                TextView description = (TextView) convertView.findViewById(
                                                R.id.violationArrayList_issue_description);
                description.setText(currentViolation.getViolDescription());

                TextView code = (TextView) convertView.findViewById(
                                                R.id.violationArrayList_vio_lv);
                code.setText(String.format("%d", currentViolation.getViolationCode()));


                return convertView;
            }
        };

        lv.setAdapter(adapter);
    }


}
