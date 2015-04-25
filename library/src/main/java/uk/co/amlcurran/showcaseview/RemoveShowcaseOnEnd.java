package uk.co.amlcurran.showcaseview;

import android.animation.Animator;

class RemoveShowcaseOnEnd implements Animator.AnimatorListener {
    private final ShowcaseView showcaseView;

    public RemoveShowcaseOnEnd(ShowcaseView showcaseView) {
        this.showcaseView = showcaseView;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        showcaseView.removeWithoutAnimation();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
