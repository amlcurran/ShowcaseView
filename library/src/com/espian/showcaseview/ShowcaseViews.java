package com.espian.showcaseview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseViews {
    public static final String TAG = "ShowcaseViews";

    private final List<ShowcaseView> mViews = new ArrayList<ShowcaseView>();
    private final Activity mActivity;
    private Listener mListener;

    public interface Listener {
        public void showcaseComplete();
    }

    public void setShowcaseCompleteListener(Listener listener){
        mListener = listener;
    }

    private OnShowcaseAcknowledged showcaseAcknowledgedListener = new OnShowcaseAcknowledged() {
        @Override
        public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
            if(mListener != null){
                mListener.showcaseComplete();
            }
        }
    };

    public interface OnShowcaseAcknowledged {
        void onShowCaseAcknowledged(ShowcaseView showcaseView);
    }

    public ShowcaseViews(Activity activity) {
        this.mActivity = activity;
    }

    public ShowcaseViews(Activity activity, OnShowcaseAcknowledged acknowledgedListener) {
        this(activity);
        this.showcaseAcknowledgedListener = acknowledgedListener;
    }

    public void markViewsAsShown() {
        for (ShowcaseView myView : mViews) {
            myView.markViewAsShown();
        }
        removeAllViews();
    }

    public void removeAllViews() {
        while (mViews.size() >= 1) {
            mViews.remove(0);
        }
        Log.d(TAG, "removeAllViews view count: " + getViewCount());
    }

    public void markViewsAsNOTShown() {
        for (ShowcaseView myView : mViews) {
            myView.markViewAsNOTShown();
        }
    }

    public int getViewCount() {
        return mViews.size();
    }

    public ShowcaseViews addView(ItemViewProperties properties) {
        ShowcaseViewBuilder builder = new ShowcaseViewBuilder(mActivity)
                .setText(properties.titleResId, properties.messageResId)
                .setShowcaseIndicatorScale(properties.scale)
                .setConfigOptions(properties.configOptions);

        if(showcaseActionBar(properties)) {
            builder.setShowcaseItem(properties.itemType, properties.id, mActivity);
        } else if (properties.id == ItemViewProperties.ID_NO_SHOWCASE) {
            builder.setShowcaseNoView();
        } else {
            builder.setShowcaseView(mActivity.findViewById(properties.id));
        }

        ShowcaseView showcaseView = builder.build();
        showcaseView.overrideOKButtonClick(createShowcaseViewDismissListener(showcaseView));
        showcaseView.overrideSKIPButtonClick(createShowcaseViewSkipListener(showcaseView));
        mViews.add(showcaseView);

        return this;
    }

    private boolean showcaseActionBar(ItemViewProperties properties) {
        return properties.itemType > ItemViewProperties.ID_NOT_IN_ACTIONBAR;
    }

    private View.OnClickListener createShowcaseViewDismissListener(final ShowcaseView showcaseView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.onClick(showcaseView); //Needed for TYPE_ONE_SHOT
                int fadeOutTime = showcaseView.getConfigOptions().fadeOutDuration;
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

    private View.OnClickListener createShowcaseViewSkipListener(final ShowcaseView showcaseView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.onClick(showcaseView); //Needed for TYPE_ONE_SHOT

                // mark all mViews as fired
                markViewsAsShown();

                int fadeOutTime = showcaseView.getConfigOptions().fadeOutDuration;
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
        if (mViews.isEmpty()) {
            showcaseAcknowledgedListener.onShowCaseAcknowledged(showcaseView);
        } else {
            show();
        }
    }

    public void show() {
        if (mViews.isEmpty()) {
            return;
        }
        final ShowcaseView view = mViews.get(0);

        boolean hasShot = mActivity.getSharedPreferences(ShowcaseView.PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
                .getBoolean("hasShot" + view.getConfigOptions().showcaseId, false);
        if (hasShot && view.getConfigOptions().shotType == ShowcaseView.TYPE_ONE_SHOT) {
            // The showcase has already been shot once, so we don't need to do show it again.
            view.setVisibility(View.GONE);
            mViews.remove(0);
            view.getConfigOptions().fadeOutDuration = 0;
            view.performOKButtonClick();
            return;
        }

        view.setVisibility(View.INVISIBLE);
        ((ViewGroup) mActivity.getWindow().getDecorView()).addView(view);
        view.show();
        mViews.remove(0);

    }

    public boolean hasViews(){
        return !mViews.isEmpty();
    }

    public static class ItemViewProperties {

        public static final int ID_NO_SHOWCASE = -2202;
        public static final int ID_NOT_IN_ACTIONBAR = -1;
        public static final int ID_SPINNER = 0;
        public static final int ID_TITLE = 1;
        public static final int ID_OVERFLOW = 2;
        private static final float DEFAULT_SCALE = 1f;

        protected final int titleResId;
        protected final int messageResId;
        protected final int id;
        protected final int itemType;
        protected final float scale;
        protected final ShowcaseView.ConfigOptions configOptions;

        public ItemViewProperties(int titleResId, int messageResId) {
            this(ID_NO_SHOWCASE, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, DEFAULT_SCALE, null);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId) {
            this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, DEFAULT_SCALE, null);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, float scale) {
            this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, scale, null);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType) {
            this(id, titleResId, messageResId, itemType, DEFAULT_SCALE, null);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType, float scale) {
            this(id, titleResId, messageResId, itemType, scale, null);
        }

        public ItemViewProperties(int titleResId, int messageResId, ShowcaseView.ConfigOptions configOptions) {
            this(ID_NO_SHOWCASE, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, DEFAULT_SCALE, configOptions);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, ShowcaseView.ConfigOptions configOptions) {
            this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, DEFAULT_SCALE, configOptions);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, float scale, ShowcaseView.ConfigOptions configOptions) {
            this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, scale, configOptions);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType, ShowcaseView.ConfigOptions configOptions) {
            this(id, titleResId, messageResId, itemType, DEFAULT_SCALE, configOptions);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType, float scale, ShowcaseView.ConfigOptions configOptions) {
            this.id = id;
            this.titleResId = titleResId;
            this.messageResId = messageResId;
            this.itemType = itemType;
            this.scale = scale;
            this.configOptions = configOptions;
        }
    }
}
