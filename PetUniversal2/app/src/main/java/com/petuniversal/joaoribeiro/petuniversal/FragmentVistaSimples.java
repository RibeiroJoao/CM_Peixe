package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by Joao Ribeiro on 01/10/2017.
 */

public class FragmentVistaSimples extends Fragment implements View.OnClickListener{

    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vista_simples,container,false);
        button = (Button) view.findViewById(R.id.animalButton2);
        button.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.animalButton2:
                Intent intent = new Intent(getActivity(), AnimalActivity.class);
                startActivity(intent);
                break;
        }
    }
}
