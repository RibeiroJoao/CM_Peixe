package com.petuniversal.joaoribeiro.petuniversal;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    @Override
    protected void onNewIntent (Intent intent){
        Toast.makeText(this,"NFC intent received!",Toast.LENGTH_SHORT).show();
        onResume();
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume(){

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,0);
        IntentFilter[] intentFilterList = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilterList, null);
        readFromIntent(intent);
        super.onResume();
    }

    @Override
    protected void onPause(){
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    /**
     * Read From NFC Tag
     */
    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
            Log.i("NFC@MAIN", String.valueOf(msgs));
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedCode@MAIN", e.toString());
        }

        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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

