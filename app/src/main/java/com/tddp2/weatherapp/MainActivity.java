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

public class MainActivity extends AppCompatActivity {
    private String UNDEFINED_VALUE = "S/D";
    private String SERVER_URL_1 = "https://arcane-badlands-54436.herokuapp.com";
    private String SERVER_URL_2 = "https://lit-cove-37031.herokuapp.com";
    private int NEW_YORK_CITY_ID = 5128638;

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.loadWeatherData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeatherData();
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

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    private void loadWeatherData() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        String url = SERVER_URL_1 + "/city/" + NEW_YORK_CITY_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    JSONObject data = response.getJSONObject("data");
                    Double temperature = data.getDouble("temperature");
                    String weather = "sunny";
                    String time = data.getString("time");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-hh:mm", Locale.ENGLISH);
                    Date date = format.parse(time);

                    updateBackgroundImage(date, temperature, weather);
                    updateTemperatureText(temperature);
                } catch (JSONException|java.text.ParseException e) {
                    e.printStackTrace();
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
