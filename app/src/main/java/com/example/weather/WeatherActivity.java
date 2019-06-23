package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity implements Runnable{
    private final String TAG = "main";
    public String updateDate, todayStr;
    Handler handler;
    TextView time;
    String ziwaixian;
    String PM;
    String temp;
    String weather;
    TextView degree;
    TextView weather_info;
    TextView clothes_text;

    TextView wrap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences=getSharedPreferences("myweather",Activity.MODE_PRIVATE);
        ziwaixian=sharedPreferences.getString("ziwaixian","");
        PM=sharedPreferences.getString("PM","");
        temp=sharedPreferences.getString("temp","");
        weather=sharedPreferences.getString("weather","");

        degree=findViewById(R.id.degree_text);
        weather_info=findViewById(R.id.detail);
        clothes_text=findViewById(R.id.clothes_text);

//        wrap=findViewById(R.id.wrap);

        degree.setText(ziwaixian);  //degree_text温度
        weather_info.setText(weather);

//        wrap.setText(ziwaixian);

        time = findViewById(R.id.title_update_time);
        todayStr = time.getText().toString();


        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        time.setText(todayStr);
        Log.i(TAG, "onCreate:sp update_date= " + updateDate);
        Log.i(TAG, "onCreate:sp todayStr= " + todayStr);

        Thread t=new Thread(this);

        t.start();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    Bundle bdl= (Bundle) msg.obj;
                    ziwaixian=bdl.getString("ziwaixian");
                    PM=bdl.getString("PM");
                    temp=bdl.getString("temp");
                    weather=bdl.getString("weather");

                    SharedPreferences sharedPreferences=getSharedPreferences("myweather",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();

                    editor.putString("ziwaixian",ziwaixian);
                    editor.putString("PM",PM);
                    editor.putString("temp",temp);
                    editor.putString("weather",weather);
                    editor.commit();
                    Log.i(TAG, "onActivityResult: 数据已保存至sp");


                }

                super.handleMessage(msg);
            }
        };
        }
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.notelist,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.note){
            Intent intent = new Intent(WeatherActivity.this, NoteActivity.class);
            startActivity(intent);

        }





        return super.onOptionsItemSelected(item);
    }

        public void run () {


            Bundle bundle = new Bundle();



            Message msg = handler.obtainMessage(5);
            bundle=getWeather();
            msg.obj = bundle;
            handler.sendMessage(msg);


        }
        private Bundle getWeather () {
            Bundle bundle = new Bundle();
            org.jsoup.nodes.Document doc1 = null;
            Document doc2 = null;


            try {

                doc1 = Jsoup.connect("http://www.tianqi.com/chengdu/?qd=tq15").get();
                doc2 = Jsoup.connect("http://www.weather.com.cn/weather1d/101270101.shtml").get();

                Elements shidu = doc1.getElementsByClass("shidu");
                Elements kongqi = doc1.getElementsByClass("kongqi");
                final Elements Weather = doc1.getElementsByTag("span");
                final Elements shenghuo = doc2.getElementsByTag("ul");

                final Element zwx=shidu.get(0);
                final Element pm=kongqi.get(0);
                final Element wendu=Weather.get(2);
                final Element jianyi=shenghuo.get(7);



                Log.i(TAG, "getWeather: shidu"+zwx);
                Log.i(TAG, "getWeather: kongqi"+pm);
                Log.i(TAG, "getWeather: weather"+Weather);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 更新UI的操作
                        degree.setText(wendu.text());
                        weather_info.setText(zwx.text()+"\n"+pm.text());
                        clothes_text.setText(jianyi.text());
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bundle;
        }


    }


