/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
class NewShowcaseDrawer extends StandardShowcaseDrawer {

    private static final int ALPHA_60_PERCENT = 153;
    
    // Probably i'm dumb as fuck, but i dont really know why we need this final
    //private final float outerRadius;
    //private final float innerRadius;
    public float outerRadius;
    public float innerRadius;

    public NewShowcaseDrawer(Resources resources) {
        super(resources);
        outerRadius = resources.getDimension(R.dimen.showcase_radius_outer);
        innerRadius = resources.getDimension(R.dimen.showcase_radius_inner);
    }
    
    public void setInnerRadius(float innerRadius){
        this.innerRadius = innerRadius;
    }

    public float getInnerRadius() {

        return innerRadius;
    }

    public void setOuterRadius(float outerRadius){
        this.outerRadius = outerRadius;
    }

    public float getOuterRadius() {

        return outerRadius;
    }

    @Override
    public void setShowcaseColour(int color) {
        eraserPaint.setColor(color);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        eraserPaint.setAlpha(ALPHA_60_PERCENT);
        bufferCanvas.drawCircle(x, y, this.outerRadius, eraserPaint);
        eraserPaint.setAlpha(0);
        bufferCanvas.drawCircle(x, y, this.innerRadius, eraserPaint);
    }

    // If it's really necessary to maintain inner/outer radius as finals so we can get a flexible radius over here
    // which i judge (without any doc) it's the function that matters
    // the problem is the getters of BlockedRadius and Showcase width/height! they both acces the inner/outer radius so
    // it's better to make those fields not finals.
    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float innerRadius, float outerRadius, float scaleMultiplier) {
        this.innerRadius = innerRadius; // make sure getShowcase method will work well
        this.outerRadius = outerRadius; // make sure getBlockedRadius show well
        Canvas bufferCanvas = new Canvas(buffer);
        eraserPaint.setAlpha(ALPHA_60_PERCENT);
        //bufferCanvas.drawCircle(x, y, outerRadius, eraserPaint);
        bufferCanvas.drawCircle(x, y, this.getOuterRadius(), eraserPaint);
        eraserPaint.setAlpha(0);
        //bufferCanvas.drawCircle(x, y, innerRadius, eraserPaint);
        bufferCanvas.drawCircle(x, y, this.getInnerRadius(), eraserPaint);
    }


    /*
    @Override
    public int getShowcaseWidth() {
        return (int) (outerRadius * 2);
    }*/
    @Override
    public int getShowcaseWidth() {
        return (int) (this.getOuterRadius() * 2);
    }

    /*@Override
    public int getShowcaseHeight() {
        return (int) (outerRadius * 2);
    }*/

    @Override
    public int getShowcaseHeight() {
        return (int) (this.outerRadius * 2);
    }

    /*@Override
    public float getBlockedRadius() {
        return innerRadius;
    }*/

    @Override
    public float getBlockedRadius() {
        return this.getInnerRadius();
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }
}
