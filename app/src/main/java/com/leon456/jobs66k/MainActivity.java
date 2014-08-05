package com.leon456.jobs66k;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MainActivity extends Activity implements ActionBar.TabListener ,MainActivityInterface{

    private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private String[] categories;
    private ArrayList<List<HashMap<String, String>>> mDatas;
    private Handler handler = new Handler();
    private AdView mAdView;
    private ViewFlipper mainViewFlipper;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"Service Running:"+this.isMyServiceRunning(GetDataService.class));

        if(!this.isMyServiceRunning(GetDataService.class)) {

            Intent i = new Intent(this, GetDataService.class );
            /* 設定新TASK的方式 */
            i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            /* 以startService方法啟動Intent */
            startService(i);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        mainViewFlipper = (ViewFlipper)findViewById(R.id.mainViewFlipper);

        categories = this.getResources().getStringArray(R.array.categories);

        mDatas = new ArrayList<List<HashMap<String,String>>>();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Ion.with(this).load("http://66kjobs.tw/").asString().setCallback(new FutureCallback<String>(){
            @Override
            public void onCompleted(Exception e, String result) {
                Document doc = Jsoup.parse(result);

                Elements els = doc.select("#myTabContent");
                Element myTabContent = els.first();
                Elements tables = myTabContent.select("table");
                for(int i = 0;i<tables.size();i++){
                   Element table =  tables.get(i);
                   Elements trs = table.select("tr");
                   Log.i(TAG,"--------------------------");
                   List<HashMap<String,String>> job =  new ArrayList<HashMap<String,String>>();
                   mDatas.add(job);

                   for(Element tr:trs){
                       Elements tds = tr.select("td");
                       Log.i(TAG,"*************");
                       HashMap<String,String> data = new HashMap<String,String>();
                       job.add(data);
                       for (int j = 0;j<tds.size();j++){
                           Element td = tds.get(j);

                           switch (j){
                               case 0:
                                   data.put("title", td.text());
                                   Log.i(TAG,"--title--"+td.text());
                                   String link = td.select("a").first().attr("href");
                                   data.put("link",link);

                                   break;
                               case 1:
                                   data.put("job",td.text());

                                   String className = td.select("span").get(0).className();
                                   if(className.contains("label-success")){
                                       data.put("color","1");
                                   }else  if(className.contains("label-danger")){
                                       data.put("color","2");
                                   }else{
                                       data.put("color","0");
                                   }
                                   Log.i(TAG,"--job--"+td.select("span").get(0).className());

                                   break;
                               case 2:
                                   data.put("date",td.text());
                                   Log.i(TAG,"--date--"+td.text());

                                   break;
                           }
                       }
                   }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSectionsPagerAdapter.notifyDataSetChanged();

                        // For each of the sections in the app, add a tab to the action bar.
                        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                            // Create a tab with text corresponding to the page title defined by
                            // the adapter. Also specify this Activity object, which implements
                            // the TabListener interface, as the callback (listener) for when
                            // this tab is selected.
                            actionBar.addTab(
                                    actionBar.newTab()
                                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                                            .setTabListener(MainActivity.this));
                        }

                        mAdView = (AdView) findViewById(R.id.adView);
                        mAdView.loadAd(new AdRequest.Builder().build());
                    }
                });

                Log.i(TAG,"doc size:"+mDatas.size());
            }
        });


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                mSectionsPagerAdapter.getItem(position);
            }
        });


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:

                 Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onBackPressed() {
        if(currentPage > 0) {
            currentPage--;
        }
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_right));
        mainViewFlipper.setDisplayedChild( currentPage );
        getActionBar().show();
        super.onBackPressed();
    }

    @Override
    public void detail(String detail) {
        Bundle argument = new Bundle();
        argument.putCharSequence("detail",detail);
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
        Fragment fragment = new WebViewFragment();
        fragment.setArguments(argument);
        ft.replace(R.id.mainSecondFrameLayout,fragment);
        ft.addToBackStack(null);
        ft.commit();
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left));
        mainViewFlipper.setDisplayedChild( currentPage = 1);
        getActionBar().hide();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            JobListFragment fragment = new JobListFragment();
            Log.i(TAG,"SectionsPagerAdapter:"+" position:"+position+" "+mDatas.get(position));
            fragment.setmDatas(mDatas.get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return categories[position].toUpperCase(l);
        }
    }

}
