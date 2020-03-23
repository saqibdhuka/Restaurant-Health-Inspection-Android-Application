package ca.cmpt276.magnesium.healthinspectionviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseHelperFacility;
import ca.cmpt276.magnesium.restaurantmodel.DatabaseHelperInspection;
import ca.cmpt276.magnesium.restaurantmodel.DatabaseReader;
import ca.cmpt276.magnesium.restaurantmodel.Facility;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        DatabaseHelperFacility dbFacility = new DatabaseHelperFacility(this);
        dbFacility.getWritableDatabase();
        dbFacility.insertData();
        dbFacility.close();

        DatabaseHelperInspection dbInspection = new DatabaseHelperInspection(this);
        dbInspection.getWritableDatabase();
        dbInspection.insertData();
        dbInspection.close();

        autoSwitchRestaurantList();
    }

    private void autoSwitchRestaurantList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = MapScreen.makeMapScreenIntent(StartActivity.this);
                startActivity(intent);
                finish();
            }
        },2000);
    }

}
