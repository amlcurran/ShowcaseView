package com.github.espiandev.showcaseview.sample.v14;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.github.espiandev.showcaseview.ShowcaseView;
import com.github.espiandev.showcaseview.ShowcaseViews;
import com.github.espiandev.showcaseview.sample.R;

import static com.github.espiandev.showcaseview.ShowcaseViews.ItemViewProperties;

public class MultipleActionItemsSampleActivity extends Activity implements ActionBar.OnNavigationListener {

    public static final int SHOWCASE_SPINNER_RADIUS = 94;
    public static final int SHOWCASE_OVERFLOW_ITEM_RADIUS = 65;
    ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Item1", "Item2", "Item3"}), this);
        mOptions.block = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        ShowcaseViews views = new ShowcaseViews(this, R.layout.showcase_view_template);
        views.addView(new ItemViewProperties(ItemViewProperties.ID_SPINNER, R.string.showcase_spinner_title, R.string.showcase_spinner_message, ShowcaseView.ITEM_SPINNER, SHOWCASE_SPINNER_RADIUS));
        views.addView(new ItemViewProperties(ItemViewProperties.ID_OVERFLOW, R.string.showcase_overflow_title, R.string.showcase_overflow_message, ShowcaseView.ITEM_ACTION_OVERFLOW, SHOWCASE_OVERFLOW_ITEM_RADIUS));
        views.show();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }
}
