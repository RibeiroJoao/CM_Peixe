package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListClinicsActivity extends AppCompatActivity {

    private ArrayList<String> clinicNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_clinics);

        getSupportActionBar().setTitle("Bem vindo à Pet Universal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.logout_icon);

        String token = null;
        String userID = null;
        clinicNames = new ArrayList<>();
        final ArrayList<String> clinicIDs = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("token")) {
            token = extras.getString("token");
            userID = extras.getString("userID");
            Log.i("STEP0@LIST", "Tem extras");

            //Async to GET list of clinics
            String clinicsUrl = "http://dev.petuniversal.com/hospitalization/api/clinics";
            AsyncGETs getRequest = new AsyncGETs();
            Log.i("Token@LIST", token);
            Log.i("USERid@LIST", userID);
            getRequest.execute(clinicsUrl, token, userID);
            try {
                if (getRequest.get() != null) {
                    Log.i("RESULT@LIST", getRequest.get());
                    JSONArray arr = new JSONArray(getRequest.get());
                    for (int i = 0; i < arr.length(); i++) {
                        clinicNames.add(arr.getJSONObject(i).getString("name"));
                        clinicIDs.add(arr.getJSONObject(i).getString("id"));
                        //Log.i("NAME"+i,clinicNames.get(i)+" added to array");
                    }
                } else Log.i("ERROR@LIST", "Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest.cancel(true);

        } else {
            Log.i("FIREBASE@LIST", "Entrou no GettingClinics");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("clinics");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String value = dataSnapshot1.getValue(String.class);
                        clinicNames.add(value);
                        Log.i("CLINICS@LIST", "through firebase= " + value);
                    }
                    if (clinicNames.size() != 0) {
                        for (int i = 0; i <= clinicNames.size() - 1; i++) {
                            LinearLayout ll = (LinearLayout) findViewById(R.id.listclinics_form);
                            Button btn = (Button) findViewById(R.id.buttonClinic);
                            btn.setText(clinicNames.get(i));
                            btn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    // Code here executes on main thread after user presses button
                                    Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                                    startActivity(myIntent);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("FIREBASE@LIST", "Got canceled");
                }
            });

        }

        //Create buttons
        Log.i("SIZE@LIST", String.valueOf(clinicNames.size()));
        if (clinicNames.size() != 0) {
            for (int i = 0; i <= clinicNames.size() - 1; i++) {
                LinearLayout ll = (LinearLayout) findViewById(R.id.listclinics_form);
                Button btn = new Button(this);
                btn.setText(clinicNames.get(i));
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                final String finalToken = token;
                final String finalUserID = userID;
                final String finalClinicID = clinicIDs.get(i);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        Intent myIntent = new Intent(ListClinicsActivity.this, MainActivity.class);
                        myIntent.putExtra("token", finalToken); //extra
                        myIntent.putExtra("userID", finalUserID);
                        myIntent.putExtra("clinicID", finalClinicID);
                        startActivity(myIntent);
                    }
                });
                ll.addView(btn);
            }
        }
    }
}