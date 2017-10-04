package com.petuniversal.joaoribeiro.petuniversal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pet Universal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.HOUR_OF_DAY,16);
        //calendar.set(Calendar.MINUTE,45);

        Intent intent= new Intent(getApplicationContext(), NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

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
        else if (id == R.id.menu_smartwatch) {
            Toast toast = Toast.makeText(getApplicationContext(), "Trying to connect to device...", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        else if(id == R.id.menu_notifications){

            if(item.isChecked()){
                item.setChecked(false);
                Toast toast = Toast.makeText(getApplicationContext(), "Notifications are now OFF!", Toast.LENGTH_SHORT);
                toast.show();
                alarmManager.cancel(pendingIntent);
            }
            else {
                item.setChecked(true);
                Toast toast = Toast.makeText(getApplicationContext(), "Notifications are now ON!", Toast.LENGTH_SHORT);
                toast.show();

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);

            }

            return true;
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
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(MainActivity.this, AnimalActivity.class);
                startActivity(myIntent);
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}
