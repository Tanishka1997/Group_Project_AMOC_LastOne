package com.pixel.lastone;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class HomeScreenPatient extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String admNoImportedString, JSON_STRING;
    SaveSharedPreference saveSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_patient);

        if (SaveSharedPreference.getUserName(HomeScreenPatient.this).length() == 0) {
            Intent intent = new Intent(this, loginAsPatient.class);
            startActivity(intent);
        } else {
            admNoImportedString = SaveSharedPreference.getUserName(HomeScreenPatient.this).toString();
            Message.message(this, admNoImportedString);
        }

        saveSharedPreference = new SaveSharedPreference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView navHeaderName = (TextView) headerLayout.findViewById(R.id.nav_header_name);
        TextView navHeaderAdmissionNo = (TextView) headerLayout.findViewById(R.id.nav_header_admission_no);
        navHeaderAdmissionNo.setText(admNoImportedString);

        FragmentHome fHome = new FragmentHome();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fHome, "HOME").commit();
        setTitle("Home");

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (getTitle()!= "Home") {
                FragmentHome fHome = new FragmentHome();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fHome, "HOME").commit();
                setTitle("Home");
            } else {
                super.onBackPressed();
                finishAffinity();
            }
        }
    }

    @Override
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentClass = FragmentHome.class;

        } else if (id == R.id.nav_profile) {
            fragmentClass = FragmentProfile.class;
            FragmentProfile fProfile = new FragmentProfile();
            fProfile.setAdmissionNo(admNoImportedString);

        } else if (id == R.id.nav_edit_profile) {
            fragmentClass = FragmentEditProfile.class;
            FragmentEditProfile fEditProfile = new FragmentEditProfile();
            fEditProfile.setAdmissionNo(admNoImportedString);

        } else if (id == R.id.nav_madical_history) {
            fragmentClass = FragmentCheckMedicalHistory.class;
            FragmentCheckMedicalHistory fCheckMedicalHistory = new FragmentCheckMedicalHistory();
            fCheckMedicalHistory.setAdmissionNo(admNoImportedString);

        } else if (id == R.id.nav_log_out) {
            saveSharedPreference.setUserName(this, "");
            Intent intent = new Intent(this, HomeScreenPatient.class);
            startActivity(intent);
        }

        if (fragmentClass != null) {

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();

            item.setChecked(true);

            if (id == R.id.nav_home) {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, "HOME").commit();
                setTitle("Home");
            } else if (id == R.id.nav_profile) {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, "PROFILE").commit();
                setTitle(item.getTitle());
            } else if (id == R.id.nav_edit_profile) {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, "EDIT_PROFILE").commit();
                setTitle(item.getTitle());
            } else if (id == R.id.nav_madical_history) {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, "MADICAL_HISTORY").commit();
                setTitle("Medical History");
            } else {
                setTitle(item.getTitle());
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
