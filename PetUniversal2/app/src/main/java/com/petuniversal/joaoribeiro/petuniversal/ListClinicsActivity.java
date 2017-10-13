package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        Log.i("Token@List",token);
        Log.i("UserID@List",userID);

                /*JSONArray arr = obj.getJSONArray("clinics");
                for (int i = 0; i < arr.length(); i++)                {
                    String clinicName = arr.getJSONObject(i).getString("clinicName");
                }*/

        final Button button1 = (Button) findViewById(R.id.button);
        button1.setText("Clínica 1");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                startActivity(myIntent);
                //finish();
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setText("Clínica 2");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                startActivity(myIntent);
                //finish();
            }
        });

    }
}
