package com.example.a10010729.weatherproject;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    EditText editText;
    int zip=0;
    String str="";
    TextView textView;
    double temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "Main Thread");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)(findViewById(R.id.editText));
        textView=(TextView)(findViewById(R.id.textView));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backgroundThread weatherThread = new backgroundThread();
        weatherThread.execute();

    }

    public class backgroundThread extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Log.d("TAG", "Background Thread");

                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?zip=08852,us&APPID=2558ea3f753a25ef96927e0b5cc7099e");
                URLConnection urlConnection = url.openConnection();

                InputStream  inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                String text,output="";
                while( (text=input.readLine())!= null)
                {
                    output+=text;
                }
                JSONObject weatherData=new JSONObject(output);
                JSONArray list = weatherData.getJSONArray("list");
                JSONObject zero = list.getJSONObject(0);
                JSONObject main = zero.getJSONObject("main");
                double temperature = main.getDouble("temp");
                Log.d("weather data",temperature+"");
                //Log.d("cod data",cod);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
