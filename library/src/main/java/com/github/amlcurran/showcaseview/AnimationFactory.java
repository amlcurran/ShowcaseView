package com.github.amlcurran.showcaseview;

public interface AnimationFactory {
    void fadeInView(Object target, long duration, AnimationStartListener listener);

    void fadeOutView(Object target, long duration, AnimationEndListener listener);

    public interface AnimationStartListener {
        void onAnimationStart();
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }
}
