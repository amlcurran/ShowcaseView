package com.github.amlcurran.showcaseview.targets;

import android.view.View;
import android.view.ViewParent;

interface Reflector {
    View getHomeButton();

    void showcaseActionItem(int itemId);

    ViewParent getActionBarView();

    public enum ActionBarType {
        STANDARD, APP_COMPAT, ACTIONBAR_SHERLOCK
    }
}
