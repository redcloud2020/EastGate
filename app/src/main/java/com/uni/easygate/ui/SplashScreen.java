package com.uni.easygate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.uni.easygate.R;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.datalayer.models.Exit;
import com.uni.easygate.datalayer.server.MyHttpClient;
import com.uni.easygate.security.SecurePreferences;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    private MyHttpClient myHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        start();

    }


    private void start() {
        Log.e("hello",SecurePreferences.getInstance(this).getString(Parameters.LOGGED) );
        if (TextUtils.isEmpty(SecurePreferences.getInstance(this).getString(Parameters.LOGGED))) {



            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            if (!TextUtils.isEmpty(SecurePreferences.getInstance(this).getString(Parameters.USER_NUMBER))) {
                Exit.getAll();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }


}
