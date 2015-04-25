package uk.co.amlcurran.showcaseview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import static uk.co.amlcurran.showcaseview.Utils.assertIsFrameLayout;

public class ShowcaseView extends FrameLayout {

    public static ShowcaseView insertIntoActivity(Activity activity) {
        ShowcaseView showcaseView = new ShowcaseView(activity, null);
        View contentView = activity.getWindow().getDecorView();
        assertIsFrameLayout(contentView);
        ((ViewGroup) contentView).addView(showcaseView);
        return showcaseView;
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.RED);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void remove() {
        ViewPropertyAnimator alpha = animate().alpha(0);
        alpha.setListener(new RemoveShowcaseOnEnd(this));
        alpha.start();
    }

    public void removeWithoutAnimation() {
        ((ViewGroup) getParent()).removeView(this);
    }

}
