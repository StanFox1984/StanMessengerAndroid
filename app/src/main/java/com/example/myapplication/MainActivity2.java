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

public class MainActivity2 extends AppCompatActivity {
    public ExecutorService executorService;
    public String login;
    public String password;
    public String server;
    public boolean register;
    public class InboxLoader implements UrlRunner.UrlResultCallback {
        @Override
        public void ResultCallback(String result_str) {
            TextView inbox = (TextView) findViewById(R.id.textView3);
            inbox.setText(result_str);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent i = getIntent();
        login = i.getStringExtra("login");
        password = i.getStringExtra("password");
        register = i.getBooleanExtra("register", false);
        server = i.getStringExtra("server");
        runner = new UrlRunner();
        runner.login = login;
        runner.password = password;
        runner.server = server;
        if (register)
            runner.command = UrlRunner.ServerCmd.REGISTER;
        else
            runner.command = UrlRunner.ServerCmd.RECV;
        runner.callback = new InboxLoader();
        executorService = Executors.newFixedThreadPool(4);
        executorService.execute(runner);
    }

    public UrlRunner runner;
    public void send_message(View view) {
        Intent i = new Intent(this, MainActivity3.class);
        i.putExtra("login", login);
        i.putExtra("password", password);
        i.putExtra("server", server);
        startActivity(i);
    }

    public void logout(View view) {
        login = "";
        password = "";
        register = false;
        server = "";
        startActivity(new Intent(this, MainActivity.class));
    }

    public void refresh(View view) {
        runner.command = UrlRunner.ServerCmd.RECV;
        executorService.execute(runner);
    }

    public void sent_folder(View view) {
        runner.command = UrlRunner.ServerCmd.RECV_OUTBOX;
        executorService.execute(runner);
    }

    public void clear(View view) {
        runner.command = UrlRunner.ServerCmd.CLEAR;
        executorService.execute(runner);
    }

    public void clearall(View view) {
        runner.command = UrlRunner.ServerCmd.CLEARALL;
        executorService.execute(runner);
    }
}