package com.github.amlcurran.showcaseview;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseViews {

    private final List<ShowcaseView.Builder> views = new ArrayList<ShowcaseView.Builder>();
    private final Activity activity;
    private OnShowcaseAcknowledged showcaseAcknowledgedListener = new OnShowcaseAcknowledged() {
        @Override
        public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
            //DEFAULT LISTENER - DOESN'T DO ANYTHING!
        }
    };

    public interface OnShowcaseAcknowledged {
        void onShowCaseAcknowledged(ShowcaseView showcaseView);
    }

    public ShowcaseViews(Activity activity) {
        this.activity = activity;
    }

    public ShowcaseViews(Activity activity, OnShowcaseAcknowledged acknowledgedListener) {
        this(activity);
        this.showcaseAcknowledgedListener = acknowledgedListener;
    }

    public ShowcaseViews addView(ItemViewProperties properties) {
        ShowcaseView.Builder builder = new ShowcaseView.Builder(activity, true);
        builder.setContentTitle(properties.titleResId);
        builder.setContentText(properties.messageResId);
        builder.setTarget(properties.target);

        if (properties.singleShotId != -1)
            builder.singleShot(properties.singleShotId);
        if (properties.buttonTextResId != -1)
            builder.setButtonText(properties.buttonTextResId);
        if (properties.hideOnTouchOutside)
            builder.hideOnTouchOutside();
        if (properties.doNotBlockTouches)
            builder.doNotBlockTouches();
        if (properties.theme != -1)
            builder.setStyle(properties.theme);

        views.add(builder);

        return this;
    }

    private View.OnClickListener createShowcaseViewDismissListener(final ShowcaseView showcaseView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.hide();
                long fadeOutTime = showcaseView.getFadeOutMillis();
                if (fadeOutTime > 0) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showNextView(showcaseView);
                        }
                    }, fadeOutTime);
                } else {
                    showNextView(showcaseView);
                }
            }
        };
    }

    private void showNextView(ShowcaseView showcaseView) {
        if (views.isEmpty()) {
            showcaseAcknowledgedListener.onShowCaseAcknowledged(showcaseView);
        } else {
            show();
        }
    }

    public void show() {
        if (views.isEmpty()) {
            return;
        }
        final ShowcaseView view = views.get(0).build();
        view.overrideButtonClick(createShowcaseViewDismissListener(view));
        views.remove(0);
    }

    public boolean hasViews(){
        return !views.isEmpty();
    }

    public static class ItemViewProperties {

        protected final int titleResId;
        protected final int messageResId;
        protected final int buttonTextResId;
        protected final int theme;
        protected final Target target;
        protected final long singleShotId;
        protected final boolean doNotBlockTouches;
        protected final boolean hideOnTouchOutside;

        public ItemViewProperties(Target target, int titleResId, int messageResId) {
            this(target, titleResId, messageResId, -1, -1, false, false);
        }

        public ItemViewProperties(Target target, int titleResId, int messageResId, int buttonTextResId) {
            this(target, titleResId, messageResId, buttonTextResId, -1, false, false);
        }

        public ItemViewProperties(Target target, int titleResId, int messageResId, long singleShotId) {
            this(target, titleResId, messageResId, -1, singleShotId, false, false);
        }

        public ItemViewProperties(Target target, int titleResId, int messageResId,
                                  boolean hideOnTouchOutside, boolean doNotBlockTouches) {
            this(target, titleResId, messageResId, -1, -1, hideOnTouchOutside, doNotBlockTouches);
        }

        public ItemViewProperties(Target target, int titleResId, int messageResId, int buttonTextResId,
                                  boolean hideOnTouchOutside, boolean doNotBlockTouches) {
            this(target, titleResId, messageResId, buttonTextResId, -1, hideOnTouchOutside, doNotBlockTouches);
        }

        public ItemViewProperties(Target target, int titleResId, int messageResId, int buttonTextResId,
                                  long singleShotId, boolean doNotBlockTouches, boolean hideOnTouchOutside) {
            this(-1, target, titleResId, messageResId, buttonTextResId, singleShotId, doNotBlockTouches, hideOnTouchOutside);
        }


        public ItemViewProperties(int theme, Target target, int titleResId, int messageResId, int buttonTextResId,
                                  long singleShotId, boolean doNotBlockTouches, boolean hideOnTouchOutside) {
            this.theme = theme;
            this.target = target;
            this.titleResId = titleResId;
            this.messageResId = messageResId;
            this.buttonTextResId = buttonTextResId;
            this.singleShotId = singleShotId;
            this.doNotBlockTouches = doNotBlockTouches;
            this.hideOnTouchOutside = hideOnTouchOutside;
        }
    }
}
