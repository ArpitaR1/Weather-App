package com.example.a10010729.weatherproject;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    String zip,location;
    TextView textView,textView0,textView1,textView2,textView3,textView4,locationText,quoteText,dateText;
    TextView high0,high1,high2,high3,high4;
    TextView low0,low1,low2,low3,low4;
    ImageView imageView,imageView0,imageView1,imageView2,imageView3,imageView4;
    double currentTemp;
    ArrayList<Double> lowTemp = new ArrayList<>();
    ArrayList<Double> highTemp = new ArrayList<>();
    ArrayList<String> mainWeather = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    Button button;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)(findViewById(R.id.editText));
        textView=(TextView)(findViewById(R.id.textView));
        textView0=(TextView)(findViewById(R.id.textView0));
        textView1=(TextView)(findViewById(R.id.textView1));
        textView2=(TextView)(findViewById(R.id.textView2));
        textView3=(TextView)(findViewById(R.id.textView3));
        textView4=(TextView)(findViewById(R.id.textView4));
        locationText=(TextView)(findViewById(R.id.locationText));
        quoteText=(TextView)(findViewById(R.id.quoteText));
        dateText=(TextView)(findViewById(R.id.dateText));
        high0=(TextView)(findViewById(R.id.high0));
        high1=(TextView)(findViewById(R.id.high1));
        high2=(TextView)(findViewById(R.id.high2));
        high3=(TextView)(findViewById(R.id.high3));
        high4=(TextView)(findViewById(R.id.high4));
        low0=(TextView)(findViewById(R.id.low0));
        low1=(TextView)(findViewById(R.id.low1));
        low2=(TextView)(findViewById(R.id.low2));
        low3=(TextView)(findViewById(R.id.low3));
        low4=(TextView)(findViewById(R.id.low4));
        imageView=(ImageView)(findViewById(R.id.imageView));
        imageView0=(ImageView)(findViewById(R.id.imageView0));
        imageView1=(ImageView)(findViewById(R.id.imageView1));
        imageView2=(ImageView)(findViewById(R.id.imageView2));
        imageView3=(ImageView)(findViewById(R.id.imageView3));
        imageView4=(ImageView)(findViewById(R.id.imageView4));
        button=(Button)(findViewById(R.id.button));
        constraintLayout=(ConstraintLayout)(findViewById(R.id.constraintLayout));

        button.setBackgroundColor(Color.rgb(255,64,129));
        constraintLayout.setBackgroundColor(Color.rgb(135, 210, 169));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG","CLICKED");
                zip=editText.getText().toString();

                if ((zip.length()==5)) {
                    new backgroundThread(zip).execute();
                }
            }
        });
    }

    public class backgroundThread extends AsyncTask<Void,Void,String>{

        String zip;

        public backgroundThread(String zip){
            this.zip=zip;
        }

        @Override
        protected String doInBackground(Void... voids) {

                try {

                    highTemp= new ArrayList<>(5);
                    lowTemp= new ArrayList<>(5);
                    dates= new ArrayList<>(5);
                    mainWeather= new ArrayList<>(5);

                    URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?zip="+zip+",us&APPID=2558ea3f753a25ef96927e0b5cc7099e");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = null ;
                    try {
                        inputStream = urlConnection.getInputStream();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                    String text, output = "";
                    while ((text = input.readLine()) != null) {
                        output += text;
                    }

                    JSONObject weatherData = new JSONObject(output);
                    JSONArray list = weatherData.getJSONArray("list");
                    JSONObject index = list.getJSONObject(0);
                    JSONObject main = index.getJSONObject("main");
                    JSONObject city = weatherData.getJSONObject("city");
                    location = city.getString("name");

                    currentTemp = main.getDouble("temp");

                    for (int i = 0; i < 5; i++) {
                        index = list.getJSONObject(i);
                        main = index.getJSONObject("main");
                        JSONArray weather = index.getJSONArray("weather");
                        JSONObject weatherMain = weather.getJSONObject(0);
                        dates.add(index.getString("dt"));
                        mainWeather.add(weatherMain.getString("main"));
                        lowTemp.add(main.getDouble("temp_min"));
                        highTemp.add(main.getDouble("temp_max"));
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            return dates.get(0);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            long unixSecond = 1000*Long.parseLong(dates.get(0));
            Date sdate = new Date(unixSecond);
            SimpleDateFormat ssdf = new SimpleDateFormat("dd MMM yyyy");
            ssdf.setTimeZone(TimeZone.getTimeZone("EST"));
            String currentDate = ssdf.format(sdate);

            for (int i=0;i<5;i++){
                highTemp.set(i,round(((9.0/5.0*(highTemp.get(i)))-459.67),1));
                lowTemp.set(i,round(((9.0/5.0*(lowTemp.get(i)))-459.67),1));
                long unixSeconds = 1000*Long.parseLong(dates.get(i));
                Date date = new Date(unixSeconds);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                sdf.setTimeZone(TimeZone.getTimeZone("EST"));
                String formattedDate = sdf.format(date);
                dates.set(i,formattedDate);
            }

            currentTemp=round(((9.0/5.0*(currentTemp))-459.67),1);

            locationText.setText(location);
            imageView.setImageResource(addPicture(mainWeather.get(0)));
            textView.setText(""+currentTemp+"°F");
            quoteText.setText(addQuote(mainWeather.get(0)));
            dateText.setText(currentDate);

            textView0.setText(dates.get(0));
            high0.setText("High: "+highTemp.get(0)+"°F");
            low0.setText("Low: "+lowTemp.get(0)+"°F");
            imageView0.setImageResource(addPicture(mainWeather.get(0)));

            textView1.setText(dates.get(1));
            high1.setText("High: "+highTemp.get(1)+"°F");
            low1.setText("Low: "+lowTemp.get(1)+"°F");
            imageView1.setImageResource(addPicture(mainWeather.get(1)));

            textView2.setText(dates.get(2));
            high2.setText("High: "+highTemp.get(2)+"°F");
            low2.setText("Low: "+lowTemp.get(2)+"°F");
            imageView2.setImageResource(addPicture(mainWeather.get(2)));

            textView3.setText(dates.get(3));
            high3.setText("High: "+highTemp.get(3)+"°F");
            low3.setText("Low: "+lowTemp.get(3)+"°F");
            imageView3.setImageResource(addPicture(mainWeather.get(3)));

            textView4.setText(dates.get(4));
            high4.setText("High: "+highTemp.get(4)+"°F");
            low4.setText("Low: "+lowTemp.get(4)+"°F");
            imageView4.setImageResource(addPicture(mainWeather.get(4)));
        }
    }

    public static int addPicture(String str){
        if (str.equals("Clear")){
            return R.drawable.sun;
        }
        else if (str.equals("Clouds")){
            return  R.drawable.clouds;
        }
        else if (str.equals("Rain")){
            return  R.drawable.rain;
        }
        else if (str.equals("Snow")){
            return R.drawable.snow;
        }
        else {
            return  R.drawable.thunderstrom;
        }
    }

    public static String addQuote(String str){
        if (str.equals("Clear")){
            return "Sunshine, daisies, butter mellow, turn this stupid, fat rat yellow";
        }
        else if (str.equals("Clouds")){
            return "Let them swim in the deepest ocean or glide over the highest cloud";
        }
        else if (str.equals("Rain")){
            return "No amount of mud, wind, or rain could tarnish Harry";
        }
        else if (str.equals("Snow")){
            return "Ron, you're making it snow";
        }
        else if (str.equals("Thunderstorm")){
            return "There's a storm coming, Harry";
        }
        return "";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}