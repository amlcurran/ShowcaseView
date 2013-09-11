package com.github.espiandev.showcaseview;

import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;

import android.app.Activity;
import android.view.View;

public class ShowcaseViewBuilder {

    private final ShowcaseView showcaseView;

    public ShowcaseViewBuilder(Activity activity) {
        this.showcaseView = new ShowcaseView(activity);
    }

    public ShowcaseViewBuilder(ShowcaseView showcaseView) {
        this.showcaseView = showcaseView;
    }

    public ShowcaseViewBuilder(Activity activity, int showcaseLayoutViewId) {
        this.showcaseView = (ShowcaseView) activity.getLayoutInflater().inflate(showcaseLayoutViewId, null);
    }

    public ShowcaseViewBuilder setShowcaseNoView() {
        showcaseView.setShowcaseNoView();
        return this;
    }

    public ShowcaseViewBuilder setShowcaseView(View view) {
        showcaseView.setShowcaseView(view);
        return this;
    }

    public ShowcaseViewBuilder setShowcasePosition(float x, float y) {
        showcaseView.setShowcasePosition(x, y);
        return this;
    }
    
    public ShowcaseViewBuilder setShowcaseOffset(float x, float y) {
    	showcaseView.setShowcaseOffset(x, y);
    	return this;
    }

    public ShowcaseViewBuilder setShowcaseItem(int itemType, int actionItemId, Activity activity) {
        showcaseView.setShowcaseItem(itemType, actionItemId, activity);
        return this;
    }

    public ShowcaseViewBuilder setShowcaseIndicatorScale(float scale) {
        showcaseView.setShowcaseIndicatorScale(scale);
        return this;
    }
    
    public ShowcaseViewBuilder setShowcaseConfigOptions(ConfigOptions configOptions) {
	if (null != configOptions) {
    	   showcaseView.setConfigOptions(configOptions);
	}
    	return this;
    }

    public ShowcaseViewBuilder overrideButtonClick(View.OnClickListener listener) {
        showcaseView.overrideButtonClick(listener);
        return this;
    }

    public ShowcaseViewBuilder animateGesture(float offsetStartX, float offsetStartY, float offsetEndX, float offsetEndY) {
        showcaseView.animateGesture(offsetStartX, offsetStartY, offsetEndX, offsetEndY);
        return this;
    }
    
    @Deprecated
    public ShowcaseViewBuilder setTextColors(int titleTextColor, int detailTextColor) {
    	return this;
    }

    public ShowcaseViewBuilder setText(String titleText, String subText) {
        showcaseView.setText(titleText, subText);
        return this;
    }

    public ShowcaseViewBuilder setText(int titleText, int subText) {
        showcaseView.setText(titleText, subText);
        return this;
    }

    public ShowcaseViewBuilder pointTo(View view) {
        showcaseView.pointTo(view);
        return this;
    }

    public ShowcaseViewBuilder pointTo(float x, float y) {
        showcaseView.pointTo(x, y);
        return this;
    }

    public ShowcaseView build(){
        return showcaseView;
    }
}
