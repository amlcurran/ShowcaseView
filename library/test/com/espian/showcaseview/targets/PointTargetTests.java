package com.espian.showcaseview.targets;

import android.graphics.Point;

import com.espian.showcaseview.ShowcaseViewTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../AndroidManifest.xml")
public class PointTargetTests {

    @Test
    public void testPointTargetReturnsSetPoint() {
        Point expectedPoint = ShowcaseViewTests.DEFAULT_POINT;

        PointTarget target = new PointTarget(ShowcaseViewTests.DEFAULT_POINT);
        Point actualPoint = target.getPoint();

        assertEquals(expectedPoint, actualPoint);
    }

    @Test
    public void testPointTargetReturnsPointFromXY() {
        Point expectedPoint = ShowcaseViewTests.DEFAULT_POINT;

        PointTarget target = new PointTarget(ShowcaseViewTests.DEFAULT_X_VALUE,
                ShowcaseViewTests.DEFAULT_Y_VALUE);
        Point actualPoint = target.getPoint();

        assertEquals(expectedPoint, actualPoint);
    }

}
