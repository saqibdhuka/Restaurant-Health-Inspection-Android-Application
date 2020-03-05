package ca.cmpt276.magnesium.healthinspectionviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        autoSwitchRestaurantList();
    }

    private void autoSwitchRestaurantList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = RestaurantsListActivity.makeRestaurantsListIntent(StartActivity.this);
                startActivity(intent);
                finish();
            }
        },3000);
    }

}
