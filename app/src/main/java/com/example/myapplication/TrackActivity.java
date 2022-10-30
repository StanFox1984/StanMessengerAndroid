package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackActivity extends AppCompatActivity {
    public String login;
    public String password;
    public String server;
    public ConditionVariable sent;
    public ExecutorService executorService;
    public Location lastKnownLocation;
    public class LocationLoader implements UrlRunner.UrlResultCallback {
        @Override
        public void ResultCallback(String result_str) {
            TextView location = (TextView) findViewById(R.id.textView6);
            location.setText(result_str);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        Intent i = getIntent();
        login = i.getStringExtra("login");
        password = i.getStringExtra("password");
        server = i.getStringExtra("server");
        executorService = Executors.newFixedThreadPool(4);
        refresh_location();
    }

    protected void refresh_location() {
        lastKnownLocation = LocationTracker.get_location(this);
        if (lastKnownLocation == null)
            return;
        String msg = "Longitude: " + new String(String.valueOf(lastKnownLocation.getLongitude())) + " Latitude: " + new String(String.valueOf(lastKnownLocation.getLatitude()));

        TextView location = (TextView) findViewById(R.id.textView6);
        location.setText(msg);
    }

    public void show_map(View view) {
        if (lastKnownLocation == null)
            return;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/maps/place/"+String.valueOf(lastKnownLocation.getLatitude()+","+String.valueOf(lastKnownLocation.getLongitude()))));
        startActivity(browserIntent);
    }

    public void refresh_location(View view) {
        String recipient =  ((TextView)findViewById(R.id.editTextTextEmailAddress)).getText().toString();
        if (recipient == login) {
            refresh_location();
        } else {
            UrlRunner runner = new UrlRunner();
            runner.login = login;
            runner.password = password;
            runner.server = server;
            runner.callback = new LocationLoader();
            runner.command = UrlRunner.ServerCmd.GET_LOCATION;
            runner.recipient = recipient;
            executorService.execute(runner);
        }
    }

    public void back(View view) {
        Intent i = new Intent(this, MainActivity2.class);
        i.putExtra("login", login);
        i.putExtra("password", password);
        i.putExtra("register", false);
        i.putExtra("server", server);
        startActivity(i);
    }
}