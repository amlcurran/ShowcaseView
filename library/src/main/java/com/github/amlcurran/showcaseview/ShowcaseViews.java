package com.github.amlcurran.showcaseview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for {@link ShowcaseView} allows to showcase multiple areas one after another.
 *
 * Created by Dariusz Deoniziak (darekdeoniziak@gmail.com)
 */
public class ShowcaseViews implements OnShowcaseEventListener {

    private static final String TAG = ShowcaseViews.class.getSimpleName();

    public static final int SHOT_MODE_MULTIPLE = 0;
    public static final int SHOT_MODE_SINGLE = 1;
    public static final int SHOT_MODE_ONCE_EACH_VIEW = 2;

    private final List<ViewProperties> views;
    private final Activity activity;
    private final int showcaseStyleResId;
    private final ShowcaseViewsListener listener;
    private boolean disableGuidedView;
    private ShowcaseView showcaseView = null;
    private int shotMode;

    private ViewGroup topViewGroup;

    private ShowcaseViews(Builder builder) {
        this.activity = builder.activity;
        this.views = builder.views;
        this.showcaseStyleResId = builder.showcaseStyleResId;
        this.listener = builder.listener;
        this.disableGuidedView = builder.disableGuidedView;
        this.shotMode = builder.shotMode;
        topViewGroup = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        show();
    }

    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   <code>true</code> to enable, <code>false</code> to disable
     *                  the views.
     */
    public void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    private boolean didShowLastGuide() {
        ViewProperties viewProperties = views.get(views.size() - 1);
        int shotId = viewProperties.id;

        return didShowGuideFor(shotId);
    }

    private boolean didShowFirstGuide() {
        ViewProperties viewProperties = views.get(0);
        int shotId = viewProperties.id;

        return didShowGuideFor(shotId);
    }

    public boolean didShowGuideFor(int shotId) {
        SharedPreferences settings = activity.getSharedPreferences("showcase_internal", Context.MODE_PRIVATE);
        return settings.getBoolean("hasShot" + shotId, false);
    }

    /**
     * Showcases will be shown in the order they where added, continuing when the button is pressed
     */
    public void show() {
        if (views != null && !views.isEmpty()) {
            switch (shotMode) {
                case (SHOT_MODE_MULTIPLE):
                    prepareTopViewGroup();
                    if (listener != null) {
                        listener.onShowcaseStart();
                    }
                    showNextGuide();
                    break;
                case (SHOT_MODE_SINGLE):
                    if (!didShowFirstGuide()) {
                        prepareTopViewGroup();
                        if (listener != null) {
                            listener.onShowcaseStart();
                        }
                        showNextGuide();
                    } else if (listener != null) {
                        listener.onShowcaseEnd(false);
                    }
                    break;
                case (SHOT_MODE_ONCE_EACH_VIEW):
                    if (!didShowLastGuide()) {
                        prepareTopViewGroup();
                        if (listener != null) {
                            listener.onShowcaseStart();
                        }
                        showNextGuide();
                    } else if (listener != null) {
                        listener.onShowcaseEnd(false);
                    }
                    break;
            }
        }
    }

    public void hide() {
        restoreTopViewGroup();
        if (showcaseView != null)
            showcaseView.hide();
    }

    public boolean isShowing() {
        if (showcaseView == null)
            return false;
        return showcaseView.isShowing();
    }

    private void prepareTopViewGroup() {
        if (disableGuidedView) {
            enableDisableViewGroup(topViewGroup, false);
        }
    }

    private void showNextGuide() {
        if (views.isEmpty()) {
            restoreTopViewGroup();
            if (listener != null)
                listener.onShowcaseEnd(true);
            return;
        }

        final ViewProperties viewProperties = views.get(0);
        showcaseView = null;
        ShowcaseView.Builder builder = new ShowcaseView.Builder(activity, true)
                .setTarget(new ViewTarget(viewProperties.id, activity))
                .setContentTitle(viewProperties.title)
                .setContentText(viewProperties.message)
                .setShowcaseEventListener(this)
                .setStyle(showcaseStyleResId);

        if (shotMode != SHOT_MODE_MULTIPLE) {
            builder.singleShot(viewProperties.id);
        }

        showcaseView = builder.build();

        if (showcaseView.getVisibility() != View.VISIBLE) {
            removeCurrentGuide();
            showNextGuide();
        }
    }

