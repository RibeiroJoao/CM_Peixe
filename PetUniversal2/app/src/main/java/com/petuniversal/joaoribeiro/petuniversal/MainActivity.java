package com.petuniversal.joaoribeiro.petuniversal;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;

public class MainActivity extends AppCompatActivity {

    private MakeActionColorPet actionTask = null;
    private NfcAdapter nfcAdapter;
    private String animalName;
    private ArrayList<String> animalNames = new ArrayList<>();                //http://dev.petuniversal.com:8080/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> clinicAnimalIDnInternID = new HashMap<>(); //http://dev.petuniversal.com:8080/hospitalization/api/internments?clinic=53&open=true
    private ArrayList <String> clinicAnimalID = new ArrayList<>();
    private HashMap<String,String> drugNamesnID = new HashMap<>();          //http://dev.petuniversal.com:8080/hospitalization/api/drugs?clinic=53
    private HashMap<String,Integer> drugStartnPeriod = new HashMap<>();     //http://dev.petuniversal.com:8080/hospitalization/api/drugs?clinic=53
    private ArrayList<Integer> drugPeriods = new ArrayList<>();             //http://dev.petuniversal.com:8080/hospitalization/api/drugs?clinic=53
    private ArrayList<String> drugStarts = new ArrayList<>();               //http://dev.petuniversal.com:8080/hospitalization/api/drugs?clinic=53
    private ArrayList<String> drugNames = new ArrayList<>();                //For Firebase
    private ArrayList<String> drugColors = new ArrayList<>();               //For Firebase
    private ArrayList<String> animalImageURLs = new ArrayList<>();          //For Firebase
    private boolean isFirebase;
    private View view;
    private JobScheduler jobScheduler;
    private String token;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Pet Universal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        view = findViewById(R.id.allElemsLayout);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String currentDateAndTime = sdf.format(new Date());
        String currentHour = String.valueOf(currentDateAndTime.charAt(9))+String.valueOf(currentDateAndTime.charAt(10));
        TextView textView = (TextView) findViewById(R.id.textViewHour);
        textView.setText(currentHour+"h");

        Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.containsKey("token")) {
            //API was success
            isFirebase= false;
            token = extras.getString("token");
            //String userID = extras.getString("userID");
            String clinicID = extras.getString("clinicID");
            Log.i("ENTROU@MAIN", "EXTRAS: token "+token+", clinicID "+ clinicID);

            //GETTING ClinicAnimals info
            String clinicAnimUrl = "http://dev.petuniversal.com:8080/hospitalization/api/internments?clinic="+clinicID+"&open=true";
            AsyncGETs getRequest = new AsyncGETs();
            Log.i("Token@MAIN", token);
            getRequest.execute(clinicAnimUrl, token);
            try {
                if(getRequest.get()!=null) {
                    Log.i("RESULT@MAIN", getRequest.get());
                    JSONArray arr = new JSONArray(getRequest.get());
                    for (int i = 0; i < arr.length(); i++) {
                        String tmpName = arr.getJSONObject(i).getString("name");
                        int beginIndex = tmpName.indexOf('-')+1;
                        int endIndex = tmpName.indexOf('[');
                        String finalName = tmpName.substring(beginIndex,endIndex);
                        animalNames.add(finalName);
                        Log.i("TEST3@MAIN",finalName);
                        clinicAnimalID.add(arr.getJSONObject(i).getString("clinicAnimal"));
                    }
                    Log.i("CLINaniID@MAIN",clinicAnimalID.toString());
                }else Log.i("ERROR@MAIN","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
                e1.printStackTrace();
            }
            getRequest.cancel(true);

            //GETTING ClinicAnimals info
            String drugsUrl = "http://dev.petuniversal.com:8080/hospitalization/api/drugs?clinic="+clinicID;
            AsyncGETs getRequest2 = new AsyncGETs();
            getRequest2.execute(drugsUrl, token);
            try {
                if(getRequest2.get()!=null) {
                    Log.i("RESULT2@MAIN", getRequest2.get());
                    JSONArray arr = new JSONArray(getRequest2.get());
                    for (int i = 0; i < arr.length(); i++) {
                        drugNamesnID.put(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"));
                        drugStartnPeriod.put(arr.getJSONObject(i).getString("started"),arr.getJSONObject(i).getInt("period"));
                        drugPeriods.add(arr.getJSONObject(i).getInt("period"));
                        drugStarts.add(arr.getJSONObject(i).getString("started"));
                    }
                    Log.i("DrugNames&InternID@MAIN", drugNamesnID.toString());
                    Log.i("drugStartnPeriod@MAIN",drugStartnPeriod.toString());
                }else Log.i("ERROR@MAIN","Request API is null");
            } catch (InterruptedException | ExecutionException | JSONException e1) {
               Log.i("CATCH3@MAIN", String.valueOf(e1));
            }
            getRequest2.cancel(true);

            Log.i("TEST4@MAIN",animalNames.toString());

            for (int i = 0; i <= animalNames.size() - 1; i++) {
                LinearLayout ll = view.findViewById(R.id.ll_animal);
                Button btn = new Button(this);
                Log.i("TEST5@MAIN",animalNames.get(i));
                btn.setText(animalNames.get(i));
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                final String finalToken = token;
                final String clinical_animal_id = clinicAnimalID.get(i);
                final String animalName = animalNames.get(i);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                        myIntent.putExtra("token", finalToken); //extras
                        myIntent.putExtra("clinical_animal_id", clinical_animal_id);
                        myIntent.putExtra("animalName", animalName);
                        startActivity(myIntent);
                    }
                });
                ll.addView(btn);
            }
            int iterator = -1;
            for ( final Map.Entry<String, String> entry : drugNamesnID.entrySet()) {
                iterator += 1;
                LinearLayout ll = view.findViewById(R.id.ll_droga);
                final Button btn = new Button(this);
                btn.setText(entry.getKey());
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                //TODO Calc current Color ? get actions para verificar se já não foi feita?
                //ISO_OFFSET_DATE_TIME	Date Time with Offset	2017-10-20T10:15:30+01:00'
                final String prettyDateAndTime = String.valueOf(currentDateAndTime.charAt(0))+String.valueOf(currentDateAndTime.charAt(1))+
                        String.valueOf(currentDateAndTime.charAt(2))+String.valueOf(currentDateAndTime.charAt(3))+'-'+
                        String.valueOf(currentDateAndTime.charAt(4))+String.valueOf(currentDateAndTime.charAt(5))+'-'+
                        String.valueOf(currentDateAndTime.charAt(6))+String.valueOf(currentDateAndTime.charAt(7))+'T'+
                        String.valueOf(currentDateAndTime.charAt(9))+String.valueOf(currentDateAndTime.charAt(10))+':'+
                        "00:00.000+0000";
                Log.i("TIME@MAIN",prettyDateAndTime);

                int drugHour = Integer.parseInt(String.valueOf(drugStarts.get(iterator).charAt(9) + drugStarts.get(iterator).charAt(10)));
                Log.i("HOUR****@MAIN", String.valueOf(drugHour%2));
                if(drugPeriods.get(iterator)==1){
                    btn.setBackgroundResource(R.color.colorOrange);
                }else if (drugPeriods.get(iterator) == 2 && (Integer.parseInt(currentHour)%2)!=0){
                    btn.setBackgroundResource(R.color.colorOrange);
                }else{
                    btn.setVisibility(View.INVISIBLE);
                }

                btn.setOnTouchListener(new View.OnTouchListener() {
                    private GestureDetector gestureDetector = new GestureDetector(getApplication(), new GestureDetector.SimpleOnGestureListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            Log.i("DoubleTAP@MAIN", "onDoubleTap");

                            btn.setBackgroundResource(R.color.colorPet);
                            actionTask = new MakeActionColorPet(token, prettyDateAndTime, Integer.parseInt(entry.getValue()), 222); //TODO 222 is static for joao@clinicaveti.com
                            actionTask.execute();


                            Toast.makeText( getApplication(), "Clicaste em "+entry.getKey() , Toast.LENGTH_LONG).show();
                            return super.onDoubleTap(e);
                        }
                    });
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.i("TAP@MAIN", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                });
                ll.addView(btn);
            }
        }else{
            Log.i("FIREBASE@MAIN", "Entrar no Firebase");
            //API was unsuccessful
            isFirebase= true;

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("animals");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String key = dataSnapshot1.getKey();
                        String value = dataSnapshot1.getValue(String.class);
                        if (key.contains("nome")) {
                            animalNames.add(value);
                        }else if (key.contains("image")) {
                            animalImageURLs.add(value);
                        }else if (key.contains("cor")){
                            drugColors.add(value);
                        }else if (key.contains("tarefa")){
                            drugNames.add(value);
                        }
                    }
                    //if (animalNames.size() != 0 && animalImages.size() != 0) {
                    if (animalNames.size() != 0) {
                        LinearLayout ll = (LinearLayout) findViewById(R.id.first_right_cell);

                        ImageView myImage = (ImageView) findViewById(R.id.first_right_image);
                        DownloadImageTask downloadImageTask = new DownloadImageTask(myImage);
                        downloadImageTask.execute(animalImageURLs.get(0));

                        final Button btn = (Button) findViewById(R.id.zeusButton);
                        btn.setText(animalNames.get(0));
                        btn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Code here executes on main thread after user presses button
                                Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                                myIntent.putExtra("animalName",btn.getText());
                                myIntent.putExtra("animalID",0);
                                startActivity(myIntent);
                            }
                        });

                        ImageView myImage2 = (ImageView) findViewById(R.id.second_left_image);
                        DownloadImageTask downloadImageTask2 = new DownloadImageTask(myImage2);
                        downloadImageTask2.execute(animalImageURLs.get(1));

                        final Button btn2 = (Button) findViewById(R.id.animalButton2);
                        btn2.setText(animalNames.get(1));
                        btn2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        btn2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Code here executes on main thread after user presses button
                                Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                                myIntent.putExtra("animalName",btn2.getText());
                                myIntent.putExtra("animalID",1);
                                startActivity(myIntent);
                            }
                        });
                    }
                    if (drugNames.size() != 0) {
                        LinearLayout ll = (LinearLayout) findViewById(R.id.first_right_cell);
                        @SuppressLint("WrongViewCast") final TextView textView = (TextView) findViewById(R.id.textViewDrug1);
                        textView.setText(drugNames.get(0));

                        if(drugColors.get(0).contains("colorOrange")) {
                            textView    .setBackgroundResource(R.color.colorOrange);
                        }else if(drugColors.get(0).contains("colorPet")) {
                            textView.setBackgroundResource(R.color.colorPet);
                        }

                        textView.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(getApplication(), new GestureDetector.SimpleOnGestureListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    Log.i("DoubleTAP@MAIN", "onDoubleTap");
                                    changeColorToGreenAtFirebase(0);
                                    textView.setBackgroundResource(R.color.colorPet);
                                    Toast.makeText(getApplication(), "Clicaste em "+textView.getText(), Toast.LENGTH_SHORT).show();

                                    return super.onDoubleTap(e);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Log.i("TAP@MAIN", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });

                        LinearLayout ll2 = (LinearLayout) findViewById(R.id.second_right_cell);
                        @SuppressLint("WrongViewCast") final TextView textView2 = (TextView) findViewById(R.id.textViewDrug2);
                        textView2.setText(drugNames.get(1));

                        if(drugColors.get(1).contains("colorOrange")) {
                            textView2.setBackgroundResource(R.color.colorOrange);
                        }else if(drugColors.get(1).contains("colorPet")) {
                            textView2.setBackgroundResource(R.color.colorPet);
                        }
                        textView2.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(getApplication(), new GestureDetector.SimpleOnGestureListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    Log.i("DoubleTAP@MAIN", "onDoubleTap");
                                    changeColorToGreenAtFirebase(1);
                                    textView2.setBackgroundResource(R.color.colorPet);
                                    Toast.makeText(getApplication(), "Clicaste em "+textView2.getText(), Toast.LENGTH_SHORT).show();

                                    return super.onDoubleTap(e);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Log.i("TAP@MAIN", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("FIREBASE@MAIN", "Got canceled");
                }
            });
        }

        //DOUBLE TAP
        view.findViewById(R.id.textViewDrug1).setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getApplication(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("DoubleTAP@MAIN", "onDoubleTap");
                    findViewById(R.id.textViewDrug1).setBackgroundResource(R.color.colorPet);
                    findViewById(R.id.textViewDrug1).setMinimumHeight(50);
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("TAP@MAIN", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    //Given index of drugNames change its color at Firebase and locally
    public static void changeColorToGreenAtFirebase(Integer id) {
        // from database instance get reference of 'users' node
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference().child("animals");
        Log.i("ENTROU@MAIN","turnButtonGreen");
        if (id==0) {
            myDatabaseRef.child("corTarefaAnimal1").setValue("colorPet");
        } else if (id==1){
            myDatabaseRef.child("corTarefaAnimal2").setValue("colorPet");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onNewIntent (Intent intent){
        super.onNewIntent(intent);
        Log.i("NFC@MAIN","NFC intent received!");

        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(parcelables!=null && parcelables.length>0){
            readTextFomMessage((NdefMessage) parcelables[0]);
        }else {
            Log.i("NFC@MAIN","no NFC content!");
        }
    }

    public void readTextFomMessage(NdefMessage ndefMessage){
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            Toast.makeText(this,"Content is '"+tagContent+"'",Toast.LENGTH_SHORT).show();
            if(tagContent.contains("Zeus")){
                Log.i("ZEUS@MAIN","***************************************");
                if (isFirebase){
                    changeColorToGreenAtFirebase(0);
                    //Refresh Main
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    myIntent.putExtra("token", token);
                    startActivity(myIntent);
                }
            }else if(tagContent.contains("Kika")){
                Log.i("KIKA@MAIN","***************************************");
                if (isFirebase){
                    changeColorToGreenAtFirebase(1);
                    //Refresh Main
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    myIntent.putExtra("token", token);
                    startActivity(myIntent);
                }
            }
        }else {
            Log.i("NFC@MAIN","no NFC text! (2)");
        }
    }

    public String getTextFromNdefRecord (NdefRecord ndefRecord){
        String tagContent = null;

        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
            int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
            tagContent = new String (payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.i("NFC@MAIN", "Falhou "+e);
        }
        return tagContent;
    }

    @Override
    protected void onResume(){
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0,intent,0);
        IntentFilter[] intentFilterList = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilterList, null);
        super.onResume();
    }

    @Override
    protected void onPause(){
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifID = 100;

        if (id == R.id.menu_qrcode) {
            Toast.makeText(getApplicationContext(), "Opening camera...", Toast.LENGTH_SHORT).show();

            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

            return true;
        }
        else if(id == R.id.menu_notifications2){
            if(item.isChecked()){
                item.setChecked(false);
                Toast.makeText(getApplicationContext(), "Notifications2 are now OFF!", Toast.LENGTH_SHORT).show();
                jobScheduler.cancel(111);
                //jobScheduler.cancelAll();
            }
            else {
                item.setChecked(true);
                Toast.makeText(getApplicationContext(), "Notifications2 are now ON!", Toast.LENGTH_SHORT).show();
                jobScheduler = JobScheduler.getInstance(this);
                constructJob();
            }



        } else if(id == R.id.menu_nfc){
            if(nfcAdapter!=null && nfcAdapter.isEnabled()){
                Toast.makeText(this,"NFC está disponível!",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"NFC não está disponível!",Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scanning cancelado.", Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents().contains("Zeus")) {
                    Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                    myIntent.putExtra("animalName", result.getContents());
                    myIntent.putExtra("animalID", 0); //Zeus
                    startActivity(myIntent);
                }else if (result.getContents().contains("Kika")) {
                    Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                    myIntent.putExtra("animalName", result.getContents());
                    myIntent.putExtra("animalID", 1); //Kika
                    startActivity(myIntent);
                }else{
                    Toast.makeText(this, "This is '"+result.getContents()+"'",Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void constructJob(){
        JobInfo.Builder jobBuilder = new JobInfo.Builder(111, new ComponentName(getPackageName(), BackgroundService.class.getName()));
        //PersistableBundle persistableBundle = new PersistableBundle();

        jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) //dados ou wifi
                .setPeriodic(3600000) // 5000 = 5 seconds  (1h = 3600000 ms)
                .setPersisted(true)
                .build();

        jobScheduler.schedule(jobBuilder.build());
    }


    /**
     * Represents an asynchronous action task used to check an activity. (with API)
     * Parameters = String date_time, int drugID, int clinicPersonID
     */
    private class MakeActionColorPet extends AsyncTask<Void, Void, Boolean> {

        private String token = null;
        private int drugID;
        private String date_time;
        private int doer;
        private String URLParameters = null;
        private String returnado = null;        // Will contain the raw JSON response as a string.

        public MakeActionColorPet(String token, String date_time, int drugID, int clinicPersonID) {
            this.token = token;
            this.date_time = date_time;
            this.drugID = drugID;
            this.doer = clinicPersonID;

            URLParameters = "{\n" +
                    "\t\"status\":1,\n" +
                    "    \"started\": \""+date_time+"\" ,\n" +
                    "    \"drug\": "+drugID+",\n" +
                    " \t\"doer\": "+doer+"\n" +
                    "}";
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            byte[] postData = URLParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;

            try {
                // Construct the URL for the Login
                URL url = new URL("http://dev.petuniversal.com:8080/hospitalization/api/actions");

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setDoOutput(true);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
                urlConnection.setUseCaches(false);
                urlConnection.connect();

                Log.i("Action0@MAIN","with API");

                try(DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write( postData );
                } catch (IOException e) {
                    Log.i("CATCH@MAIN", String.valueOf(e));
                }
                Log.i("Action1@MAIN","with API");

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.i("Action2@MAIN","with API");

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                Log.i("Action3@MAIN","with API");

                if (buffer.length() == 0) {
                    return false;
                }else{
                    returnado = buffer.toString();
                    Log.i("RETURNED@MAIN",returnado);
                    return true;
                }
            } catch (IOException e) {
                Log.i("ERROR@MAIN_Async", "Token not returned from API!");
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.i("CATCH@MAIN_Async", "Error closing stream "+e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            actionTask = null;

            if (success) {
                if (returnado.contains("created")) {
                    Toast.makeText(getApplicationContext(), "Action registered successly! ", Toast.LENGTH_SHORT).show();
                    //Refresh
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    myIntent.putExtra("token", token);
                    startActivity(myIntent);
                } else {
                    Log.w("ERROR@MAIN", "Action não returnou o esperado!");
                }
            }else {
                Log.w("ERROR@MAIN", "ACTION falhada por completo!");
            }
        }

        @Override
        protected void onCancelled() {
            actionTask = null;
        }

    }

}
