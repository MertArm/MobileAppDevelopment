package com.example.mert.inspirationrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    private static final String TAG = "InspirationRewardsLeaderboardAdapter";
    private List<LeaderboardBean> inspLeaderArrayList;
    private LeaderboardActivity LeaderboardActivity;
    public LeaderboardAdapter(LeaderboardActivity LeaderboardActivity, List<LeaderboardBean> inspLeaderArrayList) {
        this.inspLeaderArrayList = inspLeaderArrayList;
        this.LeaderboardActivity = LeaderboardActivity;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.leaderboard_viewholder, viewGroup, false);
        itemView.setOnClickListener(LeaderboardActivity);
        itemView.setOnLongClickListener(LeaderboardActivity);
        return new LeaderboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder inspLeadViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        LeaderboardBean inspLeaderBean = inspLeaderArrayList.get(position);
        inspLeadViewHolder.inspLeadName.setText(inspLeaderBean.getLastName() + ", " + inspLeaderBean.getFirstName());
        inspLeadViewHolder.inspLeadPosDept.setText(inspLeaderBean.getPosition() + ", " + inspLeaderBean.getDepartment());
        inspLeadViewHolder.inspLeadPoints.setText(""+inspLeaderBean.rewardPtAward);
        String imgString = inspLeaderBean.getImageBytes();
        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        inspLeadViewHolder.inspLeadImge.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {

        return inspLeaderArrayList.size();
    }
}
