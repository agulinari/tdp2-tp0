package com.tddp2.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

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
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadWeatherData() {
        String url = "https://ajax.googleapis.com/ajax/services/search/images";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("rsz", "8");

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                updateBackgroundImage(true, 25, "sunny");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast toast = Toast.makeText(getBaseContext(), "No fue posible conectarse al servidor, por favor reintente más tarde", 15);
                toast.show();
            }
        });
    }

    private void updateBackgroundImage(boolean isDayTime, int temperature, String weather) {
        int imageId = this.getImageId(isDayTime, temperature, weather);
        ImageView backgroundWeatherImage = (ImageView) findViewById(R.id.background_weather_image);
        backgroundWeatherImage.setImageResource(imageId);
    }

    private int getImageId(boolean isDayTime, int temperature, String weather) {
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
