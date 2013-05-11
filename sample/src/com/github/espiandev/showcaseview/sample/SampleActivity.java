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

public class SampleActivity extends Activity implements View.OnClickListener,
        ShowcaseView.OnShowcaseEventListener {

    ShowcaseView sv;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = (Button) findViewById(R.id.buttonBlocked);
        button.setOnClickListener(this);

        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        co.hideOnClickOutside = true;
        sv = ShowcaseView.insertShowcaseView(R.id.buttonBlocked, this, "ShowcaseView Sample", "When the ShowcaseView is showing, " +
                "pressing the button will show a gesture. When it is hidden " +
                "it'll go to another Activity.", co);
        sv.setOnShowcaseEventListener(this);

    }

    @Override
    public void onClick(View view) {
        if (sv.isShown()) {
            sv.animateGesture(0, 0, 0, -400);
        } else {
            startSdkLevelAppropriateActivity();
        }
    }

    private void startSdkLevelAppropriateActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            startActivity(new Intent(this, ActionItemsSampleActivity.class));
        } else {
            Toast.makeText(this, "Your Android version is < Honeycomb. Sample app ends here", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        button.setText(R.string.button_show);
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        button.setText(R.string.button_hide);
    }
}
