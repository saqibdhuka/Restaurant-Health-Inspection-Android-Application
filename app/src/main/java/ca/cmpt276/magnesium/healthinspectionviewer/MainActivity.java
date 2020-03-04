package ca.cmpt276.magnesium.healthinspectionviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseHelperFacility;
import ca.cmpt276.magnesium.restaurantmodel.DatabaseHelperInspection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelperFacility dbFacility = new DatabaseHelperFacility(this);
        DatabaseHelperInspection dbInspection = new DatabaseHelperInspection(this);
    }
}
