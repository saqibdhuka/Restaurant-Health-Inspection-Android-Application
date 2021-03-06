package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.HazardRating;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;
import ca.cmpt276.magnesium.restaurantmodel.ReadingCSVFacility;

import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.High;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.Moderate;

public class RestaurantActivity extends AppCompatActivity {

    private final static String EXTRA_REST_ID = "RestaurantActivity_restaurantID";
    private static final int ACTIVITY_REST_MAP_WINDOW = 200;

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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        setupToolbar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ReadingCSVFacility reader = ReadingCSVFacility.getCSVReader(RestaurantActivity.this);

                // TODO: refactor this? Seems like a waste to ask for ALL FACILITIES
                // just to pick one out.
                ArrayList<Facility> facilities = reader.getFacilityArrayList();
                restaurantID = getIntent().getIntExtra(EXTRA_REST_ID, 0);

                currentRestaurant = facilities.get(restaurantID);


                // Choose favourite restaurant
                CheckBox favourite = (CheckBox)findViewById(R.id.favourite_icon_checkbox);
                if(currentRestaurant.getFavourite()){
                    favourite.setBackgroundResource(R.drawable.heart_checked);
                } else {
                    favourite.setBackgroundResource(R.drawable.heart_unchecked);
                }

                favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            if(currentRestaurant.getFavourite()){
                                favourite.setBackgroundResource(R.drawable.heart_unchecked);
                                currentRestaurant.setFavourite(false);
                            } else {
                                currentRestaurant.setFavourite(true);
                                favourite.setBackgroundResource(R.drawable.heart_checked);
                            }
                        } else {
                            currentRestaurant.setFavourite(false);
                            favourite.setBackgroundResource(R.drawable.heart_unchecked);
                        }
                    }
                });


                setupTextFields();

                addTestInspection();

                TextView empty = findViewById(R.id.res_inspection_empty);
                ListView list = findViewById(R.id.res_inspection_listView);
                if(inspections.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    list.setVisibility(View.INVISIBLE);
                }else {
                    populateListView();
                    empty.setVisibility(View.INVISIBLE);
                    list.setVisibility(View.VISIBLE);
                }
                findViewById(R.id.res_loading_layout).setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }, 50);

        setupGPSToMap();

    }

//    private String readFile() {
//        File fileEvents = new File(RestaurantActivity.this.getFilesDir()+"/favourite_restaurant");
//        StringBuilder text = new StringBuilder();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
//            String line;
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//                text.append('\n');
//            }
//            br.close();
//        } catch (IOException e) { }
//        String result = text.toString();
//        return result;
//    }

    private void setupGPSToMap() {
        View gps = findViewById(R.id.res_coords_layout);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().putExtra("restTrackNum", currentRestaurant.getTrackingNumber());
                setResult(ACTIVITY_REST_MAP_WINDOW, intent);
                finish();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.res_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                editor.putBoolean("checkbox", favourite.isChecked());
//                editor.commit();
                finish();
            }
        });
    }

    private void setupTextFields() {
        // Setup all text fields and icon based on currentFacility
        TextView address, name, latitude, longitude;
        address = findViewById(R.id.res_restaurant_name);
        address.setText(currentRestaurant.getAddress());

        name = findViewById(R.id.res_restaurant_name);
        name.setText(currentRestaurant.getName());

        latitude = findViewById(R.id.res_restaurant_lat);
        latitude.setText(Double.valueOf(currentRestaurant.getLatitude()).toString());

        longitude = findViewById(R.id.res_restaurant_long);
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
                ImageView hazardIcon = convertView.findViewById(R.id.inspectionArrayList_inspection_hazard_color);
                HazardRating hazardRating = inspection.getHazardRating();
                switch (hazardRating) {
                    case High: {
                        hazardIcon.setImageResource(R.drawable.high_hazard_level);
                        break;
                    }
                    case Moderate: {
                        hazardIcon.setImageResource(R.drawable.moderate_hazard_level);
                        break;
                    }
                    case Low: {
                        hazardIcon.setImageResource(R.drawable.low_hazard_level);
                        break;
                    }
                }

                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_hazard_lv)).setText(getText(hazardLanguage(inspection.getHazardRating())));
                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_noncrit)).setText(inspection.getNumNonCritical() + "");
                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_crit)).setText(inspection.getNumCritical() + "");
                ((TextView) convertView.findViewById(R.id.inspectionArrayList_inspection_date)).setText(inspection.getInspectionDateString(RestaurantActivity.this));

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

    private int hazardLanguage(HazardRating currentHazardLevel) {
        if (currentHazardLevel == High) {
            return R.string.hazard_high;
        } else if (currentHazardLevel == Moderate) {
            return R.string.hazard_moderate;
        }
        return R.string.hazard_low;
    }

    private void addTestInspection() {
        DatabaseReader reader = new DatabaseReader(getApplicationContext());
        inspections = reader.getAllAssociatedInspections(currentRestaurant.getTrackingNumber());
    }

}
