package com.github.amlcurran.showcaseview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        TextView eventLog = (TextView) findViewById(R.id.events_log);

        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ViewTarget(R.id.imageView, this))
                .setContentTitle("Events")
                .setContentText("Listening to ShowcaseView events is easy!")
                .setShowcaseEventListener(new LogToTextListener(eventLog))
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
    }
}
