package com.hcmus_csc13009.nowwakealarm.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus_csc13009.nowwakealarm.R;
import com.wwdablu.soumya.lottiebottomnav.FontBuilder;
import com.wwdablu.soumya.lottiebottomnav.FontItem;
import com.wwdablu.soumya.lottiebottomnav.ILottieBottomNavCallback;
import com.wwdablu.soumya.lottiebottomnav.LottieBottomNav;
import com.wwdablu.soumya.lottiebottomnav.MenuItem;
import com.wwdablu.soumya.lottiebottomnav.MenuItemBuilder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ILottieBottomNavCallback {
    FragmentTransaction transaction = null;
    LottieBottomNav bottomNav;
    ArrayList<MenuItem> list;
    //private ViewPager2 mViewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mViewPager2 = findViewById(R.id.myViewPager2);

        bottomNav   = findViewById(R.id.bottomAppBar);


        //Set font item
        FontItem fontItem = FontBuilder.create("Home")
                .selectedTextColor(Color.BLACK)
                .unSelectedTextColor(Color.GRAY)
                .selectedTextSize(16) //SP
                .unSelectedTextSize(12) //SP
                .setTypeface(Typeface.createFromAsset(getAssets(), "coconutz.ttf"))
                .build();

        //Menu Home
        MenuItem homeItem = MenuItemBuilder.create("home.json", MenuItem.Source.Assets, fontItem, "dash")
                .pausedProgress(1f)
                .loop(false)
                .build();

        //Menu Alarms List
        fontItem = FontBuilder.create(fontItem).setTitle("Alarms").build();
        MenuItem alarmsItem = MenuItemBuilder.createFrom(homeItem, fontItem)
                .selectedLottieName("alarmclock.json")
                .unSelectedLottieName("alarmclock.json")
                .loop(true)
                .build();

        //Menu Add
        fontItem = FontBuilder.create(fontItem).setTitle("").build();
        MenuItem addItem = MenuItemBuilder.createFrom(homeItem, fontItem)
                .selectedLottieName("add.json")
                .unSelectedLottieName("add.json")
                .build();

        //Menu Ringtones List
        fontItem = FontBuilder.create(fontItem).setTitle("Map").build();
        MenuItem ringtonesItem = MenuItemBuilder.createFrom(homeItem, fontItem)
                .selectedLottieName("music.json")
                .unSelectedLottieName("music.json")
                .pausedProgress(0.75f)
                .build();

        //Menu Map
        fontItem = FontBuilder.create(fontItem).setTitle("Settings").build();
        MenuItem mapItem = MenuItemBuilder.createFrom(homeItem, fontItem)
                .selectedLottieName("settings.json")
                .unSelectedLottieName("settings.json")
                .build();



        list = new ArrayList<>(4);
        list.add(homeItem);
        list.add(alarmsItem);
        list.add(addItem);
        list.add(ringtonesItem);
        list.add(mapItem);

        bottomNav.setCallback(this);
        bottomNav.setMenuItemList(list);
        bottomNav.setSelectedIndex(0); //first selected index

        /*MyViewPager2Adapter adapter = new MyViewPager2Adapter(this);
        mViewPager2.setAdapter(adapter);

        mViewPager2.setPageTransformer(new DepthPageTransformer());
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        setFragment(new HomeFragment());
                        bottomNav.setSelectedIndex(0);
                        break;
                    case 1:
                        setFragment(new AlarmsFragment());
                        bottomNav.setSelectedIndex(1);
                        break;
                    case 2:
                        setFragment(new AddAlarmFragment());
                        bottomNav.setSelectedIndex(2);
                        break;
                    case 3:
                        setFragment(new RingtonesFragment());
                        bottomNav.setSelectedIndex(3);
                        break;
                    case 4:
                        setFragment(new MapFragment());
                        bottomNav.setSelectedIndex(4);
                        break;

                }
            }
        });*/

        //First selected fragment
        setFragment(new HomeFragment());
    }

    @Override
    public void onMenuSelected(int oldIndex, int newIndex, MenuItem menuItem) {
        switch (newIndex) {
            case 0: {
                setFragment(new HomeFragment());
                //mViewPager2.setCurrentItem(0);
                break;
            }
            case 1: {
                setFragment(new AlarmsFragment());
                //mViewPager2.setCurrentItem(1);
                break;
            }
            case 2: {
                Intent activityIntent = new Intent();
                activityIntent.setClass(this, AddAlarmActivity.class);
                startActivity(activityIntent);
                //mViewPager2.setCurrentItem(2);
                break;
            }
            case 3: {
                setFragment(new MapFragment());
                //mViewPager2.setCurrentItem(3);
                break;
            }
            case 4: {
                setFragment(new SettingsFragment());
                //mViewPager2.setCurrentItem(4);
                break;
            }
        }
    }

    private void setFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnimationStart(int index, MenuItem menuItem) {

    }

    @Override
    public void onAnimationEnd(int index, MenuItem menuItem) {

    }

    @Override
    public void onAnimationCancel(int index, MenuItem menuItem) {

    }
}