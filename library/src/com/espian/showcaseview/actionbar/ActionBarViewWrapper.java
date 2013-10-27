package com.espian.showcaseview.actionbar;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import java.lang.reflect.Field;

/**
 * Class which wraps round the many implementations of ActionBarView and allows finding of Action
 * items
 */
public class ActionBarViewWrapper {

    private ViewParent mActionBarView;
    private Class mActionBarViewClass;
    private Class mAbsActionBarViewClass;
    
    public ActionBarViewWrapper(ViewParent actionBarView) {
        if (!actionBarView.getClass().getName().contains("ActionBarView")) {
            String previousP = actionBarView.getClass().getName();
            actionBarView = actionBarView.getParent();
            String throwP = actionBarView.getClass().getName();
            if (!actionBarView.getClass().getName().contains("ActionBarView")) {
                throw new IllegalStateException("Cannot find ActionBarView for " +
                        "Activity, instead found " + previousP + " and " + throwP);
            }
        }
        mActionBarView = actionBarView;
        mActionBarViewClass = actionBarView.getClass();
        mAbsActionBarViewClass = actionBarView.getClass().getSuperclass();
    }

    /**
     * Return the view which represents the spinner on the ActionBar, or null if there isn't one
     */
    public View getSpinnerView() {
        try {
            Field spinnerField = mActionBarViewClass.getDeclaredField("mSpinner");
            spinnerField.setAccessible(true);
            return (View) spinnerField.get(mActionBarView);
        } catch (NoSuchFieldException e) {
            Log.e("TAG", "Failed to find actionbar spinner", e);
        } catch (IllegalAccessException e) {
            Log.e("TAG", "Failed to access actionbar spinner", e);
        }
        return null;
    }

    /**
     * Return the view which represents the title on the ActionBar, or null if there isn't one
     */
    public View getTitleView() {
        try {
            Field mTitleViewField = mActionBarViewClass.getDeclaredField("mTitleView");
            mTitleViewField.setAccessible(true);
            return (View) mTitleViewField.get(mActionBarView);
        } catch (NoSuchFieldException e) {
            Log.e("TAG", "Failed to find actionbar title", e);
        } catch (IllegalAccessException e) {
            Log.e("TAG", "Failed to access actionbar title", e);
        }
        return null;
    }
    
}
