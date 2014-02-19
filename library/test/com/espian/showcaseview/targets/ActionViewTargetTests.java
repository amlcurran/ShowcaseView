package com.espian.showcaseview.targets;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.espian.showcaseview.ShowcaseViewTests;
import com.espian.showcaseview.TestingActivity;
import com.espian.showcaseview.actionbar.ActionBarViewWrapper;
import com.espian.showcaseview.actionbar.reflection.BaseReflector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

        View mockView = ViewTargetTests.getMockView();
        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getSpinnerView()).thenReturn(mockView);

        ActionViewTarget spinnerTarget = new TestableActionViewTarget(mActivity,
                ActionViewTarget.Type.SPINNER, wrapper, null);
        spinnerTarget.mActionBarWrapper = wrapper;

        Point actualPoint = spinnerTarget.getPoint();

        assertEquals(ShowcaseViewTests.DEFAULT_POINT, actualPoint);

    }

    @Test
    public void testHomeTargetWorks() {

        View mockView = ViewTargetTests.getMockView();
        BaseReflector reflector = mock(BaseReflector.class);
        when(reflector.getHomeButton()).thenReturn(mockView);

        ActionViewTarget homeTarget = new TestableActionViewTarget(mActivity,
                ActionViewTarget.Type.HOME, null, reflector);

        Point actualPoint = homeTarget.getPoint();

        assertEquals(ShowcaseViewTests.DEFAULT_POINT, actualPoint);

    }

    @Test
    public void testOverflowTargetWorks() {

        View mockView = ViewTargetTests.getMockView();
        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getOverflowView()).thenReturn(mockView);

        ActionViewTarget overflowTarget = new TestableActionViewTarget(mActivity,
                ActionViewTarget.Type.OVERFLOW, wrapper, null);
        overflowTarget.mActionBarWrapper = wrapper;

        Point actualPoint = overflowTarget.getPoint();

        assertEquals(ShowcaseViewTests.DEFAULT_POINT, actualPoint);

    }

    @Test
    public void testTitleTargetWorks() {

        View mockView = ViewTargetTests.getMockView();
        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getTitleView()).thenReturn(mockView);

        TestableActionViewTarget titleTarget = new TestableActionViewTarget(mActivity,
                ActionViewTarget.Type.TITLE, wrapper, null);
        titleTarget.mActionBarWrapper = wrapper;

        Point actualPoint = titleTarget.getPoint();

        assertEquals(ShowcaseViewTests.DEFAULT_POINT, actualPoint);

    }

    private class TestableActionViewTarget extends ActionViewTarget {

        public TestableActionViewTarget(Activity activity, Type type, ActionBarViewWrapper wrapper,
                                        BaseReflector reflector) {
            super(activity, type);
            mActionBarWrapper = wrapper;
            mReflector = reflector;
        }

        @Override
        protected void setUp() {
            // Don't want to set up - we've manually injected our dependencies
        }
    }

}
