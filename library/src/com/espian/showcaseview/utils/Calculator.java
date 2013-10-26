package com.espian.showcaseview.utils;

import android.graphics.Point;
import android.view.View;

import com.espian.showcaseview.ShowcaseView;

/**
 * Calculates various items for use with ShowcaseView
 */
public class Calculator {

    public static final Point getShowcasePointFromView(View view, ShowcaseView.ConfigOptions options) {
        Point result = new Point();
        if (options.insert == ShowcaseView.INSERT_TO_VIEW) {
            result.x = view.getLeft() + view.getWidth() / 2;
            result.y = view.getTop() + view.getHeight() / 2;
        } else {
            int[] coordinates = new int[2];
            view.getLocationInWindow(coordinates);
            result.x = coordinates[0] + view.getWidth() / 2;
            result.y = coordinates[1] + view.getHeight() / 2;
        }
        return result;
    }

}
