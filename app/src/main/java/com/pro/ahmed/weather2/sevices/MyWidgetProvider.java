package com.pro.ahmed.weather2.sevices;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.transcode.DrawableBytesTranscoder;
import com.pro.ahmed.weather2.R;
import com.pro.ahmed.weather2.WeatherDrawerActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
    private SharedPreferences prefs;
    String icon, description;
    int temp;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        description = prefs.getString("description", "null");
        icon = prefs.getString("icon", "null");
        temp = prefs.getInt("temp", 0);
        String tempDescription = "℃" + String.valueOf(temp) + " " + description;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
        String date;
        String formattedDate = df.format(c);
        String dayOfTheWeek = sdf.format(d);
        date = dayOfTheWeek + " " + formattedDate;
        Drawable drawable = (context.getResources().getDrawable(context.getResources()
                .getIdentifier("a" + icon, "drawable", context.getPackageName())));
        BitmapDrawable iconConver = (BitmapDrawable) drawable;
        Bitmap bitmap = iconConver.getBitmap();
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            // create some random data
            Log.v("WidgetIcon", icon);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_home_screen);
            // Set the text
            remoteViews.setTextViewText(R.id.tvWidgetTempDescription, tempDescription);
            remoteViews.setTextViewText(R.id.tvWidgetDate, date);
            remoteViews.setImageViewBitmap(R.id.ivWidgetIcon, bitmap);


            // Register an onClickListener
            Intent intent = new Intent(context, WeatherDrawerActivity.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            /*PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.ivWidgetIcon, startActivityPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.tcWidgetClock, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);*/
            Intent launchActivity = new Intent(context, WeatherDrawerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            //remoteViews.setOnClickPendingIntent(R.id.ivWidgetIcon, pendingIntent);
            //remoteViews.setOnClickPendingIntent(R.id.tcWidgetClock, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.llWidget, pendingIntent);

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(thisWidget, remoteViews);
        }
    }
}
