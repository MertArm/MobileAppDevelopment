package com.example.mert.inspirationrewards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileViewHolder extends RecyclerView.ViewHolder {
    public TextView rewardDate;
    public TextView usersName;
    public TextView pointsView;
    public TextView rewardText;
    public ImageView userPic;

    public UserProfileViewHolder(View itemView) {
        super(itemView);
        rewardDate = itemView.findViewById(R.id.date);
        usersName = itemView.findViewById(R.id.name);
        pointsView = itemView.findViewById(R.id.pointsView);
        rewardText = itemView.findViewById(R.id.rewardText);
        userPic = itemView.findViewById(R.id.picView);
    }
}