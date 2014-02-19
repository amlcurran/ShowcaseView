package com.espian.showcaseview;

import android.app.Activity;
import android.os.Bundle;

public class TestingActivity extends Activity {

    public ShowcaseView mShowcaseView;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        mShowcaseView = new ShowcaseView(this);
        mShowcaseView.setShowcasePosition(0, 0);
        setContentView(mShowcaseView);
    }

}
