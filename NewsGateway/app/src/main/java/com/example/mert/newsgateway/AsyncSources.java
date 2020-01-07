package com.example.mert.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class AsyncSources extends AsyncTask <String, Integer, String> {
    private static String Url = "https://newsapi.org/v2/sources?language=en&country=us&apiKey=2dafacd24b0340909b5bf41266cedbed";
    private MainActivity mainAct;

    public AsyncSources(MainActivity mainAct) {
        this.mainAct = mainAct;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();

        try {
            Uri uri = Uri.parse(Url);
            URL url = new URL(uri.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line).append('\n');
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        this.parseJSON(s);
    }

    private void parseJSON(String s) {
        try {
            ArrayList<NewsSource> newsSourceArrayList = new ArrayList<>();
            ArrayList<String> categoryList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("sources");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = new JSONObject(jsonArray.getString(i));
                NewsSource newsSource = new NewsSource(jObject.getString("id"),
                        jObject.getString("name"),
                        jObject.getString("description"),
                        jObject.getString("url"),
                        jObject.getString("category"));
                newsSourceArrayList.add(newsSource);

                if (categoryList.indexOf(jObject.getString("category")) == -1) {
                    categoryList.add(jObject.getString("category"));
                }
            }
            this.mainAct.addSource(newsSourceArrayList, categoryList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}