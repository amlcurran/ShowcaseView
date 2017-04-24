package com.github.amlcurran.showcaseview.sample;

import android.app.Activity;
import android.view.MotionEvent;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio on 24/04/17.
 */

public class ShowcaseSequenceHelper implements OnShowcaseEventListener {


    private final Activity activity;
    List<ShowcaseData> showcaseList = new ArrayList<>();
    private int currentShowcaseIndex = 0;

    public ShowcaseSequenceHelper(Activity activity) {
        this.activity = activity;
    }

    public static ShowcaseSequenceHelper newInstace(Activity activity) {
        return new ShowcaseSequenceHelper(activity);
    }

    public ShowcaseSequenceHelper addNewShowcase(int id, Target target, String title, String detail) {
        showcaseList.add(new ShowcaseData(id, target, title, detail));
        return this;
    }

    public void show() {

        if (currentShowcaseIndex < showcaseList.size()) {
            showShowcase(currentShowcaseIndex);
        }
    }

    private void showShowcase(int index) {

        ShowcaseData showcaseData = showcaseList.get(index);
        new ShowcaseView.Builder(activity)
                .setTarget(showcaseData.getTarget())
                .setContentTitle(showcaseData.getTitle())
                .setContentText(showcaseData.getDetail())
//                .setStyle(R.style.CustomShowcaseTheme1)
                .hideOnTouchOutside()
                .setShowcaseEventListener(this)
                .singleShot(showcaseData.getId())
                .build();
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        currentShowcaseIndex++;
        if (currentShowcaseIndex < showcaseList.size()) {
            showShowcase(currentShowcaseIndex);
        }
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }



    // -------

    public class ShowcaseData{
        int id;
        Target target;
        String title;
        String detail;

        public ShowcaseData(int id, Target target, String title, String detail) {
            this.id = id;
            this.target = target;
            this.title = title;
            this.detail = detail;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Target getTarget() {
            return target;
        }

        public void setTarget(Target target) {
            this.target = target;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
