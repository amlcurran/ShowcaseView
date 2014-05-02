package com.github.amlcurran.showcaseview;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
interface ShowcaseDrawer {

    void setShowcaseColour(int color);

    void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier);

    int getShowcaseWidth();

    int getShowcaseHeight();

    float getBlockedRadius();

    void setBackgroundColour(int backgroundColor);

    void erase(Bitmap bitmapBuffer);

    void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer);
}
