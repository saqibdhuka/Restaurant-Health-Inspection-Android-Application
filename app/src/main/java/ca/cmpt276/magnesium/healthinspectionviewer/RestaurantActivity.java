package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseHelperInspection;
import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;

import static ca.cmpt276.magnesium.restaurantmodel.FacilityType.Restaurant;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.High;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.Low;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.Moderate;
import static ca.cmpt276.magnesium.restaurantmodel.InspectionType.FollowUp;
import static ca.cmpt276.magnesium.restaurantmodel.InspectionType.Routine;

public class RestaurantActivity extends AppCompatActivity {

    private final static String EXTRA_REST_ID = "RestaurantActivity_restaurantID";

    private List<InspectionReport> inspections = new ArrayList<InspectionReport>();
    private BaseAdapter adapter;
    private Facility currentRestaurant;
    private int restaurantID;

    public static Intent makeRestaurantIntent(Context context, int restaurantID){
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra(EXTRA_REST_ID, restaurantID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseReader reader = new DatabaseReader(getApplicationContext());
        ArrayList<Facility> facilities = reader.getAllFacilities();
        restaurantID = getIntent().getIntExtra(EXTRA_REST_ID, 0);

        currentRestaurant = facilities.get(restaurantID);
        setupTextFields();

        addTestInspection();
        populateListView();
    }

    private void setupTextFields() {
        // Setup all text fields and icon based on currentFacility
        TextView address, name, latitude, longitude;
        address = findViewById(R.id.res_restaurant_name);
        address.setText(currentRestaurant.getAddress());

        name = findViewById(R.id.res_restaurant_name);
        name.setText(currentRestaurant.getName());

        latitude = findViewById(R.id.res_restaurant_lat_text);
        latitude.setText(Double.valueOf(currentRestaurant.getLatitude()).toString());

        longitude = findViewById(R.id.res_restaurant_long_text);
        longitude.setText(Double.valueOf(currentRestaurant.getLongitude()).toString());

        ImageView icon = findViewById(R.id.res_icon);
        icon.setImageDrawable(getDrawable(currentRestaurant.getIconID()));
    }


    private void populateListView() {
        ListView lv = findViewById(R.id.res_inspection_listView);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return inspections.size();
            }

            @Override
            public InspectionReport getItem(int position) {
                return inspections.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.inspection_list_view, parent, false);
                }

                InspectionReport inspection = getItem(position);

                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_hazard_lv)).setText(inspection.getHazardRating().toString());
                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_noncrit)).setText(inspection.getNumNonCritical() + "");
                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_crit)).setText(inspection.getNumCritical() + "");
                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_date)).setText(inspection.getInspectionDate());

                return convertView;
            }
        };
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Intent intent = InspectionActivity.makeInspectionIntent(
                            RestaurantActivity.this,
                                    position,
                                    restaurantID);
                    startActivity(intent);
                });
    }

    private void addTestInspection() {
        DatabaseReader reader = new DatabaseReader(getApplicationContext());
        inspections = reader.getAssociatedInspections(currentRestaurant.getTrackingNumber());
    }


}
