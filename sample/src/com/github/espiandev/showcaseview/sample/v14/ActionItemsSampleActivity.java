package com.github.espiandev.showcaseview.sample.v14;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.sample.R;

public class ActionItemsSampleActivity extends Activity {

    ShowcaseView sv;
    ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mOptions.block = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        sv = ShowcaseView.insertShowcaseViewWithType(ShowcaseView.ITEM_ACTION_OVERFLOW, R.id.menu_item1, this,
                R.string.showcase_simple_title, R.string.showcase_simple_message, mOptions);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home)
            sv.setShowcaseItem(ShowcaseView.ITEM_ACTION_HOME, 0, this);
        else if (itemId == R.id.menu_item1)
            sv.setShowcaseItem(ShowcaseView.ITEM_ACTION_ITEM, R.id.menu_item1, this);
        else if (itemId == R.id.menu_item2)
            sv.setShowcaseItem(ShowcaseView.ITEM_TITLE, 0, this);
        return true;
    }

}
