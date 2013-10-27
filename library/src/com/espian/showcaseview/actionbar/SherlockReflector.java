package com.espian.showcaseview.actionbar;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by Alex on 27/10/13.
 */
public class SherlockReflector extends BaseReflector {

    private Activity mActivity;

    public SherlockReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ViewParent getActionBarView() {
        int homeId = mActivity.getResources().getIdentifier("abs__home", "id", mActivity.getPackageName());
        View homeButton = mActivity.findViewById(homeId);
        if (homeButton == null) {
            throw new RuntimeException(
                    "insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar");
        }
        return homeButton.getParent().getParent();
    }

    @Override
    public void showcaseActionItem(int itemId) {

    }
}
