package com.espian.showcaseview.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.sample.animations.AnimationSampleActivity;
import com.espian.showcaseview.sample.fragments.ShowcaseFragmentActivity;
import com.espian.showcaseview.sample.legacy.MultipleShowcaseSampleActivity;
import com.espian.showcaseview.sample.v14.ActionItemsSampleActivity;
import com.espian.showcaseview.sample.v14.MultipleActionItemsSampleActivity;
import com.espian.showcaseview.targets.ViewTarget;

public class SampleActivity extends Activity implements View.OnClickListener,
        OnShowcaseEventListener, AdapterView.OnItemClickListener {

    private static final float ALPHA_DIM_VALUE = 0.1f;

    ShowcaseView sv;
    Button buttonBlocked;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        HardcodedListAdapter adapter = new HardcodedListAdapter(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        dimView(listView);

        buttonBlocked = (Button) findViewById(R.id.buttonBlocked);
        buttonBlocked.setOnClickListener(this);

        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        co.hideOnClickOutside = true;

        // The following code will reposition the OK button to the left.
//        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
//        lps.setMargins(margin, margin, margin, margin);
//        co.buttonLayoutParams = lps;

        ViewTarget target = new ViewTarget(R.id.buttonBlocked, this);
        sv = ShowcaseView.insertShowcaseView(target, this, R.string.showcase_main_title, R.string.showcase_main_message, co);
        sv.setOnShowcaseEventListener(this);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dimView(View view) {
        if (isHoneycombOrAbove()) {
            view.setAlpha(ALPHA_DIM_VALUE);
        }
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        switch (viewId) {
            case R.id.buttonBlocked:
                sv.animateGesture(0, 0, 0, 400);
                break;
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        if (isHoneycombOrAbove()) {
            listView.setAlpha(1f);
        }
        buttonBlocked.setEnabled(false);
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        dimView(listView);
        buttonBlocked.setEnabled(true);
    }

    public static boolean isHoneycombOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {

            case 0:
                startActivity(new Intent(this, ActionItemsSampleActivity.class));
                break;

            case 1:
                startActivity(new Intent(this, MultipleActionItemsSampleActivity.class));
                break;

            case 2:
                startActivity(new Intent(this, MultipleShowcaseSampleActivity.class));
                break;

            case 3:
                startActivity(new Intent(this, ShowcaseFragmentActivity.class));
                break;

            case 4:
                startActivity(new Intent(this, AnimationSampleActivity.class));
                break;

            // Not currently used
            case 5:
                startActivity(new Intent(this, MemoryManagementTesting.class));
        }
    }

    private static class HardcodedListAdapter extends ArrayAdapter {

        private static final int[] TITLE_RES_IDS = new int[] {
                R.string.title_action_items, R.string.title_action_bar,
                R.string.title_multiple, R.string.title_fragments,
                R.string.title_animations //, R.string.title_memory
        };

        private static final int[] SUMMARY_RES_IDS = new int[] {
                R.string.sum_action_items, R.string.sum_action_bar,
                R.string.sum_multiple, R.string.sum_fragments,
                R.string.sum_animations //, R.string.sum_memory
        };

        public HardcodedListAdapter(Context context) {
            super(context, R.layout.item_next_thing);
        }

        @Override
        public int getCount() {
            return TITLE_RES_IDS.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_next_thing, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.textView)).setText(TITLE_RES_IDS[position]);
            ((TextView) convertView.findViewById(R.id.textView2)).setText(SUMMARY_RES_IDS[position]);
            return convertView;
        }
    }

}
