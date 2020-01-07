package com.example.mert.inspirationrewards;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {
    private static final String TAG = "RewardsAdapter";
    List<RewardRecords> rewardsArrayList = new ArrayList<>();
    private UserProfileActivity userProfileActivity;

    public RewardsAdapter(UserProfileActivity userProfileActivity, List<RewardRecords> rewardsArrayList) {
        this.userProfileActivity = userProfileActivity;
        this.rewardsArrayList = rewardsArrayList;
    }

    public UserProfileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reward_list, viewGroup, false);
        itemView.setOnClickListener(userProfileActivity);
        itemView.setOnLongClickListener(userProfileActivity);
        return new UserProfileViewHolder(itemView);
    }

    public void onBindViewHolder(UserProfileViewHolder userProfileViewHolder, int pos) {
        Log.d(TAG, "onBindViewHolder: ");
        RewardRecords rewardRecords = rewardsArrayList.get(pos);
        userProfileViewHolder.rewardDate.setText(rewardRecords.getDate());
        userProfileViewHolder.usersName.setText(rewardRecords.getName());
        userProfileViewHolder.rewardText.setText(rewardRecords.getNotes());
        userProfileViewHolder.pointsView.setText("" + rewardRecords.getValue());
    }

    public int getItemCount() {
        return rewardsArrayList.size();
    }
}