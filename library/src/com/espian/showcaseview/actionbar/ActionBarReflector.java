package com.espian.showcaseview.actionbar;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by Alex on 27/10/13.
 */
public class ActionBarReflector extends BaseReflector {

    private Activity mActivity;

    public ActionBarReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ViewParent getActionBarView() {
        View homeButton = mActivity.findViewById(android.R.id.home);
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
