package com.github.amlcurran.showcaseview;

import android.graphics.Bitmap;

/**
 * Created by curraa01 on 13/10/2013.
 */
interface ClingDrawer extends ShowcaseAreaCalculator {

    void setShowcaseColour(int color);

    void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier, float radius, int backgroundColor);

    int getShowcaseWidth();

    int getShowcaseHeight();

}
