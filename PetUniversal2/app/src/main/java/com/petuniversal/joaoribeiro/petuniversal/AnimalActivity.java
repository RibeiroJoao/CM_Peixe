package com.petuniversal.joaoribeiro.petuniversal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AnimalActivity extends AppCompatActivity {

    private String animalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get animalName
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            animalName =extras.getString("animalName");
        }

        TextView animalNameView = (TextView) findViewById(R.id.animalName);
        animalNameView.setText(animalName);

    }
}
