package com.namclu.android.bloquery.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.namclu.android.bloquery.PaceSharedPreference;
import com.namclu.android.bloquery.R;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                PaceSharedPreference sharedPreference = new PaceSharedPreference(getApplicationContext());
                if(sharedPreference.getBooleanValue("isLoggedIn")){
                    Intent intent = new Intent(LauncherActivity.this, BloqueryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(LauncherActivity.this, SplashActivity.class));
                }
            }
        }, 2000);
    }
}
