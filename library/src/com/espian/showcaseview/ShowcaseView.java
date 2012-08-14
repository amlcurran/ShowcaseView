package com.espian.showcaseview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.espian.showcaseview.sample.R;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout implements View.OnClickListener {

	public static final int TYPE_NO_LIMIT = 0;
	public static final int TYPE_ONE_SHOT = 1;

	private final String INTERNAL_PREFS = "showcase_internal";
	private final String SHOT_PREF_STORE = "hasShot";

	float showcaseX = -1, showcaseY = -1;
	int shotType = TYPE_NO_LIMIT;
	boolean isRedundant = false;

	Paint background;
	Drawable showcase;
	View mButton;
	OnClickListener mListener;

	public ShowcaseView(Context context) {
		super(context, null, 0);
	}

	public ShowcaseView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init() {
		boolean hasShot = getContext().getSharedPreferences(INTERNAL_PREFS, Context.MODE_PRIVATE)
				.getBoolean(SHOT_PREF_STORE, false);
		if (hasShot && shotType == TYPE_ONE_SHOT) {
			// The showcase has already been shot once, so we don't need to do anything
			setVisibility(View.GONE);
			isRedundant = true;
			return;
		}
		background = new Paint();
		background.setARGB(128, 80, 80, 80);
		showcase = getContext().getResources().getDrawable(R.drawable.cling);
		mButton = findViewById(R.id.showcase_button);
		if (mButton != null) mButton.setOnClickListener(this);
	}

	/**
	 * Set the view to showcase
	 *
	 * @param view The {@link View} to showcase.
	 */
	public void setShowcaseView(final View view) {
		if (isRedundant) return;

		view.post(new Runnable() {
			@Override
			public void run() {
				init();
				showcaseX = (float) (view.getLeft() + view.getWidth() / 2);
				showcaseY = (float) (view.getTop() + view.getHeight() / 2);
				invalidate();
			}
		});
	}

	/**
	 * Set the shot method of the showcase - only once or no limit
	 *
	 * @param shotType either TYPE_ONE_SHOT or TYPE_NO_LIMIT
	 */
	public void setShotType(int shotType) {
		if (shotType == TYPE_NO_LIMIT || shotType == TYPE_ONE_SHOT)
			this.shotType = shotType;
	}

	/**
	 * Override the standard button click event, if there is a button available
	 *
	 * @param listener Listener to listen to on click events
	 */
	public void overrideButtonClick(OnClickListener listener) {
		if (isRedundant) return;
		if (mButton != null) mButton.setOnClickListener(listener);
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		if (showcaseX < 0 || showcaseY < 0 || isRedundant) {
			super.dispatchDraw(canvas);
			return;
		}

		float dens = getResources().getDisplayMetrics().density;

		Bitmap b = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		//Draw the semi-transparent background
		c.drawColor(Color.argb(128, 80, 80, 80));

		//Erase the area for the ring
		Paint eraser = new Paint();
		eraser.setColor(0xFFFFFF);
		eraser.setAlpha(0);
		eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		c.drawCircle(showcaseX, showcaseY, dens * 94, eraser);

		int cx = (int) showcaseX, cy = (int) showcaseY;
		int dw = showcase.getIntrinsicWidth();
		int dh = showcase.getIntrinsicHeight();

		showcase.setBounds(cx - dw / 2, cy - dh / 2, cx + dw / 2, cy + dh / 2);
		showcase.draw(c);

		canvas.drawBitmap(b, 0, 0, null);
		c.setBitmap(null);
		b = null;

		super.dispatchDraw(canvas);

	}

	@Override
	public void onClick(View view) {
		// If the type is set to one-shot, store that it has shot
		if (shotType == TYPE_ONE_SHOT) {
			SharedPreferences internal = getContext().getSharedPreferences("showcase_internal", Context.MODE_PRIVATE);
			internal.edit().putBoolean("hasShot", true).commit();
		}

		if (mListener == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ObjectAnimator oa = ObjectAnimator.ofFloat(this, "alpha", 0f);
				oa.setDuration(300).addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {
					}

					@Override
					public void onAnimationEnd(Animator animator) {
						setVisibility(View.GONE);
					}

					@Override
					public void onAnimationCancel(Animator animator) {
					}

					@Override
					public void onAnimationRepeat(Animator animator) {
					}
				});
				oa.start();
			} else {
				setVisibility(View.GONE);
			}
		} else {
			mListener.onClick(view);
		}
	}
}
