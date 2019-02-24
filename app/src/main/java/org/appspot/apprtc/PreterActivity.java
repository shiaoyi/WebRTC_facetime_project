package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/12.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import java.util.zip.Inflater;

public class PreterActivity extends ConnectActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preter);

        initDrawer();
        initBody();

    }

    private void initDrawer() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment;
                        switch(menuItem.getItemId()){
                            case R.id.item_Schedule:
                                initBody();
                                break;
                            case R.id.item_Profile:
                                fragment = new ProfileFragment();
                                switchFragment(fragment);
                                setTitle(getString(R.string.page_Profile));
                                break;
                            case R.id.item_Preter_Record:
                                fragment = new PreterRecordFragment();
                                switchFragment(fragment);
                                setTitle(getString(R.string.page_Record));
                                break;
                            case R.id.item_Receipt:
                                fragment = new ReceiptFragment();
                                switchFragment(fragment);
                                setTitle(getString(R.string.page_Receipt));
                                break;
                            case R.id.item_Score:
                                fragment = new CommentFragment();
                                switchFragment(fragment);
                                setTitle(getString(R.string.page_Comment));
                                break;
                        }

                        return true;
                    }
                });

    }


    private void initBody(){
        Fragment fragment = new ScheduleFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body_preter,fragment);
        fragmentTransaction.commit();
        setTitle(R.string.page_Schedule);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;}
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body_preter,fragment);
//        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect_menu_preter, menu);
        return true;
    }

//    @Override
//    public void onBackPressed() {
//
//        int count = getFragmentManager().getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//        } else {
//            getFragmentManager().popBackStack();
//        }
//    }
}
