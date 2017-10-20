package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * Created by Joao Ribeiro on 01/10/2017.
 */

public class FragmentHoraAtual extends Fragment implements View.OnClickListener{


    private String animalName;
    private ArrayList<String> animalNames = new ArrayList<>();                //http://dev.petuniversal.com/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> clinicAnimalIDnInternID = new HashMap<>(); //http://dev.petuniversal.com/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> drugNamesnInterID = new HashMap<>();       //http://dev.petuniversal.com/hospitalization/api/drugs?clinic=53

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hora_atual, container,false);
        Button button = view.findViewById(R.id.animalButton1);
        animalName = String.valueOf(button.getText());
        button.setOnClickListener(this);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras!=null) { // !=null
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
                        Log.i("DrugNames&InternID@HORA",drugNamesnInterID.toString());
                    }
                }else Log.i("ERROR@HORA","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest2.cancel(true);

            for (int i = 0; i <= animalNames.size() - 1; i++) {
                LinearLayout ll = view.findViewById(R.id.ll_hora);
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
        }else{
            Log.i("FIREBASE@HORA", "Entrar no Firebase");
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
            case R.id.textViewOrange1:
                //TODO doubleTap to turn green
                Toast toast1 = Toast.makeText(getContext(), "Should turn green 1", Toast.LENGTH_SHORT);
                toast1.show();

                break;
            case R.id.textViewOrange2:
                Toast toast2 = Toast.makeText(getContext(), "Should turn green 2", Toast.LENGTH_SHORT);
                toast2.show();
                break;
        }
    }
}
/*findViewById(R.id.touchableText).setOnTouchListener(new OnTouchListener() {
    private GestureDetector gestureDetector = new GestureDetector(Test.this, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("TEST", "onDoubleTap");
            return super.onDoubleTap(e);
        }
        ... // implement here other callback methods like onFling, onScroll as necessary
    });

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
        gestureDetector.onTouchEvent(event);
        return true;
    }
});*/