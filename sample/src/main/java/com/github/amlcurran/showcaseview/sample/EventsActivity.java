package com.github.amlcurran.showcaseview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        TextView eventLog = (TextView) findViewById(R.id.events_log);
        Button customButton = (Button) getLayoutInflater().inflate(R.layout.view_custom_button, null);

        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme3)
                .setTarget(new ViewTarget(R.id.imageView, this))
                .setContentTitle("Events")
                .setContentText("Listening to ShowcaseView events is easy!")
                .setShowcaseEventListener(new MultiEventListener(new LogToTextListener(eventLog), new ShakeButtonListener(customButton)))
                .replaceEndButton(customButton)
                .build();
    }

    private static class LogToTextListener implements OnShowcaseEventListener {

        private final TextView eventLog;
        private final SpannableStringBuilder stringBuilder;

        public LogToTextListener(TextView eventLog) {
            this.eventLog = eventLog;
            this.stringBuilder = new SpannableStringBuilder();
        }

        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView) {
            append("Showcase hiding");
        }

        private void append(String text) {
            stringBuilder.append("\n").append(text);
            eventLog.setText(stringBuilder);
        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
            append("Showcase hidden");
        }

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView) {
            append("Showcase shown");
        }

        @Override
        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
            append("Touch blocked: x: " + motionEvent.getX() + " y: " + motionEvent.getY());
        }

    }

    private class MultiEventListener implements OnShowcaseEventListener {

        private final List<OnShowcaseEventListener> listeners;

        public MultiEventListener(OnShowcaseEventListener... listeners) {
            this.listeners = new ArrayList<>();
            this.listeners.addAll(Arrays.asList(listeners));
        }

        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView) {
            for (OnShowcaseEventListener listener : listeners) {
                listener.onShowcaseViewHide(showcaseView);
            }
        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
            for (OnShowcaseEventListener listener : listeners) {
                listener.onShowcaseViewDidHide(showcaseView);
            }
        }

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView) {
            for (OnShowcaseEventListener listener : listeners) {
                listener.onShowcaseViewShow(showcaseView);
            }
        }

        @Override
        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
            for (OnShowcaseEventListener listener : listeners) {
                listener.onShowcaseViewTouchBlocked(motionEvent);
            }
        }
    }

    private class ShakeButtonListener implements OnShowcaseEventListener {
        private final Button button;

        public ShakeButtonListener(Button button) {
            this.button = button;
        }

        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView) {
            // No-op
        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
            // No-op
        }

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView) {
            // No-op
        }

        @Override
        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
            int translation = getResources().getDimensionPixelOffset(R.dimen.touch_button_wobble);
            ViewCompat.animate(button)
                    .translationXBy(translation)
                    .setInterpolator(new WobblyInterpolator(3));
        }
    }

    private class WobblyInterpolator implements Interpolator {

        private final double CONVERT_TO_RADS = 2 * Math.PI;
        private final int cycles;

        public WobblyInterpolator(int cycles) {
            this.cycles = cycles;
        }

        @Override
        public float getInterpolation(float proportion) {
            double sin = Math.sin(cycles * proportion * CONVERT_TO_RADS);
            return (float) sin;
        }

    }
}
