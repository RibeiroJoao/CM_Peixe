package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.acl.Owner;


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

        TextView ownerName = (TextView) findViewById(R.id.animalOwnerName);
        ownerName.setClickable(true);
        ownerName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent myIntent = new Intent(AnimalActivity.this, OwnerActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
 /*   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_animal, container,false);
        return view;
    }*/
}
