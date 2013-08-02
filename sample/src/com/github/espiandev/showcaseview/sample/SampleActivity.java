package com.github.espiandev.showcaseview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.sample.v14.ActionItemsSampleActivity;
import com.github.espiandev.showcaseview.sample.v14.MultipleActionItemsSampleActivity;

public class SampleActivity extends Activity implements View.OnClickListener,
        ShowcaseView.OnShowcaseEventListener {

    ShowcaseView sv;
    Button buttonTop;
    Button buttonDown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonTop = (Button) findViewById(R.id.buttonBlocked);
        buttonTop.setOnClickListener(this);
        buttonDown = (Button) findViewById(R.id.buttonToMultipleItemsActivtiy);
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

        switch (view.getId()) {
            case R.id.buttonBlocked:
                if (sv.isShown()) {
                    sv.animateGesture(0, 0, 0, -400);
                } else {
                    startSdkLevelAppropriateActivity(R.id.buttonBlocked);
                }
                break;
            case R.id.buttonToMultipleItemsActivtiy:
                startSdkLevelAppropriateActivity(R.id.buttonToMultipleItemsActivtiy);
                break;
        }
    }

    private void startSdkLevelAppropriateActivity(int buttonId) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            Toast.makeText(this, "Your Android version is < Honeycomb. Sample app ends here", Toast.LENGTH_SHORT).show();
        }
        else if(buttonId == R.id.buttonBlocked) {
            startActivity(new Intent(this, ActionItemsSampleActivity.class));
        } else if(buttonId == R.id.buttonToMultipleItemsActivtiy) {
            startActivity(new Intent(this, MultipleActionItemsSampleActivity.class));
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        buttonTop.setText(R.string.button_show);
        buttonDown.setVisibility(View.VISIBLE);

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        buttonTop.setText(R.string.button_hide);
        buttonDown.setVisibility(View.GONE);
    }
}
