package com.petuniversal.joaoribeiro.petuniversal;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Joao Ribeiro on 13/10/2017.
 */

public class AsyncGETs extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params){
        String returndata = null;
        try {
            // Creating & connection Connection with url and required Header.
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + params[1]);
            urlConnection.setRequestMethod("GET");   //POST or GET
            urlConnection.connect();


            // Check the connection status.
            int statusCode = urlConnection.getResponseCode();

            // Connection success. Proceed to fetch the response.
            if (statusCode == 200) {
                InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                StringBuilder dta = new StringBuilder();
                String chunks;
                while ((chunks = buff.readLine()) != null) {
                    dta.append(chunks);
                }
                returndata = dta.toString();
                return returndata;
            } else {
                Log.i("CLINICS@AsyncGETs","NOT RETURNED from API");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returndata;
    }
}

