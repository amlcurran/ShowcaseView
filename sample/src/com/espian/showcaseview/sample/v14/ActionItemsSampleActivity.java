package com.espian.showcaseview.sample.v14;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.sample.R;
import com.espian.showcaseview.targets.ActionItemTarget;
import com.espian.showcaseview.targets.ActionViewTarget;

public class ActionItemsSampleActivity extends SherlockActivity {

    ShowcaseView sv;
    ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mOptions.block = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu, menu);

        ActionViewTarget target = new ActionViewTarget(this, ActionViewTarget.Type.OVERFLOW);
        sv = ShowcaseView.insertShowcaseView(target, this,
                R.string.showcase_simple_title, R.string.showcase_simple_message, mOptions);

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
