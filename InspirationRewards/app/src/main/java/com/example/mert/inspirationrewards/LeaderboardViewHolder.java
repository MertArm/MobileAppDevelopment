package com.example.mert.inspirationrewards;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
    public TextView inspLeadName;
    public TextView inspLeadPoints;
    public TextView inspLeadPosDept;
    public ImageView inspLeadImge;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        inspLeadName = itemView.findViewById(R.id.usersName);
        inspLeadPosDept = itemView.findViewById(R.id.PosAndDept);
        inspLeadPoints = itemView.findViewById(R.id.pointCount);
        inspLeadImge = itemView.findViewById(R.id.userSmallPhoto);
    }
}