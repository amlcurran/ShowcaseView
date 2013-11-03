package com.espian.showcaseview.sample.animations;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.sample.R;
import com.espian.showcaseview.sample.SampleActivity;
import com.espian.showcaseview.targets.ViewTarget;

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

        final TextView textView1 = (TextView) findViewById(R.id.textView);
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        final TextView textView3 = (TextView) findViewById(R.id.textView3);

        showcaseView = ShowcaseView.insertShowcaseView(new ViewTarget(findViewById(R.id.textView)), this);
        showcaseView.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (counter) {
                    case 0:
                        showcaseView.setShowcase(new ViewTarget(textView2), true);
                        break;

                    case 1:
                        showcaseView.setShowcase(new ViewTarget(textView3), true);
                        break;

                    case 2:
                        showcaseView.setShowcase(ShowcaseView.NONE);
                        showcaseView.setText("Look ma!", "You don't always need a target to showcase");
                        setAlpha(0.4f, textView1, textView2, textView3);
                        break;

                    case 3:
                        showcaseView.hide();
                        setAlpha(1.0f, textView1, textView2, textView3);
                        break;
                }
                counter++;
            }
        });
    }

    private void setAlpha(float alpha, View... views) {
        if (SampleActivity.isHoneycombOrAbove()) {
            for (View view : views) {
                view.setAlpha(alpha);
            }
        }
    }
}
