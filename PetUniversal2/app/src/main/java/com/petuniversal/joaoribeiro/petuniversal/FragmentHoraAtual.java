package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Joao Ribeiro on 01/10/2017.
 */

public class FragmentHoraAtual extends Fragment implements View.OnClickListener{


    private Button button;
    private String animalName;
    //private boolean doubleClick= false;
    //private Handler doubleHandler = null;
    //private TextView textView1 = getView().findViewById(R.id.textViewOrange1);
    //private TextView textView2 = getView().findViewById(R.id.textViewOrange2);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hora_atual, container,false);
        button = view.findViewById(R.id.animalButton1);
        animalName = String.valueOf(button.getText());
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
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
