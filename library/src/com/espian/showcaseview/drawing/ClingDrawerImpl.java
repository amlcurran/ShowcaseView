package com.espian.showcaseview.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.github.espiandev.showcaseview.R;

/**
 * Created by curraa01 on 13/10/2013.
 */
public class ClingDrawerImpl implements ClingDrawer {

    private Paint mEraser;
    private Drawable mShowcaseDrawable;
    private Rect mShowcaseRect;

    public ClingDrawerImpl(Resources resources, int showcaseColor) {
        PorterDuffXfermode mBlender = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFF);
        mEraser.setAlpha(0);
        mEraser.setXfermode(mBlender);
        mEraser.setAntiAlias(true);

        mShowcaseDrawable = resources.getDrawable(R.drawable.cling_bleached);
        mShowcaseDrawable.setColorFilter(showcaseColor, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void drawShowcase(Canvas canvas, float x, float y, float scaleMultiplier, float radius) {
        Matrix mm = new Matrix();
        mm.postScale(scaleMultiplier, scaleMultiplier, x, y);
        canvas.setMatrix(mm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	canvas.drawCircle(x, y, radius, mEraser);
        }

        mShowcaseDrawable.setBounds(mShowcaseRect);
        mShowcaseDrawable.draw(canvas);

        canvas.setMatrix(new Matrix());
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

        if (mShowcaseRect == null) {
            mShowcaseRect = new Rect();
        }

        int cx = (int) x, cy = (int) y;
        int dw = getShowcaseWidth();
        int dh = getShowcaseHeight();

        if (mShowcaseRect.left == cx - dw / 2) {
            return false;
        }

        Log.d("ShowcaseView", "Recalculated");

        mShowcaseRect.left = cx - dw / 2;
        mShowcaseRect.top = cy - dh / 2;
        mShowcaseRect.right = cx + dw / 2;
        mShowcaseRect.bottom = cy + dh / 2;

        return true;

    }

    @Override
    public Rect getShowcaseRect() {
        return mShowcaseRect;
    }

}
