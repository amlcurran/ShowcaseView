package com.espian.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.ViewParent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.espian.showcaseview.actionbar.ActionBarViewWrapper;
import com.espian.showcaseview.actionbar.reflection.BaseReflector;

public class ActionTabTarget implements Target {

    private final Activity mActivity;
    private final int tabId;

    ActionBarViewWrapper mActionBarWrapper;

    public ActionTabTarget(Activity activity, int tabId) {
        mActivity = activity;
		this.tabId = tabId;
    }

    @Override
    public Point getPoint() {
        setUp();
        return new ViewTarget(mActionBarWrapper.getTabItem(tabId)).getPoint();
    }

    protected void setUp() {
    	
        BaseReflector reflector = BaseReflector.getReflectorForActivity(mActivity);
        ViewParent p = reflector.getActionBarView(); //ActionBarView
        mActionBarWrapper = new ActionBarViewWrapper(p);
    }

}
