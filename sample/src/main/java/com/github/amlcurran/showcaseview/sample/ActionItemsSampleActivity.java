package com.github.amlcurran.showcaseview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.espian.showcaseview.sample.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class ActionItemsSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            ViewTarget navigationButtonViewTarget = ViewTargets.navigationButtonViewTarget(toolbar);
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(navigationButtonViewTarget)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentText("Here's how to highlight items on a toolbar")
                    .build()
                    .show();
        } catch (ViewTargets.MissingViewException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
