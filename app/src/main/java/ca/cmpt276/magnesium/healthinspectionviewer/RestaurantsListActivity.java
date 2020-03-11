package ca.cmpt276.magnesium.healthinspectionviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.HazardRating;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;

public class RestaurantsListActivity extends AppCompatActivity {
    private List<Facility> facilities = new ArrayList<Facility>();
    private BaseAdapter adapter;


    public static Intent makeRestaurantsListIntent(Context context){
        return new Intent(context, RestaurantsListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        addRestaurants();
        populateListView();
    }

    private void populateListView() {
        ListView lv = findViewById(R.id.resList_restaurants_listView);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return facilities.size();
            }

            @Override
            public Facility getItem(int position) {
                return facilities.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.restaurants_list_view, parent, false);
                }

                Facility restaurant = getItem(position);

                // Get maximum hazard and set the text:
                DatabaseReader reader = new DatabaseReader(getApplicationContext());
                ArrayList<InspectionReport> inspections = reader.getAssociatedInspections(restaurant.getTrackingNumber());
                // Loop through all inspections and pull the worst one:
                HazardRating restaurantRating = HazardRating.Low;
                for (InspectionReport inspection : inspections) {
                    if (inspection.getHazardRating() == HazardRating.High) {
                        restaurantRating = HazardRating.High;
                    }

                    if ((inspection.getHazardRating() == HazardRating.Moderate)
                            && (restaurantRating == HazardRating.Low)) {
                        restaurantRating = HazardRating.Moderate;
                    }
                }

                // Now we should have the worst rating:
                View hazardColor = convertView.findViewById(R.id.resArrayList_res_hazard_color);
                TextView hazardText = convertView.findViewById(R.id.resArrayList_res_hazard_lv);
                switch(restaurantRating) {
                    case Low:
                        hazardColor.setBackgroundColor(Color.GREEN);
                        break;
                    case Moderate:
                        hazardColor.setBackgroundColor(Color.YELLOW);
                        break;
                    case High:
                        hazardColor.setBackgroundColor(Color.RED);
                        break;
                }
                hazardText.setText(restaurantRating.toString());

                // Set the number of issues as of the last inspection:
                TextView numIssues = convertView.findViewById(R.id.resArrayList_res_issue_num);
                if (inspections.size() > 0) {
                    Integer issueCount = inspections.get(0).getNumCritical()
                            + inspections.get(0).getNumNonCritical();
                    numIssues.setText(issueCount.toString());
                } else {
                    numIssues.setText("0");
                }

                // Make the "last inspection" field more useful:
                TextView lastInspection =
                        convertView.findViewById(R.id.resArrayList_res_inspection_date);
                lastInspection.setText(restaurant.getDateString());


                TextView resName = (TextView) convertView.findViewById(R.id.resArrayList_res_name);
                resName.setText(restaurant.getName());
                ImageView icon = (ImageView) convertView.findViewById(R.id.resArrayList_res_icon);
                icon.setImageResource(restaurant.getIconID());

                return convertView;
            }
        };
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(
                (parent, view, position, id) -> {
                    // test for display restaurant
                    Intent intent = RestaurantActivity.makeRestaurantIntent(RestaurantsListActivity.this, position);
                    startActivity(intent);
        });
    }

    private void addRestaurants() {
        DatabaseReader reader = new DatabaseReader(getApplicationContext());
        facilities = reader.getAllFacilities();
    }






}
