package com.espian.showcaseview.drawing;

import com.espian.showcaseview.utils.ShowcaseAreaCalculator;

import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface ClingDrawer extends ShowcaseAreaCalculator {
    void eraseCircle(Canvas canvas, float x, float y, float radius);
    void eraseRectangle(Canvas canvas, float left, float top, float right, float bottom);

    void scale(Canvas canvas, float x, float y, float scaleMultiplier);

    void revertScale(Canvas canvas);

    void drawCling(Canvas canvas);

    int getShowcaseWidth();

    int getShowcaseHeight();

}
