package com.espian.showcaseview.actionbar;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by Alex on 27/10/13.
 */
public class AppCompatReflector extends BaseReflector {

    private Activity mActivity;

    public AppCompatReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public View getHomeButton() {
        int homeId = mActivity.getResources()
                .getIdentifier("home", "id", mActivity.getPackageName());
        View homeButton = mActivity.findViewById(homeId);
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
