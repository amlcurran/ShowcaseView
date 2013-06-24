package com.github.espiandev.showcaseview;


import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;
import com.github.espiandev.showcaseview.ShowcaseView.OnShowcaseEventListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public abstract class BaseTutorialActivity extends Activity implements
		OnShowcaseEventListener {

	private final static String PASSED_POSITION = "passedPos";
	private final static String PASSED_ID = "passedId";
	private final static String PASSED_RADIUS = "passedRadius";
	private final static String PASSED_TITLE = "passedTitle";
	private final static String PASSED_DESC = "passedDesc";
	private final static String PASSED_TITLE_COLOR = "passedTitleColor";
	private final static String PASSED_DESC_COLOR = "passedDescColor";
	public static final String RESULT_DATA = "tutResult";
	public static final String REDO = "redoTut";
	public static final String OK = "okTut";

	protected ShowcaseView mShowcaseView;
	private Button skip;
 
	public static void newIstance(Activity ctx, int[] position,int radius,Integer titleColor,Integer descColor,
			String title, String description, int requestCode, Class<? extends BaseTutorialActivity> act) {
		Intent caller = new Intent(ctx,act);
		caller.putExtra(PASSED_POSITION, position);
		caller.putExtra(PASSED_TITLE, title);
		caller.putExtra(PASSED_DESC, description);
		caller.putExtra(PASSED_RADIUS, radius);
		if(titleColor!=null)
			caller.putExtra(PASSED_TITLE_COLOR, titleColor);
		if(descColor!=null)
			caller.putExtra(PASSED_DESC_COLOR, descColor);
		ctx.startActivityForResult(caller, requestCode)	;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setResult(RESULT_CANCELED);
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_activity);

		mShowcaseView = (ShowcaseView) findViewById(R.id.tut_sv);
		skip = (Button) findViewById(R.id.skip_tutorial_btn);
		
		setParams(getIntent());

		mShowcaseView.show();
		
		mShowcaseView.setOnShowcaseEventListener(this);
	}

	private void setParams(Intent intent) {
		ConfigOptions co = new ConfigOptions();
		
		co.buttonText = getString(R.string.next_tut);
		
		if(intent.getExtras().getInt(PASSED_DESC_COLOR,Integer.MIN_VALUE)!=Integer.MIN_VALUE)
			co.detailTextColor = intent.getExtras().getInt(PASSED_DESC_COLOR);
		
		if(intent.getExtras().getInt(PASSED_TITLE_COLOR,Integer.MIN_VALUE)!=Integer.MIN_VALUE)
			co.titleTextColor = intent.getExtras().getInt(PASSED_TITLE_COLOR);
		
		co.circleRadius = intent.getExtras().getInt(PASSED_RADIUS)/2;
		
		
		if (mShowcaseView != null) {
			
			
			int[] position = intent.getExtras().getIntArray(PASSED_POSITION);
			if(position!=null)
				mShowcaseView.setShowcasePosition(position[0]+co.circleRadius, position[1]);
			
			String title = intent.getExtras().getString(PASSED_TITLE);
			String desc = intent.getExtras().getString(PASSED_DESC);
			
			mShowcaseView.setText(title, desc);
		}
		mShowcaseView.setConfigOptions(co);
	}
	
	public abstract void skipTutorial(View v);

	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(RESULT_DATA, OK);
		this.setResult(RESULT_OK,returnIntent);
		this.finish();
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {
		
	}

}
