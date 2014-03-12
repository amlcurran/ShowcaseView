package com.espian.showcaseview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ShowcaseViewBuilderTest {

    ShowcaseViewBuilder builder;

    @Before
    public void setUp() {
        TestingActivity activity = Robolectric.buildActivity(TestingActivity.class)
                .create().start().resume().get();
        builder = new ShowcaseViewBuilder(activity);
    }

    @Test
    public void testSettingTheTitle_SetsTheTitleOnTheShowcaseView() {
        String title = "TITLE";

        builder.setContentTitle(title);

        assertEquals(title, String.valueOf(builder.showcaseView.getContentTitle()));
    }

    @Test
    public void testSettingTheText_SetsTheTextOnTheShowcaseView() {
        String text = "text which should be shown";

        builder.setContentText(text);

        assertEquals(text, String.valueOf(builder.showcaseView.getContentText()));
    }

    private static class TestingActivity extends Activity {

        private Button view;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            view = new Button(this);
            setContentView(view);
        }
    }

}
