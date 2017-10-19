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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
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
        ArrayList <String> clinicNames = new ArrayList<>();
        ArrayList <String> clinicIDs = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        if (extras!=null) { // !=null
            token = extras.getString("token");
            userID = extras.getString("userID");
            Log.i("ENTROU@LIST","Tem extras");
            /**
             * Async to GET list of clinics
             */
            String clinicsUrl = "http://dev.petuniversal.com/hospitalization/api/clinics";
            AsyncGETs getRequest = new AsyncGETs();
            Log.i("Token@LIST", token);
            Log.i("USERid@LIST", userID);
            getRequest.execute(clinicsUrl, token, userID);
            try {
                if(getRequest.get()!=null) {
                    Log.i("RESULT@LIST", getRequest.get());
                    JSONArray arr = new JSONArray(getRequest.get());
                    for (int i = 0; i < arr.length(); i++) {
                        clinicNames.add(arr.getJSONObject(i).getString("name"));
                        clinicIDs.add(arr.getJSONObject(i).getString("id"));
                        //Log.i("NAME"+i,clinicNames.get(i)+" added to array");
                    }
                }else Log.i("ERROR@LIST","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest.cancel(true);
        }else {
            Log.i("FIREBASE@LIST","Entrou no GettingClinics");
            //clinicNames.add("Clin1 tmp @list1");
            //clinicNames.add("Clin2 tmp @list1");
            //clinicNames = getClinicsForFirebase();
            // Write a message to the database
            DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

            myDatabaseRef.child("clinics").child("name1").setValue("@List Clin 1 firebase");
            myDatabaseRef.child("clinics").child("name2").setValue("@List Clin 2 firebase");
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            //clinicNames.add("Clin1 firebase @list2");
            //clinicNames.add("Clin2 firebase @list2");


            //TODO AsyncTask for getting firstTime ClinicNames

            clinicNames = getClinicsForFirebase();
        }
        Log.i("SIZE", String.valueOf(clinicNames.size()));
        if (clinicNames.size()!=0) {
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
                        myIntent.putExtra( "clinicID", finalClinicID);
                        startActivity(myIntent);
                    }
                });
                ll.addView(btn);
            }
        }

    }

    private ArrayList<String> getClinicsForFirebase() {
        final ArrayList<String> clinicNames = new ArrayList<>();

        // Write a message to the database
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        myDatabaseRef.child("clinics").child("name1").setValue("@List Clin 1 firebase");
        myDatabaseRef.child("clinics").child("name2").setValue("@List Clin 2 firebase");
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //clinicNames.add("Clin1 firebase @list2");
        //clinicNames.add("Clin2 firebase @list2");

        Log.i("CLINICS@LIST", "Chegou aqui");
        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("clinics");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot
                Map<String, String> all = (Map<String, String>) dataSnapshot.getValue();
                for (Map.Entry<String, String> entry : all.entrySet()){
                    clinicNames.add(entry.getValue());
                }

                Log.i("NAMES NAMES",clinicNames.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.i("FIREBASE@LIST", "Failed to read database, " + databaseError.toException());
            }
        });
        Log.i("CLINICS@LIST", "saiu daqui names="+clinicNames.toString());
        return clinicNames;
    }

    private void collectClinics(Map<String, String> users) {

        ArrayList<String> names = new ArrayList<>();

    }
}