package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private EditText city;
    private Button button_ready;
    private TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.city);
        button_ready = findViewById(R.id.button_ready);
        results = findViewById(R.id.results);

        button_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, "R.string.nocity", Toast.LENGTH_LONG).show();
                else{
                    String mycity = city.getText().toString();
                    String key = "f394d7c6cb1ecccd4915a37a4049f2e9";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + mycity + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }
    private class GetURLData extends AsyncTask<String, String, String>{

        protected void onPreExecute(){

            super.onPreExecute();
            results.setText("Ожидайте...");
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

                while((line = reader.readLine()) !=null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection != null)
                    connection.disconnect();

                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                }
                return null;
            }
            @SuppressLint("SetTextI18n")
            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    results.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + "°C" + "\n");
                    results.setText("Ощущается как: " + jsonObject.getJSONObject("main").getDouble("feels_like") + "°C" + "\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }