package com.example.ikshita.travel;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TopAdapter extends FragmentPagerAdapter {
    private  final List<String> toptitle = new ArrayList<>();
    private  final List<Fragment> toplist = new ArrayList<>();
    private int[] imgtop = { R.drawable.search, R.drawable.favorites };
    private Context context;
    public void addtab(Fragment fragment, String title){
        toplist.add(fragment);
        toptitle.add(title);
    }
    public TopAdapter(FragmentManager fm , Context context){super(fm); this.context = context;}

    public int getCount() { return toplist.size();}

    public Fragment getItem(int position) {return toplist.get(position);}

    public CharSequence getPageTitle(int position){return toptitle.get(position);}

    public View getTop(int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.custom, null);
        TextView tv = (TextView) v.findViewById(R.id.textSF);
        tv.setText(toptitle.get(position));
        ImageView img = (ImageView) v.findViewById(R.id.topSF);
        img.setImageResource(imgtop[position]);
        return v;
    }
}
