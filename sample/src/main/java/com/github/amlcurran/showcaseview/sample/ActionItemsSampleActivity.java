package com.github.amlcurran.showcaseview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.github.amlcurran.showcaseview.ShowcaseView;

public class ActionItemsSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_items);
        Toolbar viewById = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(viewById);

        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ToolbarActionItemTarget(viewById, R.id.menu_item1))
                .setStyle(R.style.CustomShowcaseTheme2)
                .setContentText("Here's how to highlight items on a toolbar")
                .build()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
