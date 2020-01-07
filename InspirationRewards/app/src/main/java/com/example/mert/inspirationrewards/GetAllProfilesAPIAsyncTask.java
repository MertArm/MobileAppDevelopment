package com.example.mert.inspirationrewards;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static java.net.HttpURLConnection.HTTP_OK;

public class GetAllProfilesAPIAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetAllProfilesAPIAsyncTask";
    private LeaderboardActivity leaderboardActivity;

    private final String baseURL = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private final String loginURL = "/allprofiles";
    private List<LeaderboardBean> inspLeaderArrayList = new ArrayList<>();
    int code = -1;

    public GetAllProfilesAPIAsyncTask(LeaderboardActivity leaderboardActivity) {
        this.leaderboardActivity = leaderboardActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String studentId = strings[0];
        String username = strings[1];
        String password = strings[2];

        try {
            JSONObject json = new JSONObject();
            json.put("studentId", studentId);
            json.put("username", username);
            json.put("password", password);

            Log.d(TAG, "doInBackground: " );
            return doAPICall(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseURL + loginURL;

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
        Log.d(TAG, "onPostExecute: " );
        LeaderboardBean bean;
        if (code == HTTP_OK) {
            try {
                JSONArray jsonArray = new JSONArray(connectionResult);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    String studentId = json.getString("studentId");
                    String username = json.getString("username");
                    String password = json.getString("password");
                    String firstName = json.getString("firstName");
                    String lastName = json.getString("lastName");
                    int pointsToAward = json.getInt("pointsToAward");
                    String department = json.getString("department");
                    String story = json.getString("story");
                    String position = json.getString("position");
                    boolean admin = json.getBoolean("admin");
                    String location = json.getString("location");
                    String imageBytes = json.getString("imageBytes");
                    String rwrd = json.getString("rewards");

                    List<RewardRecords> rewardsList = new ArrayList<>();

                    if (rwrd != "null") {
                        JSONArray rewards = new JSONArray(rwrd);
                        for (int j = 0; j < rewards.length(); j++) {
                            RewardRecords rewardRecordsBean = new RewardRecords();
                            JSONObject reward = rewards.getJSONObject(j);
                            String studentIdR = reward.getString("studentId");
                            String usernameR = reward.getString("username");
                            String nameR = reward.getString("name");
                            String notesR = reward.getString("notes");
                            String dateR = reward.getString("date");
                            int valueR = reward.getInt("value");
                            rewardRecordsBean.setStudentId(studentIdR);
                            rewardRecordsBean.setUsernameRecord(usernameR);
                            rewardRecordsBean.setName(nameR);
                            rewardRecordsBean.setDate(dateR);
                            rewardRecordsBean.setNotes(notesR);
                            rewardRecordsBean.setValue(valueR);
                            rewardsList.add(rewardRecordsBean);
                        }
                        bean = new LeaderboardBean(studentId, username, password, firstName, lastName, pointsToAward, department, story, position, admin, location, imageBytes, rewardsList);
                    }
                    else
                        bean = new LeaderboardBean(studentId, username, password, firstName, lastName, pointsToAward, department, story, position, admin, location, imageBytes, null);
                    inspLeaderArrayList.add(bean);
                }
                leaderboardActivity.getAllProfilesAPIResp(inspLeaderArrayList,"SUCCESS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (code == -1)
            leaderboardActivity.getAllProfilesAPIResp(null,"Some Other Error Occured");
        else
        {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                leaderboardActivity.getAllProfilesAPIResp(null,errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



    }
}
