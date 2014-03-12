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
    public void testSetOnShowcaseViewListenerIsSet() {
        showcaseView.setOnShowcaseEventListener(mockListener);
        assertEquals(mockListener, showcaseView.mEventListener);
    }

    @Test
    public void testSetShowcaseXDoesNotChangeShowcaseY() {
        showcaseView.setShowcaseX(50);
        assertEquals(DEFAULT_Y_VALUE, showcaseView.getShowcaseY());
    }

    @Test
    public void testSetShowcaseYDoesNotChangeShowcaseX() {
        showcaseView.setShowcaseY(50);
        assertEquals(DEFAULT_X_VALUE, showcaseView.getShowcaseX());
    }

    @Test
    public void testSetTargetGetsPointFromTarget() {
        Target target = mock(Target.class);
        when(target.getPoint()).thenReturn(new Point());

        showcaseView.setTarget(target);
        Robolectric.runUiThreadTasksIncludingDelayedTasks();

        verify(target).getPoint();
    }

    @Test
    public void testWhenAShowcaseIsShown_TheListenerIsNotified() {
        showcaseView.setOnShowcaseEventListener(mockListener);

        showcaseView.show();

        verify(mockListener).onShowcaseViewShow(showcaseView);
    }

    @Test
    public void testWhenAShowcaseIsHidden_TheListenerIsNotified() {
        showcaseView.setOnShowcaseEventListener(mockListener);

        showcaseView.hide();

        verify(mockListener).onShowcaseViewHide(showcaseView);
    }

    @Test
    public void testWhenAShowcaseIsHidden_TheListenerIsNotNotified_AboutHidingImmediately() {
        showcaseView.setOnShowcaseEventListener(mockListener);

        showcaseView.hide();

        verify(mockListener, never()).onShowcaseViewDidHide(showcaseView);
    }

    @Test
    public void testWhenContentTextIsSet_TheTextDrawerIsUpdated() {
        String expected = "WOOP";
        showcaseView.setContentText(expected);

        assertEquals(expected, String.valueOf(showcaseView.textDrawer.getContentText()));
    }

    @Test
    public void testWhenContentTitleIsSet_TheTextDrawerIsUpdated() {
        String expected = "WOOP";
        showcaseView.setContentTitle(expected);

        assertEquals(expected, String.valueOf(showcaseView.textDrawer.getContentTitle()));
    }

}
