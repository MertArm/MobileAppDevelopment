package com.example.mert.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<Stock> stocklist;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> stocklist, MainActivity ma) {
        this.stocklist = stocklist;
        mainActivity = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock = stocklist.get(position);

        if(stock.getPriceChange() > 0){
            holder.symbol.setTextColor(Color.GREEN);
            holder.name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
            holder.percentagechange.setTextColor(Color.GREEN);
            holder.arrow.setColorFilter(Color.GREEN);
            holder.arrow.setImageResource(R.drawable.up_arrow);
        }
        else{
            holder.symbol.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
            holder.percentagechange.setTextColor(Color.RED);
            holder.arrow.setColorFilter(Color.RED);
            holder.arrow.setImageResource(R.drawable.up_arrow);
        }

        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        holder.price.setText(String.format(Locale.US, "%.2f", stock.getPrice()));
        holder.change.setText(String.format(Locale.US, "%.2f", stock.getPriceChange()));
        holder.percentagechange.setText(String.format(Locale.US, "(%.2f%%)", stock.getChangePercent()));
    }

    @Override
    public int getItemCount() {
        return stocklist.size();
    }
}