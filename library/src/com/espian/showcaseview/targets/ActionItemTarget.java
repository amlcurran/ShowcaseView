package com.espian.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.ViewParent;

import com.espian.showcaseview.actionbar.ActionBarViewWrapper;
import com.espian.showcaseview.actionbar.reflection.ReflectorFactory;
import com.espian.showcaseview.actionbar.reflection.Reflector;

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
