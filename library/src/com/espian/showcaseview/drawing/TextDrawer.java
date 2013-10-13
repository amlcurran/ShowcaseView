package com.espian.showcaseview.drawing;

import com.espian.showcaseview.ShowcaseView;

import android.graphics.Canvas;
import android.text.style.TextAppearanceSpan;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface TextDrawer {

    void draw(Canvas canvas, boolean hasPositionChanged);

    void setDetails(CharSequence details, TextAppearanceSpan detailSpan);

    void setTitle(CharSequence title, TextAppearanceSpan titleSpan);

    void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView);
}
