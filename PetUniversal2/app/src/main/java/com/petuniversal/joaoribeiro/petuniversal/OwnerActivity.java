package com.petuniversal.joaoribeiro.petuniversal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Joao Ribeiro on 05/10/2017.
 */

public class OwnerActivity extends AppCompatActivity {

    private String ownerName;
    private int ownerID;
    private int ownerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}