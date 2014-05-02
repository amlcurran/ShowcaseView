package com.github.amlcurran.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.ViewParent;

/**
 * Represents an Action item to showcase (e.g., one of the buttons on an ActionBar).
 * To showcase specific action views such as the home button, use {@link com.github.amlcurran.showcaseview.targets.ActionItemTarget}
 *
 * @see com.github.amlcurran.showcaseview.targets.ActionItemTarget
 */
public class ActionItemTarget implements Target {

    private final Activity mActivity;
    private final int mItemId;

    ActionBarViewWrapper mActionBarWrapper;

    public ActionItemTarget(Activity activity, int itemId) {
        mActivity = activity;
        mItemId = itemId;
    }

    @Override
    public Point getPoint() {
        setUp();
        return new ViewTarget(mActionBarWrapper.getActionItem(mItemId)).getPoint();
    }

    protected void setUp() {
        Reflector reflector = ReflectorFactory.getReflectorForActivity(mActivity);
        ViewParent p = reflector.getActionBarView(); //ActionBarView
        mActionBarWrapper = new ActionBarViewWrapper(p);
    }

}
