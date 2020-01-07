package com.example.mert.multinote;

import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView description;
    public TextView date;

    public MyViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        date = view.findViewById(R.id.date);
    }
}
