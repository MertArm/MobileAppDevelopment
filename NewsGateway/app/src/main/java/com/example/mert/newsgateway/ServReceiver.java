package com.example.mert.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

public class ServReceiver extends BroadcastReceiver {
    private MainActivity mainAct;
    public ServReceiver(MainActivity mainAct) {
        this.mainAct = mainAct;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null)  {
            return;
        }
        if (intent.getAction().equals(MainActivity.broadcastRec)) {
            ArrayList<Article> arrayList = (ArrayList) intent.getSerializableExtra("articleList");
            mainAct.addAllFragments(arrayList);
        }
    }
}

