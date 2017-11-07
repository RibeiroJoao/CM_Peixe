package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LogoFullscreenActivity extends AppCompatActivity {

    private boolean net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen_fullscreen);

        //Check if Network is available
        net = isNetworkAvailable();
        Log.i("NET@LOGO", String.valueOf(net));
        if (net) {
            //Show splash screen for 3 sec
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent myIntent = new Intent(LogoFullscreenActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            }, 3000);
        }else{
            Toast.makeText(this, "No Internet connection...",Toast.LENGTH_LONG).show();

            //Show splash screen for 3 sec
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent myIntent = new Intent(LogoFullscreenActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            }, 3000);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
