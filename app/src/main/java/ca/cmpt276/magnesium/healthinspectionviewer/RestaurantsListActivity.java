package ca.cmpt276.magnesium.healthinspectionviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.magnesium.restaurantmodel.Facility;
import static ca.cmpt276.magnesium.restaurantmodel.FacilityType.Restaurant;

public class RestaurantsListActivity extends AppCompatActivity {
    private List<Facility> facilities = new ArrayList<Facility>();
    private BaseAdapter adapter;


    public static Intent makeRestaurantsListIntent(Context context){
        return new Intent(context, RestaurantsListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        addDefaultRest();
        populateListView();
    }

    private void populateListView() {
        ListView lv = findViewById(R.id.resList_restaurants_listView);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return facilities.size();
            }

            @Override
            public Facility getItem(int position) {
                return facilities.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.restaurants_list_view, parent, false);
                }

                Facility restaurant = getItem(position);

                TextView resName = (TextView) convertView.findViewById(R.id.resArrayList_res_name);
                resName.setText(restaurant.getName());
                ImageView icon = (ImageView) convertView.findViewById(R.id.resArrayList_res_icon);
                icon.setImageResource(restaurant.getIconID());

                return convertView;
            }
        };
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(
                (parent, view, position, id) -> {
                    // test for display restaurant
                    Intent intent = RestaurantActivity.makeRestaurantIntent(RestaurantsListActivity.this, position);
                    startActivity(intent);
        });
    }

    private void addDefaultRest() {
        facilities.add(new Facility("0001","rest1","123 street","Surrey",Restaurant,47.08,60.32,R.drawable.burger));
        facilities.add(new Facility("0012","rest2","452 road","Surrey",Restaurant,234.23,83.64,R.drawable.fish));
        facilities.add(new Facility("3154","rest3","123 str","Surrey",Restaurant,12.58,23.34,R.drawable.kitchen));
        facilities.add(new Facility("2034","rest4","342 road","Surrey",Restaurant,54.58,70.34,R.drawable.kitchen));
        facilities.add(new Facility("0123","rest5","1000 ave","Surrey",Restaurant,76.58,74.34,R.drawable.pizza));
        facilities.add(new Facility("0213","rest6","1 st","Surrey",Restaurant,34.58,28.34,R.drawable.kitchen));
        facilities.add(new Facility("7823","rest7","west road","Surrey",Restaurant,48.58,53.34,R.drawable.fish));
    }






}
