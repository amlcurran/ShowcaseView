package com.espian.showcaseview;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml", reportSdk = 9)
public class ShowcaseViewLegacyTests extends ShowcaseViewTests {

    @Test
    public void testWhenAShowcaseIsHidden_TheListenerIsNotified() {
        mShowcaseView.setOnShowcaseEventListener(mockListener);

        mShowcaseView.hide();

        verify(mockListener).onShowcaseViewHide(mShowcaseView);
    }

    @Test
    public void testWhenAShowcaseHasBeenHidden_TheListenerIsNotified() {
        mShowcaseView.setOnShowcaseEventListener(mockListener);

        mShowcaseView.hide();

        verify(mockListener).onShowcaseViewDidHide(mShowcaseView);
    }

}
