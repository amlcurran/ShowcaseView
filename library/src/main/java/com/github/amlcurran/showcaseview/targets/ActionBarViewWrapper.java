package com.github.amlcurran.showcaseview.targets;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import java.lang.reflect.Field;

/**
 * Class which wraps round the many implementations of ActionBarView and allows finding of Action
 * items
 */
class ActionBarViewWrapper {

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

    /**
     * Return the view which represents the overflow action item on the ActionBar, or null if there isn't one
     */
    public View getOverflowView() {
        try {
            Field actionMenuPresenterField = mAbsActionBarViewClass.getDeclaredField("mActionMenuPresenter");
            actionMenuPresenterField.setAccessible(true);
            Object actionMenuPresenter = actionMenuPresenterField.get(mActionBarView);
            Field overflowButtonField = actionMenuPresenter.getClass().getDeclaredField("mOverflowButton");
            overflowButtonField.setAccessible(true);
            return (View) overflowButtonField.get(actionMenuPresenter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public View getActionItem(int actionItemId) {
        try {
            Field actionMenuPresenterField = mAbsActionBarViewClass.getDeclaredField("mActionMenuPresenter");
            actionMenuPresenterField.setAccessible(true);
            Object actionMenuPresenter = actionMenuPresenterField.get(mActionBarView);

            Field menuViewField = actionMenuPresenter.getClass().getSuperclass().getDeclaredField("mMenuView");
            menuViewField.setAccessible(true);
            Object menuView = menuViewField.get(actionMenuPresenter);

            Field mChField;
            if (menuView.getClass().toString().contains("com.actionbarsherlock")) {
                // There are thousands of superclasses to traverse up
                // Have to get superclasses because mChildren is private
                mChField = menuView.getClass().getSuperclass().getSuperclass()
                        .getSuperclass().getSuperclass().getDeclaredField("mChildren");
            } else if (menuView.getClass().toString().contains("android.support.v7")) {
                mChField = menuView.getClass().getSuperclass().getSuperclass()
                        .getSuperclass().getDeclaredField("mChildren");
            } else {
                mChField = menuView.getClass().getSuperclass().getSuperclass()
                        .getDeclaredField("mChildren");
            }
            mChField.setAccessible(true);
            Object[] mChs = (Object[]) mChField.get(menuView);
            for (Object mCh : mChs) {
                if (mCh != null) {
                    View v = (View) mCh;
                    if (v.getId() == actionItemId) {
                        return v;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

}
