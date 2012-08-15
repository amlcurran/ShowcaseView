package com.espian.showcaseview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.espian.showcaseview.ShowcaseView;

public class SampleActivity extends Activity implements View.OnClickListener,
		ShowcaseView.OnShowcaseEventListener {

	ShowcaseView sv;
	Button button;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sv = (ShowcaseView) findViewById(R.id.showcase);
		sv.setShowcaseView(findViewById(R.id.button));
		sv.setOnShowcaseEventListener(this);
		(button = (Button) findViewById(R.id.button)).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (sv.isShown()) {
			sv.hide();
		} else {
			sv.show();
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
