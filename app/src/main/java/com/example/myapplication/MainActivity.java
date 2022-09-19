package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void login(View view) {
        Intent i = new Intent(this, MainActivity2.class);
        EditText login = (EditText) findViewById(R.id.textView);
        EditText password = (EditText) findViewById(R.id.textView2);
        EditText server = (EditText) findViewById(R.id.editTextServerAddress);
        String login_str = login.getText().toString();
        String password_str = password.getText().toString();
        i.putExtra("login", login_str);
        i.putExtra("password", password_str);
        i.putExtra("register", false);
        i.putExtra("server", server.getText().toString());
        startActivity(i);
    }

    public void register(View view) {
        Intent i = new Intent(this, MainActivity2.class);
        EditText login = (EditText) findViewById(R.id.textView);
        EditText password = (EditText) findViewById(R.id.textView2);
        EditText server = (EditText) findViewById(R.id.editTextServerAddress);
        String login_str = login.getText().toString();
        String password_str = password.getText().toString();
        i.putExtra("login", login_str);
        i.putExtra("password", password_str);
        i.putExtra("register", true);
        i.putExtra("server", server.getText().toString());
        startActivity(i);
    }
}