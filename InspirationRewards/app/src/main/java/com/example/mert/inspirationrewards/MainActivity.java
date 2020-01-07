package com.example.mert.inspirationrewards;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String studentId = "A20374655";

    private static final int requestCode = 1;
    public static final int requestPerms = 1;

    private RewardsPreferences preferences;
    private LocationManager locationManager;
    private EditText editUsername;
    private EditText editPassword;
    private CheckBox credChkBox;
    private ProgressBar progressBar;
    private Criteria criteria;
    MainActivity mainActivity = this;
    private ArrayList<RewardRecords> rewardsArrList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);
        setTitle("  Rewards");

        if (isOnline())
            setFileLocationPermissions();
        else
            errorDialog("errorDialog: No Internet Connectivity!!", "No Internet Connection", "Cannot start application");
        editUsername = (EditText) findViewById(R.id.usernameView);
        editPassword = (EditText) findViewById(R.id.passwordView);
        credChkBox = (CheckBox) findViewById(R.id.rememberCheck);
        progressBar=findViewById(R.id.progressBar);
        preferences = new RewardsPreferences(this);

        Log.d(TAG, "onCreate: " + preferences.getValue(getString(R.string.userPreferences)));
        editUsername.setText(preferences.getValue(getString(R.string.userPreferences)));
        editPassword.setText(preferences.getValue(getString(R.string.passwordPreferences)));
        credChkBox.setChecked(preferences.getBoolValue(getString(R.string.checkPreferences)));
    }

    public void setFileLocationPermissions() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        int permLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permExt = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permLoc != PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permExt != PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestPerms);
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void errorDialog(String logStmt, String title, String message) {
        int d = Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onLoginBtnClick(View v) {

        if (credChkBox.isChecked()) {
            Log.d(TAG, ":onLoginBtnClick ");
            preferences.save(getString(R.string.userPreferences), editUsername.getText().toString());
            preferences.save(getString(R.string.passwordPreferences), editPassword.getText().toString());
            preferences.saveBool(getString(R.string.checkPreferences), credChkBox.isChecked());
        }
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        if (username.equals("") || password.equals("")) {
            errorDialog("errorDialog: incomplete input fields!!", "Incomplete Input Fields", "Please Enter Valid UserName/Password");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new LoginAPIAsyncTask(mainActivity).execute(studentId, username, password);

        }
    }

    public void onNewAccCreateClick(View v) {
        Log.d(TAG, "onNewAccCreateClick: Main");
        if (isOnline()) {
            makeCustomToast(this, "Creating a New Profile", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, CreateActivity.class);
            startActivityForResult(intent, requestCode);
        } else
            makeCustomToast(this, "No Internet Connection", Toast.LENGTH_SHORT);
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, " " + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }

    public void getLoginAPIResp(CreateProfileBean respBean, List<RewardRecords> rewardsList, String connectionResult) {
        Log.d(TAG, "getLoginAPIResp: " + respBean);
        if (respBean == null) {
            makeCustomToast(this, connectionResult, Toast.LENGTH_SHORT);
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            Log.d(TAG, "getLoginAPIResp: " + respBean.getUsername() + respBean.getFirstName() + respBean.getLastName() + respBean.getLocation() + respBean.getDepartment() + respBean.getPassword() + respBean.getPosition() + respBean.getStory() + respBean.getPointsToAward());

            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("USERPROFILE", respBean);
            intent.putExtra("USERPROFILE_LIST", (Serializable) rewardsList);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);

        }
    }
}