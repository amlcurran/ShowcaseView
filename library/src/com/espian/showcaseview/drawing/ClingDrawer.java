package com.espian.showcaseview.drawing;

import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface ClingDrawer {
    void eraseCircle(Canvas canvas, float x, float y, float radius);

    void scale(Canvas canvas, float scaleMultiplier, float x, float y);

    void revertScale(Canvas canvas);
}
