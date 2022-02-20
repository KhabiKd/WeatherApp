package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ScrollView sv;
    private ProgressBar pb;
    private TextView todayDate, mainTemperature, mainCharacteristic_1, mainCharacteristic_2, mainCharacteristic_3;
    private EditText cityEdt;
    private ImageView mainIcon, searchBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sv = findViewById(R.id.scrollView2);
        pb = findViewById(R.id.idPBLoading);
        cityEdt = findViewById(R.id.city);
        todayDate = findViewById(R.id.todayDate);
        mainTemperature = findViewById(R.id.mainTemperature);
        mainCharacteristic_1 = findViewById(R.id.mainCharacteristic_1);
        mainCharacteristic_2 = findViewById(R.id.mainCharacteristic_2);
        mainCharacteristic_3 = findViewById(R.id.mainCharacteristic_3);
        mainIcon = findViewById(R.id.mainIcon);
        searchBtn = findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cityEdt.getText().toString().isEmpty()){
                    cityEdt.setHintTextColor(getResources().getColor(R.color.red));
                    cityEdt.setHint("Enter the city");
                }
                else{

                    hideKeyboard(MainActivity.this);
                    String city = cityEdt.getText().toString();
                    String key = "5dbc3acc6aac7cac485f6d0103104989";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";

                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    todayDate.setText(date);

                    new Weather().execute(url);
                }
            }
        });
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class Weather extends AsyncTask<String, String, String>{

        protected void onPreExecuted(){
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null )
                    buffer.append(line).append("\n");
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                try{
                    if(reader != null)
                        reader.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            pb.setVisibility(View.GONE);

            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                String description = jsonObjectWeather.getString("description").substring(0,1).toUpperCase(Locale.ROOT) + jsonObjectWeather.getString("description").substring(1);
                int id = jsonObjectWeather.getInt("id");

                long feels_like = Math.round(jsonObject.getJSONObject("main").getDouble("feels_like"));
                long tempMin = Math.round(jsonObject.getJSONObject("main").getDouble("temp_min"));
                long tempMax = Math.round(jsonObject.getJSONObject("main").getDouble("temp_max"));
                long temp = Math.round(jsonObject.getJSONObject("main").getDouble("temp"));


                mainCharacteristic_3.setText("Feels like " + feels_like + "°");
                mainCharacteristic_1.setText(description);
                mainCharacteristic_2.setText(tempMin + "°/" + tempMax + "°");
                mainTemperature.setText("" + temp + "°");

                if(id == 800){ mainIcon.setImageResource(R.drawable.weathericon_800); }
                else if(id == 801){ mainIcon.setImageResource(R.drawable.weathericon_801); }
                else if(id == 802){ mainIcon.setImageResource(R.drawable.weathericon_802); }
                else if(id == 803 || id == 804){ mainIcon.setImageResource(R.drawable.weathericon_803_804); }
                else if((id >= 300 && id <= 321) || (id>=520 && id <=531)){ mainIcon.setImageResource(R.drawable.weathericon_300_321); }
                else if(id>=500 && id<=504){ mainIcon.setImageResource(R.drawable.weathericon_801); }
                else if(id>=200 && id<=232){ mainIcon.setImageResource(R.drawable.weathericon_200_232); }
                else if((id>=600 && id<=622) || id==511) { mainIcon.setImageResource(R.drawable.weathericon_600_622); }
                else if(id>=701 && id<=781){ mainIcon.setImageResource(R.drawable.weathericon_701_781); }

            } catch(JSONException e) {
                e.printStackTrace();
            }


        }
    }
}