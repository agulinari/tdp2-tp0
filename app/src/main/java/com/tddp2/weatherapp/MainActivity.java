package com.tddp2.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {
    private String UNDEFINED_VALUE = "S/D";
    private String SERVER_URL_1 = "https://arcane-badlands-54436.herokuapp.com";
    private String SERVER_URL_2 = "https://lit-cove-37031.herokuapp.com";
    private String NEW_YORK_CITY_ID = "5128638";
    private String NEW_YORK_CITY_NAME = "New York (US)";

    public static final int REQUEST_CODE = 1;
    private final String CITY_ID = "cityId";
    private final String CITY_NAME = "city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadWeatherDataOfLastCity();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeatherDataOfLastCity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, CityListActivity.class);
            this.startActivityForResult(intent, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                String id = data.getStringExtra("id");
                String name = data.getStringExtra("name");
                Log.i("ID", id);
                Log.i("CITY NAME", name);
                saveLastCity(id, name);
                loadWeatherData(id, name);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    private void loadWeatherData(String cityId, String cityName) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(cityName);

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        String url = SERVER_URL_1 + "/city/" + cityId;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    JSONObject data = response.getJSONObject("data");
                    Log.i("Weather", "Response: " + data.toString());

                    Double temperature = data.getDouble("temperature");
                    String weather = "sunny";
                    String time = data.getString("time");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                    Date date = format.parse(time);

                    updateBackgroundImage(date, temperature, weather);
                    updateTemperatureText(temperature);
                } catch (JSONException|java.text.ParseException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                Toast toast = Toast.makeText(getBaseContext(), "No fue posible conectarse al servidor, por favor reintente más tarde", Toast.LENGTH_SHORT);
                toast.show();

                TextView temperatureView = (TextView)findViewById(R.id.temperature);
                if (temperatureView.getText() == "") {
                    temperatureView.setText(UNDEFINED_VALUE);
                }
            }
        });
    }

    private void loadWeatherDataOfLastCity() {
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        String id = settings.getString(CITY_ID, NEW_YORK_CITY_ID);
        String name = settings.getString(CITY_NAME, NEW_YORK_CITY_NAME);
        this.loadWeatherData(id, name);
    }

    private void saveLastCity(String cityId, String cityName) {
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CITY_ID, cityId);
        editor.putString(CITY_NAME, cityName);
        editor.apply();
    }

    private void updateTemperatureText(Double temperature) {
        int roundedTemperature = (int) Math.round(temperature);
        String temperatureText = String.valueOf(roundedTemperature).concat("ºC");
        TextView temperatureView = (TextView)findViewById(R.id.temperature);
        temperatureView.setText(temperatureText);
    }

    private void updateBackgroundImage(Date date, Double temperature, String weather) {
        int hours = date.getHours();
        boolean isDayTime = (9 <= hours) && (17 >= hours);
        int imageId = this.getImageId(isDayTime, temperature, weather);
        ImageView backgroundWeatherImage = (ImageView) findViewById(R.id.background_weather_image);
        backgroundWeatherImage.setImageResource(imageId);
    }

    private int getImageId(boolean isDayTime, Double temperature, String weather) {
        if (isDayTime) {
            if (weather.equals("sunny")) {
                if (temperature > 20) {
                    return R.drawable.day_sunny_hot; //playa soleada sin nubes
                } else if (temperature < 8) {
                    return R.drawable.day_sunny_cold; //montaña nevada con cielo despejado,
                } else {
                    return R.drawable.day_sunny_warm; //ciudad soleada
                }
            } else if (weather.equals("cloudy")) {
                if (temperature > 20) {
                    return R.drawable.day_sunny_hot; //selva con cielo nublado
                } else if (temperature < 8) {
                    return R.drawable.day_sunny_hot; //puente cubierto por nubes
                } else {
                    return R.drawable.day_sunny_hot; //ciudad nublada
                }
            } else {
                if (temperature > 20) {
                    return R.drawable.day_sunny_hot; //calle de una ciudad balnearia bajo una lluvia torrencial
                } else if (temperature < 8) {
                    return R.drawable.day_sunny_hot; //pareja abrigada sosteniendo un paraguas debajo de la tormenta
                } else {
                    return R.drawable.day_sunny_hot; //ciudad lluviosa
                }
            }
        } else {
            if (weather.equals("rainy")) {
                return R.drawable.day_sunny_hot; //una tormenta a través de una ventana mojada.
            } else {
                return R.drawable.day_sunny_hot; //noche estrellada
            }
        }
    }
}
