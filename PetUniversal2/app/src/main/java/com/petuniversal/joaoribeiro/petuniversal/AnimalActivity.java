package com.petuniversal.joaoribeiro.petuniversal;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AnimalActivity extends AppCompatActivity {

    private String animalName;
    private int animalID;
    private Bitmap image;
    private String especie;
    private String raça;
    private String sexo;
    private String idade;
    private String peso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("animalID")) { //Through Firebase
            animalName =extras.getString("animalName");
            animalID=extras.getInt("animalID");

            if (animalID == 0){
                ImageView imageView = (ImageView) findViewById(R.id.animalImg_animalDetails);
                imageView.setBackgroundResource(R.drawable.dog);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("animals");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String key = dataSnapshot1.getKey();
                            String value = dataSnapshot1.getValue(String.class);
                            if (key.contains("especieAnimal1")) {
                                //especie = value;
                                TextView textView = (TextView) findViewById(R.id.textViewEspecie);
                                textView.setText("Espécie : "+value);
                            }else if (key.contains("pesoAnimal1")){
                                //peso = value;
                                TextView textView = (TextView) findViewById(R.id.textViewPeso);
                                textView.setText("Peso : "+value);
                            }else if (key.contains("raçaAnimal1")){
                                //raça = value;
                                TextView textView = (TextView) findViewById(R.id.textViewRaca);
                                textView.setText("Raça : "+value);
                            }else if (key.contains("sexoAnimal1")){
                                //sexo = value;
                                TextView textView = (TextView) findViewById(R.id.textViewSexo);
                                textView.setText("Sexo : "+value);
                            }else if (key.contains("idadeAnimal1")){
                                //idade = value;
                                TextView textView = (TextView) findViewById(R.id.textViewIdade);
                                textView.setText("Idade : "+value);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("CATCH@ANIMAL",databaseError.toString());
                    }
                });
            }else if (animalID == 1){
                ImageView imageView = (ImageView) findViewById(R.id.animalImg_animalDetails);
                imageView.setBackgroundResource(R.drawable.cat);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("animals");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String key = dataSnapshot1.getKey();
                            String value = dataSnapshot1.getValue(String.class);
                            if (key.contains("especieAnimal2")) {
                                //especie = value;
                                TextView textView = (TextView) findViewById(R.id.textViewEspecie);
                                textView.setText("Espécie : "+value);
                            }else if (key.contains("pesoAnimal2")){
                                //peso = value;
                                TextView textView = (TextView) findViewById(R.id.textViewPeso);
                                textView.setText("Peso : "+value);
                            }else if (key.contains("raçaAnimal2")){
                                //raça = value;
                                TextView textView = (TextView) findViewById(R.id.textViewRaca);
                                textView.setText("Raça : "+value);
                            }else if (key.contains("sexoAnimal2")){
                                //sexo = value;
                                TextView textView = (TextView) findViewById(R.id.textViewSexo);
                                textView.setText("Sexo : "+value);
                            }else if (key.contains("idadeAnimal2")){
                                //idade = value;
                                TextView textView = (TextView) findViewById(R.id.textViewIdade);
                                textView.setText("Idade : "+value);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("CATCH@ANIMAL",databaseError.toString());
                    }
                });
            }


        }

        TextView animalNameView = (TextView) findViewById(R.id.animalName);
        animalNameView.setText(animalName);

    }
}
