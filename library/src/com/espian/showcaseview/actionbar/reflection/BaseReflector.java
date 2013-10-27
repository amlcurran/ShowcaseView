package com.espian.showcaseview.actionbar.reflection;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

/**
 * Base class which uses reflection to determine how to showcase Action Items and Action Views.
 */
public abstract class BaseReflector {

    public abstract View getHomeButton();

    public abstract void showcaseActionItem(int itemId);

    public ViewParent getActionBarView() {
        return getHomeButton().getParent().getParent();
    }

    public static BaseReflector getReflectorForActivity(Activity activity) {
        switch (searchForActivitySuperClass(activity)) {
            case STANDARD:
                return new ActionBarReflector(activity);
            case APP_COMPAT:
                return new AppCompatReflector(activity);
            case ACTIONBAR_SHERLOCK:
                return new SherlockReflector(activity);
        }
        return null;
    }

    private static ActionBarType searchForActivitySuperClass(Activity activity) {
        Class currentLevel = activity.getClass();
        while (currentLevel != Activity.class) {
            if (currentLevel.getSimpleName().equals("SherlockActivity")) {
                return ActionBarType.ACTIONBAR_SHERLOCK;
            }
            if (currentLevel.getSimpleName().equals("ActionBarActivity")) {
                return ActionBarType.APP_COMPAT;
            }
            currentLevel = currentLevel.getSuperclass();
        }
        return ActionBarType.STANDARD;
    }

    private enum ActionBarType {
        STANDARD, APP_COMPAT, ACTIONBAR_SHERLOCK
    }

}
