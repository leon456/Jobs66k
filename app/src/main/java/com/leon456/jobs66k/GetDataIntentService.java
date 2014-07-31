package com.leon456.jobs66k;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetDataIntentService extends Service {
    private static final String TAG = "GetDataIntentService";
    private SharedPreferences settings;
    private Handler handler = new Handler();
    private boolean hasNew = false;
    private String title;
    private String content;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(showTime, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        super.onDestroy();
    }

    private Runnable showTime = new Runnable() {
        public void run() {
            settings = getSharedPreferences(getString(R.string.app_name), 0);
            //log目前時間
            final String[] categories = getResources().getStringArray(R.array.categories);


            Ion.with(GetDataIntentService.this).load("http://66kjobs.tw/").asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Document doc = Jsoup.parse(result);

                            Elements els = doc.select("#myTabContent");
                            Element myTabContent = els.first();
                            Elements tables = myTabContent.select("table");
                            Loop:
                            for (int i = 0; i < tables.size(); i++) {
                                Element table = tables.get(i);
                                Elements trs = table.select("tr");
                                List<HashMap<String, String>> job = new ArrayList<HashMap<String, String>>();


                                for (Element tr : trs) {
                                    Elements tds = tr.select("td");

                                    HashMap<String, String> data = new HashMap<String, String>();
                                    job.add(data);
                                    for (int j = 0; j < tds.size(); j++) {
                                        Element td = tds.get(j);

                                        switch (j) {
                                            case 0:
                                                data.put("title", td.text());
                                                if (td.text().trim().contains("NEW")) {
                                                    Log.i(TAG, "******Get New*******");
                                                    Log.i(TAG, td.text());
                                                    hasNew = true;
                                                    title = categories[i];
                                                    content = td.text() + "-" + tds.get(1).text();
                                                    break Loop;
                                                }

                                                break;
                                            case 1:
                                                data.put("job", td.text());

                                                break;
                                            case 2:
                                                data.put("date", td.text());

                                                break;
                                        }
                                    }
                                }
                            }

                            if (hasNew) {
                                if(settings.getString(content,null)==null)
                                    sendNotification(title, content);
                            }
                        }
                    });

            handler.postDelayed(this, 24 * 60 * 60 * 1000);
        }
    };


    private void sendNotification(String title,String content){
        SharedPreferences.Editor editor =  settings.edit();
        editor.putString(content,content);
        editor.commit();

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //設定當按下這個通知之後要執行的activity
        Intent notifyIntent = new Intent(GetDataIntentService.this,MainActivity.class);
        notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent appIntent= PendingIntent.getActivity(GetDataIntentService.this, 0,notifyIntent, 0);
        Notification notification = new Notification();
        //設定出現在狀態列的圖示
        notification.icon= R.drawable.logo;
        //顯示在狀態列的文字
        notification.tickerText=getString(R.string.noti_title);
        //會有通知預設的鈴聲、振動、light
        notification.defaults=Notification.DEFAULT_ALL;
        //設定通知的標題、內容
        notification.setLatestEventInfo(GetDataIntentService.this,title,content,appIntent);
        //送出Notification
        notificationManager.notify(0,notification);
    }

}
