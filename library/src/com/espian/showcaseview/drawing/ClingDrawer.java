package com.espian.showcaseview.drawing;

import com.espian.showcaseview.utils.ShowcaseAreaCalculator;

import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface ClingDrawer extends ShowcaseAreaCalculator {

    void drawShowcase(Canvas canvas, float x, float y, float scaleMultiplier, float radius);

    int getShowcaseWidth();

    int getShowcaseHeight();

}