    private void restoreTopViewGroup() {
        if (disableGuidedView) {
            enableDisableViewGroup(topViewGroup, true);
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        removeCurrentGuide();
        showNextGuide();
    }

    private void removeCurrentGuide() {
        try {
            ViewProperties vp = views.get(0);
            views.remove(vp);
            vp = null;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
        // nothing to do here
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        // nothing to do here
    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
        // nothing to do here
    }

    @Override
    protected void finalize() throws Throwable {
        showcaseView = null;

        super.finalize();
    }

    /**
     * Used for views on the ActionBar
     */
    public class ItemViewProperties extends ViewProperties {
        public static final int ID_SPINNER = 0;
        public static final int ID_TITLE = 0;
        protected final int itemType;

        public ItemViewProperties(int id, String title, String message, int itemType) {
            super(id, title, message);
            this.itemType = itemType;
        }
    }

    /**
     * Used for all views except those on the ActionBar
     */
    public static class ViewProperties {
        protected final CharSequence title;
        protected final CharSequence message;
        protected final int id;

        public ViewProperties(int id, CharSequence title, CharSequence message) {
            this.id = id;
            this.title = title;
            this.message = message;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void dimView(View view, float alphaValue) {
        ApiUtils apiUtils = new ApiUtils();
        if (apiUtils.isCompatWithHoneycomb()) {
            view.setAlpha(alphaValue);
        }
    }

    public static class Builder {
        private final Activity activity;
        private int showcaseStyleResId;
        private ShowcaseViewsListener listener;
        private boolean disableGuidedView;
        private int shotMode = SHOT_MODE_MULTIPLE;

        private List<ViewProperties> views = new ArrayList<ViewProperties>();

        /**
         * @param activity The activity containing the views you wish to showcase
         */
        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * @param showcaseStyleResId Must be the layout of a ShowcaseView - use this to style your showcase
         */
        public Builder setStyle(int showcaseStyleResId) {
            this.showcaseStyleResId = showcaseStyleResId;
            return this;
        }

        /**
         * @param listener Provides callbacks before showing first showcase and after showing last.
         */
        public Builder setListener(ShowcaseViewsListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setDisableViewGroup() {
            disableGuidedView = true;
            return this;
        }

        public Builder addView(ItemViewProperties properties) {
            views.add(properties);
            return this;
        }

        public Builder addView(ViewProperties properties) {
            views.add(properties);
            return this;
        }

        public Builder addViews(List<ViewProperties> views, boolean override) {
            if (override)
                views.clear();
            views.addAll(views);
            return this;
        }

        /**
         * <p>Change mode to either display showcases multiple times, single or each one once.</p>
         * <p>Available modes are:</p>
         * <br>{@link #SHOT_MODE_MULTIPLE} - default mode, will always show all guides.</br>
         * <br>{@link #SHOT_MODE_SINGLE} - all group of showcase views will be shown only once,
         * even if user will not finish the guide.</br>
         * <br>{@link #SHOT_MODE_ONCE_EACH_VIEW} - each one view will be shown once,
         * if user will not finish the guide the showcase will start from last, not shown view</br>
         *
         * @param mode a mode to select
         */
        public Builder setShotMode(int mode) {
            if (mode == SHOT_MODE_MULTIPLE || mode == SHOT_MODE_SINGLE || mode == SHOT_MODE_ONCE_EACH_VIEW) {
                shotMode = mode;
            }
            return this;
        }

        /**
         * Showcases will be shown in the order they where added, continuing when the button is pressed
         */
        public ShowcaseViews show() {
            return new ShowcaseViews(this);
        }
    }

    public interface ShowcaseViewsListener {
        public void onShowcaseStart();
        public void onShowcaseEnd(boolean hadViews);
    }
}
