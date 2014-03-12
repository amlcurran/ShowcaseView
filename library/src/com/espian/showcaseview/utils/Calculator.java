package com.espian.showcaseview.utils;

import android.graphics.Point;
import android.view.View;

/**
 * Calculates various items for use with ShowcaseView
 */
public class Calculator {

    public static final Point getShowcasePointFromView(View view) {
        Point result = new Point();
        result.x = view.getLeft() + view.getWidth() / 2;
        result.y = view.getTop() + view.getHeight() / 2;
        return result;
    }

}
