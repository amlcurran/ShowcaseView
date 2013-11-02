package com.espian.showcaseview.targets;

import android.app.ActionBar;
import android.graphics.Point;
import android.view.View;

import com.espian.showcaseview.TestingActivity;
import com.espian.showcaseview.actionbar.ActionBarViewWrapper;
import com.espian.showcaseview.actionbar.reflection.BaseReflector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static java.lang.Math.random;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ActionViewTargetTests {

    private TestingActivity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(TestingActivity.class)
                .create().start().resume().get();
    }

    @Test
    public void testSpinnerTargetWorks() {

        mActivity.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        Point expectedPoint = new Point((int) random(), (int) random());
        View mockView = ViewTargetTests.getMockView(expectedPoint);

        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getSpinnerView()).thenReturn(mockView);

        ActionViewTarget spinnerTarget = new ActionViewTarget(mActivity,
                ActionViewTarget.Type.SPINNER);
        spinnerTarget.mActionBarWrapper = wrapper;

        Point actualPoint = spinnerTarget.getPoint();

        assertEquals(actualPoint, expectedPoint);

    }

    @Test
    public void testHomeTargetWorks() {

        Point expectedPoint = new Point((int) random(), (int) random());
        View mockView = ViewTargetTests.getMockView(expectedPoint);

        BaseReflector reflector = mock(BaseReflector.class);
        when(reflector.getHomeButton()).thenReturn(mockView);

        ActionViewTarget homeTarget = new ActionViewTarget(mActivity,
                ActionViewTarget.Type.HOME);
        homeTarget.mReflector = reflector;

        Point actualPoint = homeTarget.getPoint();

        assertEquals(actualPoint, expectedPoint);

    }

    @Test
    public void testOverflowTargetWorks() {

        Point expectedPoint = new Point((int) random(), (int) random());
        View mockView = ViewTargetTests.getMockView(expectedPoint);

        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getOverflowView()).thenReturn(mockView);

        ActionViewTarget overflowTarget = new ActionViewTarget(mActivity,
                ActionViewTarget.Type.OVERFLOW);
        overflowTarget.mActionBarWrapper = wrapper;

        Point actualPoint = overflowTarget.getPoint();

        assertEquals(actualPoint, expectedPoint);

    }

    @Test
    public void testTitleTargetWorks() {

        Point expectedPoint = new Point((int) random(), (int) random());
        View mockView = ViewTargetTests.getMockView(expectedPoint);

        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getTitleView()).thenReturn(mockView);

        ActionViewTarget titleTarget = new ActionViewTarget(mActivity,
                ActionViewTarget.Type.TITLE);
        titleTarget.mActionBarWrapper = wrapper;

        Point actualPoint = titleTarget.getPoint();

        assertEquals(actualPoint, expectedPoint);

    }



}
