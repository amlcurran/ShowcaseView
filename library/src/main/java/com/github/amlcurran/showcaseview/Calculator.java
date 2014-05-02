package com.github.amlcurran.showcaseview;

import android.graphics.Point;
import android.view.View;

/**
 * Calculates various items for use with ShowcaseView
 */
class Calculator {

    static Point getShowcasePointFromView(View view) {
        Point result = new Point();
        result.x = view.getLeft() + view.getWidth() / 2;
        result.y = view.getTop() + view.getHeight() / 2;
        return result;
    }

}
