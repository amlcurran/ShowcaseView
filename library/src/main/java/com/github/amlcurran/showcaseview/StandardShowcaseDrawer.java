package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

/**
 * Created by curraa01 on 13/10/2013.
 */
class StandardShowcaseDrawer implements ShowcaseDrawer {

    protected final Paint eraserPaint;
    protected final Drawable showcaseDrawable;
    private final Paint basicPaint;
    private final float showcaseRadius;
    protected int backgroundColour;
    protected float scaleMultiplier = 1F;

    public StandardShowcaseDrawer(Resources resources) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        basicPaint = new Paint();
        showcaseRadius = resources.getDimension(R.dimen.showcase_radius);
        showcaseDrawable = resources.getDrawable(R.drawable.cling_bleached);
    }

    @Override
    public void setShowcaseColour(int color) {
        showcaseDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y) {
        Canvas bufferCanvas = new Canvas(buffer);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleMultiplier, scaleMultiplier, x, y);
        bufferCanvas.setMatrix(matrix);

        bufferCanvas.drawCircle(x, y, showcaseRadius, eraserPaint);
        int halfW = showcaseDrawable.getIntrinsicWidth() / 2;
        int halfH = showcaseDrawable.getIntrinsicHeight() / 2;
        int left = (int) (x - halfW);
        int top = (int) (y - halfH);
        showcaseDrawable.setBounds(left, top, left + 2 * halfW, top + 2 * halfH);
        showcaseDrawable.draw(bufferCanvas);
    }

    @Override
    public int getShowcaseWidth() {
        return (int) (scaleMultiplier * showcaseDrawable.getIntrinsicWidth());
    }

    @Override
    public int getShowcaseHeight() {
        return (int) (scaleMultiplier * showcaseDrawable.getIntrinsicHeight());
    }

    @Override
    public float getBlockedRadius() {
        return showcaseRadius * scaleMultiplier;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(backgroundColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }

}
