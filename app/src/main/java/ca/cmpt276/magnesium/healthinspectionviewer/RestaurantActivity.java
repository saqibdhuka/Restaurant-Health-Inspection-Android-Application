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

import ca.cmpt276.magnesium.restaurantmodel.Facility;
import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;

import static ca.cmpt276.magnesium.restaurantmodel.FacilityType.Restaurant;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.High;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.Low;
import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.Moderate;
import static ca.cmpt276.magnesium.restaurantmodel.InspectionType.FollowUp;
import static ca.cmpt276.magnesium.restaurantmodel.InspectionType.Routine;

public class RestaurantActivity extends AppCompatActivity {
    private List<InspectionReport> inspections = new ArrayList<InspectionReport>();
    private BaseAdapter adapter;

    public static Intent makeRestaurantIntent(Context context, int restaurantID){
        Intent intent = new Intent(context, RestaurantActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addTestInspection();
        populateListView();
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
                    // test for display restaurant
                    Intent intent = InspectionActivity.makeInspectionIntent(RestaurantActivity.this, position);
                    startActivity(intent);
                });
    }

    private void addTestInspection() {
        ArrayList<Integer> test = new ArrayList<Integer>();
        test.add(203);
        test.add(306);
        test.add(201);
        inspections.add(new InspectionReport("Do","20180123",Routine,1,2,Low,test));
        inspections.add(new InspectionReport("Ra","20191234",Routine,1,2,High,test));
        inspections.add(new InspectionReport("Me","20170817",FollowUp,1,2,Moderate,test));
        inspections.add(new InspectionReport("Do","20180123",Routine,1,2,Low,test));
        inspections.add(new InspectionReport("Ra","20191234",Routine,1,2,High,test));
        inspections.add(new InspectionReport("Me","20170817",FollowUp,1,2,Moderate,test));
        inspections.add(new InspectionReport("Do","20180123",Routine,1,2,Low,test));
        inspections.add(new InspectionReport("Ra","20191234",Routine,1,2,High,test));
        inspections.add(new InspectionReport("Me","20170817",FollowUp,1,2,Moderate,test));
    }


}
