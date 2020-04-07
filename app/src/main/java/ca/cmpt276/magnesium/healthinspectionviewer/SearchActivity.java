package ca.cmpt276.magnesium.healthinspectionviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import ca.cmpt276.magnesium.restaurantmodel.DatabaseHelperFacility;

public class SearchActivity extends AppCompatActivity {

    public static final String SEARCH_QUERYSTRING = "SearchActivity_SQLQueryString";
    public static final String SEARCH_PREFSFILE = "SearchActivity_PrefsFile";
    public static final String SEARCH_MAPS_NEED_UPDATE = "SearchActivity_MapScreen_Bool";
    public static final String SEARCH_RESTLIST_NEED_UPDATE = "SearchActivity_RestList_Bool";

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
        setupButtons();

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

    private void setupButtons() {
        // Do the majority of the work here in building up our SQL queries!
        Button resetButton = findViewById(R.id.searchActivity_resetFilter);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset SharedPrefs saved SQL query:
                SharedPreferences prefs = getSharedPreferences(SEARCH_PREFSFILE, 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(SEARCH_QUERYSTRING, null);
                // Ensure other activities reset to "all" restaurants
                edit.putBoolean(SEARCH_RESTLIST_NEED_UPDATE, true);
                edit.putBoolean(SEARCH_MAPS_NEED_UPDATE, true);
                edit.apply();
                edit.apply();
                // Finish the activity:
                finish();
            }
        });

        // Now, the "fun" part...
        DatabaseHelperFacility facilityDBHelper = new DatabaseHelperFacility(this);
        Button applyButton = findViewById(R.id.search_applyFilter);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase facilityDB = facilityDBHelper.getReadableDatabase();
                // Get data from all these fields:
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("SELECT * FROM facility_table INNER JOIN inspection_table ON " +
                        "facility_table.TrackingNumber == inspection_table.TrackingNumber\n");

                // Add all conditionals.

                // TextBox:
                TextInputEditText nameFilter = findViewById(R.id.searchActivity_nameFilterEditable);
                if (nameFilter.getText() != null) {
                    // We have text.
                    queryBuilder.append(" WHERE ");
                    queryBuilder.append(DatabaseHelperFacility.COL_2);
                    queryBuilder.append(" LIKE '%");
                    queryBuilder.append(nameFilter.getText().toString());
                    queryBuilder.append("%'");
                }

                Spinner hazardDropDown = findViewById(R.id.searchActivity_hazardSpinner);
                String currentItem = (String)hazardDropDown.getSelectedItem();
                if (!currentItem.equals("")) {
                    queryBuilder.append(" AND ");
                    queryBuilder.append(String.format("HazardRating == '%s'", currentItem));
                }

                CheckBox lessThan = findViewById(R.id.searchActivity_enableLessThen);
                if (lessThan.isChecked()) {
                    EditText lessThanText = findViewById(R.id.searchActivity_lessThanNumberInput);
                    String input = lessThanText.getText().toString();
                    if (!input.equals("")) {
                        queryBuilder.append(" AND ");
                        String queryString = String.format("facility_table.TrackingNumber in " +
                                "(SELECT TrackingNumber FROM (SELECT TrackingNumber, " +
                                "COUNT(NumCritical) as yearlyCriticals FROM inspection_table " +
                                "WHERE (20200406 - InspectionDate) < 10000" +
                                " GROUP BY TrackingNumber) WHERE yearlyCriticals <= %s)", input);
                        queryBuilder.append(queryString);
                    }
                }

                CheckBox greaterThan = findViewById(R.id.searchActivity_enableMoreThen);
                if (greaterThan.isChecked()) {
                    EditText greaterThanText = findViewById(R.id.searchActivity_greaterThanNumberInput);
                    String input = greaterThanText.getText().toString();
                    if (!input.equals("")) {
                        queryBuilder.append(" AND ");
                        String queryString = String.format("facility_table.TrackingNumber in " +
                                "(SELECT TrackingNumber FROM (SELECT TrackingNumber, " +
                                "COUNT(NumCritical) as yearlyCriticals FROM inspection_table " +
                                "WHERE (20200406 - InspectionDate) < 10000" +
                                " GROUP BY TrackingNumber) WHERE yearlyCriticals >= %s)", input);
                        queryBuilder.append(queryString);
                    }
                }

                queryBuilder.append(" GROUP BY facility_table.TrackingNumber ORDER BY Name ASC;");
                String sqlQuery = queryBuilder.toString();
                // Error checking - don't allow this query to be saved unless it runs properly!
                try {
                    Cursor result = facilityDB.rawQuery(sqlQuery, null);
                    result.close();
                    // This means the query is good.
                    SharedPreferences prefs = getSharedPreferences(SEARCH_PREFSFILE, 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(SEARCH_QUERYSTRING, sqlQuery);
                    edit.putBoolean(SEARCH_RESTLIST_NEED_UPDATE, true);
                    edit.putBoolean(SEARCH_MAPS_NEED_UPDATE, true);
                    edit.apply();
                    SearchActivity.super.onBackPressed();
                } catch (Exception e) {
                    // Something went wrong.
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    builder.setTitle(R.string.search_invalid_title);
                    builder.setMessage(R.string.search_invalid_content);
                    builder.show();
                } finally {
                    facilityDB.close();
                }
            }
        });
    }
}