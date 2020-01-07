package com.example.mert.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView name;
    public TextView price;
    public TextView change;
    public TextView percentagechange;
    public ImageView arrow;

    public MyViewHolder(View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.symbol);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        change = itemView.findViewById(R.id.change);
        percentagechange = itemView.findViewById(R.id.percentagechange);
        arrow = itemView.findViewById(R.id.arrow);
    }
}