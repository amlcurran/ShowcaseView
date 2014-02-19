package com.espian.showcaseview;

import android.graphics.Point;

import com.espian.showcaseview.targets.Target;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ShowcaseViewTests {

    public static final int DEFAULT_X_VALUE = 90;
    public static final int DEFAULT_Y_VALUE = 70;
    public static final Point DEFAULT_POINT = new Point(DEFAULT_X_VALUE, DEFAULT_Y_VALUE);
    private ShowcaseView mShowcaseView;

    @Before
    public void setup() {
        ActivityController<TestingActivity> controller = Robolectric.buildActivity(TestingActivity.class);
        TestingActivity activity = controller.create().start().resume().get();
        mShowcaseView = activity.mShowcaseView;
        mShowcaseView.setShowcasePosition(DEFAULT_POINT);
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

    @Test
    public void testSetShowcaseXDoesNotChangeShowcaseY() {
        mShowcaseView.setShowcaseX(50);
        assertEquals(DEFAULT_Y_VALUE, mShowcaseView.getShowcaseY());
    }

    @Test
    public void testSetShowcaseYDoesNotChangeShowcaseX() {
        mShowcaseView.setShowcaseY(50);
        assertEquals(DEFAULT_X_VALUE, mShowcaseView.getShowcaseX());
    }

    @Test
    public void testSetTargetGetsPointFromTarget() {
        Target target = mock(Target.class);
        when(target.getPoint()).thenReturn(new Point());

        mShowcaseView.setShowcase(target);
        Robolectric.runUiThreadTasksIncludingDelayedTasks();

        verify(target).getPoint();
    }

}
