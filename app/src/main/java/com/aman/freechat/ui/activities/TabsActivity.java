package com.aman.freechat.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aman.freechat.R;
import com.aman.freechat.adapters.ViewPagerAdapter;
import com.aman.freechat.ui.fragments.FriendChatFragment;
import com.aman.freechat.ui.fragments.GroupChatFragment;
import com.aman.freechat.utils.AppUtility;

public class TabsActivity extends AppCompatActivity {
    private static String TAG = TabsActivity.class.getName();
    private ViewPager viewPager;
    private TabLayout tabLayout = null;

    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("FreeFireChat");
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        initTab();
    }

    private void setViewPager(ViewPager viewPager) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFrag(new FriendChatFragment(), "Chats");
        //pagerAdapter.addFrag(new GroupChatFragment(), "Groups");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private void initTab() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabIcons();
    }

    private void setUpTabIcons() {
        final int[] tabIcons = {
                R.drawable.chat,
                R.drawable.group_chat
        };

        final int[] tabIconsOff = {
                R.drawable.chat_off,
                R.drawable.group_chat_off
        };


        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        //tabLayout.getTabAt(1).setIcon(tabIconsOff[1]);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).setIcon(tabIcons[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).setIcon(tabIconsOff[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                AppUtility.logout(this);
                return true;
            case R.id.about:
                AppUtility.showAboutDialog(TabsActivity.this, "Free Firebase enabled chat",
                        "FreeChat v1.0\n\n" +
                                "This is a simple demo for firebase chat app.\n\n" +
                                "Next updates :\n1) Group Chat\n2) Send audio/video/images\n3) Update your profile");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
