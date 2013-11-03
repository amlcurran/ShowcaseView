package com.espian.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.espian.showcaseview.ShowcaseViewTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    public static View getMockView() {
        return getMockView(ShowcaseViewTests.DEFAULT_POINT);
    }

    /**
     * Return a mock view for testing with set values for x and y
     */
    public static View getMockView(Point point) {
        View mockView = mock(View.class);
        when(mockView.getX()).thenReturn(Float.valueOf(point.x));
        when(mockView.getY()).thenReturn(Float.valueOf(point.y));
        return mockView;
    }

}
