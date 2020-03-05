package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import ca.cmpt276.magnesium.restaurantmodel.InspectionReport;

public class InspectionActivity extends AppCompatActivity {

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


    }

}
