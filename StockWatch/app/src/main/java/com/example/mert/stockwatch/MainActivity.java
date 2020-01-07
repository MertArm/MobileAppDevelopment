package com.example.mert.stockwatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<Stock> stocklist = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private StockAdapter stockadapter;
    private HashMap<String,String> symbolmapping;
    private DatabaseHandler databaseHandler;
    private static String mWatch="http://www.marketwatch.com/investing/stock/";

    @Override
    public void onClick(View v) {
        final int pos = recyclerView.getChildAdapterPosition(v);
        Stock stock = stocklist.get(pos);
        String url = mWatch+stock.getSymbol();
        Intent i = new Intent((Intent.ACTION_VIEW));
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addstock, menu);
        return true;
    }

    @Override
    protected void onResume() {
        stocklist.size();
        super.onResume();
        stockadapter.notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View v) {

        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.remove);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(stocklist.get(pos).getSymbol());
                stocklist.remove(pos);
                stockadapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        TextView tv = v.findViewById(R.id.symbol);
        String symbol = tv.getText().toString();
        builder.setMessage("Delete Stock Symbol "+symbol+"?");
        builder.setTitle("Delete Stock");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    public void networkDialog(String text){
        String message;
        if(text.equals("add")){
            message = "Stocks Cannot Be Added Without A Network Connection";
        }
        else if(text.equals("update")){
            message = "Stocks Cannot Be Updated Without A Network Connection";
        }
        else{
            message = "Please Check Your Network";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean internetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (cm == null) {
            Toast.makeText(this, "Cannot Access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (activeNetwork != null && isConnected) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        stockadapter = new StockAdapter(stocklist, this);
        recyclerView.setAdapter(stockadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!internetCheck()){
                    swiper.setRefreshing(false);
                    networkDialog("update");
                }else {
                    doRefresh();
                }
            }
        });

        databaseHandler = new DatabaseHandler(this);
        new AsyncDataLoader(this).execute();
        ArrayList<Stock> tempList = databaseHandler.loadStocks();

        if(!internetCheck()){
            networkDialog("");
            for(int i=0; i<tempList.size(); i++){
                stocklist.add(tempList.get(i));
            }
            Collections.sort(stocklist, new Comparator<Stock>() {
                @Override
                public int compare(Stock lhs, Stock rhs) {
                    return lhs.getSymbol().compareTo(rhs.getSymbol());
                }
            });
            stockadapter.notifyDataSetChanged();
        }else{
            for(int i=0; i<tempList.size(); i++){
                String symbol = tempList.get(i).getSymbol();
                new AsyncSymbolLoader(this).execute(symbol);
            }
        }
    }

    public void updateData(HashMap<String,String> symbolmapping){
        if(symbolmapping!=null && !symbolmapping.isEmpty()) {
            this.symbolmapping = symbolmapping;
        }
        else{
            Toast.makeText(this, "Problem loading Name and Symbol", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EditText edittext = new EditText(this);

        if(!internetCheck()){
            networkDialog("add");
            return false;
        }
        else{
            switch (item.getItemId()) {
                case R.id.addStock:
                    if(symbolmapping == null)
                        new AsyncDataLoader(MainActivity.this).execute();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

                    edittext.setText("");
                    edittext.setInputType(InputType.TYPE_CLASS_TEXT);

                    InputFilter[] editFilters = edittext.getFilters();
                    InputFilter[] newFilters = new InputFilter[editFilters.length + 1];

                    System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                    newFilters[editFilters.length] = new InputFilter.AllCaps();

                    edittext.setFilters(newFilters);
                    editFilters = edittext.getFilters();
                    newFilters = new InputFilter[editFilters.length + 1];

                    System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                    newFilters[editFilters.length] =  new InputFilter() {
                        @Override
                        public CharSequence filter(CharSequence charseq, int i, int i1, Spanned spanned, int i2, int i3) {
                            if(charseq.equals("")){
                                return charseq;
                            }
                            if(charseq.toString().matches("[a-z A-Z 0-9]+")){
                                return charseq;
                            }
                            return "";
                        }
                    };

                    edittext.setFilters(newFilters);
                    edittext.setGravity(Gravity.CENTER_HORIZONTAL);
                    dialogBuilder.setView(edittext);
                    dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(!internetCheck()){
                                networkDialog("add");
                            }
                            else{
                                if(edittext.getText().toString().length() > 0){
                                    final ArrayList<String> stockoption = new ArrayList<>();
                                    ArrayList<String> tempList = searchForStock(edittext.getText().toString());
                                    if(!tempList.isEmpty()){
                                        stockoption.addAll(tempList);
                                        if(stockoption.size() == 1){
                                            if(duplicateStock(stockoption.get(0))){
                                                duplicateDialog(edittext.getText().toString());
                                            }
                                            else{
                                                saveStock(stockoption.get(0));
                                            }
                                        }
                                        else{
                                            AlertDialog.Builder builders = new AlertDialog.Builder(MainActivity.this);
                                            builders.setTitle("Make a selection");
                                            final String [] arr = new String[stockoption.size()];
                                            for(int i=0; i< arr.length; i++)
                                                arr[i] = stockoption.get(i);
                                            builders.setItems(arr, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(duplicateStock(arr[which])){
                                                        duplicateDialog(edittext.getText().toString());
                                                    }
                                                    else{
                                                        saveStock(arr[which]);
                                                    }
                                                }
                                            });

                                            builders.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });
                                            AlertDialog dialogs = builders.create();

                                            dialogs.show();
                                        }

                                    }
                                    else{
                                        dataNotFoundDialog(edittext.getText().toString());
                                    }
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Please Enter a valid Stock Symbol!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });

                    dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    dialogBuilder.setTitle("Stock Selection");
                    dialogBuilder.setMessage("Please enter a Stock Symbol:");
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }


    }
    public void dataNotFoundDialog(String symbol){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Data for stock symbol");
        dialogBuilder.setTitle("Symbol Not Found: "+symbol);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public void duplicateDialog(String symbol){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setIcon(R.drawable.error);
        dialogBuilder.setMessage("Stock Symbol " + symbol + " is already displayed");
        dialogBuilder.setTitle("Duplicate Stock");
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    private boolean duplicateStock(String item){

        String symbol = item.split("-")[0].trim();
        System.out.println("Symbol: "+ symbol);
        Stock temp = new Stock();
        temp.setSymbol(symbol);
        stocklist.contains(temp);
        return stocklist.contains(temp);
    }

    private void saveStock(String s) {
        String symbol = s.split("-")[0].trim();
        System.out.println("Symbol: "+ symbol);

        new AsyncSymbolLoader(this).execute(symbol);

        Stock ts = new Stock();
        ts.setSymbol(symbol);
        ts.setName(symbolmapping.get(symbol));
        databaseHandler.addStock(ts);

        return;
    }

    private ArrayList<String> searchForStock(String text) {
        ArrayList<String> stockoption = new ArrayList<>();

        if(symbolmapping != null && !symbolmapping.isEmpty()) {

            Iterator<String> it = symbolmapping.keySet().iterator();
            while (it.hasNext()) {
                String symbol = it.next();
                String name = symbolmapping.get(symbol);
                if (symbol.toUpperCase().contains(text.toUpperCase())) {
                    stockoption.add(symbol + " - " + name);
                } else if (name.toUpperCase().contains(text.toUpperCase())) {
                    stockoption.add(symbol + " - " + name);
                }

            }
        }
        return stockoption;
    }
    private void doRefresh() {

        swiper.setRefreshing(false);

        ArrayList<Stock> tempList = databaseHandler.loadStocks();

        for(int i=0; i<tempList.size(); i++){
            String symbol = tempList.get(i).getSymbol();
            new AsyncSymbolLoader(this).execute(symbol);
        }

        Toast.makeText(this, "Refreshed stocks", Toast.LENGTH_SHORT).show();

    }
    public void updateStock(Stock newStock) {

        if(newStock != null){
            int index;
            if((index = stocklist.indexOf(newStock)) > -1){
                stocklist.remove(index);
            }
            stocklist.add(newStock);
            Collections.sort(stocklist, new Comparator<Stock>() {
                @Override
                public int compare(Stock lhs, Stock rhs) {
                    return lhs.getSymbol().compareTo(rhs.getSymbol());
                }
            });
            stockadapter.notifyDataSetChanged();
        }
    }
}
