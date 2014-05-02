package com.github.amlcurran.showcaseview;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

public class ApiUtils {

    public boolean isCompatWith(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    @TargetApi(14)
    public void setFitsSystemWindowsCompat(View view) {
        if (isCompatWith(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            view.setFitsSystemWindows(true);
        }
    }
}
