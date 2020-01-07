package com.example.mert.inspirationrewards;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import static java.net.HttpURLConnection.HTTP_OK;

public class RewardsAPIAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "RewardsAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";

    private static final String loginEndPoint = "/rewards";

    private RewardsBean bean;
    private Reward rewardActivity;
    private int code = -1;

    public RewardsAPIAsyncTask(Reward rewardActivity, RewardsBean bean) {

        this.rewardActivity = rewardActivity;
        this.bean = bean;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String studentIdSource = bean.studentIdSource;
        String usernameSource = bean.usernameSource;
        String passwordSource = bean.passwordSource;
        String studentIdTarget = bean.studentIdTarget;
        String usernameTarget = bean.usernameTarget;
        String nameTarget = bean.nameTarget;
        String dateTarget = bean.dateTarget;
        String notesTarget = bean.notesTarget;
        int valueTarget = bean.valueTarget;
        JSONObject sourceJson = new JSONObject();
        JSONObject targetJson = new JSONObject();
        JSONObject jsonToSend = new JSONObject();
        Log.d(TAG, "doInBackground: Rewards" + usernameSource + passwordSource + studentIdSource + studentIdTarget + usernameTarget + nameTarget + dateTarget + notesTarget + valueTarget);
        try {
            sourceJson.put("studentId", studentIdSource);
            sourceJson.put("username", usernameSource);
            sourceJson.put("password", passwordSource);
            targetJson.put("studentId", studentIdTarget);
            targetJson.put("username", usernameTarget);
            targetJson.put("name", nameTarget);
            targetJson.put("date", dateTarget);
            targetJson.put("notes", notesTarget);
            targetJson.put("value", valueTarget);

            jsonToSend.put("target", targetJson);
            jsonToSend.put("source", sourceJson);
            Log.d(TAG, "input JSON" + jsonToSend);
            return doAPICall(jsonToSend);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseUrl + loginEndPoint;
            Uri uri = Uri.parse(urlString);
            URL url = new URL(uri.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();
            Log.d(TAG, "doAPICall: rewards" + responseCode);

            if (responseCode == HTTP_OK) {
                code = responseCode;
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                return result.toString();

            } else {
                code = responseCode;
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                return result.toString();
            }

        } catch (Exception e) {
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return "Some error has occurred";
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " + connectionResult);
        if (code == HTTP_OK)
            rewardActivity.getRewardsAPIResp(connectionResult);
        else if (code == -1)
            rewardActivity.getRewardsAPIResp("Some other error");
        else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                rewardActivity.getRewardsAPIResp(errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}