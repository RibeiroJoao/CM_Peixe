package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * Created by Joao Ribeiro on 01/10/2017.
 */

public class FragmentVistaSimples extends Fragment implements View.OnClickListener{

    private Button button;
    private String animalName;
    private String clinicID;
    private ArrayList<String> animalNames = new ArrayList<>();
    private String token;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vista_simples,container,false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras!=null) { // !=null
            token = extras.getString("token");
            userID = extras.getString("userID");
            clinicID= extras.getString("clinicID");
            Log.i("STEP0@VISTA", token+","+userID+","+clinicID);

            //TODO AsyncGets AnimaisInternados
            /**
             * Async to GET list of internados
             */
            String animalsUrl = "http://dev.petuniversal.com/hospitalization/api/internments?clinic="+clinicID+"&open=true";
            AsyncGETs getRequest = new AsyncGETs();
            //Log.i("Token@VISTA", token);
            //Log.i("USERid@VISTA", userID);
            getRequest.execute(animalsUrl, token, userID);
            try {
                if(getRequest.get()!=null) {
                    Log.i("RESULT@VISTA", getRequest.get());
                    JSONArray arr = new JSONArray(getRequest.get());
                    for (int i = 0; i < arr.length(); i++) {
                        String tmpName = arr.getJSONObject(i).getString("name");
                        int beginIndex = tmpName.indexOf('-')+1;
                        int endIndex = tmpName.indexOf('[');
                        String finalName = tmpName.substring(beginIndex,endIndex);
                        animalNames.add(finalName);
                        Log.i("NAME@VISTA"+i,animalNames.get(i)+" added to array");
                    }
                }else Log.i("ERROR@VISTA","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest.cancel(true);

            for (int i = 0; i <= animalNames.size() - 1; i++) {
                LinearLayout ll = view.findViewById(R.id.listanimals_layout);
                Button btn = new Button(getActivity());
                btn.setText(animalNames.get(i));
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                final String finalToken = token;
                final String finalUserID = userID;
                final String finalClinicNames = animalNames.get(i);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        Intent myIntent = new Intent(getActivity(), AnimalActivity.class);
                        myIntent.putExtra("token", finalToken); //extras
                        myIntent.putExtra("userID", finalUserID);
                        myIntent.putExtra( "clinicName", finalClinicNames);
                        startActivity(myIntent);
                    }
                });
                ll.addView(btn);
            }
        }else {
            Log.i("ERROR@VISTA", "ERROOO");
        }

        button = view.findViewById(R.id.animalButton1);
        animalName = String.valueOf(button.getText());
        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.animalButton2:
                Intent intent = new Intent(getActivity(), AnimalActivity.class);
                intent.putExtra( "animalName", animalName);
                startActivity(intent);
                break;
        }
    }

}
