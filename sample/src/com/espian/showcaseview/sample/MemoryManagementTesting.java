package com.espian.showcaseview.sample;

import android.app.Activity;
import android.os.Bundle;

import com.espian.showcaseview.ShowcaseViews;

public class MemoryManagementTesting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ShowcaseViews showcaseViews = new ShowcaseViews(this);

        ShowcaseViews.ItemViewProperties properties = new ShowcaseViews.ItemViewProperties(
                R.id.buttonBlocked, R.string.showcase_like_title, R.string.showcase_like_message
        );

        showcaseViews.addView(properties).addView(properties).addView(properties).addView(properties)
                .addView(properties).addView(properties).show();

    }
}
