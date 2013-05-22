package com.github.espiandev.showcaseview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseViews {

    private final List<ShowcaseView> views = new ArrayList<ShowcaseView>();
    private final Activity activity;
    private final int showcaseTemplateId;

    private interface OnShowcaseAcknowledged {
        void onShowCaseAcknowledged(ShowcaseView oldView);
    }

    public ShowcaseViews(Activity activity, int showcaseTemplateLayout) {
        this.activity = activity;
        this.showcaseTemplateId = showcaseTemplateLayout;
    }

    public void addView(ItemViewProperties properties) {
        ShowcaseView viewTemplate = newInstance();
        viewTemplate.setShowcaseItem(properties.itemType, properties.id, activity);
        viewTemplate.setText(properties.titleResId, properties.messageResId);
        viewTemplate.setShowcaseRadius(properties.radius);
        overrideDefaultClickListener(viewTemplate);
        views.add(viewTemplate);
    }

    private ShowcaseView newInstance() {
        return (ShowcaseView) activity.getLayoutInflater().inflate(showcaseTemplateId, null);
    }

    private void overrideDefaultClickListener(final ShowcaseView viewTemplate) {
        viewTemplate.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTemplate.hide();
                show();
            }
        });
    }

    public void show() {
        if (views.isEmpty()) {
            return;
        }
        final ShowcaseView view = views.get(0);
        ((ViewGroup) activity.getWindow().getDecorView()).addView(view);
        views.remove(0);
    }

    public static class ItemViewProperties {
        public static final int ID_SPINNER = 0;
        public static final int ID_TITLE = 1;
        public static final int ID_OVERFLOW = 2;
        private static final int DEFAULT_RADIUS = 94;

        protected final int titleResId;
        protected final int messageResId;
        protected final int id;
        protected final int itemType;
        protected final int radius;

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType) {
            this(id, titleResId, messageResId, itemType, DEFAULT_RADIUS);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType, int radius) {
            this.id = id;
            this.titleResId = titleResId;
            this.messageResId = messageResId;
            this.itemType = itemType;
            this.radius = radius;
        }
    }
}
