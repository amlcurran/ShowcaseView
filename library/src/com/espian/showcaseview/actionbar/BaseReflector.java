package com.espian.showcaseview.actionbar;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by Alex on 27/10/13.
 */
public abstract class BaseReflector {

    public abstract ViewParent getActionBarView();
    public abstract void showcaseActionItem(int itemId);

    public static BaseReflector getReflectorForActivity(Activity activity) {
        // This should be looking at superclasses!
        String classQualifier = activity.getClass().getName();
        if (classQualifier.contains("SherlockActivity")) {
            return new SherlockReflector(activity);
        } else if (classQualifier.contains("ActionBarActivity")) {
            return new AppCompatReflector(activity);
        } else {
            return new ActionBarReflector(activity);
        }
    }

}
