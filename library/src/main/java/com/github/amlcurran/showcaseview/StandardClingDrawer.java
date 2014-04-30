package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by curraa01 on 13/10/2013.
 */
class StandardClingDrawer implements ClingDrawer {

    protected final Paint mEraser;
    protected final Drawable mShowcaseDrawable;
    protected final Rect mShowcaseRect = new Rect();

    public StandardClingDrawer(Resources resources) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFF);
        mEraser.setAlpha(0);
        mEraser.setXfermode(xfermode);
        mEraser.setAntiAlias(true);

        mShowcaseDrawable = resources.getDrawable(R.drawable.cling_bleached);
    }

    @Override
    public void setShowcaseColour(int color) {
        mShowcaseDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier, float radius, int backgroundColor) {
        Canvas bufferCanvas = new Canvas(buffer);
        bufferCanvas.drawCircle(x, y, radius, mEraser);
        mShowcaseDrawable.draw(bufferCanvas);
    }

    @Override
    public int getShowcaseWidth() {
        return mShowcaseDrawable.getIntrinsicWidth();
    }

    @Override
    public int getShowcaseHeight() {
        return mShowcaseDrawable.getIntrinsicHeight();
    }

    /**
     * Creates a {@link android.graphics.Rect} which represents the area the showcase covers. Used
     * to calculate where best to place the text
     *
     * @return true if voidedArea has changed, false otherwise.
     */
    public boolean calculateShowcaseRect(float x, float y) {

        int cx = (int) x, cy = (int) y;
        int dw = getShowcaseWidth();
        int dh = getShowcaseHeight();

        if (mShowcaseRect.left == cx - dw / 2 && mShowcaseRect.top == cy - dh / 2) {
            return false;
        }

        Log.d("ShowcaseView", "Recalculated");

        mShowcaseRect.left = cx - dw / 2;
        mShowcaseRect.top = cy - dh / 2;
        mShowcaseRect.right = cx + dw / 2;
        mShowcaseRect.bottom = cy + dh / 2;

        mShowcaseDrawable.setBounds(mShowcaseRect);

        return true;

    }

    @Override
    public Rect getShowcaseRect() {
        return mShowcaseRect;
    }

}
