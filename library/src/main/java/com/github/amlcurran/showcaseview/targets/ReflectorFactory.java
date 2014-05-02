package com.github.amlcurran.showcaseview.targets;

import android.app.Activity;

/**
 * Base class which uses reflection to determine how to showcase Action Items and Action Views.
 */
class ReflectorFactory {

    public static Reflector getReflectorForActivity(Activity activity) {
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

    private static Reflector.ActionBarType searchForActivitySuperClass(Activity activity) {
        Class currentLevel = activity.getClass();
        while (currentLevel != Activity.class) {
            if (currentLevel.getSimpleName().equals("SherlockActivity") || currentLevel.getSimpleName().equals("SherlockFragmentActivity")) {
                return Reflector.ActionBarType.ACTIONBAR_SHERLOCK;
            }
            if (currentLevel.getSimpleName().equals("ActionBarActivity")) {
                return Reflector.ActionBarType.APP_COMPAT;
            }
            currentLevel = currentLevel.getSuperclass();
        }
        return Reflector.ActionBarType.STANDARD;
    }

}
