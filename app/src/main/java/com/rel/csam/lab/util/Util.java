package com.rel.csam.lab.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.TintContextWrapper;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.webkit.URLUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

//    /**
//     * Android ID 가져오기
//     *
//     * @return
//     */
//    public static String getAndroidID() {
//        String androidID = Secure.getString(App.Companion.getContentResolver(), Secure.ANDROID_ID);
//
//        if (androidID == null) {
//            androidID = "";
//        }
//
//        return androidID;
//    }

    /**
     * UUID 가져오기
     * \n Ver 2.0.8에서 추가  - 김원태
     * \n Android ID 중복과 빈값 발생으로 인한 유니크한 아이디 보강
     * \n 우선순위 1. Android ID 2. IMEI 3. MAC ADDRESS
     * \n 조합 1(무조건 포함) + [ 2 | 3 ] : 2값이 없을 경우 3을 붙임
     *
     * @return
     */
    public static String getUUID(Context context) {

        String androidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        if (androidID == null) {
            androidID = "";
        }

        // 권한이 되는사람은 있던 값 쓰는게 맞을 것 같습니다.
//        String mac = getMacAddress(context);
//        String uuid = androidID;
//
//        uuid = String.format("%s____%s", androidID, mac);

        // 권한 때문에 IMEI 값 사용하지 않는다.
        String imei = getIMEI(context);
        String mac = getMacAddress();

        String uuid = androidID;
        if (imei == null || imei.equals("")) {
            uuid = String.format("%s____%s", androidID, mac);
        } else {
            uuid = String.format("%s____%s", androidID, imei);
        }
        return uuid;
    }

    /**
     * IMEI 값 가져오기
     *
     * @param context
     * @return 없으면 null 리턴
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {

            String imei = null;

//            if (Qtalk.PERMISSION.PHONE.isCheck()) {
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return null;
//                }
//                imei = telephonyManager.getDeviceId();
//            }

            if (imei == null || imei.length() < 1) {
                return null;
            } else {
                return imei;
            }
        } else {
            return null;
        }
    }

    /**
     * Wifi Mac Address 가져오기
     *
     * @return 없으면 null 리턴
     */
    public static String getMacAddress() {
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//        if (wifiManager != null) {
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            if (wifiInfo != null) {
//                String macAddress = wifiInfo.getMacAddress();
//                return macAddress;
//            }
//        }
//
//        return null;

        String interfaceName = "wlan0";
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {

        } // for now eat exceptions

        return "";
    }

    public static boolean sendEmailForm(String emailStr) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();

    }

