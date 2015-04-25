package uk.co.amlcurran.showcaseview;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

public class ShowcaseView extends FrameLayout {

    private static final String TAG = ShowcaseView.class.getSimpleName();

    public static ShowcaseView insertIntoActivity(Activity activity) {
        ShowcaseView showcaseView = new ShowcaseView(activity, null);
        View contentView = activity.getWindow().getDecorView();
        assertIsFrameLayout(contentView);
        ((ViewGroup) contentView).addView(showcaseView);
        return showcaseView;
    }

    private static void assertIsFrameLayout(View view) {
        if (!(view instanceof FrameLayout)) {
            Log.w(TAG, "Expected a FrameLayout as a parent, unexpected things may occur");
        }
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
        alpha.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeWithoutAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alpha.start();
    }

    public void removeWithoutAnimation() {
        ((ViewGroup) getParent()).removeView(this);
    }
}
