package com.petuniversal.joaoribeiro.petuniversal;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Joao Ribeiro on 13/10/2017.
 */

public class AsyncGETs extends AsyncTask<Void, Void, String> {

    String URLParameters = "grant_type=password&username=joao.ribeiro@petuniversal.com&password=a";
    //String URLtokens = "http://dev.petuniversal.com/hospitalization/api/tokens";

    @Override
    protected String doInBackground(Void... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        byte[] postData = URLParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        // Will contain the raw JSON response as a string.
        String returnado = null;

        try {
            // Construct the URL for the Login query
            URL url = new URL("http://dev.petuniversal.com/hospitalization/api/tokens");

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            //Não percebo este pedaço, não funciona sem ele
            try(DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                wr.write( postData );
            } catch (IOException e) {
                Log.i("EXCEPTIONLoginToken", String.valueOf(e));
                e.printStackTrace();
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            /*if (inputStream == null) {
                // Nothing to do.
                return null;
            }*/
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            returnado = buffer.toString();
            //Log.i("RETURNED",returnado);
            return returnado;
        } catch (IOException e) {
            Log.i("ERROR", "Token not returned!");
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

}
