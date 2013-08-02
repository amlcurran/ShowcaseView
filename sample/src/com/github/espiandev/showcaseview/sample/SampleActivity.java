package com.github.espiandev.showcaseview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.sample.legacy.MultipleShowcaseSampleActivity;
import com.github.espiandev.showcaseview.sample.v14.ActionItemsSampleActivity;
import com.github.espiandev.showcaseview.sample.v14.MultipleActionItemsSampleActivity;

public class SampleActivity extends Activity implements View.OnClickListener,
        ShowcaseView.OnShowcaseEventListener {

    ShowcaseView sv;
    Button buttonTop;
    Button buttonMiddle;
    Button buttonDown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonTop = (Button) findViewById(R.id.buttonBlocked);
        buttonTop.setOnClickListener(this);
        buttonMiddle = (Button) findViewById(R.id.buttonToMultipleItemsActivtiy);
        buttonMiddle.setOnClickListener(this);
        buttonDown = (Button) findViewById(R.id.buttonToMultipleShowcaseViewsActivtiy);
        buttonDown.setOnClickListener(this);

        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        co.hideOnClickOutside = true;
        sv = ShowcaseView.insertShowcaseView(R.id.buttonBlocked, this, "ShowcaseView Sample", "When the ShowcaseView is showing, " +
                "pressing the buttonTop will show a gesture. When it is hidden " +
                "it'll go to another Activity.", co);
        sv.setOnShowcaseEventListener(this);

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        switch (viewId) {
            case R.id.buttonBlocked:
                if (sv.isShown()) {
                    sv.animateGesture(0, 0, 0, -400);
                } else {
                    startSdkLevelAppropriateActivity(R.id.buttonBlocked);
                }
                break;
            default:
                startSdkLevelAppropriateActivity(viewId);
                break;
        }
    }

    private void startSdkLevelAppropriateActivity(int buttonId) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            if(buttonId == R.id.buttonToMultipleShowcaseViewsActivtiy) {
                startActivity(new Intent(this,
                        MultipleShowcaseSampleActivity.class));
            } else {
                Toast.makeText(this, "Your Android version is < Honeycomb. You need actionbar support to run this sample.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(buttonId == R.id.buttonBlocked) {
            startActivity(new Intent(this,
                    ActionItemsSampleActivity.class));
        } else if(buttonId == R.id.buttonToMultipleItemsActivtiy) {
            startActivity(new Intent(this,
                    MultipleActionItemsSampleActivity.class));
        } else if(buttonId == R.id.buttonToMultipleShowcaseViewsActivtiy) {
            startActivity(new Intent(this,
                    MultipleShowcaseSampleActivity.class));
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        buttonTop.setText(R.string.button_show);
        buttonMiddle.setVisibility(View.VISIBLE);
        buttonDown.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        buttonTop.setText(R.string.button_hide);
        buttonMiddle.setVisibility(View.GONE);
        buttonDown.setVisibility(View.GONE);
    }
}
