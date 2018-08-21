package com.example.ikshita.travel;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class Menu_Activity extends AppCompatActivity {

    private static final String TAG = "MA";
    private Menu_Adapter menu_activity_Adapter;
    private ViewPager m_Viewmenu;
    String placeid;
    String url;
    private static String myString ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activity);
        Intent intent = getIntent();
        String msg = intent.getStringExtra("resultmenu");
        myString = msg.toString();
        Log.i("Mes",msg);
       /* try {
            JSONObject json = new JSONObject(msg);
            placeid = json.getString("place_id");
            Log.i("Place_id",placeid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        menu_activity_Adapter = new Menu_Adapter(getSupportFragmentManager(),Menu_Activity.this);
        m_Viewmenu = (ViewPager) findViewById(R.id.container_menu);
        topViewP_menu(m_Viewmenu);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_menu);
        tabLayout.setupWithViewPager(m_Viewmenu);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(menu_activity_Adapter.getTop(i));
        }
    }

    private void topViewP_menu(ViewPager viewPager){
        menu_activity_Adapter.addtab(new Menutab1(), "    INFO");
        menu_activity_Adapter.addtab(new Menutab2(),"   PHOTOS");
        menu_activity_Adapter.addtab(new Menutab3(), "    MAPS");
        menu_activity_Adapter.addtab(new Menutab4(),"   REVIEWS");
        viewPager.setAdapter(menu_activity_Adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topicons, menu);
        return true;
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    public String getMyData() {
        return myString;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }


}




 /*resultdetails = response.getJSONObject("result");
                            Log.i("tt","Response: " +resultdetails.toString());

                            String  name = resultdetails.getString("name");
                            String vicinity = resultdetails.getString("vicinity");
                            String price_level = resultdetails.getString("price_level");
                            String rating = resultdetails.getString("rating");
                            Integer formatted_phone_number = resultdetails.getInt("formatted_phone_number");
                            String urlgoogle = resultdetails.getString("url");
                            String website = resultdetails.getString("website");
                            Log.i(TAG,"name : " + name);
                            Log.i(TAG,"vicinity : " + vicinity);
                            Log.i(TAG,"price_level : " + price_level);
                            Log.i(TAG,"rating : " + rating);
                            Log.i(TAG,"formatted_phone_number : " + formatted_phone_number);
                            Log.i(TAG,"url : " + urlgoogle);
                            Log.i(TAG,"website : " + website);
*/
