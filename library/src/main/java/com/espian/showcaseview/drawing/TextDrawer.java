package com.espian.showcaseview.drawing;

import android.content.Context;
import android.graphics.Canvas;

import com.espian.showcaseview.ShowcaseView;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface TextDrawer {

    void draw(Canvas canvas, boolean hasPositionChanged);

    void setContentText(CharSequence details);

    void setContentTitle(CharSequence title);

    void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView);

    void setTitleStyling(int styleId);

    void setDetailStyling(int styleId);

    CharSequence getContentTitle();

    CharSequence getContentText();

}
