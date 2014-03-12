package com.espian.showcaseview;

import android.graphics.Point;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml", reportSdk = 9)
public class ShowcaseViewLegacyTests {

    public static final int DEFAULT_X_VALUE = 90;
    public static final int DEFAULT_Y_VALUE = 70;
    public static final Point DEFAULT_POINT = new Point(DEFAULT_X_VALUE, DEFAULT_Y_VALUE);
    protected ShowcaseView showcaseView;
    protected OnShowcaseEventListener mockListener;

    @Before
    public void setup() {
        mockListener = mock(OnShowcaseEventListener.class);
        ActivityController<TestingActivity> controller = Robolectric.buildActivity(TestingActivity.class);
        TestingActivity activity = controller.create().start().resume().get();
        showcaseView = activity.mShowcaseView;
        showcaseView.setShowcasePosition(DEFAULT_POINT);
    }

    @Test
    public void testWhenAShowcaseIsHidden_TheListenerIsNotified() {
        showcaseView.setOnShowcaseEventListener(mockListener);

        showcaseView.hide();

        verify(mockListener).onShowcaseViewHide(showcaseView);
    }

    @Test
    public void testWhenAShowcaseHasBeenHidden_TheListenerIsNotified() {
        showcaseView.setOnShowcaseEventListener(mockListener);

        showcaseView.hide();

        verify(mockListener).onShowcaseViewDidHide(showcaseView);
    }

}
