package com.petuniversal.joaoribeiro.petuniversal;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private String animalName;
    private ArrayList<String> animalNames = new ArrayList<>();                //http://dev.petuniversal.com/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> clinicAnimalIDnInternID = new HashMap<>(); //http://dev.petuniversal.com/hospitalization/api/internments?clinic=53&open=true
    private HashMap<String,String> drugNamesnInterID = new HashMap<>();       //http://dev.petuniversal.com/hospitalization/api/drugs?clinic=53
    private HashMap<String,String> drugNamesnID = new HashMap<>();            //http://dev.petuniversal.com/hospitalization/api/drugs?clinic=53
    private ArrayList<String> drugNames = new ArrayList<>();                  //For Firebase
    private ArrayList<String> drugColors = new ArrayList<>();                 //For Firebase
    //private ArrayList<Bitmap> animalImages = new ArrayList<>();             //For Firebase
    private boolean isFirebase;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Pet Universal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        View view = findViewById(R.id.allElemsLayout);


        Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.containsKey("token")) {
            //API was success
            isFirebase= false;
            String token = extras.getString("token");
            String userID = extras.getString("userID");
            String clinicID = extras.getString("clinicID");
            Log.i("ENTROU@MAIN", "EXTRAS: token "+token+", userID "+userID+", clinicID "+ clinicID);

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
                Button btn = new Button(this);
                btn.setText(animalNames.get(i));
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                final String finalToken = token;
                final String finalUserID = userID;
                final String animalName = animalNames.get(i);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
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
                final Button btn = new Button(this);
                btn.setText(entry.getKey());
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                //TODO get buttonColor according to...
                btn.setBackgroundResource(R.color.colorOrange);

                btn.setOnTouchListener(new View.OnTouchListener() {
                    private GestureDetector gestureDetector = new GestureDetector(getApplication(), new GestureDetector.SimpleOnGestureListener() {
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
                            Toast.makeText( getApplication(), "Clicaste em "+entry.getKey() , Toast.LENGTH_LONG).show();

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
                        }else /*if (key.contains("image")) {
                            try {
                                URL url = new URL(value);
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                animalImages.add(bmp);
                            } catch (IOException e) {
                                Log.i("CATCH@MAIN", String.valueOf(e));
                            }
                        }else*/ if (key.contains("cor")){
                            drugColors.add(value);
                        }else if (key.contains("tarefa")){
                            drugNames.add(value);
                        }
                    }
                    //if (animalNames.size() != 0 && animalImages.size() != 0) {
                    if (animalNames.size() != 0) {
                        LinearLayout ll = (LinearLayout) findViewById(R.id.first_right_cell);
                        /*ImageView myImage = (ImageView) findViewById(R.id.first_right_image);
                        myImage.setImageBitmap(animalImages.get(0));
                        ll.addView(myImage);
                        */
                        final Button btn = (Button) findViewById(R.id.zeusButton);
                        btn.setText(animalNames.get(0));
                        btn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Code here executes on main thread after user presses button
                                Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                                myIntent.putExtra("animalName",btn.getText());
                                startActivity(myIntent);
                            }
                        });

                        LinearLayout ll2 = (LinearLayout) findViewById(R.id.second_left_cell);
                        /*ImageView myImage2 = (ImageView) findViewById(R.id.second_left_image);
                        myImage2.setImageBitmap(animalImages.get(1));
                        ll2.addView(myImage2);
                        */
                        final Button btn2 = (Button) findViewById(R.id.animalButton2);
                        btn2.setText(animalNames.get(1));
                        btn2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        btn2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Code here executes on main thread after user presses button
                                Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                                myIntent.putExtra("animalName",btn2.getText());
                                startActivity(myIntent);
                            }
                        });
                    }
                    if (drugNames.size() != 0) {
                        LinearLayout ll = (LinearLayout) findViewById(R.id.first_right_cell);
                        @SuppressLint("WrongViewCast") final TextView textView = (TextView) findViewById(R.id.textViewDrug1);
                        textView.setText(drugNames.get(0));

                        if(drugColors.get(0).contains("colorOrange")) {
                            textView.setBackgroundResource(R.color.colorOrange);
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
                    Log.i("FIREBASE@HORA", "Got canceled");
                }
            });
        }

        if (!String.valueOf(view.findViewById(R.id.textViewDrug1).getBackground()).contains("colorPet")){
            view.findViewById(R.id.textViewDrug1).setBackgroundResource(R.color.colorOrange);
            view.findViewById(R.id.textViewDrug1).setMinimumHeight(50);
        }

        //DOUBLE TAP
        view.findViewById(R.id.textViewDrug1).setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getApplication(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("DoubleTAP@HORA", "onDoubleTap");
                    findViewById(R.id.textViewDrug1).setBackgroundResource(R.color.colorPet);
                    findViewById(R.id.textViewDrug1).setMinimumHeight(50);
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
    }

    //Given index of drugNames change its color at Firebase and locally
    public static void changeColorToGreenAtFirebase(Integer id) {
        // from database instance get reference of 'users' node
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();
        Log.i("ENTROU@HORA","turnButtonGreen");
        if (id==0) {
            myDatabaseRef.child("animals").child("corTarefaAnimal1").setValue("colorPet");
        } else if (id==1){
            myDatabaseRef.child("animals").child("corTarefaAnimal2").setValue("colorPet");
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
            Log.i("NFC@MAIN","no NFC text!");
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
                    startActivity(myIntent);
                }
            }else if(tagContent.contains("Kika")){
                Log.i("KIKA@MAIN","***************************************");
                if (isFirebase){
                    changeColorToGreenAtFirebase(1);
                    //Refresh Main
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
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
            Toast toast = Toast.makeText(getApplicationContext(), "Opening camera...", Toast.LENGTH_SHORT);
            toast.show();

            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

            return true;
        }
        else if(id == R.id.menu_notifications){

            if(item.isChecked()){
                item.setChecked(false);
                Toast toast = Toast.makeText(getApplicationContext(), "Notifications are now OFF!", Toast.LENGTH_SHORT);
                toast.show();
                notificationManager.cancel(notifID);
            }
            else {
                item.setChecked(true);
                Toast toast = Toast.makeText(getApplicationContext(), "Notifications are now ON!", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(this, MainActivity.class);

                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
                taskStackBuilder.addParentStack(MainActivity.class);
                taskStackBuilder.addNextIntent(intent);

                PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.dog2)
                        .setContentTitle("Pet Universal Notification")
                        .setContentText("Zeus precisa da Vacina 2!")
                        .setAutoCancel(true) //remove when swiped
                        .setTicker("Pet has new task!")
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

                notificationManager.notify(notifID, notification.build());
            }
            return true;
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
                Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                myIntent.putExtra( "animalName", result.getContents());
                //TODO Arranjar forma de buscar outros campos
                startActivity(myIntent);
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void setNotificationRepeat (View view){
        Long notificationTime = new GregorianCalendar().getTimeInMillis()+5*1000; //5seconds
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, PendingIntent.getBroadcast(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
    }
}