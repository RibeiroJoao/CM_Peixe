package com.petuniversal.joaoribeiro.petuniversal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class AnimalActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
 /*   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_animal, container,false);
        return view;
    }*/
}
