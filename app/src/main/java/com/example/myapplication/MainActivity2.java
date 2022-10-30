package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {
    public ExecutorService executorService;
    public String login;
    public String password;
    public String server;
    public boolean register;
    public class PassThroughLoader implements UrlRunner.UrlResultCallback {
        @Override
        public void ResultCallback(String result_str) {
            return;
        }
    }
    public class InboxLoader implements UrlRunner.UrlResultCallback {
        public class Message {
            public String date;
            public String from;
            public String to;
            public String message;
            int message_start;
            int message_end;
        };
        @Override
        public void ResultCallback(String result_str) {
            int index;
            ArrayList<Message> messages = new ArrayList<Message>();
            TextView inbox = (TextView) findViewById(R.id.textView3);
            index = result_str.indexOf("Date:");
            while(index != -1) {
                Message msg = new Message();
                int to_index = result_str.indexOf("|To:", index);
                if (to_index == -1)
                    break;
                msg.date = result_str.substring(index, to_index);
                int from_index = result_str.indexOf("|From:", index);
                if (from_index == -1)
                    break;
                msg.to = result_str.substring(to_index, from_index);
                int message_index = result_str.indexOf("|Message:", index);
                if (message_index == -1)
                    break;
                msg.from = result_str.substring(from_index, message_index);
                index = result_str.indexOf("Date:", message_index);
                int message_end = index == -1 ? result_str.length() : index;
                msg.message = result_str.substring(message_index, message_end - 1);
                messages.add(msg);
            }
            String final_string = "";

            for (Message msg : messages)
            {
                final_string += msg.date.replace("|"," ");
                final_string += msg.from.replace("|"," ");;
                final_string += msg.to.replace("|"," ");;
                final_string += "\n";
                msg.message_start = final_string.length();
                String message = msg.message.replace("|","").replace("Message:","");
                message = message.replace("\"","");
                final_string += message;
                msg.message_end = final_string.length();
                final_string += "\n";
            }

            if (messages.isEmpty()) {
                final_string = result_str;
            }

            SpannableString spannable = new SpannableString(final_string);
            for(Message msg : messages)
            {
                spannable.setSpan(new ForegroundColorSpan(Color.YELLOW), msg.message_start, msg.message_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            inbox.setText(spannable);
        }
    }
    public InboxLoader loader;
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
        loader = new InboxLoader();
        runner.callback = loader;
        executorService = Executors.newFixedThreadPool(4);
        executorService.execute(runner);

        runner.callback = new PassThroughLoader();
        runner.command = UrlRunner.ServerCmd.SET_LOCATION;
        Location loc = LocationTracker.get_location(this);
        runner.location = String.valueOf(loc.getLongitude()) + "," + String.valueOf(loc.getLatitude());
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

    public void map(View view) {
        Intent i = new Intent(this, TrackActivity.class);
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
        runner.callback = loader;
        runner.command = UrlRunner.ServerCmd.RECV;
        executorService.execute(runner);
    }

    public void sent_folder(View view) {
        runner.callback = loader;
        runner.command = UrlRunner.ServerCmd.RECV_OUTBOX;
        executorService.execute(runner);
    }

    public void clear(View view) {
        runner.callback = loader;
        runner.command = UrlRunner.ServerCmd.CLEAR;
        executorService.execute(runner);
    }

    public void clearall(View view) {
        runner.callback = loader;
        runner.command = UrlRunner.ServerCmd.CLEARALL;
        executorService.execute(runner);
    }
}