//    /**
//     * Send Email Activity로 가기
//     *
//     * @param context
//     * @param email
//     * @param title
//     * @param contents
//     */
//    public static void sendEmail(Context context, String email, String title, String contents) {
//        StartActivityModel activityModel = new StartActivityModel();
//        activityModel.bindListener = startActivityModel ->
//                startActivityModel
//                        .intent(Intent.createChooser(new Intent(Intent.ACTION_SEND), "Choose Email Client"))
//                        .putExtra(Intent.EXTRA_EMAIL, email)
//                        .putExtra(Intent.EXTRA_SUBJECT, title)
//                        .putExtra(Intent.EXTRA_TEXT, contents)
//                        .setType("text/plain");
//
//        ((ViewModelActivity)context).viewModel.startActivity(activityModel);
//
//    }

    /**
     * 현재 앱버전 정보
     *
     * @param context
     * @return
     */
    public static String getAppCurrentVersion(Context context) {
        String version = "";
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 업데이트를 해야하는 지 체크
     *
     * @param currentVersion
     * @param compVersion
     * @return
     */
    public static boolean checkUpdateVersion(String currentVersion, String compVersion) {
        if (currentVersion == null || compVersion == null) {
            return false;
        }

        boolean isUpdateApp = false;

        isUpdateApp = !currentVersion.equalsIgnoreCase(compVersion);

        return isUpdateApp;
    }

//    public static String getTimeMinute(String time) {
//        String min = "";
//
//        try {
//            int timeInt = Integer.valueOf(time);
//            NumberFormat formatter = new DecimalFormat("00");
//            NumberFormat formatterForSmallNum = new DecimalFormat("0");
//
//            int minuteInt = timeInt / 60;
//            int secondInt = timeInt % 60;
//
//            Context context = Qtalk.instance;
//            String minText = context.getString(R.string.keypad_min);
//            String secText = context.getString(R.string.keypad_sec);
//
//            String minute = null;
//            String second = null;
//
//            if (minuteInt < 10) {
//                minute = formatterForSmallNum.format(minuteInt);
//            } else {
//                minute = formatter.format(minuteInt);
//            }
//
//            if (secondInt < 10) {
//                second = formatterForSmallNum.format(secondInt);
//            } else {
//                second = formatter.format(secondInt);
//            }
//
//            if (second.equals("00") || second.equals("0")) {
//                min = String.format("%s %s", minute, minText);
//            } else {
//                min = String.format("%s %s %s %s", minute, minText, second, secText);
//            }
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        return min;
//    }

    public static String urlDecode(String target) {
        String decodeString = "";

        try {
            decodeString = URLDecoder.decode(target, "utf-8");
        } catch (UnsupportedEncodingException e) {
            decodeString = "";
        }

        return decodeString;
    }

    public static String convertUniToStr(String uni) {

        StringBuffer buff = new StringBuffer();
        if (uni != null) {
            Pattern pattern = Pattern.compile("\\\\u....");
            Matcher matcher = pattern.matcher(uni);
            boolean result = matcher.find();
            while (result) {
                String matchStr = matcher.toMatchResult().group();
                String appStr = "%" + matchStr.substring(2, 4) + "%" + matchStr.substring(4);
                try {
                    appStr = URLDecoder.decode(appStr, "UTF-16");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                matcher.appendReplacement(buff, appStr);
                result = matcher.find();
            }
            matcher.appendTail(buff);
        }

        return unescapeCString(buff.toString());
    }

    public static String convertUniToStrForSpann(SpannableStringBuilder uni) {

        Pattern pattern = Pattern.compile("\\\\u....");
        Matcher matcher = pattern.matcher(uni);
        StringBuffer buff = new StringBuffer();
        boolean result = matcher.find();
        while (result) {
            String matchStr = matcher.toMatchResult().group();
            String appStr = "%" + matchStr.substring(2, 4) + "%" + matchStr.substring(4);
            try {
                appStr = URLDecoder.decode(appStr, "UTF-16");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            matcher.appendReplacement(buff, appStr);
            result = matcher.find();
        }
        matcher.appendTail(buff);

        return unescapeCString(buff.toString());
    }

    /**
     * Unescape any C escape sequences (\n, \r, \\, \ooo, etc) and return the
     * resulting string.
     */
    public static String unescapeCString(String s) {
        if (s.indexOf('\\') < 0) {
            // Fast path: nothing to unescape
            return s;
        }

        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; ) {
            char c = s.charAt(i++);
            if (c == '\\' && (i < len)) {
                c = s.charAt(i++);
                switch (c) {
                    case 'a':
                        c = '\007';
                        break;
                    case 'b':
                        c = '\b';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    case 'v':
                        c = '\013';
                        break;
                    case '\\':
                        c = '\\';
                        break;
                    case '?':
                        c = '?';
                        break;
                    case '\'':
                        c = '\'';
                        break;
                    case '"':
                        c = '\"';
                        break;

                    default: {
                        if ((c == 'x') && (i < len) && isHex(s.charAt(i))) {
                            // "\xXX"
                            int v = hexValue(s.charAt(i++));
                            if ((i < len) && isHex(s.charAt(i))) {
                                v = v * 16 + hexValue(s.charAt(i++));
                            }
                            c = (char) v;
                        } else if (isOctal(c)) {
                            // "\OOO"
                            int v = (c - '0');
                            if ((i < len) && isOctal(s.charAt(i))) {
                                v = v * 8 + (s.charAt(i++) - '0');
                            }
                            if ((i < len) && isOctal(s.charAt(i))) {
                                v = v * 8 + (s.charAt(i++) - '0');
                            }
                            c = (char) v;
                        } else {
                            // Propagate unknown escape sequences.
                            sb.append('\\');
                        }
                        break;
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isOctal(char c) {
        return (c >= '0') && (c <= '7');
    }

    private static boolean isHex(char c) {
        return ((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'));
    }

    private static int hexValue(char c) {
        if ((c >= '0') && (c <= '9')) {
            return (c - '0');
        } else if ((c >= 'a') && (c <= 'f')) {
            return (c - 'a') + 10;
        } else {
            return (c - 'A') + 10;
        }
    }

    public static int colorStringCheck(String colorText) {
        return colorStringCheck(colorText, null);
    }

    public static int colorStringCheck(String colorText, String defaultColor) {
        if (!TextUtils.isEmpty(colorText)) {
            if (!colorText.contains("#"))
                return Color.parseColor("#" + colorText);
            else
                return Color.parseColor(colorText);
        } else {
            if (!TextUtils.isEmpty(defaultColor)) {
                return Color.parseColor(defaultColor);
            } else {
                return 0;
            }
        }
    }

    public static void checkKeyboardHeight(View targetView, int softKeyboard) {
        Context context = Util.getSafeContext(targetView.getContext());
        Rect r = new Rect();
        targetView.getWindowVisibleDisplayFrame(r);

        int screenHeight = targetView.getRootView().getHeight();
        int heightDiffrence = screenHeight - (r.bottom - r.top);

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            heightDiffrence -= context.getResources().getDimensionPixelSize(resourceId);
        }

        boolean isPortrait = true;

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            int orientation = activity.getWindowManager().getDefaultDisplay().getRotation();

            switch (orientation) {
                case Surface.ROTATION_0:
                    isPortrait = true;
                    break;
                case Surface.ROTATION_90:
                    isPortrait = false;
                    break;
                case Surface.ROTATION_180:
                    isPortrait = true;
                    break;
                case Surface.ROTATION_270:
                    isPortrait = false;
                    break;
            }

            /**
             * table 계열은 portrait값이 false일때가 세로이므로 아래의 처리.
             */
            if ((orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) && isTablet(context)) {
                isPortrait = true;
            }
        }

        if (heightDiffrence > 200 && isPortrait) {
//            SharedPreferenceManager.getInstance().setKeyboardHeight(heightDiffrence - softKeyboard);
        }
    }


//    public static void setKeyboardHeightListener(final Context context, final View targetView, final OnGetKeyboardHeightListener listener) {
//        targetView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                targetView.getWindowVisibleDisplayFrame(r);
//
//                int screenHeight = targetView.getRootView().getHeight();
//                int heightDiffrence = screenHeight - (r.bottom - r.top);
//
//                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//
//                if (resourceId > 0) {
//                    heightDiffrence -= context.getResources().getDimensionPixelSize(resourceId);
//                }
//
//                boolean isPortrait = true;
//
//                if (context instanceof Activity) {
//                    Activity activity = (Activity) context;
//                    int orientation = activity.getWindowManager().getDefaultDisplay().getRotation();
//
//                    switch (orientation) {
//                        case Surface.ROTATION_0:
//                            isPortrait = true;
//                            break;
//                        case Surface.ROTATION_90:
//                            isPortrait = false;
//                            break;
//                        case Surface.ROTATION_180:
//                            isPortrait = true;
//                            break;
//                        case Surface.ROTATION_270:
//                            isPortrait = false;
//                            break;
//                    }
//
//                    /**
//                     * table 계열은 portrait값이 false일때가 세로이므로 아래의 처리.
//                     */
//                    if ((orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) && isTablet(context)) {
//                        isPortrait = true;
//                    }
//                }
//
//                if (heightDiffrence != 0 && heightDiffrence > 200 && isPortrait) {
//                    SharedPreferenceManager.getInstance().setKeyboardHeight(heightDiffrence);
//
//                    if (listener != null) {
//                        listener.onGetKeyboardHeight(heightDiffrence);
//                    }
//                }
//            }
//        });
//    }

    private static boolean isTablet(Context context) {
        int xlargeBit = 4; // Configuration.SCREENLAYOUT_SIZE_XLARGE;  // upgrade to HC SDK to get this
        Configuration config = context.getResources().getConfiguration();
        return (config.screenLayout & xlargeBit) == xlargeBit;
    }

    public static boolean isTopRunningActivity(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info = activityManager.getRunningTasks(100);

        if (info != null && info.size() > 0) {
            RunningTaskInfo runningTaskInfo = info.get(0);
            if (runningTaskInfo != null && runningTaskInfo.topActivity.getClassName().equals(className)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param context
     * @param packageName
     * @return TODO : RunningTaskInfo 가 deprecate 된다고 하니 수정해야함
     * Use an AccessibilityService 참고
     */
    public static boolean isTopRunningApplication(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info = activityManager.getRunningTasks(100);

        if (info != null && info.size() > 0) {
            RunningTaskInfo runningTaskInfo = info.get(0);
            if (runningTaskInfo != null && runningTaskInfo.topActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isActivityRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info = activityManager.getRunningTasks(100);

        for (RunningTaskInfo runningTaskInfo : info) {
//			Log.d("QUtil", "runningTaskInfo.baseActivity.getClassName() : " + runningTaskInfo.baseActivity.getClassName());

            if (runningTaskInfo.baseActivity.getClassName().equals(className)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isScreenON(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    public static String getLocaleCodeByDeviceSetting(Context context) {
        return context.getResources().getConfiguration().locale.getCountry();
    }

//    public static String getLangCodeByDeviceSetting() {
////        Context context = Qtalk.instance;
//        String langCode = SharedPreferenceManager.getInstance().getLocale(); // ja, ko, zh ...
//        String countryCode = context.getResources().getConfiguration().locale.getCountry(); // HK, CN ...
//
//        String returnCode = "en";
//
//        // ***  android에서 values-zh-rHANS, rHANT 지원이 되지 않음 - 근데 왜 설정이 가능?
//
//        if (langCode != null) {
//            if (langCode.equals("zh")) {
//                if (countryCode != null) {
//                    if (countryCode.equals("HK") || countryCode.equals("TW") || countryCode.equals("HANT")) {
//                        //returnCode = langCode + "-tw"; // 언어 설정 변경 - 기본 zh : 홍콩, 대만 번체, HANT - zh
//                        returnCode = langCode;
//                    } else {
//                        returnCode = langCode + "-cn"; // 언어 설정 변경 - 기본 zh-cn : 간체, HANS - zh-cn
//                    }
//                } else {
//                    returnCode = langCode + "-cn";
//                }
//            } else {
//                if (langCode.equals("ja") || langCode.equals("ko")) {
//                    returnCode = langCode;
//                } else {
//                    returnCode = "en";
//                }
//            }
//        }
//
//        return returnCode;
//
//    }

    public static String getOnlyPhoneNumber(String countryNumberWithPhoneNumber) {
        String returnNumber = "";

        if (!TextUtils.isEmpty(countryNumberWithPhoneNumber)) {

            int idx1 = countryNumberWithPhoneNumber.indexOf("-");

            if (idx1 >= 0) {
                returnNumber = countryNumberWithPhoneNumber.substring(idx1 + 1);
            } else {
                returnNumber = countryNumberWithPhoneNumber;
            }
        }

        return returnNumber;
    }

    /**
     * 위치정보 사용, 네트워크 상태 check
     *
     * @param context
     * @return String
     */
//    public static String getLocationInfo(Context context) {
//        //Location checking
//        LocationManager locManager;
//        double myLatitude = 0, myLongitude = 0;
//
//        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setSpeedRequired(false);
//        criteria.setCostAllowed(true);
//        String provider = locManager.getBestProvider(criteria, true);
//
//        Location location = null;
//        try {
//            location = locManager.getLastKnownLocation(provider);
//        } catch (Exception e) {
//            //GMKT_Log.e(e.getLocalizedMessage());
//        }
//
//        if (location != null) {
//            myLatitude = location.getLatitude();
//            myLongitude = location.getLongitude();
//        }
//
//
//        // 포멧 : 위도_경도
//
//        return String.format("%f/%f", myLatitude, myLongitude);
//    }

//    /**
//     * 전화번호 체크해서 +국가코드-전화번호 로 변경해줌
//     * ex. +89-00000000
//     *
//     * @param rawNum
//     * @return
//     */
//    public static String validNumber(Activity activity, String rawNum) {
//        String resultNum = "";
//        String strNation = Qtalk.instance.getNationCd();
//        CountryNumberData data;
//
//        // 전화번호 국가코드 있을 때
//        if (rawNum.startsWith("+")) {
//            rawNum = rawNum.replaceAll("\\D", "");
//            if (rawNum.length() > 9) {
//                resultNum = "+" + rawNum.substring(0, 2) + "-" + rawNum.substring(2);
//            }
//
//        } else { // 국가코드 없을 때
//            rawNum = rawNum.replaceAll("\\D", "");
//            if (rawNum.length() > 7) {
//                TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//                String nation_cd = tm.getNetworkCountryIso().toUpperCase();
//
//                if (nation_cd.length() == 2)
//                    data = CountryCallCodeManager.getInstance().getByNationCd(nation_cd);
//                else
//                    data = CountryCallCodeManager.getInstance().getByNationCd(strNation);
//
//                resultNum = "+" + data.getCountryNumber().trim() + "-" + rawNum;
//            }
//        }
//        return resultNum;
//    }

    /**
     * 네트워크 상태 check
     *
     * @param context
     * @return String
     */
    public static String getNetworkState(Context context) {
        //wifi&3G checking
        ConnectivityManager connManager;
        NetworkInfo wifiInfo, mobileInfo;

        connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        String network = "None";

        if (wifiInfo != null && wifiInfo.isConnected()) {
            network = "WiFi";
        } else if (mobileInfo != null && mobileInfo.isConnected()) {
            network = "3G";
        }

        return String.format("%s", network);
    }

    /**
     * Date : 2014. 8. 14.
     * Creater : cookie
     * Description : 값이 있으면 true
     *
     * @param strColum
     * @return
     */
    public static boolean isSafeEmptyCheck(String... strColum) {
        boolean bool = true;
        int count = strColum.length;
        for (String str : strColum) {
            if (TextUtils.isEmpty(str)) {
                bool = false;
                break;
            }
        }
        return bool;
    }

    /**
     * 안전하게 똑같은 경우만 비교하고자 하는겁니다.
     * 두개의 인자중 하나라도 빈값이 들어온다면 true 값을 체크하는것은 문제가 없겠지만,
     * false값은 값이 틀리기 때문인지
     * 둘중 값이 없는게 있기때문인지는 지금 결과로만은 알수없음을 분명히 적어놓습니다.
     * <p/>
     * 다시, 강조해드리면 false값은 A 가 B가 아니다 만이 아니라 A가 없거나 B가 없다도
     * 포함된다는 말입니다.
     * <p/>
     * by sam
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean safeEqual(String s1, String s2) {
        return isSafeEmptyCheck(s1, s2) && s1.equals(s2);
    }

    public static boolean safeEqualStr(String s1, String s2) {
        // "," 로 구분
        String[] str = null;
        if (!TextUtils.isEmpty(s2)) {
            str = s2.split(",");
            if (str != null && str.length > 0) {
                for (int i = 0; i < str.length; i++) {
                    if (isSafeEmptyCheck(s1, str[i]) && s1.equals(str[i])) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    public static String safeString(String s1) {
        return s1 == null ? "" : s1;
    }

    public static boolean safeEqualYn(String s1) {
        return safeEqual(s1, "Y");
    }

    public static boolean safeContains(String str1, String str2) {

        boolean val = false;

        if (isSafeEmptyCheck(str1, str2)) {
            if (str1.contains(str2) || str2.contains(str1)) {
                val = true;
            }
        }

        return val;
    }

    public static boolean safeStartsWith(String str1, String str2) {
        boolean val = false;

        if (isSafeEmptyCheck(str1, str2)) {
            if (str1.startsWith(str2)) {
                val = true;
            }
        }
        return val;
    }

    public static String safeSubString(String str, int end) {

        if (str == null) str = "";

        if (str.length() > end) {
            str = str.substring(0, end);
        }

        return str;
    }

    public static int safeParseInt(String str) {

        int val = 0;
        try {
            if (!TextUtils.isEmpty(str)) {
                val = Integer.parseInt(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    public static boolean safePhoneNumberEqual(String number1, String number2) {

        if (number1.contains("-")) {
            number1 = number1.split("-")[1];
        }

        if (number2.contains("-")) {
            number2 = number2.split("-")[1];
        }

        if (safeEqual(number1, number2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Date : 2013. 6. 21.
     * Creator : nadan
     * Description : 폰번호가 이상하게 들어왔을 경우 올바르게 치환하여 넣어준다. (모든 케이스가 적용되어 있지않음)
     *
     * @param phoneNumber
     * @return
     */
    public static String getCorrectPhoneNumber(String phoneNumber) {

        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.contains(" ")) {
                phoneNumber = phoneNumber.replace("-", "");
                phoneNumber = phoneNumber.replace(" ", "-");
            } else if (!phoneNumber.contains("-")) {
                StringBuffer sb = new StringBuffer(phoneNumber);
                sb.insert(3, "-");
                phoneNumber = sb.toString();
            }
        }

        return phoneNumber;
    }


    /**
     * Date : 2013. 8. 19.
     * Creator : cookie
     * Description : 글자로 integer값얻기
     *
     * @param strVal
     * @return
     */
    public static int getIntegerValueToString(String strVal) {
        int countInt = 0;
        try {
            if (!TextUtils.isEmpty(strVal)) {
                countInt = Integer.parseInt(strVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countInt;
    }

    /**
     * Date : 2013. 8. 19.
     * Creator : cookie
     * Description : 글자로 int값 얻기
     *
     * @param strVal
     * @return
     */
    public static int getIntValueToString(String strVal) {
        return getIntegerValueToString(strVal);
    }

    /**
     * Date : 2013. 8. 19.
     * Creator : cookie
     * Description : 내 OS 버젼이 체크하고자 하는 버젼보다 큰지 체크
     *
     * @param version
     * @return
     */
    public static boolean checkAndroidVersionLargerThan(String version) {

        boolean checkVal = true;

        String releaseVersion = Build.VERSION.RELEASE.toString();

        try {
            releaseVersion = releaseVersion.replace(".", "");
            version = version.replace(".", "");

            int releaseVersionInt = Integer.parseInt(releaseVersion);
            int toCheckVersion = Integer.parseInt(version);

            checkVal = releaseVersionInt >= toCheckVersion;
        } catch (Exception e) {
            e.printStackTrace();
            checkVal = false;
        }

        return checkVal;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.i("QUtil", "checkPlayService(), This device is not supported.");
            return false;
        }

        return true;
    }

    /**
     * Date : 2014. 3. 24.
     * Creator : nadan
     * Description : 앞으로 11자리의 내 qid를 가져오는건 이 메소드로 호출하며, 기존 사용하고 있는것 들은
     * userInfo를 메모리에 올려서 항상 그 메모리를 통해 접근 (없으면 디비사용) 하는 방식으로 교체한다.
     *
     * @return
     */
//    public static String getMyQidFor11() {
//        return getMyQidFor11(Qtalk.instance);
//    }

    /**
     * Date : 2014. 3. 24.
     * Creator : nadan
     * Description : 앞으로 11자리의 내 qid를 가져오는건 이 메소드로 호출하며, 기존 사용하고 있는것 들은
     * userInfo를 메모리에 올려서 항상 그 메모리를 통해 접근 (없으면 디비사용) 하는 방식으로 교체한다.
     *
     * @return
     */
//    public static String getMyQidFor11(Context context) {
//
//        if (!DeviceInfo.isLogin()) return null;
//
//        String myQid = Contacts.getInstance().getMyInfo().getqIdNoneDevice();
//        if (TextUtils.isEmpty(myQid)) {
//            myQid = UserTable.getBasicInfo(context, Constants.BASIC_INFO_KEY_QID_NO_DEVICE);
//        }
//
//        if (!TextUtils.isEmpty(myQid) && myQid.length() > 11) {
//            myQid = myQid.substring(0, 11);
//        }
//
//        return myQid;
//    }

    public static int dpToPixel(View view, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, view.getResources().getDisplayMetrics());
    }

    public static float spToPixel(View view, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, view.getResources().getDisplayMetrics());
    }

    public static float pxToDp(View view, float px) {
        Resources resources = view.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        float dp = px / (metrics.density);

        return dp;
    }

    /**
     * Date : 2014. 8. 6.
     * Creator : nadan
     * Description :
     *
     * @param activity
     * @return
     */
    public static int getDisplayWidthPixel(Activity activity, Bitmap bitmap) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int smallerPixel = displayMetrics.widthPixels;
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            smallerPixel = displayMetrics.heightPixels;
        }

        int displayHalfWidth = smallerPixel / 2;

        int bitmapWidth = 0;

        if (bitmap != null) {
            bitmapWidth = bitmap.getWidth();
        } else {
            return displayHalfWidth;
        }

        int returnValue = (displayHalfWidth * bitmapWidth) / 300;

        if (returnValue > displayHalfWidth) {
            returnValue = displayHalfWidth;
        }

        return returnValue;
    }

    public static int getDisplayMaxWidthPixel(Activity activity, boolean isHalf) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int smallerPixel = displayMetrics.widthPixels;
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            smallerPixel = displayMetrics.heightPixels;
        }

        int displayHalfWidth = smallerPixel;
        if (isHalf) {
            displayHalfWidth = smallerPixel / 2;
        }

        return displayHalfWidth;
    }


    /**
     * google playstore 에 관련 어플리케이션 다운로드 페이지로 이동.
     *
     * @param context
     * @param packageName
     */

    private static void moveToMarketForAppDownload(Context context, String packageName) {
        String marketUrl = "market://details?id=" + packageName;
        Uri uri = Uri.parse(marketUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // soft navi height
    public static int getSoftbarHeight(Context context) {
        Resources resources = context.getResources();
        int softkeyHeight = 0;
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean result = id > 0 && resources.getBoolean(id);

        if (result) {
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                softkeyHeight = resources.getDimensionPixelSize(resourceId);
            }
        }
        return softkeyHeight;
    }

    public static String getDecimalPrice(String svcNationCd, double price) {
        String currencyPrice = "";

        NumberFormat numberFormat = null;

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        decimalFormatSymbols.setGroupingSeparator(',');
        decimalFormatSymbols.setDecimalSeparator('.');

        Locale locale = null;

        String decimalPrice = "";
        if (svcNationCd.equals("SG")) {
            locale = new Locale("en", "SG");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            decimalPrice = numberFormat.format(price);
            currencyPrice = decimalPrice;
        } else if (svcNationCd.equals("JP")) {
            locale = new Locale("ja", "JP");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            numberFormat.setMaximumFractionDigits(0);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            decimalPrice = numberFormat.format(price);
            currencyPrice = decimalPrice;
        } else if (svcNationCd.equals("CN")) {
            locale = new Locale("zh", "CN");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            decimalPrice = numberFormat.format(price);
            decimalPrice = decimalPrice.replace(".00", "");

            currencyPrice = decimalPrice;
        } else if (svcNationCd.equals("HK")) {
            locale = new Locale("zh", "HK");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            decimalPrice = numberFormat.format(price);
            currencyPrice = decimalPrice;
        } else if (svcNationCd.equals("US")) {
            locale = new Locale("en", "US");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            decimalPrice = numberFormat.format(price);
            currencyPrice = decimalPrice;
        } else if (svcNationCd.equals("ID")) {
            locale = new Locale("in", "ID");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);
            numberFormat.setMaximumFractionDigits(0);

            decimalPrice = numberFormat.format(price);
            currencyPrice = decimalPrice;
        } else if (svcNationCd.equals("MY")) {
            locale = new Locale("ms", "MY");
            numberFormat = NumberFormat.getCurrencyInstance(locale);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            decimalPrice = numberFormat.format(price);
            currencyPrice = decimalPrice;
        }

        // 앞 뒤에 공백문자, nbsp 가 있는지 채크 해주는 코드
        if (Util.isSafeEmptyCheck(currencyPrice)) {
            currencyPrice = currencyPrice.replace('\u00A0', ' ').trim();
        }

        return currencyPrice;
    }


    public static void deleteRemoteImageDir(Context inContext) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + inContext.getPackageName() + "/files/remote_photo/";

        File dir = new File(dirPath);
        File[] childFileList = dir.listFiles();
        if (dir.exists()) {
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    DeleteFile(childFile.getAbsolutePath());     //하위 디렉토리 루프
                } else {
                    childFile.delete();    //하위 파일삭제
                }
            }
            dir.delete();    //root 삭제
        }
    }

    public static void DeleteFile(String path) {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for (File childFile : childFileList) {
            if (childFile.isDirectory()) {
                DeleteFile(childFile.getAbsolutePath());    //하위 디렉토리 루프
            } else {
                childFile.delete();    //하위 파일삭제
            }
        }
        file.delete();
    }

    public static boolean LoadLibrary(Context context, String libraryname) {
        StringBuilder strblibpath = new StringBuilder(128);
        File f;
        String strlibpath;
        strblibpath.append("/data/data/");
        strblibpath.append(context.getPackageName());
        strblibpath.append("/lib/lib");
        strblibpath.append(libraryname);
        strblibpath.append(".so");
        strlibpath = strblibpath.toString();
        f = new File(strlibpath);
        if (f.exists()) {
            try {
                System.load(strlibpath);
                return true;
            } catch (java.lang.UnsatisfiedLinkError ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public interface ImageLoaderListener {
        void onComplete(Bitmap bitmap);
    }

    public static HashMap<String, String> parsingUploadResult(String strReturn) {
        HashMap<String, String> hmReturn = new HashMap<String, String>();

        if (strReturn != null) {
            String[] arrStrItemList = strReturn.split(",");

            for (String string : arrStrItemList) {
                if (string != null) {
                    String[] arrStrItem = string.split(":");
                    if (arrStrItem != null && arrStrItem.length == 2) {
//                        String key = arrStrItem[0].replace(" ","");
//                        key = key.replace("\"","");
                        hmReturn.put(arrStrItem[0], arrStrItem[1]);
                    } else if (arrStrItem != null && arrStrItem.length > 2) {
                        // 시간의경우는 "00:00:00으로 표시되어 length 가 2보다 크다
                        try {
                            String item = arrStrItem[1] + ":";
                            for (int i = 2; i < arrStrItem.length; i++) {
                                item = item + arrStrItem[i];
                                if (i != arrStrItem.length - 1) {
                                    item = item + ":";
                                }
                            }

                            hmReturn.put(arrStrItem[0], item);
                        } catch (Exception e) {
                            hmReturn.put(arrStrItem[0], arrStrItem[1]);
                        }
                    }
                }
            }
        }

        return hmReturn;
    }

    public static String getValidYoutubeVideoID(String url) {

        if (url == null) {
            return null;
        }

        if (URLUtil.isValidUrl(url)) {
            if (!url.contains("youtu.be") && !url.contains("youtube.com")) {
                return null;
            }
        }

        Pattern pattern = Pattern.compile(
                //"^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                "^https?://(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\n]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return null;
    }

    public static String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }


    public static String getSrchType(String searchValue) {
        if (searchValue == null) {
            searchValue = "";
        }
        searchValue = searchValue.trim();

        // 이름검색
        String searchGbn = "CUSTNM";

        // 이메일 정규식
        Pattern emailPattern = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");
        Matcher m = emailPattern.matcher(searchValue);

        if (m.matches()) {
            searchGbn = "EMAIL";
        } else {
            try {
                if (searchValue != null && searchValue.length() >= 8) {
                    Long.parseLong(searchValue);
                    searchGbn = "TELNO";
                } else {
                    searchGbn = "CUSTNM";
                }
            } catch (Exception e) {
                searchGbn = "CUSTNM";
            }
        }

        return searchGbn;
    }

    public static StringBuilder getCommaArrStr(Set<Integer> keySet) {
        StringBuilder sb = new StringBuilder();

        for (Integer key : keySet) {
            if (sb.length() > 0) {
                sb.append(",");
            }

            sb.append(key);
        }

        return sb;
    }


    public static boolean permissionCheck(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getNationFormTelephony(Context context) {
        String nation = "sg";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            nation = tm.getNetworkCountryIso().toLowerCase();

            nation = "";

            if (TextUtils.isEmpty(nation)) {
                try {
                    if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                        Class<?> c = Class.forName("android.os.SystemProperties");
                        Method get = c.getMethod("get", String.class);

                        String homeOperator = ((String) get.invoke(c, "ro.cdma.home.operator.numeric"));
                        nation = homeOperator.substring(0, 3).toLowerCase(); // the last three digits is MNC

                    } else {
                        nation = context.getResources().getConfiguration().locale.getCountry().toLowerCase();
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(nation)) {
            nation = "sg";
        }

        String serviceNation;

        if (nation.equals("sg") || nation.equals("hk") || nation.equals("th") || nation.equals("ph") || nation.equals("vn")) {
            serviceNation = "SG";
        } else if (nation.equals("id")) {
            serviceNation = "ID";
        } else if (nation.equals("my")) {
            serviceNation = "MY";
        } else {
            serviceNation = "US";
        }

        return serviceNation;
    }

    //    API 19에서 context가 tintContextWrapper로 떨어지는 문제
    public static Context getSafeContext(Context ctx) {
        if (ctx instanceof TintContextWrapper) {
            return ((TintContextWrapper)ctx).getBaseContext();
        } else {
            return ctx;
        }
    }

    //  낮은 버전에서 context.getDrawable(resId), resource.getDrawable(res,theme) 안되는 현상 수정 메소드
    public static Drawable getSafeDrawable(Context context, int resId) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(resId, null);
        } else {
            drawable = context.getResources().getDrawable(resId);
        }
        return drawable;

    }

}
