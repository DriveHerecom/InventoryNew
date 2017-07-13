package com.creadigol.drivehere.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by ADMIN on 22-05-2017.
 */

public class CommonFunctions {

    public static String dateFormat = "dd/MMM/yyyy";

    /**
     * @param context Context of activity
     * @param msg     message for toast
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    /**
     * Return date in dd/MMM/yyyy format.
     *
     * @param milliSeconds Date in milliseconds
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds) {

        return getDate(milliSeconds, dateFormat);
    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param format Date format for convert
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String format) {

        if (milliSeconds <= 0)
            return "";
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * @param date   date in string
     * @param format Format of date
     * @return
     */
    public static long getMilliseconds(String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date dateToMill = null;
        try {
            dateToMill = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToMill);
        return calendar.getTimeInMillis();
    }

    /**
     * @param days   number of days
     * @return
     */
    public static long getMillisecondsForDaysPast(int days) {

        String dateInString = "2011-11-30";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, -days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date resultdate = new Date(c.getTimeInMillis());
        dateInString = sdf.format(resultdate);
        Log.e("date", "String date:"+dateInString +" "+ c.getTimeInMillis());
        return c.getTimeInMillis();
    }

    public static long daysDifferent(String startDate) {
        Calendar calendar = Calendar.getInstance();
        String endDate = String.valueOf(calendar.getTimeInMillis());
        return daysDifferent(startDate, endDate);
    }

    public static long daysDifferent(String startDate, String endDate) {

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        if (startDate.trim().length() <= 0 || startDate.trim().equalsIgnoreCase("0")) {
            return -1;
        }

        Date start = null, end = null;
        try {
            start = formatter.parse(getDate(Long.parseLong(startDate)));
            if (endDate == null) {
                Calendar calendar = Calendar.getInstance();
                end = formatter.parse(getDate(calendar.getTimeInMillis()));
            } else
                end = formatter.parse(getDate(Long.parseLong(endDate)));

        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        //milliseconds
        if (start == null || end == null) {
            return 0;
        }

        long different = end.getTime() - start.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;
    }

    public static String base64Decode(String base64_string) {
        byte[] data = Base64.decode(base64_string, Base64.DEFAULT);
        String converted_string = null;
        try {
            converted_string = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return converted_string;
    }

    public static String base64Encode(String string) {
        String base64 = null;
        try {
            byte[] data = string.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return base64;
    }

    static public String getLotCodeByLocation(double latitude, double longitude) {
        /*double latitude = StartP.latitude;
        double longitude = StartP.longitude;*/
        String LotCode = null;

        Location currentLocation = new Location("point A");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);

        /////    40.0995671,-75.3037165           DHC-04
        double latDHC04 = 40.0995671;
        double longDHC04 = -75.3037165;
        Location locationB = new Location("point B");
        locationB.setLatitude(latDHC04);
        locationB.setLongitude(longDHC04);
        Float distanceDHC04 = currentLocation.distanceTo(locationB);

        ////     39.90962, -75.22429              DHP-05
        double latDHP05 = 39.90962;
        double longDHP05 = -75.22429;
        Location locationC = new Location("point C");
        locationC.setLatitude(latDHP05);
        locationC.setLongitude(longDHP05);
        Float distanceDHP05 = currentLocation.distanceTo(locationC);

        ///      40.1427045,-75.3921425          CV-51
        double latCV51 = 40.1427045;
        double longCV51 = -75.3921425;
        Location locationD = new Location("point D");
        locationD.setLatitude(latCV51);
        locationD.setLongitude(longCV51);
        Float distanceCV51 = currentLocation.distanceTo(locationD);

        ///      40.1226602,-75.3444571          BS-52
        double latBS52 = 40.1226602;
        double longBS52 = -75.3444571;
        Location locationE = new Location("point E");
        locationE.setLatitude(latBS52);
        locationE.setLongitude(longBS52);
        Float distanceBS52 = currentLocation.distanceTo(locationE);

        ///      40.1229971,-75.3456364          405-53
        double lat40553 = 40.1229971;
        double long40553 = -75.3456364;
        Location locationF = new Location("point F");
        locationF.setLatitude(lat40553);
        locationF.setLongitude(long40553);
        Float distance40553 = currentLocation.distanceTo(locationF);
        Log.e("distance40553", "test 1 = " + distance40553);

        ////     39.91622318649415,-75.22170383087598   KIA-LOT
        double latINT = 39.91622318649415;
        double longINT = -75.22170383087598;
        Location locationG = new Location("point G");
        locationG.setLatitude(latINT);
        locationG.setLongitude(longINT);
        Float distanceINT = currentLocation.distanceTo(locationG);

        ArrayList<Float> list = new ArrayList<>();
        list.add(distanceDHC04);
        list.add(distanceDHP05);
        list.add(distanceCV51);
        list.add(distanceBS52);
        list.add(distance40553);
        list.add(distanceINT);
        int minIndex = list.indexOf(Collections.min(list));

        switch (minIndex) {
            case 0:
                if (distanceDHC04 < 1500) {
                    LotCode = "DHC-04";
                } /*else {
                    LotCode = "Unknown";
                }*/
                break;
            case 1:
                if (distanceDHP05 < 1500) {
                    LotCode = "DHP-05";
                }
                break;
            case 2:
                if (distanceCV51 < 1500) {
                    LotCode = "CV-51";
                }
                break;
            case 3:
                if (distanceBS52 < 1500) {
                    LotCode = "BS-52";
                }
                break;
            case 4:
                if (distance40553 < 1500) {
                    LotCode = "405-53";
                }
                break;
            case 5:
                if (distanceINT < 1500) {
                    LotCode = "Int";
                }
                break;
        }
        return LotCode;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void hidekeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 2);
        }

    }

    public static void showkeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(1, 0);
        }
    }

    /**
     *
     * @param context activity context
     * @param title alert title
     * @param message alert message
     */
    public static void showAlert(final Context context, String title, String message) {
        DialogInterface.OnClickListener onClickPositive = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        showAlert(context, title, message, "Yes", "No", onClickPositive);
    }

    /**
     *
     * @param context activity context
     * @param title alert title
     * @param message alert message
     * @param btnTitlePositive text for positive button
     * @param btnTitleNegative text for negative button
     * @param onClickPositive Click listener for positive button
     */
    public static void showAlert(final Context context, String title, String message, String btnTitlePositive, String btnTitleNegative, DialogInterface.OnClickListener onClickPositive) {
        DialogInterface.OnClickListener onClickNegative = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        showAlert(context, title, message, btnTitlePositive, btnTitleNegative, onClickPositive, onClickNegative);
    }

    /**
     *
     * @param context
     * @param title
     * @param message
     * @param btnTitlePositive
     * @param btnTitleNegative
     * @param onClickPositive
     * @param onClickNegative
     */
    public static void showAlert(Context context, String title, String message, String btnTitlePositive, String btnTitleNegative, DialogInterface.OnClickListener onClickPositive, DialogInterface.OnClickListener onClickNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(true);

        builder.setPositiveButton(btnTitlePositive, onClickPositive);

        builder.setNegativeButton(btnTitleNegative, onClickNegative);

        AlertDialog alert = builder.create();
        alert.show();

    }



    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public static void showAlertWithNegativeButton(final Context context, String title, String message, DialogInterface.OnClickListener positiveButtonListener) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok", positiveButtonListener);

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //to colse activity
                ((Activity) context).finish();

            }
        });

        // Showing Alert Message
        alertDialog.show();

    }
}
