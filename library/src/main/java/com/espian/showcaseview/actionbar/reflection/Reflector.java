package com.espian.showcaseview.actionbar.reflection;

import android.view.View;
import android.view.ViewParent;

public interface Reflector {
    View getHomeButton();

    void showcaseActionItem(int itemId);

    ViewParent getActionBarView();

    public enum ActionBarType {
        STANDARD, APP_COMPAT, ACTIONBAR_SHERLOCK
    }
}
