package com.espian.showcaseview.sample;

import android.app.Activity;
import android.os.Bundle;
import com.espian.showcaseview.ShowcaseView;

public class SampleActivity extends Activity {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ShowcaseView sv = (ShowcaseView) findViewById(R.id.showcase);
		sv.setShotType(ShowcaseView.TYPE_ONE_SHOT);
		sv.setShowcaseView(findViewById(R.id.button));
	}
}
