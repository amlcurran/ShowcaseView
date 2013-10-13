package com.espian.showcaseview.drawing;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by curraa01 on 13/10/2013.
 */
public class ClingDrawerImpl implements ClingDrawer {

    private Paint mEraser;

    public ClingDrawerImpl() {
        PorterDuffXfermode mBlender = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFF);
        mEraser.setAlpha(0);
        mEraser.setXfermode(mBlender);
        mEraser.setAntiAlias(true);
    }

    @Override
    public void eraseCircle(Canvas canvas, float x, float y, float radius) {
        canvas.drawCircle(x, y, radius, mEraser);
    }

    @Override
    public void scale(Canvas canvas, float scaleMultiplier, float x, float y) {
        Matrix mm = new Matrix();
        mm.postScale(scaleMultiplier, scaleMultiplier, x, y);
        canvas.setMatrix(mm);
    }

    @Override
    public void revertScale(Canvas canvas) {
        canvas.setMatrix(new Matrix());
    }
}
