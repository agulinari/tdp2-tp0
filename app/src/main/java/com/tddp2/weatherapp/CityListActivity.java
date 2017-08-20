package com.tddp2.weatherapp;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tddp2.weatherapp.com.tddp2.weatherapp.listener.EndlessScrollListener;

import java.util.ArrayList;
import java.util.Arrays;

public class CityListActivity extends ListActivity {

    static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
            "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
            "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple","Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple",
            "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple","Sugar-apple"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(FRUITS));

        setListAdapter(new ArrayAdapter<String>(this, R.layout.city_item,lst));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        // ... the usual
        // ListView lvItems = (ListView) findViewById(R.id.lvItems);

        // Attach the listener to the AdapterView onCreate
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
        final ArrayAdapter adapter = ((ArrayAdapter)getListAdapter());
        adapter.add("Nuevo1");
        adapter.add("Nuevo2");
        adapter.add("Nuevo3");
        adapter.notifyDataSetChanged();
    }
}
