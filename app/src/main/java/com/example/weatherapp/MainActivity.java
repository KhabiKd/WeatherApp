package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
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
                long temp = Math.round(jsonObject.getJSONObject("main").getDouble("temp"));
                long tempMin = Math.round(jsonObject.getJSONObject("main").getDouble("temp_min"));
                long tempMax = Math.round(jsonObject.getJSONObject("main").getDouble("temp_max"));
                long feels_like = Math.round(jsonObject.getJSONObject("main").getDouble("feels_like"));


                mainTemperature.setText("" + temp + "°");
                mainCharacteristic_1.setText(jsonObject.getJSONObject("weather").getString("description"));
                mainCharacteristic_2.setText(tempMin + "°/" + tempMax + "°");
                mainCharacteristic_3.setText("Feels like" + feels_like + "°");
                mainIcon.setImageDrawable(Drawable.createFromPath(jsonObject.getJSONObject("weather").getString("icon")));
            } catch(JSONException e) {
                e.printStackTrace();
            }


        }
    }
}