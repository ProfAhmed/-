package com.pro.ahmed.weather2.sevices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.pro.ahmed.weather2.R;
import com.pro.ahmed.weather2.WeatherDrawerActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Notifications {
    Context mContext;
    static int l = 0;

    public Notifications(Context mContext) {
        this.mContext = mContext;
    }

    public void fireNotify(int temp, String descriptionForecast_5_Days, String iconForecast_5_Days) {
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.custom_push);
        String url = "http://openweathermap.org/img/w/" + iconForecast_5_Days + ".png";
        if (iconForecast_5_Days != null) {
            Log.v("Icon_Cast" + l, iconForecast_5_Days);
        }
        ++l;
        new DownloadImageTask(contentView, mContext, temp, descriptionForecast_5_Days, false)
                .execute(url);
    }

    public void fireSoundNotify(int temp, String description, String icon) {
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.custom_push);
        String url = "http://openweathermap.org/img/w/" + icon + ".png";

        new DownloadImageTask(contentView, mContext, temp, description, true)
                .execute(url);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        RemoteViews contentView;
        Context context;
        int temp;
        String descriptionForecast_5_Days;
        boolean checkSound;

        public DownloadImageTask(RemoteViews contentView, Context context, int temp,
                                 String descriptionForecast_5_Days, boolean checkSound) {
            this.contentView = contentView;
            this.context = context;
            this.temp = temp;
            this.descriptionForecast_5_Days = descriptionForecast_5_Days;
            this.checkSound = checkSound;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            Log.v("URLL", urldisplay);
            try {
                bitmap = Glide.with(mContext)
                        .asBitmap()
                        .load(urldisplay)
                        .submit(150, 150)
                        .get();
                Log.v("Bitmap", String.valueOf(bitmap.getByteCount()));
            } catch (Exception e) {
                Log.v("Error((((())))", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            Locale locale = new Locale("ar");
            DateFormat forFormat = new SimpleDateFormat("dd-MM-yyyy" + " " + "h:mm a", locale);
            String time = forFormat.format(Calendar.getInstance().getTime());
            contentView.setImageViewBitmap(R.id.image, result);
            contentView.setTextViewText(R.id.title, descriptionForecast_5_Days);
            contentView.setTextViewText(R.id.text, String.valueOf(temp) + "℃");
            contentView.setTextViewText(R.id.tvPushNotifyTime, time);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(mContext, WeatherDrawerActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //if we want ring on notifcation then uncomment below line// //
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentSound = PendingIntent.getActivity(mContext, 10, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).
                    setSmallIcon(R.mipmap.ic_launcher).
                    setContentIntent(pendingIntent).setCustomContentView(contentView);
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(mContext).
                    setSmallIcon(R.mipmap.ic_launcher).
                    setContentIntent(pendingIntentSound).setCustomContentView(contentView);
            if (checkSound) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder2.setSound(alarmSound);
                notificationManager.notify(10, builder2.build());
            } else {
                builder.setOngoing(true);
                notificationManager.notify(100, builder.build());
            }
        }
    }
}
