package com.pro.ahmed.weather2;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pro.ahmed.weather2.fragments.MapsFragment;
import com.pro.ahmed.weather2.fragments.Weather5DaysFragment;
import com.pro.ahmed.weather2.fragments.WeatherNowFragment;
import com.pro.ahmed.weather2.sevices.NotificationService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Nullable
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @Nullable
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @Nullable
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private FragmentManager fragmentManager;
    private Intent mServiceIntent;
    private NotificationService mNotificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_drawer);
        ButterKnife.bind(this);

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
                Toast.makeText(this, "من فضلك قم بتفعيل خاصية التشغيل التلقائي للحصول على الاشعارات ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.v("Exception", (e).toString());
        }
        setSupportActionBar(toolbar);
        @Nullable
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        fragmentManager = getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(this);
        mNotificationService = new NotificationService();
        mServiceIntent = new Intent(this, mNotificationService.getClass());
        if (!isMyServiceRunning(mNotificationService.getClass())) {
            startService(mServiceIntent);
        }
        fragmentShow(WeatherNowFragment.class);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.weather_now_menu) {
            toolbar.setTitle("الطقس");
            fragmentShow(WeatherNowFragment.class);

        } else if (id == R.id.weather5days_menu) {
            toolbar.setTitle("الطقس خمسة أيام");
            fragmentShow(Weather5DaysFragment.class);

        } else if (id == R.id.setting_menu) {
            Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.about_menu) {
            startActivity(new Intent(this, AboutActivity.class));

        } else if (id == R.id.share_menu) {

            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_SUBJECT, "تطبيق الطقس الان");
            String uri = " " + "تطبيق الطقس الان لمعرفة أحوال الطقس والخرائط والتوقعات والتنبيهات مجانا...أنصحك بتحميله الان " + "\n" + "https://play.google.com/store/apps/details?id=com.pro.ahmed.weather2";
            i.putExtra(Intent.EXTRA_TEXT, uri);
            startActivity(Intent.createChooser(i, "Share via"));
        } else if (id == R.id.menuMaps) {
            toolbar.setTitle("الخريطة");
            fragmentShow(MapsFragment.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Show Fragment Fn
    private void fragmentShow(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_weather_drawer, fragment);
        transaction.commit();
    }

    //Hide Item
    private void hideItem() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.weather5days_menu).setVisible(false);
    }

    private void showItem() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.weather5days_menu).setVisible(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!(isMyServiceRunning(mNotificationService.getClass()))) {
            Toast.makeText(this, "I am here ", Toast.LENGTH_SHORT).show();
            Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
            sendBroadcast(broadcastIntent);
        }
    }

}
