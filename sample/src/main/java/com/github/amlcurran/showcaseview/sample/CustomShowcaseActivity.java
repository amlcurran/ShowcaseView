package com.github.amlcurran.showcaseview.sample;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;

import com.github.amlcurran.showcaseview.ShowcaseDrawer;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class CustomShowcaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_showcase);

        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.imageView, this))
                .setContentTitle(R.string.custom_text_painting_title)
                .setContentText(R.string.custom_text_painting_text)
                .setShowcaseDrawer(new CustomShowcaseView(getResources()))
                .build();
    }

    private static class CustomShowcaseView implements ShowcaseDrawer {

        private static final int ALPHA_60_PERCENT = 153;
        private final float outerRadius;
        private final float innerRadius;
        private final Paint eraserPaint;
        private final Paint basicPaint;
        private int backgroundColour;

        public CustomShowcaseView(Resources resources) {
            outerRadius = resources.getDimension(com.github.amlcurran.showcaseview.R.dimen.showcase_radius_outer);
            innerRadius = resources.getDimension(com.github.amlcurran.showcaseview.R.dimen.showcase_radius_inner);
            PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
            eraserPaint = new Paint();
            eraserPaint.setColor(0xFFFFFF);
            eraserPaint.setAlpha(0);
            eraserPaint.setXfermode(xfermode);
            eraserPaint.setAntiAlias(true);
            basicPaint = new Paint();
        }

        @Override
        public void setShowcaseColour(int color) {
            eraserPaint.setColor(color);
        }

        @Override
        public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
            Canvas bufferCanvas = new Canvas(buffer);
            eraserPaint.setAlpha(ALPHA_60_PERCENT);
            bufferCanvas.drawCircle(x, y, outerRadius, eraserPaint);
            eraserPaint.setAlpha(0);
            bufferCanvas.drawCircle(x, y, innerRadius, eraserPaint);
        }

        @Override
        public int getShowcaseWidth() {
            return (int) (outerRadius * 2);
        }

        @Override
        public int getShowcaseHeight() {
            return (int) (outerRadius * 2);
        }

        @Override
        public float getBlockedRadius() {
            return innerRadius;
        }

        @Override
        public void setBackgroundColour(int backgroundColor) {
            this.backgroundColour = backgroundColor;
        }

        @Override
        public void erase(Bitmap bitmapBuffer) {
            bitmapBuffer.eraseColor(Color.RED);
        }

        @Override
        public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
            canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
        }

    }

}
