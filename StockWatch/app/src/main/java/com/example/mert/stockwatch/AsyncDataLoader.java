package com.example.mert.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.net.URL;
import java.util.HashMap;

public class AsyncDataLoader extends AsyncTask<Void, Void, String> {
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    public AsyncDataLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Uri dataUri = Uri.parse(DATA_URL);
        String urls = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urls);
            HttpURLConnection connections = (HttpURLConnection) url.openConnection();
            connections.setRequestMethod("GET");
            InputStream inStream = connections.getInputStream();
            BufferedReader buffreader = new BufferedReader((new InputStreamReader(inStream)));
            String line;
            while ((line = buffreader.readLine()) != null) sb.append(line).append('\n');
        } catch (Exception e) {
            return null;
        }

        return sb.toString();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        HashMap<String,String> symbolNameList = parseJSON(s);
        mainActivity.updateData(symbolNameList);
    }

    private HashMap<String,String> parseJSON(String s) {

        HashMap<String,String> nameSymbolMap = new HashMap<>();
        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jSymbolName = (JSONObject) jObjMain.get(i);
                String symbol = jSymbolName.getString("symbol");
                String name = jSymbolName.getString("name");
                nameSymbolMap.put(symbol, name);
            }
            return nameSymbolMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
