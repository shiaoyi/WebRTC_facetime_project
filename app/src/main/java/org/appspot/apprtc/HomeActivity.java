package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/12.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class HomeActivity extends ConnectActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_EMAIL = "email";
    SharedPreferences preferences;
    String email;

    ProgressDialog pd;
    private static String PERSONAL_URL = "https://shiaoyi.000webhostapp.com/personal.php";
    String name,image;

    TextView tvName;
    TextView tvEmail;
    ImageView ivImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpActionBar();
        initDrawer();
        initBody();

        pd = new ProgressDialog(HomeActivity.this);
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        email = preferences.getString("email", DEFAULT_EMAIL);
        personalFetch();
    }

    private void personalFetch() {
        pd.setMessage("loading . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, PERSONAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response
                        Log.d("personalFetch",response);
                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(response);
                            image = responseJSON.getString("image");
                            name = responseJSON.getString("name");
                            tvName.setText(name);
                            tvEmail.setText(email);
                            Picasso.with(getBaseContext()).load(image).into(ivImage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email",email);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawer(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.text_Open, R.string.text_Close);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
        }else{
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
        }

        NavigationView view_start = (NavigationView)findViewById(R.id.navigation_start);
        View header = view_start.getHeaderView(0);
        tvName = (TextView) header.findViewById(R.id.tvUserNickname);
        tvEmail = (TextView) header.findViewById(R.id.tvUserEmail);
        ivImage = (ImageView) header.findViewById(R.id.ivUser);

        view_start.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                Fragment fragment;
                switch(menuItem.getItemId()){
                    case R.id.item_Home:
                        initBody();
                        break;
                    case R.id.item_Account:
                        fragment = new AccountFragment();
                        switchFragment(fragment);
                        setTitle(getString(R.string.page_Account));
                        break;
                    case R.id.item_Record:
                        fragment = new RecordFragment();
                        switchFragment(fragment);
                        setTitle(getString(R.string.page_Record));
                        break;
                    case R.id.item_Booking:
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, BookingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.item_Logout:
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Logout")
                                .setMessage(R.string.ques_logout)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(HomeActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                        break;
                    case R.id.item_Deposit:
                        fragment = new DepositFragment();
                        switchFragment(fragment);
                        setTitle(getString(R.string.page_Deposit));
                        break;
                    case R.id.item_Favoriate:
                        fragment = new FavoriateFragment();
                        switchFragment(fragment);
                        setTitle(getString(R.string.page_Favorite));
                        break;
                }

                return false;
            }
        });

    }

    private void initBody(){
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body,fragment);
        fragmentTransaction.commit();
        setTitle(R.string.language_english);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;}
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body,fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
