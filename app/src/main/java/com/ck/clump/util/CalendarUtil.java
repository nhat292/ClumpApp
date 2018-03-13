package com.ck.clump.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.ck.clump.App;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nhat on 5/22/17.
 */

public class CalendarUtil {

    public static void saveEventToCalendar(String title, long startDate, String eventId) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(startDate));
            calendar.add(Calendar.HOUR, 1);
            long endTime = calendar.getTime().getTime();
            ContentResolver cr = App.getInstance().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startDate);
            values.put(CalendarContract.Events.DTEND, endTime);
            values.put(CalendarContract.Events.TITLE, title);
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            System.out.println(Calendar.getInstance().getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(App.getInstance(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long id = Long.parseLong(uri.getLastPathSegment());
            SharedPreference.saveLong(eventId, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeEventFromCalendar(String eventId) {
        long id = SharedPreference.getLong(eventId, 0);
        SharedPreference.deleteKeyAndValue(eventId);
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
        App.getInstance().getContentResolver().delete(deleteUri, null, null);
    }
}
