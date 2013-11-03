package com.espian.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.espian.showcaseview.ShowcaseViewTests;
import com.espian.showcaseview.TestingActivity;
import com.espian.showcaseview.actionbar.ActionBarViewWrapper;

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
public class ActionItemTargetTests {

    private static final int FAKE_ACTION_ITEM_ID = 70;
    private TestingActivity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(TestingActivity.class)
                .create().start().resume().get();
    }

    @Test
    public void testActionItemIsRetrievedAsView() {

        View mockView = ViewTargetTests.getMockView();

        ActionBarViewWrapper wrapper = mock(ActionBarViewWrapper.class);
        when(wrapper.getActionItem(FAKE_ACTION_ITEM_ID)).thenReturn(mockView);

        TestableActionItemTarget actionItemTarget = new TestableActionItemTarget(mActivity,
                FAKE_ACTION_ITEM_ID, wrapper);
        Point actualPoint = actionItemTarget.getPoint();

        assertEquals(ShowcaseViewTests.DEFAULT_POINT, actualPoint);
    }

    public class TestableActionItemTarget extends ActionItemTarget {

        public TestableActionItemTarget(Activity activity, int itemId, ActionBarViewWrapper wrapper) {
            super(activity, itemId);
            mActionBarWrapper = wrapper;
        }

        @Override
        protected void setUp() {

        }
    }

}
