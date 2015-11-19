package com.github.amlcurran.showcaseview;

import android.graphics.Point;
import android.view.View;

class NoAnimationFactory implements AnimationFactory {

    @Override
    public void fadeInView(View target, long duration, AnimationStartListener listener) {
        listener.onAnimationStart();
    }

    @Override
    public void fadeOutView(View target, long duration, AnimationEndListener listener) {
        listener.onAnimationEnd();
    }

    @Override
    public void animateTargetToPoint(ShowcaseView showcaseView, Point point) {
        showcaseView.setShowcasePosition(point.x, point.y);
    }
}
