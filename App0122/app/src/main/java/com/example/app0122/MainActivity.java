package com.example.app0122;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getName();
    ViewGroup wrapper;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//이 메서드 자체가 이미 inflation 기능이 들어있다!!!

        //우리의 경우 아래의 인플레이션이 발생한 후, profile_item.xml의 최상위 레이아웃이 반환된다.
        //인플레이션이 종료된 후, 생성된 객체들의 정보를 이용해서 접근
        handler = new Handler(){
            public void handleMessage(@NonNull Message msg) {
                //여기서 메인 쓰레드로  할 수 있는 작업을 수행

                Bundle bundle = msg.getData();

                printData(bundle.getString("data"));
            }
        };
    }

    //게시물 출력하기(서버로 부터 받은 Json String 데이터를 넘겨받아 출력)
    public void printData(String data) {
        Log.d(TAG, "핸들러로부터 전달받은 데이터는 "+data);
        //이 시점 부터는 String을 객체화시켜서 사용해야 한다!!
        List<Member> memberList = new ArrayList<Member>();


        try {
            JSONArray jsonArray = new JSONArray(data);
            Log.d(TAG, "제이슨 배열의 길이는 "+jsonArray.length());
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject json = (JSONObject) jsonArray.get(i);

                Member member = new Member();
                member.setM_id((String)json.get("m_id"));
                member.setM_pass((String)json.get("m_pass"));
                member.setM_name((String)json.get("m_name"));

                memberList.add(member);//리스트에 멤버 추가!!

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //재사용하기 위해 미리 정의해놓은 레이아웃 파일을 인플레이션 시켜본다.
        //inflation 이란? xml에서 정의해 놓은 태그들을 실제 자바 안드로이드 객체로 인스턴스화 시키는 과정
        LayoutInflater layoutInflater = this.getLayoutInflater();

        wrapper = (ViewGroup) this.findViewById(R.id.wrapper);

        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            ViewGroup root_wrapper = (ViewGroup) layoutInflater.inflate(R.layout.profile_item, wrapper);//최상위 view가 반환

            //profiled의 LinearLayout에 접근
            ViewGroup profile_root = (ViewGroup) root_wrapper.getChildAt(i);

            ViewGroup text_root = (ViewGroup) profile_root.getChildAt(1);

            TextView t_id = (TextView) text_root.getChildAt(0);//아이디 텍스트뷰
            TextView t_pass = (TextView) text_root.getChildAt(1);//비밀번호 텍스트뷰
            TextView t_name = (TextView) text_root.getChildAt(2);//이름 텍스트뷰

            t_id.setText(member.getM_id());
            t_pass.setText(member.getM_pass());
            t_name.setText(member.getM_name());

        }
    }

    public void loadData(View view){//반드시 View가 있어야 xml의 클릭과 연결이 된다.
        Log.d(TAG, "A");

        //쓰레드는 하나의 프로세스내에서 독립적으로(또한 비동기적 특징도 있음...) 실행되는, 또하나의 세부 실행단위
        //통신 쓰레드를 동작시키자!!
        ConnectManager manager = new ConnectManager(this, "http://192.168.0.8:6060/rest/member", null);
        manager.start();

        //화면 갱신
        Log.d(TAG, "C");
    }
}