package com.petuniversal.joaoribeiro.petuniversal;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pet Universal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

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

        //setupForegroundDispatch(this, nfcAdapter);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,0);
        IntentFilter[] intentFilterList = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilterList, null);
        //readFromIntent(intent);
        super.onResume();
    }


    @Override
    protected void onPause(){
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    public void setupViewPager (ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentVistaSimples(),"Vista Simples");
        adapter.addFragment(new FragmentHoraAtual(),"Hora Atual");
        viewPager.setAdapter(adapter);
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