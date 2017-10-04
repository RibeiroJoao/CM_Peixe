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
        Log.i("AAAAAAAAAAAAAAAAHHH","Está tudo bem ListClinics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bem vindo à Pet Universal");

        final Button button1 = (Button) findViewById(R.id.button);
        button1.setText("Clínica 1");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setText("Clínica 2");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
