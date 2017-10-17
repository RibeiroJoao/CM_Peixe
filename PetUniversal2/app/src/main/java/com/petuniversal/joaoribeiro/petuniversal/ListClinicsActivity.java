package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;
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
        DatabaseReference databaseReference = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
            userID = extras.getString("userID");
            databaseReference = (DatabaseReference) extras.get("databaseReference");
        }

        ArrayList <String> names = new ArrayList<>();

        /**
         * Async to GET list of clinics
         */
        String clinicsUrl = "http://dev.petuniversal.com/hospitalization/api/clinics";
        AsyncGETs getRequest = new AsyncGETs();
        getRequest.execute(clinicsUrl, token, userID);
        try {
            if(getRequest.get()!=null) {
                Log.i("RESULT@LIST", getRequest.get());
                JSONArray arr = new JSONArray(getRequest.get());
                for (int i = 0; i < arr.length(); i++) {
                    names.add(arr.getJSONObject(i).getString("name"));
                    //Log.i("NAME"+i,names.get(i)+" added to array");
                }
            }else {
                Log.i("FIREBASE@LIST","GettingClinics");
                names = getClinicsForFirebase();
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i <= names.size()-1; i++) {
            LinearLayout ll = (LinearLayout)findViewById(R.id.listclinics_form);
            Button btn = new Button(this);
            btn.setText(names.get(i));
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
            });
            ll.addView(btn);
        }

        //Log.i("ArrNAMES",names.toString());
    }
    private ArrayList<String> getClinicsForFirebase() {
        ArrayList<String> clinicNames = new ArrayList<>();

        // Write a message to the database
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        myDatabaseRef.child("clinics").child("name1").setValue("Clinica 1 firebase");
        myDatabaseRef.child("clinics").child("name2").setValue("Clinica 2 firebase");

        clinicNames.set(0, "Clinica 1 firebase");
        clinicNames.set(1, "Clinica 2 firebase");
        Log.i("CLINICS@LIST", "Chegou aqui");
        // Read from the database
        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String snapshot = dataSnapshot.getValue(String.class);
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.i("FIREBASE@LIST Content", singleSnapshot.getValue(String.class));
                }
                //Log.i("FIREBASEcontent", snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("FIREBASE@LIST", "Failed to read database, " + error.toException());
            }
        });
        return clinicNames;
    }
}