package com.espian.showcaseview.targets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.espian.showcaseview.ShowcaseViewTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../AndroidManifest.xml")
public class ViewTargetTests {

    @Test
    public void testViewTargetReturnsViewsPoint() {
        View mockView = getMockView();
        Point expectedPoint = ShowcaseViewTests.DEFAULT_POINT;

        ViewTarget target = new ViewTarget(mockView);
        Point actualPoint = target.getPoint();

        assertEquals(expectedPoint, actualPoint);
    }

    @Test
    public void testIdIsFoundFromActivity() {
        Activity mockActivity = mock(Activity.class);
        int MOCK_VIEW_ID = 9090;

        ViewTarget target = new ViewTarget(MOCK_VIEW_ID, mockActivity);

        verify(mockActivity).findViewById(MOCK_VIEW_ID);
    }

    /**
     * Return a mock view for testing with set values for x and y
     */
    public static View getMockView() {
        View mockView = new WrappedView(Robolectric.application);
        return mockView;
    }

    public static class WrappedView extends View {

        public WrappedView(Context context) {
            super(context);
        }

        public WrappedView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public WrappedView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void getLocationInWindow(int[] location) {
            location[0] = ShowcaseViewTests.DEFAULT_X_VALUE;
            location[1] = ShowcaseViewTests.DEFAULT_Y_VALUE;
        }
    }

}
