package com.example.ikshita.travel;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "Main";
    private ViewPager m_ViewP;
    private TopAdapter m_SectionPA;


    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starts:");

        m_SectionPA = new TopAdapter(getSupportFragmentManager(),MainActivity.this);

        m_ViewP = (ViewPager) findViewById(R.id.container);
        topViewP(m_ViewP);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(m_ViewP);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(m_SectionPA.getTop(i));
        }
    }



    private void topViewP(ViewPager viewPager){
        m_SectionPA.addtab(new tab1(), "    EXPLORE");
        viewPager.setAdapter(m_SectionPA);
    }
}