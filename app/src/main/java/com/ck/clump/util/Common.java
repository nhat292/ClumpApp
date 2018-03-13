package com.ck.clump.util;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;

import com.ck.clump.App;
import com.ck.clump.BuildConfig;
import com.ck.clump.enums.NotificationType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.TAG_ORIENTATION;

public class Common {

    public static boolean isNotAllowNumber(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) return false;
        }
        return true;
    }

    public static String formatMoney(String money) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return "Rp " + formatter.format(Double.valueOf(money));
    }

    public static String formatAccountNumber(String account) {
        char[] accountArr = account.toCharArray();
        return "" + accountArr[0] + accountArr[1] + accountArr[2]
                + "-" + accountArr[3] + accountArr[4] + accountArr[5]
                + "-" + accountArr[6] + accountArr[7] + accountArr[8];
    }

    public static boolean checkConnectInternet(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conManager.getActiveNetworkInfo();
        return !((i == null) || (!i.isConnected()) || (!i.isAvailable()));
    }

    public static String showTime(String time) {
        SimpleDateFormat fmt = new SimpleDateFormat("h:mm a");
        String dateString = fmt.format(new Date(Long.valueOf(time)));
        return dateString;
    }

    public static String showTime(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("h:mm a");
        String dateString = fmt.format(date);
        return dateString;
    }

    public static String showDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = fmt.format(date);
        return dateString;
    }

    public static String showDateTime(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String dateString = fmt.format(date);
        return dateString;
    }

    public static String getDateFormat(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = fmt.format(date);
        return dateString;
    }

    public static boolean isYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int curDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        return day == curDay - 1;
    }

    public static boolean isToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int curDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        return day == curDay;
    }

    public static String formatTimeStamp(long timeStamp) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }

    public static void playNotificationSound(Context context) {
        int notificationType = SharedPreference.getInt(SharedPreference.KEY_NOTIFICATION_TYPE, NotificationType.SOUND.getValue());
        if (notificationType == NotificationType.SOUND.getValue()) {
            try {
                Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + App.getInstance().getApplicationContext().getPackageName() + "/raw/notification");
                Ringtone r = RingtoneManager.getRingtone(App.getInstance().getApplicationContext(), alarmSound);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (notificationType == NotificationType.VIBRATE.getValue()) {
            try {
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Date convertTimestampToDate(long lastTime) {
        Date date = null;
        if (lastTime > 0) {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.add(Calendar.MILLISECOND, tz.getOffset(lastTime));
            date = calendar.getTime();
        }
        return date;
    }


    public static void startActionCall(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(App.getInstance(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent);
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(Constant.TAG, "Require permission: " + Manifest.permission.CALL_PHONE + " to make call.");
            }
        }
    }

    public static Bitmap createBitmapWithSize(Context context, int resDrawable, int width, int height) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(resDrawable);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap outBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return outBitmap;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static String randomString(int length) {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(new Random().nextInt(letters.length())));
        }
        return sb.toString();
    }

    public static List<String> getIdListFromString(String content, String regex) {
        List<String> strings = new ArrayList<>();
        String[] split = content.split(regex);
        if (split.length > 0) {
            for (String id : split) {
                if (!id.isEmpty()) {
                    strings.add(id);
                }
            }
        }
        return strings;
    }

    public static String getStringFromStringArray(List<String> ids, String regex) {
        StringBuilder stringBuilder = new StringBuilder();
        if (ids.size() > 0) {
            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                if (!id.isEmpty()) {
                    stringBuilder.append(id + regex);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String getChatContent(String originalContent) {
        String content = originalContent;
        List<String> emojis = Arrays.asList(EmojiUnicode.getiOSEmojiUnicode());
        for (int i = 0; i < emojis.size(); i++) {
            String emojiEncode = emojis.get(i);
            if (content.contains(emojiEncode)) {
                content = content.replace(emojiEncode, EmojiUnicode.getAndroidEmojiUnicode()[i]);
            }
        }
        content = replaceSpecialSymbols(content);
        return content;
    }

    public static String replaceSpecialSymbols(String content) {
        content = content.replace("\\u201c", "\"");
        content = content.replace("\\u2019", "'");
        content = content.replace("\\u20ac", "€");
        content = content.replace("\\243", "£");
        content = content.replace("\\245", "¥");

        content = content.replace("\\u00A5", "¥");
        content = content.replace("\\u221A", "√");
        content = content.replace("\\u03C0", "π");
        content = content.replace("\\u00F7", "÷");
        content = content.replace("\\u00D7", "×");
        content = content.replace("\\u00B6", "¶");
        content = content.replace("\\u2206", "∆");
        content = content.replace("\\u00A3", "£");
        content = content.replace("\\u00A2", "¢");

        content = content.replace("\\u20AC", "€");
        content = content.replace("\\u00A5", "¥");
        content = content.replace("\\u00B0", "°");
        content = content.replace("\\u00A9", "©");
        content = content.replace("\\u00AE", "®");
        content = content.replace("\\u2122", "™");
        content = content.replace("\\u2105", "℅");
        content = content.replace("\\u2022", "•");
        content = content.replace("\\u201c", "“");
        content = content.replace("\\u201d", "”");
        content = content.replace("\\\"", "\"");
        content = content.replace("\\\\", "\\");

        content = content.replace("\\ue24e", "©");
        content = content.replace("\\ue24f", "®");
        content = content.replace("\\ue537", "™");

        return content;
    }

    public static void setDefaultLocale(Context context, String locale) {
        Locale locJa = new Locale(locale.trim());
        Locale.setDefault(locJa);

        Configuration config = new Configuration();
        config.locale = locJa;

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getDeleteMessage() {
        final long timeSend = System.currentTimeMillis() / 1000L;
        String message = "0@clump@DELETE@clump@" + timeSend + "@clump@DELETE@clump@DELETE@clump@DELETE@clump@DELETE@clump@delete";
        return message;
    }

    public static int getNotificationIDFromChannelID(String channelID) {
        BigInteger bigInt = new BigInteger(channelID.getBytes());
        return bigInt.intValue() / 1000000;
    }

    public static void cancelNotification(Context context, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Rect getLocationOnScreen(Activity activity, EditText mEditText) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Rect mRect = new Rect();
        int[] location = new int[2];
        mEditText.getLocationOnScreen(location);
//        mRect.left = location[0];
        mRect.left = 0;
        mRect.top = location[1] - 20;
//        mRect.right = location[0] + mEditText.getWidth();
        mRect.right = location[0] + width;
        mRect.bottom = location[1] + mEditText.getHeight() + 20;
        return mRect;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "clump");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    public static int getFileExifRotation(Uri uri) throws IOException {
        ExifInterface exifInterface = new ExifInterface(uri.getPath());
        int orientation = exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        switch (orientation) {
            case ORIENTATION_ROTATE_90:
                return 90;
            case ORIENTATION_ROTATE_180:
                return 180;
            case ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void saveBitmapToCache(Bitmap bmp, String filename) {
        File dir = new File(App.getInstance().getCacheDir(), "temps");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }
        File file = new File(dir.toString() + "/" + filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }

    public static File getFileFromCache(String filename) {
        File dir = new File(App.getInstance().getCacheDir(), "temps");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }
        File file = new File(dir.toString() + "/" + filename);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public static int convertDpToPx(int dp) {
        return Math.round(dp * (App.getInstance().getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    public static int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
