package com.example.myapplication;

import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UrlRunner implements Runnable {
    public interface UrlResultCallback {
        public void ResultCallback(String result_str);
    }
    public enum ServerCmd {
        REGISTER,
        RECV,
        RECV_OUTBOX,
        SEND,
        CLEAR,
        CLEARALL
    }
    public String s;
    public String login;
    public String password;
    public String message;
    public String recipient;
    public String server;
    public boolean use_http_post;
    public UrlResultCallback callback;
    public ServerCmd command;
    public UrlRunner() {
        login = "";
        password = "";
        message = "";
        recipient = "";
        command = ServerCmd.RECV;
        callback = null;
        use_http_post = true;
    }
    private void writeStream(OutputStream stream, String s) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
        BufferedWriter w = new BufferedWriter(writer);
        w.write(s);
        w.flush();
        writer.flush();
    }
    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }
    private String downloadUrlGet(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 500);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
    private String downloadUrlPost(URL url, String request) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();

            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("id","Value");
            connection.setDoOutput(true);

            // Open communications link (network traffic occurs here).
            OutputStream outputPost = new BufferedOutputStream(connection.getOutputStream());
            writeStream(outputPost, request);
            outputPost.flush();
            outputPost.close();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 500);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
    @Override
    public void run() {
        try {
            String addr = server + "/?mailbox_new=%22%22";
            String cmd_str="";
            switch (command)
            {
                case REGISTER:
                    cmd_str = "&register=%22%22";
                    break;
                case RECV:
                    cmd_str = "&recv=%22%22";
                    break;
                case RECV_OUTBOX:
                    cmd_str = "&sent=%22%22";
                    break;
                case CLEAR:
                    cmd_str = "&clear=%22%22";
                    break;
                case CLEARALL:
                    cmd_str = "&clearall=%22%22";
                    break;
                case SEND:
                    cmd_str = "&send=%22%22";
                    cmd_str += "&to=%22" + recipient + "%22";
                    cmd_str += "&message=%22" + message + "%22";
                    break;
            }
            String login_str = "&id=%22" + login + "%22";
            String pass;
            if (command != ServerCmd.REGISTER) {
                pass = "&hash=%22" + password + "%22";
            } else {
                pass = "&pass=%22" + password + "%22";
                pass += "&hash=%22" + password + "%22";
            }
            if (use_http_post)
                s = this.downloadUrlPost(new URL(addr), cmd_str + login_str + pass);
            else
                s = this.downloadUrlGet(new URL(addr + cmd_str + login_str + pass));
        } catch(Exception e) {
            s = "";
        }
        if (callback != null)
            callback.ResultCallback(s);
    }
}