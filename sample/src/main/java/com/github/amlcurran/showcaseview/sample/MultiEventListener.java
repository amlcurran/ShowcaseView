package com.github.amlcurran.showcaseview.sample;

import android.view.MotionEvent;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiEventListener implements OnShowcaseEventListener {

    private final List<OnShowcaseEventListener> listeners;

    public MultiEventListener(OnShowcaseEventListener... listeners) {
        this.listeners = new ArrayList<>();
        this.listeners.addAll(Arrays.asList(listeners));
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        for (OnShowcaseEventListener listener : listeners) {
            listener.onShowcaseViewHide(showcaseView);
        }
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
        for (OnShowcaseEventListener listener : listeners) {
            listener.onShowcaseViewDidHide(showcaseView);
        }
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        for (OnShowcaseEventListener listener : listeners) {
            listener.onShowcaseViewShow(showcaseView);
        }
    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
        for (OnShowcaseEventListener listener : listeners) {
            listener.onShowcaseViewTouchBlocked(motionEvent);
        }
    }
}
