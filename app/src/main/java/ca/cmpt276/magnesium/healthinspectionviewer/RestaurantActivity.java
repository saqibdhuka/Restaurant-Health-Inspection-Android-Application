package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import ca.cmpt276.magnesium.restaurantmodel.Facility;

import static ca.cmpt276.magnesium.restaurantmodel.FacilityType.Restaurant;

public class RestaurantActivity extends AppCompatActivity {


    // test for display
    Facility rest1 = new Facility("0001","rest1","123 street",
            "Surrey",Restaurant,47.08,60.32,R.drawable.burger);

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

    }



}
