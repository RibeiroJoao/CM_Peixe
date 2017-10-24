package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;


/**
 * Created by Joao Ribeiro on 01/10/2017.
 */

public class FragmentHoraAtual extends Fragment implements View.OnClickListener{


    private String animalName;
    private ArrayList<String> animalNames = new ArrayList<>();                //http://dev.petuniversal.com/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> clinicAnimalIDnInternID = new HashMap<>(); //http://dev.petuniversal.com/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> drugNamesnInterID = new HashMap<>();       //http://dev.petuniversal.com/hospitalization/api/drugs?clinic=53
    private HashMap<String,String> drugNamesnID = new HashMap<>();            //http://dev.petuniversal.com/hospitalization/api/drugs?clinic=53
    private ArrayList<String> drugNames = new ArrayList<>();                  //For Firebase
    private ArrayList<String> drugCor = new ArrayList<>();                  //For Firebase

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hora_atual, container,false);

        Button button = view.findViewById(R.id.animalButton1);
        animalName = String.valueOf(button.getText());
        button.setOnClickListener(this);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras!=null && extras.containsKey("token")) {
            String token = extras.getString("token");
            String userID = extras.getString("userID");
            String clinicID = extras.getString("clinicID");
            Log.i("ENTROU@HORA", token +","+ userID +","+ clinicID);

            //GETTING ClinicAnimals info
            String clinicAnimUrl = "http://dev.petuniversal.com/hospitalization/api/internments?clinic="+clinicID+"&open=true";
            AsyncGETs getRequest = new AsyncGETs();
            Log.i("Token@HORA", token);
            Log.i("USERid@HORA", userID);
            getRequest.execute(clinicAnimUrl, token, userID);
            try {
                if(getRequest.get()!=null) {
                    Log.i("RESULT@HORA", getRequest.get());
                    JSONArray arr = new JSONArray(getRequest.get());
                    for (int i = 0; i < arr.length(); i++) {
                        String tmpName = arr.getJSONObject(i).getString("name");
                        int beginIndex = tmpName.indexOf('-')+1;
                        int endIndex = tmpName.indexOf('[');
                        String finalName = tmpName.substring(beginIndex,endIndex);
                        animalNames.add(finalName);
                        clinicAnimalIDnInternID.put(arr.getJSONObject(i).getString("clinicAnimal"),arr.getJSONObject(i).getString("id"));
                        Log.i("HASMAP@HORA",clinicAnimalIDnInternID.toString());
                    }
                }else Log.i("ERROR@HORA","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest.cancel(true);

            //GETTING ClinicAnimals info
            String drugsUrl = "http://dev.petuniversal.com/hospitalization/api/drugs?clinic="+clinicID;
            AsyncGETs getRequest2 = new AsyncGETs();
            getRequest2.execute(drugsUrl, token, userID);
            try {
                if(getRequest2.get()!=null) {
                    Log.i("RESULT2@HORA", getRequest2.get());
                    JSONArray arr = new JSONArray(getRequest2.get());
                    for (int i = 0; i < arr.length(); i++) {
                        String name = arr.getJSONObject(i).getString("name");
                        drugNamesnInterID.put(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("internment"));
                        drugNamesnID.put(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"));
                        Log.i("DrugNames&InternID@HORA",drugNamesnInterID.toString());
                        Log.i("DrugNames&ID@HORA",drugNamesnID.toString());
                    }
                }else Log.i("ERROR@HORA","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest2.cancel(true);

            for (int i = 0; i <= animalNames.size() - 1; i++) {
                LinearLayout ll = view.findViewById(R.id.ll_animal);
                Button btn = new Button(getActivity());
                btn.setText(animalNames.get(i));
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                final String finalToken = token;
                final String finalUserID = userID;
                final String animalName = animalNames.get(i);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        Intent myIntent = new Intent(getActivity(), AnimalActivity.class);
                        myIntent.putExtra("token", finalToken); //extras
                        myIntent.putExtra("userID", finalUserID);
                        myIntent.putExtra("animalName", animalName);
                        startActivity(myIntent);
                    }
                });
                ll.addView(btn);
            }
            for ( final Map.Entry<String, String> entry : drugNamesnInterID.entrySet()) {
                LinearLayout ll = view.findViewById(R.id.ll_droga);
                final Button btn = new Button(getActivity());
                btn.setText(entry.getKey());
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                btn.setBackgroundResource(R.color.colorOrange);

                btn.setOnTouchListener(new View.OnTouchListener() {
                    private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            Log.i("DoubleTAP@HORA", "onDoubleTap");
                            btn.setBackgroundResource(R.color.colorPet);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String currentDateAndTime = sdf.format(new Date());
                            //ISO_OFFSET_DATE_TIME	Date Time with Offset	2017-10-20T10:15:30+01:00'
                            String prettyDateAndTime = String.valueOf(currentDateAndTime.charAt(0))+String.valueOf(currentDateAndTime.charAt(1))+
                                    String.valueOf(currentDateAndTime.charAt(2))+String.valueOf(currentDateAndTime.charAt(3))+'-'+
                                    String.valueOf(currentDateAndTime.charAt(4))+String.valueOf(currentDateAndTime.charAt(5))+'-'+
                                    String.valueOf(currentDateAndTime.charAt(6))+String.valueOf(currentDateAndTime.charAt(7))+'T'+
                                    String.valueOf(currentDateAndTime.charAt(9))+String.valueOf(currentDateAndTime.charAt(10))+':'+
                                    "00:00.000+0000";
                            Log.i("TIME@HORA",prettyDateAndTime);
                            //TODO metodo que vai criar action verde (papel)

                            Toast toastTime = Toast.makeText(getActivity().getBaseContext(), "Clicaste em "+entry.getKey() , Toast.LENGTH_LONG);
                            toastTime.show();
                            return super.onDoubleTap(e);
                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.i("TAP@HORA", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                });
                ll.addView(btn);
            }
        }else{
            Log.i("FIREBASE@HORA", "Entrar no Firebase");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("animals");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String key = dataSnapshot1.getKey();
                        String value = dataSnapshot1.getValue(String.class);
                        if (key.contains("nome")) {
                            animalNames.add(value);
                        }else if (key.contains("cor")){
                            drugCor.add(value);
                        }else if (key.contains("tarefa")){
                            drugNames.add(value);
                        }
                    }
                    if (animalNames.size() != 0) {
                        for (int i = 0; i <= animalNames.size() - 1; i++) {
                            LinearLayout ll = getActivity().findViewById(R.id.ll_animal);
                            Button btn = new Button(getActivity());
                            btn.setText(animalNames.get(i));
                            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            btn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    // Code here executes on main thread after user presses button
                                    Intent myIntent = new Intent(getActivity(), AnimalActivity.class);
                                    startActivity(myIntent);
                                }
                            });
                            ll.addView(btn);
                        }
                    }
                    if (drugNames.size() != 0) {
                        for (int i = 0; i <= drugNames.size() - 1; i++) {
                            LinearLayout ll = getActivity().findViewById(R.id.ll_droga);
                            final Button btn = new Button(getActivity());
                            btn.setText(drugNames.get(i));
                            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            btn.setBackgroundResource(R.color.colorOrange);

                            btn.setOnTouchListener(new View.OnTouchListener() {
                                private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public boolean onDoubleTap(MotionEvent e) {
                                        Log.i("DoubleTAP@HORA", "onDoubleTap");
                                        btn.setBackgroundResource(R.color.colorPet);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                                        String currentDateAndTime = sdf.format(new Date());
                                        //ISO_OFFSET_DATE_TIME	Date Time with Offset	2017-10-20T10:15:30+01:00'
                                        String prettyDateAndTime = String.valueOf(currentDateAndTime.charAt(0))+String.valueOf(currentDateAndTime.charAt(1))+
                                                String.valueOf(currentDateAndTime.charAt(2))+String.valueOf(currentDateAndTime.charAt(3))+'-'+
                                                String.valueOf(currentDateAndTime.charAt(4))+String.valueOf(currentDateAndTime.charAt(5))+'-'+
                                                String.valueOf(currentDateAndTime.charAt(6))+String.valueOf(currentDateAndTime.charAt(7))+'T'+
                                                String.valueOf(currentDateAndTime.charAt(9))+String.valueOf(currentDateAndTime.charAt(10))+':'+
                                                "00:00.000+0000";
                                        Log.i("TIME@HORA",prettyDateAndTime);
                                        //TODO metodo que vai criar action verde (papel)

                                        Toast toastTime = Toast.makeText(getActivity().getBaseContext(), "Clicaste em "+btn.getText() , Toast.LENGTH_LONG);
                                        toastTime.show();
                                        return super.onDoubleTap(e);
                                    }
                                });

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    Log.i("TAP@HORA", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                                    gestureDetector.onTouchEvent(event);
                                    return true;
                                }
                            });
                            ll.addView(btn);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("FIREBASE@HORA", "Got canceled");
                }
            });
        }



        view.findViewById(R.id.textViewOrange1).setBackgroundResource(R.color.colorOrange);
        view.findViewById(R.id.textViewOrange1).setMinimumHeight(50);

        //DOUBLE TAP
        view.findViewById(R.id.textViewOrange1).setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("DoubleTAP@HORA", "onDoubleTap");
                    getActivity().findViewById(R.id.textViewOrange1).setBackgroundResource(R.color.colorPet);
                    getActivity().findViewById(R.id.textViewOrange1).setMinimumHeight(50);
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("TAP@HORA", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        return view;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.animalButton1:
                Intent intent = new Intent(getActivity(), AnimalActivity.class);
                intent.putExtra( "animalName", animalName);
                startActivity(intent);
                break;

        }
    }
}