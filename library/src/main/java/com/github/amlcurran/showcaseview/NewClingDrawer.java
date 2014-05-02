package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
class NewClingDrawer extends StandardClingDrawer {

    private static final int ALPHA_60_PERCENT = 153;
    private final float outerRadius;
    private final float innerRadius;

    public NewClingDrawer(Resources resources) {
        super(resources);
        outerRadius = resources.getDimension(R.dimen.showcase_radius_outer);
        innerRadius = resources.getDimension(R.dimen.showcase_radius_inner);
    }

    @Override
    public void setShowcaseColour(int color) {
        mEraser.setColor(color);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier, float radius, int backgroundColor) {
        Canvas bufferCanvas = new Canvas(buffer);
        mEraser.setAlpha(ALPHA_60_PERCENT);
        bufferCanvas.drawCircle(x, y, outerRadius, mEraser);
        mEraser.setAlpha(0);
        bufferCanvas.drawCircle(x, y, innerRadius, mEraser);
    }

    @Override
    public int getShowcaseWidth() {
        return (int) (outerRadius * 2);
    }

    @Override
    public int getShowcaseHeight() {
        return (int) (outerRadius * 2);
    }
}
