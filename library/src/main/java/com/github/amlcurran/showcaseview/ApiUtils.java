package com.github.amlcurran.showcaseview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ApiUtils {

    public boolean isCompatWith(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    public boolean isCompatWithHoneycomb() {
        return isCompatWith(Build.VERSION_CODES.HONEYCOMB);
    }

    @TargetApi(14)
    public void setFitsSystemWindowsCompat(View view) {
        if (isCompatWith(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            view.setFitsSystemWindows(true);
        }
    }

    public static DisplayMetrics getDisplayMetrics(final Context context) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static int getScreenWidth(final Context context) {
        if (context == null)
            return 0;
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeight(final Context context) {
        if (context == null)
            return 0;
        return getDisplayMetrics(context).heightPixels;
    }
}
