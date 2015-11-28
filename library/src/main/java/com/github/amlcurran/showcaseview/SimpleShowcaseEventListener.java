package com.github.amlcurran.showcaseview;

import android.view.MotionEvent;

/**
 * Basic implementation of {@link OnShowcaseEventListener} which does nothing
 * for each event, but can be override for each one.
 */
public class SimpleShowcaseEventListener implements OnShowcaseEventListener {
    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        // Override to do stuff
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
        // Override to do stuff
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        // Override to do stuff
    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
        // Override to do stuff
    }
}
