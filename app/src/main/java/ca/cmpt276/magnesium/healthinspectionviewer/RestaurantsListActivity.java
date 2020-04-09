package ca.cmpt276.magnesium.healthinspectionviewer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
<<<<<<< HEAD
import android.graphics.Color;
=======
>>>>>>> origin/master
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.magnesium.restaurantmodel.DataUpdater;
import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.HazardRating;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;
import ca.cmpt276.magnesium.restaurantmodel.ReadingCSVFacility;

import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.High;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.Moderate;

public class RestaurantsListActivity extends AppCompatActivity {
    private List<Facility> facilities = new ArrayList<Facility>();
    private BaseAdapter adapter;


    private static final int ACTIVITY_REST_LIST_MAP_BUTTON = 100;
    private static final int ACTIVITY_REST_WINDOW = 200;

    public static Intent makeRestaurantsListIntent(Context context){
        return new Intent(context, RestaurantsListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addRestaurants();
                populateListView();
                setupMapButton();
                setupFilterButton();
                findViewById(R.id.resList_loading_layout).setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //Load_checkbox();
            }
        }, 50);

    }


    private void setupMapButton() {
        Button mapButton = findViewById(R.id.restaurantMap);
        mapButton.setVisibility(View.VISIBLE);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().putExtra("button","back");
                setResult(ACTIVITY_REST_LIST_MAP_BUTTON,intent);
                finish();
            }
        });
    }

    // TODO ensure that this has correct back-button activity
    private void setupFilterButton() {
        Button filterButton = findViewById(R.id.startSearchActivity_restaurantList);
        filterButton.setVisibility(View.VISIBLE);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SearchActivity.getSearchActivityIntent(RestaurantsListActivity.this);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if we need to prompt for updates:
        DataUpdater.notifyIfUpdateAvailable(RestaurantsListActivity.this);
        populateListView();

        // Check if we need to update the data:
        SharedPreferences prefs = getSharedPreferences(SearchActivity.SEARCH_PREFSFILE, 0);
        boolean needToUpdate = prefs.getBoolean(SearchActivity.SEARCH_RESTLIST_NEED_UPDATE, false);
        if (needToUpdate) {
            addRestaurants();
            populateListView();
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(SearchActivity.SEARCH_RESTLIST_NEED_UPDATE, false);
            edit.apply();
        }
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

                // Set favourite restaurant
                if(restaurant.getFavourite()){
                    TextView layout = convertView.findViewById(R.id.resArrayList_res_name);
                    layout.setBackgroundColor(Color.parseColor("#ff726f"));
                }

                // Get maximum hazard and set the text:
                DatabaseReader reader = new DatabaseReader(getApplicationContext());
                InspectionReport firstInspection = reader.getFirstAssociatedInspection(restaurant.getTrackingNumber());

                TextView empty = convertView.findViewById(R.id.resArrayList_res_no_inspection);
                ConstraintLayout layout = convertView.findViewById(R.id.resArrayList_res_inspection_layout);
                if (firstInspection == null){
                    empty.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                } else {
                    empty.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                }


                // Get HazardRating from last inspection:
                HazardRating restaurantRating = HazardRating.Low;
                // Only give a different rating if we have more than zero inspections:
                if (firstInspection != null) {
                    restaurantRating = firstInspection.getHazardRating();
                }


                // Now we should have the worst rating:
                ImageView hazardIcon = convertView.findViewById(R.id.resArrayList_res_hazard_icon);
                TextView hazardText = convertView.findViewById(R.id.resArrayList_res_hazard_lv);
                switch(restaurantRating) {
                    case Low:
                        hazardIcon.setImageResource(R.drawable.low_hazard_level);
                        break;
                    case Moderate:
                        hazardIcon.setImageResource(R.drawable.moderate_hazard_level);
                        break;
                    case High:
                        hazardIcon.setImageResource(R.drawable.high_hazard_level);
                        break;
                }
                hazardText.setText(getText(hazardLanguage(restaurantRating)));

                // Set the number of issues as of the last inspection:
                TextView numIssues = convertView.findViewById(R.id.resArrayList_res_issue_num);
                if (firstInspection != null) {
                    Integer issueCount = firstInspection.getNumCritical()
                            + firstInspection.getNumNonCritical();
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
                    startActivityForResult(intent, ACTIVITY_REST_WINDOW);
        });
    }

    private int hazardLanguage(HazardRating currentHazardLevel) {
        if (currentHazardLevel == High) {
            return R.string.hazard_high;
        } else if (currentHazardLevel == Moderate) {
            return R.string.hazard_moderate;
        }
        return R.string.hazard_low;
    }

    private void addRestaurants() {
        ReadingCSVFacility reader = ReadingCSVFacility.getCSVReader(this);
        facilities = reader.getFacilityArrayList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REST_WINDOW:
                if (data!=null){
                    setResult(ACTIVITY_REST_LIST_MAP_BUTTON, data);
                    finish();
                }
        }
    }
}
