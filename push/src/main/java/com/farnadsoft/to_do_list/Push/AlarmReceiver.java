package com.farnadsoft.to_do_list.Push;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.farnadsoft.to_do_list.Server.Server;
import com.farnadsoft.to_do_list.Server.ServerConfig;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by Beladiya Nileshon 23/07/2016.
 */
public class AlarmReceiver extends Service {

    Context context;
    String latitude, longitude;


    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        context = this;
    }

    public synchronized void buildGoogleApiClient() {
        Log.e("FCM", "Updating Coordinates");

        if (SmartLocation.with(getApplicationContext()).location().state().locationServicesEnabled()) {
            SmartLocation.with(getApplicationContext()).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            double latPoint = location.getLatitude();
                            double lngPoint = location.getLongitude();

                            final SharedPreferences prefs = getSharedPreferences("GCM",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(PushUser.LAST_LAT, latPoint + "");
                            editor.putString(PushUser.LAST_LONG, lngPoint + "");
                            editor.commit();
                            try {
                                sendLocation();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } else {

// --> omitting notification "Requires location enabled" <--
/* v-------------------------------------------------------------------v

            NotificationManager mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this);

            mBuilder.setSmallIcon(R.drawable.ic_stat_fcm)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(
                            new NotificationCompat.BigTextStyle().bigText("Requires location enabled"))
                    .setAutoCancel(true)
                    .setContentText("Requires location enabled");
            Intent notificationIntent = new Intent(AlarmReceiver.this, PushActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent intent = PendingIntent.getActivity(AlarmReceiver.this, 0,
                    notificationIntent, 0);
            mBuilder.setContentIntent(intent);

            mBuilder.setDefaults(Notification.DEFAULT_ALL);

            mNotificationManager.notify(9999, mBuilder.build());

//      ^-----------------------------------------------------------------------^*/
        }

        //tracker.stopUsingGPS();

    }


    private void sendLocation() throws IOException {
        RequestParams params = new RequestParams();
        params.put(PushUser.EMAIL, FCMActivity.getRegistrationEmail(getApplicationContext()));
        params.put(PushUser.APP_TYPE, ServerConfig.APP_TYPE);
        params.put(PushUser.DEVICE_MODEL, DeviceUtils.getDeviceModel());
        params.put(PushUser.DEVICE_API, DeviceUtils.getDeviceAPILevel());
        params.put(PushUser.DEVICE_OS, DeviceUtils.getDeviceOS());
        params.put(PushUser.DEVICE_NAME, DeviceUtils.getDeviceName());
        params.put(PushUser.TIMEZONE, DeviceUtils.getDeviceTimeZone());
        params.put(PushUser.LAST_LAT, DeviceUtils.getLastlat(getApplicationContext()) + "");
        params.put(PushUser.LAST_LONG, DeviceUtils.getLastLng(getApplicationContext()) + "");
        params.put(PushUser.MEMORY, DeviceUtils.getDeviceMemory(getApplicationContext()) + "");
        params.put(PushUser.DEVICE_ID, DeviceUtils.getDeviceId(getApplicationContext()) + "");

        Server.post(ServerConfig.REGISTRATION_URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopSelf();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.e("Advance Push", response.toString());
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
