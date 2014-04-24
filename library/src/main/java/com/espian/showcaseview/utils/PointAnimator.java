package com.espian.showcaseview.utils;

import android.graphics.Point;

import com.espian.showcaseview.ShowcaseView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class PointAnimator {

    public static Animator ofPoints(Object object, String xMethod, String yMethod, Point... values) {
        AnimatorSet set = new AnimatorSet();
        int[] xValues = new int[values.length];
        int[] yValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            xValues[i] = values[i].x;
            yValues[i] = values[i].y;
        }
        ObjectAnimator xAnimator = ObjectAnimator.ofInt(object, xMethod, xValues);
        ObjectAnimator yAnimator = ObjectAnimator.ofInt(object, yMethod, yValues);
        set.playTogether(xAnimator, yAnimator);
        return set;
    }

    public static Animator ofPoints(ShowcaseView showcaseView, Point... values) {
        return ofPoints(showcaseView, "showcaseX", "showcaseY", values);
    }

}
