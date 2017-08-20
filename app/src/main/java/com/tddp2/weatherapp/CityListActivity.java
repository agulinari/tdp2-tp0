package com.tddp2.weatherapp;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tddp2.weatherapp.com.tddp2.weatherapp.listener.EndlessScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class CityListActivity extends AppCompatActivity {

    static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
            "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
            "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple","Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple",
            "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple","Sugar-apple"};

    ListView lv;
    EditText etSearchbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(FRUITS));


        etSearchbox=(EditText)findViewById(R.id.etSearchbox);
        lv=(ListView)findViewById(R.id.lvCities);

        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.city_item,lst));

        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
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
        lv.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });


        etSearchbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO llamar a la api

                String url = "https://ajax.googleapis.com/ajax/services/search/images";
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("q", "android");
                params.put("rsz", "8");
                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                        // Handle resulting parsed JSON response here
                        Log.i("OK", response.toString());
                        final ArrayAdapter adapter = ((ArrayAdapter)lv.getAdapter());

                        ArrayList<String> lst = new ArrayList<String>();
                        lst.add("Filtro1");
                        lst.add("Filtro2");
                        lst.add("Filtro3");
                        lv.setAdapter(new ArrayAdapter<String>(CityListActivity.this, R.layout.city_item,lst));
                        
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.e("ERROR", res.toString());                    }
                });

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

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
        final ArrayAdapter adapter = ((ArrayAdapter)lv.getAdapter());
        adapter.add("Nuevo1");
        adapter.add("Nuevo2");
        adapter.add("Nuevo3");
        adapter.notifyDataSetChanged();
    }
}
