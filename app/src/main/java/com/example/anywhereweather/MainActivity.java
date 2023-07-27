package com.example.anywhereweather;

import static java.net.URLEncoder.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText cityName ;
    TextView weather ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        weather = findViewById(R.id.weather);

    }

    public void getWeather(View view){

        DownloadTask task = new DownloadTask();
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName.getText().toString() + "&appid=d1629f897e3075c053beaa1e9d79721d");

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(weather.getWindowToken(),0);

    }

    public class DownloadTask extends AsyncTask<String , Void , String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url ;
            HttpURLConnection urlConnection = null ;

            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                return result;


            }catch(Exception e){
                e.printStackTrace();

                Toast.makeText(MainActivity.this, "Could not find weather :(", Toast.LENGTH_SHORT).show();

                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("JSON",s);

            try {

                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather",weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                String details = jsonObject.getString("main");

                Log.i("Extra",details);

                details+=",";

                String message = "";


                for(int i = 0 ; i < arr.length() ; i++){
                    JSONObject jsonpart = arr.getJSONObject(i);

                    String main = jsonpart.getString("main");
                    String description = jsonpart.getString("description");

                    Log.i("main",jsonpart.getString("main"));
                    Log.i("description",jsonpart.getString("description"));

                    if(!main.equals("") && !description.equals("")){
                        message += main + " : " + description + "\r\n";
                    }

                }

                Pattern p = Pattern.compile("temp\":(.*?),");
                Matcher m = p.matcher(details);

                String temperature="";
                while(m.find()){
                    temperature+= m.group(1);
                }
                double temper = Double.valueOf(temperature)-273.15;

                Pattern a = Pattern.compile("feels_like\":(.*?),");
                Matcher b = a.matcher(details);

                String feels="";
                while(b.find()){
                    feels+= b.group(1);
                }
                double feel = Double.valueOf(feels)-273.15;


                Pattern x = Pattern.compile("humidity\":(.*?),");
                Matcher y = x.matcher(details);

                String humidity="";
                while(y.find()){
                    humidity+= y.group(1);
                }

                message += "\r\n Temperature : "+ Math.round(temper) +"°"+ "\r\n\r\nFeels like :"+Math.round(feel)+"°"+"\r\n\r\n Humidity : "+humidity+"%";

                String wind = jsonObject.getString("wind");
                Log.i("Wind",wind);

                Pattern c = Pattern.compile("speed\":(.*?),");
                Matcher d = c.matcher(wind);

                String windSpeed="";
                while(d.find()){
                    windSpeed+= d.group(1);
                }

                message += "\r\n\r\n Wind Speed : "+windSpeed+" km/h";

                if(!message.equals("")){
                    weather.setText(message);
                }else{
                    Toast.makeText(MainActivity.this, "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }


}