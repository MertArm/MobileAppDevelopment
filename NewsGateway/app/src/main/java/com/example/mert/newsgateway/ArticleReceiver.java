package com.example.mert.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

public class ArticleReceiver extends BroadcastReceiver {
    private ArrayList<Article> arrayList;
    public ArticleReceiver(ArrayList<Article> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null)  {
            return;
        }
        if (intent.getAction().equals(MainActivity.broadcastReq)) {
            AsyncArticles asyncArticles = new AsyncArticles(intent.getStringExtra("id"), this.arrayList);
            asyncArticles.execute();
        }
    }
}