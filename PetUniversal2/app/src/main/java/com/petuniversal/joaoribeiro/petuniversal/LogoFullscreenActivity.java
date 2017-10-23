package com.petuniversal.joaoribeiro.petuniversal;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.net.InetAddress;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LogoFullscreenActivity extends AppCompatActivity {

    private boolean net = isInternetAvailable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen_fullscreen);

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

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("127.0.0.1"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
