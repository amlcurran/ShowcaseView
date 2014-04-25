package com.github.amlcurran.showcaseview;

import android.graphics.Rect;

/**
 * Class responsible for calculating where the Showcase should position itself
 */
interface ShowcaseAreaCalculator {

    boolean calculateShowcaseRect(float showcaseX, float showcaseY);

    Rect getShowcaseRect();
}
