package com.example.mert.newsgateway;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    public static final String broadcastReq = "Article Request";
    public static final String broadcastRec = "Articles Received";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayList<NewsSource> sourceList = new ArrayList<>();
    HashMap<String, ArrayList<NewsSource>> sourceHashMap = new HashMap<>();
    HashMap<String, Integer> colorHashMap = new HashMap<>();
    Menu mainMenu;
    ArrayAdapter<String> newsArrayAdapter;
    ArrayList<String> sourceNameList = new ArrayList<>();

    private ServReceiver servReceiver;
    private ViewPager viewPager;
    private FragAdapter fragAdapter;
    private ArrayList<ArticleFragment> articleFragmentsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        viewPager = findViewById(R.id.pager);


        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = sourceNameList.get(position);

                        for (int i = 0; i < sourceList.size(); i++) {
                            NewsSource tempNewsSource = sourceList.get(i);
                            if (tempNewsSource.getName().equals(name)) {
                                Toast.makeText(MainActivity.this, tempNewsSource.getId(), Toast.LENGTH_SHORT).show();
                                broadcastArticleReq(tempNewsSource.getId());

                                mDrawerLayout.closeDrawer(mDrawerList);
                                return;
                            }
                        }

                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        AsyncSources asyncSources = new AsyncSources(this);
        asyncSources.execute();
        servReceiver = new ServReceiver(this);
        IntentFilter intentFilter = new IntentFilter(broadcastRec);
        registerReceiver(servReceiver, intentFilter);



        Intent intent = new Intent(MainActivity.this, ArticleService.class);
        startService(intent);

        fragAdapter = new FragAdapter(getSupportFragmentManager(), articleFragmentsList);
        viewPager.setAdapter(fragAdapter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(servReceiver);
        super.onDestroy();

    }

    public void broadcastArticleReq(String id) {
        Intent intent = new Intent();
        intent.setAction(broadcastReq);
        intent.putExtra("id", id);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        categoryList.add("all");
        menu.add(Menu.NONE, categoryList.indexOf("all"), Menu.NONE, "all");
        mainMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        sourceNameList.clear();
        ArrayList<NewsSource> tempList = sourceHashMap.get(item.getTitle().toString());

        for (int i = 0; i < tempList.size(); i++) {
            sourceNameList.add(tempList.get(i).getName());
        }
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);

    }

    public void addSource(ArrayList<NewsSource> newsSourceArrayList, ArrayList<String> categoryList) {
        for (int i = 0; i < categoryList.size(); i++) {
            ArrayList<NewsSource> tempList = new ArrayList<>();
            for (int j = 0; j < newsSourceArrayList.size(); j++) {
                NewsSource source = newsSourceArrayList.get(j);
                if (categoryList.get(i).equals(source.getCategory())) {
                    tempList.add(source);
                }
            }
            sourceHashMap.put(categoryList.get(i), tempList);
        }
        for (String key: sourceHashMap.keySet()) {
            mainMenu.add(key);

        }

        for (int i = 1; i < mainMenu.size(); i++) {
            MenuItem menuItem = mainMenu.getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
        }

        sourceHashMap.put("all", newsSourceArrayList);

        this.categoryList = categoryList;
        this.sourceList.addAll(newsSourceArrayList);

        sourceNameList = new ArrayList<>();

        for (int i = 0; i < sourceList.size(); i++) {
            sourceNameList.add(sourceList.get(i).getName());
            colorHashMap.put(sourceList.get(i).getName(), colorHashMap.get(sourceList.get(i).getCategory()));
        }

        newsArrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, sourceNameList);

        mDrawerList.setAdapter(newsArrayAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void addAllFragments(ArrayList<Article> arrayList) {

        for (int i = 0; i < fragAdapter.getCount(); i++) {
            fragAdapter.notifyChangeInPosition(i);
        }

        articleFragmentsList.clear();

        for (int i = 0; i < arrayList.size(); i++) {
            articleFragmentsList.add(ArticleFragment.newInstance(arrayList.get(i), arrayList.size()));
        }

        fragAdapter.notifyDataSetChanged();

        viewPager.setCurrentItem(0);


    }


}