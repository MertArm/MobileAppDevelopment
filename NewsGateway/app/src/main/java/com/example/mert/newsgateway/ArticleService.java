package com.example.mert.newsgateway;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import java.util.ArrayList;

public class ArticleService extends Service {

    private boolean isRunning = true;
    private ArrayList<Article> arrayList = new ArrayList<>();

    public ArticleService() { }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArticleReceiver articleReceiver = new ArticleReceiver(arrayList);
                    IntentFilter filter = new IntentFilter(MainActivity.broadcastReq);
                    registerReceiver(articleReceiver, filter);

                    while(isRunning) {
                        while(arrayList.isEmpty()) {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.broadcastRec);
                        intent.putExtra("articleList", arrayList);
                        sendBroadcast(intent);
                        arrayList.clear();
                    }
                }
            }).start();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}