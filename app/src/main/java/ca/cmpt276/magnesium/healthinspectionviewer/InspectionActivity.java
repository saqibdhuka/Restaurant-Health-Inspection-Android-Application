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

import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;

import static ca.cmpt276.magnesium.restaurantmodel.HazardRating.*;
import static ca.cmpt276.magnesium.restaurantmodel.InspectionType.*;

public class InspectionActivity extends AppCompatActivity {

    private InspectionReport inspection ;
    private BaseAdapter adapter;

    public static Intent makeInspectionIntent(Context context, int inspectionID){
        Intent intent = new Intent(context, InspectionActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addTestViolation();
        populateListView();
    }

    private void populateListView() {
        ListView lv = findViewById(R.id.inspection_violation_listView);
        ArrayList<Integer> violationList = inspection.getViolations();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return violationList.size();
            }

            @Override
            public Integer getItem(int position) {
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
                convertView.setClickable(false);
                return convertView;
            }
        };

        lv.setAdapter(adapter);
    }

    private void addTestViolation() {
        ArrayList<Integer> test = new ArrayList<Integer>();
        test.add(203);
        test.add(306);
        test.add(201);
        inspection = new InspectionReport("Do","20180123",Routine,1,2,Low,test);
    }


}
