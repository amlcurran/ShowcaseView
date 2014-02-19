package com.espian.showcaseview.actionbar.reflection;

import android.app.Activity;
import android.view.View;

/**
 * Reflector which finds action items in the standard API 11 ActionBar implementation
 */
public class ActionBarReflector extends BaseReflector {

    private Activity mActivity;

    public ActionBarReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public View getHomeButton() {
        View homeButton = mActivity.findViewById(android.R.id.home);
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
