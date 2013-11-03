package com.espian.showcaseview.actionbar.reflection;

import android.app.Activity;
import android.view.View;

/**
 * Created by Alex on 27/10/13.
 */
public class SherlockReflector extends BaseReflector {

    private Activity mActivity;

    public SherlockReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public View getHomeButton() {
        View homeButton = mActivity.findViewById(android.R.id.home);
        if (homeButton != null) {
            return homeButton;
        }
        int homeId = mActivity.getResources().getIdentifier("abs__home", "id", mActivity.getPackageName());
        homeButton = mActivity.findViewById(homeId);
        if (homeButton == null) {
            throw new RuntimeException(
                    "insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar");
        }
        return homeButton;
    }

    @Override
    public void showcaseActionItem(int itemId) {

    }
}
