package com.espian.showcaseview.drawing;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.utils.ShowcaseAreaCalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

/**
 * Draws the text as required by the ShowcaseView
 */
public class TextDrawerImpl implements TextDrawer {

    private final TextPaint mPaintTitle;
    private final TextPaint mPaintDetail;

    private CharSequence mTitle, mDetails;
    private float mDensityScale;
    private ShowcaseAreaCalculator mCalculator;
    private float[] mBestTextPosition = new float[3];
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;
    private TextAppearanceSpan mTitleSpan;
    private TextAppearanceSpan mDetailSpan;

    public TextDrawerImpl(float densityScale, ShowcaseAreaCalculator calculator) {
        mDensityScale = densityScale;
        mCalculator = calculator;

        mPaintTitle = new TextPaint();
        mPaintTitle.setAntiAlias(true);

        mPaintDetail = new TextPaint();
        mPaintDetail.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas, boolean hasPositionChanged) {
        if (shouldDrawText()) {
            float[] textPosition = getBestTextPosition();

            if (!TextUtils.isEmpty(mTitle)) {
                canvas.save();
                if (hasPositionChanged) {
                    mDynamicTitleLayout = new DynamicLayout(mTitle, mPaintTitle,
                            (int) textPosition[2], Layout.Alignment.ALIGN_NORMAL,
                            1.0f, 1.0f, true);
                }
                canvas.translate(textPosition[0], textPosition[1] - textPosition[0]);
                mDynamicTitleLayout.draw(canvas);
                canvas.restore();
            }

            if (!TextUtils.isEmpty(mDetails)) {
                canvas.save();
                if (hasPositionChanged) {
                    mDynamicDetailLayout = new DynamicLayout(mDetails, mPaintDetail,
                            ((Number) textPosition[2]).intValue(),
                            Layout.Alignment.ALIGN_NORMAL,
                            1.2f, 1.0f, true);
                }
                canvas.translate(textPosition[0], textPosition[1] + 12 * mDensityScale + (
                        mDynamicTitleLayout.getLineBottom(mDynamicTitleLayout.getLineCount() - 1)
                                - mDynamicTitleLayout.getLineBottom(0)));
                mDynamicDetailLayout.draw(canvas);
                canvas.restore();

            }
        }
    }

    @Override
    public void setDetails(CharSequence details) {
        SpannableString ssbDetail = new SpannableString(details);
        ssbDetail.setSpan(mDetailSpan, 0, ssbDetail.length(), 0);
        mDetails = ssbDetail;
    }

    @Override
    public void setTitle(CharSequence title) {
        SpannableString ssbTitle = new SpannableString(title);
        ssbTitle.setSpan(mTitleSpan, 0, ssbTitle.length(), 0);
        mTitle = ssbTitle;
    }

    /**
     * Calculates the best place to position text
     *
     * @param canvasW width of the screen
     * @param canvasH height of the screen
     */
    @Override
    public void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView) {

        //if the width isn't much bigger than the voided area, just consider top & bottom
        float spaceTop = mCalculator.getShowcaseRect().top;
        float spaceBottom = canvasH - mCalculator.getShowcaseRect().bottom
                - 64 * mDensityScale; //64dip considers the OK button
        //float spaceLeft = voidedArea.left;
        //float spaceRight = canvasW - voidedArea.right;

        //TODO: currently only considers above or below showcase, deal with left or right
        mBestTextPosition[0] = 24 * mDensityScale;
        mBestTextPosition[1] = spaceTop > spaceBottom ? 128 * mDensityScale
                : 24 * mDensityScale + mCalculator.getShowcaseRect().bottom;
        mBestTextPosition[2] = canvasW - 48 * mDensityScale;

    }

    @Override
    public void setTitleStyling(Context context, int styleId) {
        mTitleSpan = new TextAppearanceSpan(context, styleId);
    }

    @Override
    public void setDetailStyling(Context context, int styleId) {
        mDetailSpan = new TextAppearanceSpan(context, styleId);
    }

    public float[] getBestTextPosition() {
        return mBestTextPosition;
    }

    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(mTitle) || !TextUtils.isEmpty(mDetails);
    }
}
