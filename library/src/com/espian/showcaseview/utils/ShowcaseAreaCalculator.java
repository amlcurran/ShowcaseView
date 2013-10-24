package com.espian.showcaseview.utils;

import android.graphics.Rect;

/**
 * Class responsible for calculating where the Showcase should position itself
 */
public interface ShowcaseAreaCalculator {

    boolean calculateShowcaseRect(float showcaseX, float showcaseY);
    boolean calculateShowcaseRect(int left, int top, int right, int bottom);

    Rect getShowcaseRect();
}
