package com.espian.showcaseview;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ShowcaseViewTests {

    private ShowcaseView mShowcaseView;

    @Before
    public void setup() {
        ActivityController<TestingActivity> controller = Robolectric.buildActivity(TestingActivity.class);
        TestingActivity activity = controller.create().start().resume().get();
        mShowcaseView = activity.mShowcaseView;
    }

    @Test
    public void testSetOnShowcaseViewListenerIsSet() {
        OnShowcaseEventListener listener = new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        };
        mShowcaseView.setOnShowcaseEventListener(listener);
        assertEquals(listener, mShowcaseView.mEventListener);
    }

}
