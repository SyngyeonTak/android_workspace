package com.example.practice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectManager extends Thread{
    String TAG = this.getClass().getName();

    URL url;
    HttpURLConnection con;
    BufferedReader buffr;


    String requestURL;
    MainActivity mainActivity;

    public ConnectManager(MainActivity mainActivity, String requestURL) {
        this.requestURL = requestURL;
        this.mainActivity = mainActivity;
    }

    public int requestByGet(){
        int code = 0;
        try {
            url = new URL(requestURL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            buffr = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String data = null;

            StringBuilder sb = new StringBuilder();

            while(true){
                data = buffr.readLine();
                if (data == null) break;
                sb.append(data);
            }

            Log.d(TAG, "읽어들인 데이터는? "+sb.toString());

            code = con.getResponseCode();
            Log.d(TAG, "code는? "+code);

            Bundle bundle = new Bundle();
            bundle.putString("data", sb.toString());

            Message message = new Message();
            message.setData(bundle);

            mainActivity.handler.sendMessage(message);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(buffr !=null) buffr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return code;
    }

    @Override
    public void run() {
        int code = requestByGet();
    }
}
