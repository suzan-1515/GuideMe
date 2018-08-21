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


public class Menu_Adapter extends FragmentPagerAdapter {
    private  final List<String> toptitle = new ArrayList<>();
    private  final List<Fragment> toplist = new ArrayList<>();
    private int[] imgtop_menu = { R.drawable.info, R.drawable.photos,R.drawable.maps,R.drawable.reviews };
    private Context context;
    public void addtab(Fragment fragment, String title){
        toplist.add(fragment);
        toptitle.add(title);
    }
    public Menu_Adapter(FragmentManager fm , Context context){super(fm); this.context = context;}

    public int getCount() { return toplist.size();}

    public Fragment getItem(int position) {return toplist.get(position);}

    public CharSequence getPageTitle(int position){return toptitle.get(position);}

    public View getTop(int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.custom_menu, null);
        TextView tv = (TextView) v.findViewById(R.id.textSF_menu);
        tv.setText(toptitle.get(position));
        ImageView img = (ImageView) v.findViewById(R.id.topSF_menu);
        img.setImageResource(imgtop_menu[position]);
        return v;
    }
}
