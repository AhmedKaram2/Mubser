package com.samebits.beacon.mubser.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Karam on 18/12/15.
 * A class, with general purpose utility methods (useful for many projects).
 */
public class Utils {
    public static final boolean DEBUGGABLE = true;
    private static final String KEY_APP_VERSION_CODE = "app_version_code_key";
    private static final String UAE_MOBILE_NUMBER_REJEX = "^(?:\\+971|00971|0)?(?:50|51|52|54|55|56|58)\\d{7}$";
    private static final String PACKAGE_SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
    private static final String PACKAGE_CHROME = "com.android.chrome";

    /**
     * get the hash key
     */
    public static String getHashKey(Context context) {
        String hashKey = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
//            logE("Failed to get hash key");
        }

        return hashKey;
    }


    /**
     * Validate email address.
     *
     * @param email the email to validate
     * @return true if valid email or false.
     */
    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidUAEMobileNumber(String number) {
        return matchRegex(UAE_MOBILE_NUMBER_REJEX, number);
    }

    /**
     * Get a shared preferences file named Const.SHARED_PREFERENCES_FILE_NAME, keys added to it must be unique
     *
     * @param ctx
     * @return the shared preferences
     */
    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(Constants.TAG, Context.MODE_PRIVATE);
    }

    public static void cacheBoolean(Context ctx, String k, Boolean v) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        prefs.edit().putBoolean(k, v).apply();
    }

    public static Boolean getCachedBoolean(Context ctx, String k, Boolean defaultValue) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        return prefs.getBoolean(k, defaultValue);
    }

    public static void cacheString(Context ctx, String k, String v) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        prefs.edit().putString(k, v).apply();
    }

    public static String getCachedString(Context ctx, String k, String defaultValue) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        return prefs.getString(k, defaultValue);
    }


    public static void cacheInt(Context ctx, String k, int v) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        prefs.edit().putInt(k, v).apply();
    }

    public static int getCachedInt(Context ctx, String k, int defaultValue) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        return prefs.getInt(k, defaultValue);
    }


    public static void cacheLong(Context ctx, String k, long v) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        prefs.edit().putLong(k, v).apply();
    }

    public static long getCachedLong(Context ctx, String k, long defaultValue) {
        SharedPreferences prefs = getSharedPreferences(ctx);
        return prefs.getLong(k, defaultValue);
    }


    public static void clearCachedKey(Context context, String key) {
        getSharedPreferences(context).edit().remove(key).apply();
    }

    /**
     * Gets a formatted % percent from numerator/denominator values.
     *
     * @param numerator
     * @param denominator
     * @param locale      the locale of the returned string.
     * @return the formatted percent.
     */
    public static String getPercent(int numerator, int denominator, Locale locale) {
        //http://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html
        NumberFormat nf = NumberFormat.getNumberInstance(locale);  //Locale.US, .....
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###.#");
        if (denominator == 0) {
            return df.format(0) + "%";
        }
        float percent = (numerator / (float) denominator) * 100;
        return df.format(percent) + "%";
    }


    /**
     * hide keyboard in edit text field
     */
    public static void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * hide keyboard in activity
     */
    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showLongToast(Context context, String text) {
//        DialogUtils.showAlertDialog(context, text, null);
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    public static void showLongToast(Context context, int textID) {
        Toast.makeText(context, textID, Toast.LENGTH_LONG).show();
//        DialogUtils.showAlertDialog(context, context.getString(textID), null);
    }

    public static void logE(String msg) {
        if (DEBUGGABLE) Log.e(Constants.LOG_TAG, msg == null ? "null" : msg);
    }

    /**
     * Executes the given AsyncTask Efficiently.
     *
     * @param task the task to execute.
     */
    public static void executeAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    /**
     * Remove '-' and other characters from mobile numbers and replace any + with 00
     *
     * @param oldNumber
     * @return the enhanced number
     */
    public static String enhanceMobileNumber(String oldNumber) {
        return oldNumber.replaceAll("[()\\s-]", "").replace("+", "00");
    }

    /**
     * Checks to see if EditText contains whitespace or no text
     *
     * @param et the EditText
     * @return true if EditText contains whitespace or no text otherwise false
     */
    public static boolean isEmpty(EditText et) {
        return TextUtils.isEmpty(et.getText().toString().trim());
    }

    /**
     * Checks to see if text is null or contains whitespace or no content
     *
     * @param charSequence
     * @return true if text contains whitespace or no content otherwise false
     */
    public static boolean isEmpty(CharSequence charSequence) {
        if (charSequence == null) return true;
        return TextUtils.isEmpty(charSequence.toString().trim());
    }

    public static void setEditError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }


    /**
     * Get the EditText text trimmed
     *
     * @param et
     * @return the EditText text trimmed
     */
    public static String getText(EditText et) {
        return et.getText().toString().trim();
    }

    public static String getText(TextView et) {
        return et.getText().toString().trim();
    }

    /**
     * Get the EditText text as integer
     *
     * @param et
     * @return the EditText text as integer
     */
    public static int getInt(EditText et) {
        String text = getText(et);
        return convertToInt(text);
    }

    public static void GoneEmptyText(String text, View view) {

        view.setVisibility(View.VISIBLE);
        if (text.equals("")) {
            view.setVisibility(View.GONE);
        }
    }


    public static void GoneEmptyText(String text, View view, View view1) {

        view.setVisibility(View.VISIBLE);
        if (text.equals("")) {

            view.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        }
    }


    /**
     * Gets the app version code.
     *
     * @param context
     * @return the version code.
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void cacheAppVersionCode(Context context) {
        cacheInt(context, KEY_APP_VERSION_CODE, getAppVersionCode(context));
    }

    public static int getCachedAppVersionCode(Context context) {
        return getCachedInt(context, KEY_APP_VERSION_CODE, Integer.MIN_VALUE);
    }

    /**
     * Get the application version name
     *
     * @param context
     * @return The app version name
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Gets the current app locale language like: ar, en, ....
     *
     * @return the current app locale language like: ar, en, ....
     */
    public static String getAppLanguage() {
        return Locale.getDefault().getLanguage().toLowerCase().substring(0, 2);
    }

    /**
     * Set the app language
     *
     * @param ctx  the application context
     * @param lang the language as ar, en, .....
     */
    public static void changeAppLocale(Context ctx, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
    }

    /**
     * method, used to format a double number as string with maximum 1 number after the point
     *
     * @param number
     * @return the formatted double as string
     */
    public static String formatDouble(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return String.format("%.1f", number).toString();
        }
    }

    /**
     * Checks if a specified service is running or not.
     *
     * @param ctx          the context
     * @param serviceClass the class of the service
     * @return true if the service is running otherwise false
     */
    public static boolean isServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * method, used to format the url to prevent app from crash when open browser intent
     *
     * @param url
     * @return the formatted url
     */
    public static String formatUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * method, used to check if string is null or empty
     *
     * @param str to check
     * @return boolean true if null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * method, used to check if list is null or empty
     *
     * @param list to check
     * @return boolean true if null or empty
     */
    public static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    /**
     * method, used to prepare the url and open it in the browser
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * method, used to check if app is installed in the device or not
     *
     * @param context
     * @param appPackageName
     * @return
     */
    public static boolean isAppInstalledAndEnabled(Context context, String appPackageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
            boolean installed = true;
            boolean enabled = info.applicationInfo.enabled;

            return installed && enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * method, used to share text to specific app
     *
     * @param context
     * @param appPackageName
     * @param text
     * @return
     */
    public static boolean shareTextToApp(Context context, String appPackageName, String text) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(appPackageName);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // app is not installed
            return false;
        }
    }

    /**
     * method, used to convert string number to double number
     *
     * @param number
     * @return
     */
    public static double convertToDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * method, used to convert string number to int number
     *
     * @param number
     * @return
     */
    public static int convertToInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * method, used to concatenate array of strings with the passed divider
     *
     * @param divider
     * @param strings
     * @return
     */
    public static String getFullString(String divider, String... strings) {
        String finalString = "";
        for (String str : strings) {
            if (!isNullOrEmpty(str)) {
                if (finalString.isEmpty()) {
                    finalString += str;
                } else {
                    finalString += divider + str;
                }
            }
        }

        return finalString;
    }

    /**
     * method, used to match the regex on the passed text and return true or false
     *
     * @param regex
     * @param text
     * @return
     */
    public static boolean matchRegex(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }

    /**
     * method, used to override a font in all the app
     *
     * @param context
     * @param staticFontName
     * @param fontAssetName
     */
    public static void overrideFont(Context context, String staticFontName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticFontName);
            staticField.setAccessible(true);
            staticField.set(null, regular);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method, used to open the phone intent using the passed phone number
     *
     * @param context
     * @param phone
     */
    public static void openPhoneIntent(Context context, String phone) {
        if (Utils.isNullOrEmpty(phone)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /**
     * method, used to open the email intent using the passed email address
     *
     * @param context
     * @param emailAddress
     */
    public static void openEmailIntent(Context context, String emailAddress) {
        if (Utils.isNullOrEmpty(emailAddress)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.setType("message/rfc822");
        context.startActivity(intent);
    }

    /**
     * method, used to open the map intent with passed params
     *
     * @param context
     * @param lat
     * @param lng
     */
    public static void openMapIntent(Context context, double lat, double lng) {
        openMapIntent(context, null, lat, lng);
    }

    /**
     * method, used to open the map intent with passed params
     *
     * @param context
     * @param title
     * @param lat
     * @param lng
     */
    public static void openMapIntent(Context context, String title, double lat, double lng) {
        String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng;
        if (!isNullOrEmpty(title)) {
            geoUri += " (" + title + ")";
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        context.startActivity(intent);
    }

    /**
     * method, used to set the text underlined in the text view
     *
     * @param textView
     * @param text
     */
    public static void setUnderlined(TextView textView, String text) {
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        textView.setText(spannable);
    }

    /**
     * method, used to trim string if not null or return null
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null) {
            return null;
        } else {
            return str.trim();
        }
    }

    /**
     * method, used to reverse array of objects and return it
     *
     * @param arr
     * @return
     */
    public static Object[] reverseArray(Object[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            Object tmpObject = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = tmpObject;
        }

        return arr;
    }

    /**
     * show key board in edit text field
     */
    public void showKeyboard(Activity activity, EditText et) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInputFromInputMethod(et.getWindowToken(), 0);
    }

    /**
     * method, used to check if custom tabs is available or not
     *
     * @param context
     * @return
     */
    public static boolean isChromeCustomTabsSupported(Context context) {
        Intent serviceIntent = new Intent(PACKAGE_SERVICE_ACTION);
        serviceIntent.setPackage(PACKAGE_CHROME);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !(resolveInfos == null || resolveInfos.isEmpty());
    }

    /**
     * method, used to encode string to base 64
     *
     * @param text
     * @return
     */
    public static String encodeToBase64(String text) {
        try {
            byte[] data = text.getBytes("UTF-8");
            String str = Base64.encodeToString(data, Base64.DEFAULT)
                    .replaceAll(" ", "")
                    .replaceAll("\\n", "");
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * method, used to hash string to sha-256
     *
     * @param text
     * @return
     */
    public static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean checkBoolean(String str) {
        // prepare the string
        str = str == null ? null : str.toLowerCase();

        // check it
        if (str == null) {
            return false;
        } else if ("true".equals(str)) {
            return true;
        } else if ("false".equals(str)) {
            return false;
        } else if ("0".equals(str)) {
            return false;
        } else {
            try {
                return Integer.parseInt(str) != 0;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
