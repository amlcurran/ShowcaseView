package com.github.amlcurran.showcaseview.sample.v14;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.sample.R;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;

public class ActionItemsSampleActivity extends SherlockActivity {

    ShowcaseView sv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu, menu);

        ActionViewTarget target = new ActionViewTarget(this, ActionViewTarget.Type.OVERFLOW);
        sv = new ShowcaseView.Builder(this)
                .setTarget(target)
                .setContentTitle(R.string.showcase_simple_title)
                .setContentText(R.string.showcase_simple_message)
                .doNotBlockTouches()
                .build();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            ActionViewTarget target = new ActionViewTarget(this, ActionViewTarget.Type.HOME);
            sv.setShowcase(target, true);
        }
        else if (itemId == R.id.menu_item1) {
            ActionItemTarget target = new ActionItemTarget(this, R.id.menu_item1);
            sv.setShowcase(target, true);
        }
        else if (itemId == R.id.menu_item2) {
            ActionViewTarget target = new ActionViewTarget(this, ActionViewTarget.Type.TITLE);
            sv.setShowcase(target, true);
        }
        return true;
    }

}
