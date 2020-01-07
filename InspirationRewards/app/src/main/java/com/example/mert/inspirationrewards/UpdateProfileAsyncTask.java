package com.example.mert.inspirationrewards;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateProfileAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "UpdateProfileAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/profiles";
    private CreateProfileBean bean;
    private List<RewardRecords> rewardArrayList = new ArrayList<>();
    private EditActivity editActivity;
    private JSONArray rewardsJsonArr;
    private int code = -1;

    public UpdateProfileAsyncTask(EditActivity editActivity, CreateProfileBean bean, List<RewardRecords> rewardArrayList) {

        this.editActivity = editActivity;
        this.bean = bean;
        this.rewardArrayList = rewardArrayList;
    }

    @Override
    protected String doInBackground(Void... voids) {
        JSONObject json = new JSONObject();
        String studentId = bean.studentId;
        String username = bean.username;
        String password = bean.password;
        String firstName = bean.firstName;
        String lastName = bean.lastName;
        int pointsToAward = bean.pointsToAward;
        String story = bean.story;
        String department = bean.department;
        String position = bean.position;
        boolean admin = bean.admin;
        String location = bean.location;
        String imageBytes = bean.imageBytes;
        rewardsJsonArr = new JSONArray();
        if (!rewardArrayList.isEmpty()) {
            for (int i = 0; i < rewardArrayList.size(); i++) {
                JSONObject rewJson = new JSONObject();
                RewardRecords r = rewardArrayList.get(i);
                rewardsJsonArr.put(rewJson);
            }
        }

        Log.d(TAG, "doInBackground: UpdateProfile" + studentId + username + password + firstName + lastName + pointsToAward + department + position + admin + location + imageBytes);
        try {
            json.put("studentId", studentId);
            json.put("username", username);
            json.put("password", password);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("pointsToAward", pointsToAward);
            json.put("department", department);
            json.put("story", story);
            json.put("position", position);
            json.put("admin", admin);
            json.put("location", location);
            json.put("imageBytes", imageBytes);
            if (rewardArrayList.size() == 0)
                json.put("rewardRecords", "[]");
            else
                json.put("rewardRecords", rewardsJsonArr);

            return doAPICall(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {

            String urlString = baseUrl + loginEndPoint;

            Uri uri = Uri.parse(urlString);
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int responseCode = conn.getResponseCode();

            StringBuilder result = new StringBuilder();
            Log.d(TAG, "doAPICall: UpdateProfile" + responseCode);
            if (responseCode == HTTP_OK) {
                code = responseCode;
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                return result.toString();

            } else {
                code = responseCode;
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                return result.toString();
            }

        } catch (Exception e) {
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally {
            if (conn != null) {
                conn.disconnect();
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
        Log.d(TAG, "onPostExecute: UpdateProfile" + connectionResult);
        if (code == HTTP_OK)
            editActivity.getEditProfileAPIResp("SUCCESS");
        else if (code == -1)
            editActivity.getEditProfileAPIResp("Some other error occured");
        else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                editActivity.getEditProfileAPIResp(errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}