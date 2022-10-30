package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity3 extends AppCompatActivity {
    public String login;
    public String password;
    public String server;
    public ConditionVariable sent;
    public ExecutorService executorService;
    public class MessageSender implements UrlRunner.UrlResultCallback {
        public String res;
        public ConditionVariable sent;
        public MessageSender(ConditionVariable sent) {
            this.sent = sent;
        }
        @Override
        public void ResultCallback(String result_str) {
            res = result_str;
            sent.open();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        executorService = Executors.newFixedThreadPool(4);
        Intent i = getIntent();
        login = i.getStringExtra("login");
        password = i.getStringExtra("password");
        server = i.getStringExtra("server");
        sent = new ConditionVariable(false);
    }

    public void send(View view) {
        UrlRunner runner = new UrlRunner();
        MessageSender snd = new MessageSender(sent);
        runner.callback = snd;
        runner.command = UrlRunner.ServerCmd.SEND;
        runner.login = login;
        runner.password = password;
        runner.server = server;
        String recipient = ((EditText) findViewById(R.id.textView5)).getText().toString();
        String message = ((EditText) findViewById(R.id.textView4)).getText().toString();
        runner.recipient = recipient;
        runner.message = message;
        executorService.execute(runner);
        Intent i = new Intent(this, MainActivity2.class);
        i.putExtra("login", login);
        i.putExtra("password", password);
        i.putExtra("register", false);
        i.putExtra("server", server);
        try {
            sent.block();
        } catch (Exception e) {

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(snd.res).setTitle("Sending message...").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int o) {
                startActivity(i);
            }
        });
        AlertDialog dlg = builder.create();
        dlg.show();
    }

    public void cancel(View view) {
        Intent i = new Intent(this, MainActivity2.class);
        i.putExtra("login", login);
        i.putExtra("password", password);
        i.putExtra("register", false);
        i.putExtra("server", server);
        startActivity(i);
    }
}