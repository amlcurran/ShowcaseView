package com.github.amlcurran.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.ViewParent;

public class ActionViewTarget implements Target {

    private final Activity mActivity;
    private final Type mType;

    ActionBarViewWrapper mActionBarWrapper;
    Reflector mReflector;

    public ActionViewTarget(Activity activity, Type type) {
        mActivity = activity;
        mType = type;
    }

    protected void setUp() {
        mReflector = ReflectorFactory.getReflectorForActivity(mActivity);
        ViewParent p = mReflector.getActionBarView(); //ActionBarView
        mActionBarWrapper = new ActionBarViewWrapper(p);
    }

    @Override
    public Point getPoint() {
        Target internal = null;
        setUp();
        switch (mType) {

            case SPINNER:
                internal = new ViewTarget(mActionBarWrapper.getSpinnerView());
                break;

            case HOME:
                internal = new ViewTarget(mReflector.getHomeButton());
                break;

            case OVERFLOW:
                internal = new ViewTarget(mActionBarWrapper.getOverflowView());
                break;

            case TITLE:
                internal = new ViewTarget(mActionBarWrapper.getTitleView());
                break;

        }
        return internal.getPoint();
    }

    public enum Type {
        SPINNER, HOME, TITLE, OVERFLOW
    }
}
