package com.hcmus_csc13009.nowwakealarm.receiver;

import static com.hcmus_csc13009.nowwakealarm.service.AlarmService.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.service.AlarmService;
import com.hcmus_csc13009.nowwakealarm.service.RescheduleAlarmService;
import com.hcmus_csc13009.nowwakealarm.utils.MapUtil;
import com.hcmus_csc13009.nowwakealarm.utils.SettingConstant;
import com.hcmus_csc13009.nowwakealarm.utils.WeekDays;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final int MIN_TIME_REQUEST = 5 * 1000;
    private static Location currentLocation;
    private static Location prevLocation;
    private static LocationManager locationManager;
    private static LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            try {
                String strStatus = "";
                switch (status) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        strStatus = "GPS_EVENT_FIRST_FIX";
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        strStatus = "GPS_EVENT_SATELLITE_STATUS";
                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                        strStatus = "GPS_EVENT_STARTED";
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        strStatus = "GPS_EVENT_STOPPED";
                        break;
                    default:
                        strStatus = String.valueOf(status);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }
    };
    Alarm alarm;
    private String provider = LocationManager.GPS_PROVIDER;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = "Alarm Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        } else {
            Bundle bundle = intent.getBundleExtra(context.getString(R.string.bundle_alarm_obj));

            if (bundle != null)
                alarm = (Alarm) bundle.getSerializable(context.getString(R.string.arg_alarm_obj));
            String toastText = "Alarm Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            if (alarm != null) {
                if (alarm.getPosition() != null) {
                    locationManager = (LocationManager) context
                            .getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(provider)) {
                        if (ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            startAlarm(context);
                        } else {
                            locationManager.requestLocationUpdates(provider,
                                    MIN_TIME_REQUEST, 5, locationListener);
                            Location location = locationManager
                                    .getLastKnownLocation(provider);
                            LatLng dest = alarm.getLatLngPosition();
                            if (MapUtil.getDistance(location.getLatitude(), location.getLongitude(),
                                    dest.latitude, dest.longitude) < SettingConstant.NEARBY_RANGE) {
                                startNotifyService(context, alarm);
                            } else {
                                startAlarm(context);
                            }
                        }
                    } else {
                        startAlarm(context);
                    }
                } else {
                    startAlarm(context);
                }
            }
        }
    }

    private void startAlarm(Context context) {
        if (!alarm.isRepeatMode()) {
            startAlarmService(context, alarm);
        } else {
            if (isAlarmToday(alarm)) {
                startAlarmService(context, alarm);
            }
        }
    }

    private void startNotifyService(Context context, Alarm alarm) {
        final int id = alarm.getID();

        String channelName = "Alarm Background Service";
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(alarm.getTitle() + " - Great! You are already here")
                .setContentText(alarm.getDescription())
                .setSmallIcon(R.drawable.wall_clock)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        if (alarm.getTagUri() != null) {
            if (alarm.getTagUri().length() != 0) {
                Intent notificationIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(alarm.getTagUri()));
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, 0);
                builder.setContentText(alarm.getDescription() + " - Tap to open url: " + alarm.getTagUri());
                builder.setContentIntent(contentIntent);
            }
        }

        notificationManager.notify(1, builder.build());
    }

    private boolean isAlarmToday(Alarm alarm1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.MONDAY:
                return alarm1.isRepeatAt(WeekDays.MON);
            case Calendar.TUESDAY:
                return alarm1.isRepeatAt(WeekDays.TUE);
            case Calendar.WEDNESDAY:
                return alarm1.isRepeatAt(WeekDays.WED);
            case Calendar.THURSDAY:
                return alarm1.isRepeatAt(WeekDays.THU);
            case Calendar.FRIDAY:
                return alarm1.isRepeatAt(WeekDays.FRI);
            case Calendar.SATURDAY:
                return alarm1.isRepeatAt(WeekDays.SAT);
            case Calendar.SUNDAY:
                return alarm1.isRepeatAt(WeekDays.SUN);
        }
        return false;
    }

    private void startAlarmService(Context context, Alarm alarm) {
        Intent intentService = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(context.getString(R.string.arg_alarm_obj), alarm);
        intentService.putExtra(context.getString(R.string.bundle_alarm_obj), bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
