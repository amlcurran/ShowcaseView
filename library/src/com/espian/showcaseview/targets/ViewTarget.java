package com.espian.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

public class ViewTarget implements Target {

    private final View mView;

    public ViewTarget(View view) {
        mView = view;
    }

    public ViewTarget(int viewId, Activity activity) {
        mView = activity.findViewById(viewId);
    }

    @Override
    public Point getPoint() {
        try {
            int[] location = new int[2];
            mView.getLocationInWindow(location);
            int x = location[0] + mView.getWidth() / 2;
            int y = location[1] + mView.getHeight() / 2;

            return new Point(x, y);
        } catch (NullPointerException e) {
            Log.d("ShowCaseView", "Could not identify Target ");
        }
        return null;
    }
}
