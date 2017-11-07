package com.petuniversal.joaoribeiro.petuniversal;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class AnimalActivity extends AppCompatActivity {

    private String token;
    private String clinical_animal_id;
    private String animalName;
    private int animalID;
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
                            }else if (key.contains("imagemLink1")){
                                //imagemLink = value;
                                ImageView imageView = (ImageView) findViewById(R.id.animalImg_animalDetails);
                                DownloadImageTask downloadImageTask = new DownloadImageTask(imageView);
                                downloadImageTask.execute(value);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("CATCH@ANIMAL",databaseError.toString());
                    }
                });
            }else if (animalID == 1){
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
                            }else if (key.contains("imagemLink2")){
                                //imagemLink = value;
                                ImageView imageView = (ImageView) findViewById(R.id.animalImg_animalDetails);
                                DownloadImageTask downloadImageTask = new DownloadImageTask(imageView);
                                downloadImageTask.execute(value);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("CATCH@ANIMAL",databaseError.toString());
                    }
                });
            }

        }else{ //THROUGH API
            animalName = extras.getString("animalName");
            token = extras.getString("token");
            clinical_animal_id = extras.getString("clinical_animal_id");
            Log.i("EXTRAS@ANIMAL",animalName+", clinical_animal_id= "+clinical_animal_id);

            int animalIDint = 0;
            String getAnimalIDurl = "http://dev.petuniversal.com:8080/hospitalization/api/clinicAnimals/"+clinical_animal_id;
            AsyncGETs getRequest3 = new AsyncGETs();
            getRequest3.execute(getAnimalIDurl, token);
            try {
                if(getRequest3.get()!=null) {
                    Log.i("RESULT1@ANIMAL", getRequest3.get());
                    animalIDint = new JSONObject(getRequest3.get()).getInt("animal");
                    Log.i("animalIDint@ANIMAL", String.valueOf(animalIDint));
                }else Log.i("ERROR@ANIMAL","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                Log.i("CATCH1@ANIMAL", String.valueOf(e1));
            }
            getRequest3.cancel(true);

            int racaInt;
            String raca = null;
            int pesoInt;
            String sexo;
            int especieInt;
            String especie = null;
            String esterilizado;
            String getAnimalDetailsURL = "http://dev.petuniversal.com:8080/hospitalization/api/animals/"+animalIDint;
            AsyncGETs getRequest4 = new AsyncGETs();
            getRequest4.execute(getAnimalDetailsURL, token);
            try {
                if(getRequest4.get()!=null) {
                    Log.i("RESULT2@ANIMAL", getRequest4.get());
                    JSONObject arr = new JSONObject(getRequest4.get());
                    racaInt = arr.getInt("breed");
                    if (racaInt==85){
                        raca = "German Shepard";
                    }else if (racaInt == 7){
                        raca = "Bengal";
                    }else if (racaInt == 117){
                        raca = "Labrador Retriever";
                    }
                    pesoInt = arr.getInt("currentWeightValue");
                    sexo = arr.getString("sex");
                    especieInt = arr.getInt("species");
                    if(especieInt==9615){
                        especie = "Cão";
                        ImageView imageView = (ImageView) findViewById(R.id.animalImg_animalDetails);
                        imageView.setBackgroundResource(R.drawable.dog);
                    } else if(especieInt==9685){
                        especie = "Gato";
                        ImageView imageView = (ImageView) findViewById(R.id.animalImg_animalDetails);
                        imageView.setBackgroundResource(R.drawable.cat);
                    }
                    esterilizado = arr.getString("sterilized");
                    Log.i("animalDetails@ANIMAL","raca("+raca+"), peso("+pesoInt+"), especie("+especie+"), sexo("+sexo+"), esterilizado("+esterilizado+")");

                    TextView textView = (TextView) findViewById(R.id.textViewEspecie);
                    textView.setText("Espécie : "+especie);
                    TextView textView2 = (TextView) findViewById(R.id.textViewPeso);
                    textView2.setText("Peso : "+pesoInt+" kg");
                    TextView textView3 = (TextView) findViewById(R.id.textViewRaca);
                    textView3.setText("Raça : "+raca);
                    TextView textView4 = (TextView) findViewById(R.id.textViewSexo);
                    textView4.setText("Sexo : "+sexo);
                    TextView textView5 = (TextView) findViewById(R.id.textViewIdade);
                    textView5.setText("Esterilizado : "+esterilizado);

                }else Log.i("ERROR@ANIMAL","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                Log.i("CATCH2@ANIMAL", String.valueOf(e1));
            }
            getRequest4.cancel(true);

        }

        TextView animalNameView = (TextView) findViewById(R.id.animalName);
        animalNameView.setText(animalName);

    }
}
