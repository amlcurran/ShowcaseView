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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ShowcaseViewTests {

    public static final int DEFAULT_X_VALUE = 90;
    public static final int DEFAULT_Y_VALUE = 70;
    public static final Point DEFAULT_POINT = new Point(DEFAULT_X_VALUE, DEFAULT_Y_VALUE);
    protected ShowcaseView mShowcaseView;
    protected OnShowcaseEventListener mockListener;

    @Before
    public void setup() {
        mockListener = mock(OnShowcaseEventListener.class);
        ActivityController<TestingActivity> controller = Robolectric.buildActivity(TestingActivity.class);
        TestingActivity activity = controller.create().start().resume().get();
        mShowcaseView = activity.mShowcaseView;
        mShowcaseView.setShowcasePosition(DEFAULT_POINT);
    }

    @Test
    public void testSetOnShowcaseViewListenerIsSet() {
        mShowcaseView.setOnShowcaseEventListener(mockListener);
        assertEquals(mockListener, mShowcaseView.mEventListener);
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

    @Test
    public void testWhenAShowcaseIsShown_TheListenerIsNotified() {
        mShowcaseView.setOnShowcaseEventListener(mockListener);

        mShowcaseView.show();

        verify(mockListener).onShowcaseViewShow(mShowcaseView);
    }

    @Test
    public void testWhenAShowcaseIsHidden_TheListenerIsNotified() {
        mShowcaseView.setOnShowcaseEventListener(mockListener);

        mShowcaseView.hide();

        verify(mockListener).onShowcaseViewHide(mShowcaseView);
    }

    @Test
    public void testWhenAShowcaseIsHidden_TheListenerIsNotNotified_AboutHidingImmediately() {
        mShowcaseView.setOnShowcaseEventListener(mockListener);

        mShowcaseView.hide();

        verify(mockListener, never()).onShowcaseViewDidHide(mShowcaseView);
    }

}
