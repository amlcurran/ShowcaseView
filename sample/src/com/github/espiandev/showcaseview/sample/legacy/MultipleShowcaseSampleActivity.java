package com.github.espiandev.showcaseview.sample.legacy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.ShowcaseViews;
import com.github.espiandev.showcaseview.sample.R;

public class MultipleShowcaseSampleActivity extends Activity {

    private static final float SHOWCASE_KITTEN_SCALE = 1.2f;
    private static final float SHOWCASE_LIKE_SCALE = 0.5f;
    ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
    ShowcaseViews mViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_legacy);

        findViewById(R.id.buttonLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), R.string.like_message, Toast.LENGTH_SHORT).show();
            }
        });

        mOptions.block = false;
        mOptions.hideOnClickOutside = false;

        mViews = new ShowcaseViews(this,
                R.layout.showcase_view_template, new ShowcaseViews.OnShowcaseAcknowledged() {
            @Override
            public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
                Toast.makeText(MultipleShowcaseSampleActivity.this, R.string.dismissed_message, Toast.LENGTH_SHORT).show();
            }
        });
        mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.image,
                R.string.showcase_image_title,
                R.string.showcase_image_message,
                SHOWCASE_KITTEN_SCALE));
        mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.buttonLike,
                R.string.showcase_like_title,
                R.string.showcase_like_message,
                SHOWCASE_LIKE_SCALE));
        mViews.show();
    }

}
