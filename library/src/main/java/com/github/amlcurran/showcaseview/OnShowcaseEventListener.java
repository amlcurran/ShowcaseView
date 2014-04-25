package com.github.amlcurran.showcaseview;

/**
* @author Alex
*/
public interface OnShowcaseEventListener {

    /**
     * Called when the ShowcaseView has been told to hide. Use {@link #onShowcaseViewDidHide(ShowcaseView)}
     * if you want to know when the ShowcaseView has been fully hidden.
     */
    public void onShowcaseViewHide(ShowcaseView showcaseView);

    /**
     * Called when the animation hiding the ShowcaseView has finished, and it is no longer visible on the screen.
     */
    public void onShowcaseViewDidHide(ShowcaseView showcaseView);

    /**
     * Called when the ShowcaseView is shown.
     */
    public void onShowcaseViewShow(ShowcaseView showcaseView);

    /**
     * Empty implementation of OnShowcaseViewEventListener such that null
     * checks aren't needed
     */
    public static final OnShowcaseEventListener NONE = new OnShowcaseEventListener() {
        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView) {

        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

        }

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView) {

        }
    };

}
