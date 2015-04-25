package uk.co.amlcurran.showcaseview;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class Utils {

    private static final String TAG = ShowcaseView.class.getSimpleName();

    static void assertIsFrameLayout(View view) {
        if (!(view instanceof FrameLayout)) {
            Log.w(TAG, "Expected a FrameLayout as a parent, unexpected things may occur");
        }
    }
}
