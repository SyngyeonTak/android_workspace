package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getName();
    ViewGroup wrapper;

    ConnectManager manager;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(){
            public void handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                printData(bundle.getString("data"));
            }
        };
    }

    public void printData(String data){
        List<Member> memberList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(data);
            for(int i =0; i<jsonArray.length(); i++){
                Member member = new Member();
                JSONObject json = (JSONObject) jsonArray.get(i);

                member.setMember_id((int)json.get("member_id"));
                member.setM_id((String)json.get("m_id"));
                member.setM_name((String)json.get("m_name"));
                member.setM_pass((String)json.get("m_pass"));

                memberList.add(member);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        LayoutInflater layoutInflater = this.getLayoutInflater();

        wrapper = (ViewGroup) this.findViewById(R.id.wrapper);

        for(int i =0; i<memberList.size(); i++){
            Member member = memberList.get(i);

            ViewGroup root_wrapper= (ViewGroup)layoutInflater.inflate(R.layout.profile_item, wrapper);
            ViewGroup profile_item = (ViewGroup) root_wrapper.getChildAt(i);

            ViewGroup text_root = (ViewGroup)profile_item.getChildAt(1);

            TextView t_id = (TextView) text_root.getChildAt(0);
            TextView t_pass = (TextView) text_root.getChildAt(1);
            TextView t_name = (TextView) text_root.getChildAt(2);

            t_id.setText(member.getM_id());
            t_pass.setText(member.getM_pass());
            t_name.setText(member.getM_name());

        }

    }

    public void loadData(View view){
        manager = new ConnectManager(this,"http://192.168.0.8:6060/rest/member");
        manager.start();
        Log.d(TAG, "드디어 첫 안드로이드 연습");

    }
}