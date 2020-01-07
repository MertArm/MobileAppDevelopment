package com.example.mert.inspirationrewards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "LeaderboardActivity";
    private static final int INSP_REQUEST_CODE = 1;
    private static final int INSP_TO_REWAED_REQUEST_CODE = 2;
    private RecyclerView recyclerView;
    private final List<LeaderboardBean> inspLeaderArrayList = new ArrayList<>();
    CreateProfileBean bean = null;
    private int pointsToAward = 0;
    private LeaderboardAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_with_logo);
        setTitle("  Inspiration Leaderboard");
        recyclerView = findViewById(R.id.recycler);
        leaderboardAdapter = new LeaderboardAdapter(this, inspLeaderArrayList);
        recyclerView.setAdapter(leaderboardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        if (intent.hasExtra("INSPLEADPROFILE")) {
            bean = (CreateProfileBean) intent.getSerializableExtra("INSPLEADPROFILE");
            Log.d(TAG, "getInspLeaderboard: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {

                pointsToAward = bean.getPointsToAward();
                Log.d(TAG, "onCreate: PointsToAward onCreate" + pointsToAward);
                new GetAllProfilesAPIAsyncTask(LeaderboardActivity.this).execute(bean.getStudentId(), bean.getUsername(), bean.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        LeaderboardBean beanRec = inspLeaderArrayList.get(pos);
        Log.d(TAG, "onClick: Insp" + bean.getStudentId() + " " + bean.getUsername() + " " + bean.getPassword() + " ");
        Intent intent = new Intent(this, Reward.class);
        intent.putExtra("AWARDPROFILE", beanRec);
        intent.putExtra("STUDENTIDLOGIN", bean.getStudentId());
        intent.putExtra("PASSLOGIN", bean.getPassword());
        intent.putExtra("UNAMELOGIN", bean.getUsername());
        intent.putExtra("NAMELOGIN", bean.getFirstName() + " " + bean.getLastName());
        startActivityForResult(intent, INSP_TO_REWAED_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Insp: " + requestCode + " " + resultCode);
        if (requestCode == INSP_TO_REWAED_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getIntentFromRewardToInsp(data);
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
    }

    public void getIntentFromRewardToInsp(Intent data) {
        if (data.hasExtra("STIDLOGIN")) {
            Log.d(TAG, "getInspLeaderboard: to refresh Leaderboard");
            try {
                new GetAllProfilesAPIAsyncTask(LeaderboardActivity.this).execute(data.getStringExtra("STIDLOGIN")
                        , data.getStringExtra("USERLOGIN"), data.getStringExtra("PSSLOGIN"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void getAllProfilesAPIResp(List<LeaderboardBean> respBeanList, String connectionResult) {
        inspLeaderArrayList.clear();
        Log.d(TAG, "getAllProfilesAPIResp: " + connectionResult);
        if (respBeanList != null) {
            for (int i = 0; i < respBeanList.size(); i++) {
                if (bean.getUsername().trim().equals(respBeanList.get(i).getUsername().trim())) {
                    pointsToAward = respBeanList.get(i).getPointsToAward();
                    Log.d(TAG, "getAllProfiles: PointsToAward" + pointsToAward);
                    break;
                }
            }
        }
        if (respBeanList != null) {
            for (int i = 0; i < respBeanList.size(); i++) {
                Log.d(TAG, "getAllProfilesAPIResp: Usernames: " + respBeanList.size()
                        + respBeanList.get(i).getUsername());
            }
        }
        Collections.sort(respBeanList);
        inspLeaderArrayList.addAll(respBeanList);
        leaderboardAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: Up Navigation");
                makeCustomToast(this, "Going Back To Profile Page", Toast.LENGTH_SHORT);
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("POINTSTOAWARD", "" + pointsToAward);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        makeCustomToast(this, "Going Back To Profile Page", Toast.LENGTH_SHORT);
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("POINTSTOAWARD", "" + pointsToAward);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(50, 50, 50, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }
}
