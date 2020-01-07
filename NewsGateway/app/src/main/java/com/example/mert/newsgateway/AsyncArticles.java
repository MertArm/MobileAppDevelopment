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

public class AsyncArticles extends AsyncTask <String, Integer, String> {

    private String id;
    private ArrayList<Article> arrayList;

    public AsyncArticles(String id, ArrayList<Article> arrayList) {
        this.id = id;
        this.arrayList = arrayList;
    }

    @Override
    protected String doInBackground(String... strings) {
        String Urls = "https://newsapi.org/v2/everything?sources=" + this.id + "&language=en&pageSize=100&apiKey=2dafacd24b0340909b5bf41266cedbed";
        StringBuilder sb = new StringBuilder();

        try {
            Uri uri = Uri.parse(Urls);
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
        parseJSON(s);
    }

    private void parseJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                String author = "";
                String title = "";
                String description = "";
                String url = "";
                String date = "";
                String imageUrl = "https://softsmart.co.za/wp-content/uploads/2018/06/image-not-found-1038x576.jpg";

                if (!jObject.isNull("author")) {
                    author = jObject.getString("author");
                }

                if (!jObject.isNull("title")) {
                    title = jObject.getString("title");
                }
                if (!jObject.isNull("urlToImage")) {
                    imageUrl = jObject.getString("urlToImage");
                }

                if (!jObject.isNull("publishedAt")) {
                    date = jObject.getString("publishedAt");
                }
                if (!jObject.isNull("description")) {
                    description = jObject.getString("description");
                }

                if (!jObject.isNull("url")) {
                    url = jObject.getString("url");
                }
                arrayList.add(new Article(author, title, description, url, imageUrl, date, i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}