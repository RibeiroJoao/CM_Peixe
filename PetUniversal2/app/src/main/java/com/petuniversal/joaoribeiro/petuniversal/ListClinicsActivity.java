package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListClinicsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_clinics);

        getSupportActionBar().setTitle("Bem vindo à Pet Universal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.logout_icon);

        String token = null;
        String userID = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
            userID = extras.getString("userID");
        }

        ArrayList <String> names = new ArrayList<>();

        /**
         * Async to GET list of clinics
         */
        String clinicsUrl = "http://dev.petuniversal.com/hospitalization/api/clinics";
        AsyncGETs getRequest = new AsyncGETs();
        getRequest.execute(clinicsUrl, token, userID);

        try {
            if(getRequest.get()!=null)
                Log.i("RESULT@LIST",getRequest.get());
                //JSONObject obj = new JSONObject(getRequest.get());
                //name1 = obj.getString("name");
                JSONArray arr =  new JSONArray(getRequest.get());
                for (int i = 0; i < arr.length(); i++){
                    names.add(arr.getJSONObject(i).getString("name"));
                    Log.i("NAME"+i,arr.getJSONObject(i).getString("name"));
                    Log.i("ArrNAME"+i,names.get(i));
                }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        

        final Button button1 = (Button) findViewById(R.id.button);
        button1.setText(names.get(0));
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button1.setText(names.get(1));
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });
        Log.i("ArrNAMES",names.toString());
    }
}
