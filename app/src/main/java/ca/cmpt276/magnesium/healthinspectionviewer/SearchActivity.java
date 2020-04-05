package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public static Intent getSearchActivityIntent(Context context) {
        Intent returnIntent = new Intent(context, SearchActivity.class);
        return returnIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Call helper setup funcs:
        setupSpinner();
        setupCheckBoxes();

    }

    private void setupSpinner() {
        // Populate our "spinner":
        ArrayList<String> spinnerAdapter = new ArrayList<>();
        spinnerAdapter.add(""); // For cases when we don't want anything
        spinnerAdapter.add(getString(R.string.search_high));
        spinnerAdapter.add(getString(R.string.search_mod));
        spinnerAdapter.add(getString(R.string.search_low));
        Spinner hazardSpinner = findViewById(R.id.searchActivity_hazardSpinner);
        // Create adapter:
        ArrayAdapter<String> hazardAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerAdapter);
        hazardSpinner.setAdapter(hazardAdapter);
    }

    private void setupCheckBoxes() {
        CheckBox greaterThan = findViewById(R.id.searchActivity_enableMoreThen);
        CheckBox lessThan = findViewById(R.id.searchActivity_enableLessThen);

        greaterThan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the other checkbox and disable it:
                if (lessThan.isChecked() && greaterThan.isChecked()) {
                    lessThan.setChecked(false);
                    greaterThan.setChecked(true);
                } else if (greaterThan.isChecked()) {
                    greaterThan.setChecked(true);
                } else {
                    greaterThan.setChecked(false);
                }

            }
        });

        lessThan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (greaterThan.isChecked() && lessThan.isChecked()) {
                    lessThan.setChecked(true);
                    greaterThan.setChecked(false);
                } else if (lessThan.isChecked()) {
                    lessThan.setChecked(true);
                } else {
                    lessThan.setChecked(false);
                }
            }
        });
    }
}