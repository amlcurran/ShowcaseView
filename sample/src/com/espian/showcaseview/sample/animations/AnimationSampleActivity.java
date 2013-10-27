package com.espian.showcaseview.sample.animations;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.sample.R;
import com.espian.showcaseview.utils.Calculator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by Alex on 26/10/13.
 */
public class AnimationSampleActivity extends Activity {

    private ShowcaseView showcaseView;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        counter = 0;

        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        final TextView textView3 = (TextView) findViewById(R.id.textView3);

        showcaseView = ShowcaseView.insertShowcaseView(findViewById(R.id.textView), this);
        showcaseView.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter == 0) {
                    Point textView2Point = Calculator.getShowcasePointFromView(textView2, showcaseView.getConfigOptions());
                    Animator xAnimation = ObjectAnimator.ofFloat(showcaseView, "showcaseX", textView2Point.x);
                    Animator yAnimation = ObjectAnimator.ofFloat(showcaseView, "showcaseY", textView2Point.y);
                    AnimatorSet set = new AnimatorSet();
                    set.setInterpolator(new AccelerateDecelerateInterpolator());
                    set.setDuration(600);
                    set.playTogether(xAnimation, yAnimation);
                    set.start();
                } else if (counter == 1) {
                    Point textView3Point = Calculator.getShowcasePointFromView(textView3, showcaseView.getConfigOptions());
                    Animator xAnimation = ObjectAnimator.ofFloat(showcaseView, "showcaseX", textView3Point.x);
                    Animator yAnimation = ObjectAnimator.ofFloat(showcaseView, "showcaseY", textView3Point.y);
                    Animator scaleAnimation = ObjectAnimator.ofFloat(showcaseView, "scaleMultiplier", 0.6f);
                    AnimatorSet set = new AnimatorSet();
                    set.setInterpolator(new AccelerateDecelerateInterpolator());
                    set.setDuration(600);
                    set.playTogether(xAnimation, yAnimation, scaleAnimation);
                    set.start();
                } else {
                    showcaseView.hide();
                }
                counter++;
            }
        });
    }
}
