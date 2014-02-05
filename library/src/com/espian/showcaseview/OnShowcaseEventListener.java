package com.espian.showcaseview;

/**
* @author Alex
*/
public interface OnShowcaseEventListener {

    public void onShowcaseViewHide(ShowcaseView showcaseView);

    public void onShowcaseViewDidHide(ShowcaseView showcaseView);

    public void onShowcaseViewShow(ShowcaseView showcaseView);

    public void onShowcaseViewEndButtonClick(ShowcaseView showcaseView);

    public void onShowcaseViewTargetClick(ShowcaseView showcaseView);

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

        @Override
        public void onShowcaseViewEndButtonClick(ShowcaseView showcaseView) {
        }

        @Override
        public void onShowcaseViewTargetClick(ShowcaseView showcaseView) {
        }
    };

}
