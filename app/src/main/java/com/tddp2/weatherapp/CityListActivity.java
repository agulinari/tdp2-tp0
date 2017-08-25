package com.tddp2.weatherapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.offset;
import static android.os.Build.VERSION_CODES.M;

public class CityListActivity extends AppCompatActivity {

    static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
            "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
            "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple","Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple",
            "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple","Sugar-apple"};

    static final String[] FILTER = new String[] { "Apple", "Avocado", "Banana",
            "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple","Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple", "Sugar-apple",
            "Sugar-apple", "Sugar-apple","Sugar-apple"};

    ListView lv;
    EditText etSearchbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        ArrayList<CityItem> lst = new ArrayList<CityItem>();


        etSearchbox=(EditText)findViewById(R.id.etSearchbox);
        lv=(ListView)findViewById(R.id.lvCities);

        lv.setAdapter(new ArrayAdapter<CityItem>(this, R.layout.city_item,lst));

        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CityItem item = (CityItem)lv.getItemAtPosition(position);
                Intent intent = new Intent();
                Log.i("ITEM", item.getId() + " " + item.getName());
                intent.putExtra("id", item.getId());
                intent.putExtra("name", item.getName());
                setResult(Activity.RESULT_OK, intent);
                finish();
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
                Log.i("PAGE", String.valueOf(page));
                Log.i("ITEM COUNT", String.valueOf(totalItemsCount));
                loadNextDataFromApi(totalItemsCount);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });


        etSearchbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO llamar a la api
                getCities(etSearchbox.getText().toString(), 0, 50);

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

        getCities(etSearchbox.getText().toString(), offset, 50);
    }


    private void getCities(String term, final int offset, int count){
        String url = "https://arcane-badlands-54436.herokuapp.com/cities?term="+term+"&offset="+offset+"&count="+count;
        Log.i("URL", url);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here

                ArrayList<CityItem> lst = new ArrayList<CityItem>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String id = obj.getString("id");
                        String name = obj.getString("name");
                        String country = obj.getString("country");
                        CityItem city = new CityItem();
                        city.setId(id);
                        city.setName(name + " (" + country + ")");
                        lst.add(city);
                    }catch(Exception e){
                        Log.e("ERROR", e.getMessage());
                    }
                }

                if (offset == 0) {
                    ArrayAdapter adapter = new ArrayAdapter<CityItem>(CityListActivity.this, R.layout.city_item,lst);
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{
                    final ArrayAdapter adapter = ((ArrayAdapter)lv.getAdapter());
                    adapter.addAll(lst);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("ERROR", res.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject res) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("ERROR", statusCode+" "+res.toString());
            }
        });

    }
}
