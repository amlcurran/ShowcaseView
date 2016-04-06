package com.github.amlcurran.showcaseview.sample;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.lang.reflect.Field;

/**
 * A collection of not-officially supported ViewTargets. Use them at your own risk!
 */
public class ViewTargets {

    /**
     * Highlight the navigation button (the Up or Navigation drawer button) in a Toolbar
     * @param toolbar The toolbar to search for the view in
     * @return the {@link ViewTarget} to supply to a {@link com.github.amlcurran.showcaseview.ShowcaseView}
     * @throws MissingViewException when the view couldn't be found. Raise an issue on Github if you get this!
     */
    public static ViewTarget navigationButtonViewTarget(Toolbar toolbar) throws MissingViewException {
        try {
            Field field = Toolbar.class.getDeclaredField("mNavButtonView");
            field.setAccessible(true);
            View navigationView = (View) field.get(toolbar);
            return new ViewTarget(navigationView);
        } catch (NoSuchFieldException e) {
            throw new MissingViewException(e);
        } catch (IllegalAccessException e) {
            throw new MissingViewException(e);
        }
    }

    public static class MissingViewException extends Exception {

        public MissingViewException(Throwable throwable) {
            super(throwable);
        }
    }
}
