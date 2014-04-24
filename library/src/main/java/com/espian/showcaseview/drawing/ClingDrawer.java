package com.espian.showcaseview.drawing;

import android.graphics.Bitmap;

import com.espian.showcaseview.utils.ShowcaseAreaCalculator;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface ClingDrawer extends ShowcaseAreaCalculator {

    void setShowcaseColour(int color);

    void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier, float radius, int backgroundColor);

    int getShowcaseWidth();

    int getShowcaseHeight();

}
