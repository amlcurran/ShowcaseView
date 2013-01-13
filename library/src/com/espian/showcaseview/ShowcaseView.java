package com.espian.showcaseview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout implements View.OnClickListener,
		View.OnTouchListener {

	public static final int TYPE_NO_LIMIT = 0;
	public static final int TYPE_ONE_SHOT = 1;

	public static final int INSERT_TO_DECOR = 0;
	public static final int INSERT_TO_VIEW = 1;

    public static final int ITEM_ACTION_HOME = 0;
    public static final int ITEM_TITLE_OR_SPINNER = 1;
    public static final int ITEM_ACTION_ITEM = 2;
    public static final int ITEM_ACTION_OVERFLOW = 6;

	private final String INTERNAL_PREFS = "showcase_internal";
	private final String SHOT_PREF_STORE = "hasShot";

	private float showcaseX = -1, showcaseY = -1, showcaseRadius = -1, metricScale = 1.0f;
	private boolean isRedundant = false;

	private ConfigOptions mOptions;
	private Paint mPaintTitle, mEraser;
	private TextPaint mPaintSub;
	private final int backColor;
	private Drawable showcase;
	private View mButton, mHandy;
	private final Button mBackupButton;
	private OnShowcaseEventListener mEventListener;
	private PorterDuffXfermode mBlender;
	private Rect voidedArea;
	private String mTitleText, mSubText;
	private Context mContext;

	public ShowcaseView(Context context) {
		this(context, null, 0);
		this.mContext = context;
	}

	public ShowcaseView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		if (attrs != null) {
			TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.ShowcaseView, defStyle, 0);
			backColor = styled.getInt(R.styleable.ShowcaseView_backgroundColor, Color.argb(128, 80, 80, 80));
			styled.recycle();
		} else {
			backColor = Color.parseColor("#3333B5E5");
		}
		metricScale = getContext().getResources().getDisplayMetrics().density;
		mBackupButton = (Button) LayoutInflater.from(context).inflate(R.layout.showcase_button, null);
		setConfigOptions(new ConfigOptions());
	}

	private void init() {
		boolean hasShot = getContext().getSharedPreferences(INTERNAL_PREFS, Context.MODE_PRIVATE)
				.getBoolean(SHOT_PREF_STORE, false);
		if (hasShot && mOptions.shotType == TYPE_ONE_SHOT) {
			// The showcase has already been shot once, so we don't need to do anything
			setVisibility(View.GONE);
			isRedundant = true;
			return;
		}
		showcase = getContext().getResources().getDrawable(R.drawable.cling);
		mButton = findViewById(R.id.showcase_button);
		if (mButton != null) {
			mButton.setOnClickListener(this);
		}
		showcaseRadius = metricScale * 94;
		mBlender = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
		setOnTouchListener(this);

		mPaintTitle = new Paint();
		mPaintTitle.setColor(Color.parseColor("#49C0EC"));
		mPaintTitle.setShadowLayer(2.0f, 0f, 2.0f, Color.BLACK);
		mPaintTitle.setTextSize(24 * metricScale);

		mPaintSub = new TextPaint();
		mPaintSub.setColor(Color.WHITE);
		mPaintSub.setShadowLayer(2.0f, 0f, 2.0f, Color.BLACK);
		mPaintSub.setTextSize(16 * metricScale);

		mEraser = new Paint();
		mEraser.setColor(0xFFFFFF);
		mEraser.setAlpha(0);
		mEraser.setXfermode(mBlender);

		if (mButton == null && !mOptions.noButton) {
			RelativeLayout.LayoutParams lps = (LayoutParams) generateDefaultLayoutParams();
			lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			int margin = ((Number) (metricScale * 12)).intValue();
			lps.setMargins(margin, margin, margin, margin);
			lps.height = LayoutParams.WRAP_CONTENT;
			lps.width = LayoutParams.WRAP_CONTENT;
			mBackupButton.setLayoutParams(lps);
			mBackupButton.setText("OK");
			mBackupButton.setOnClickListener(this);
			addView(mBackupButton);
		}

        mHandy = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
        addView(mHandy);
        mHandy.setAlpha(0f);

	}

	/**
	 * Set the view to showcase
	 *
	 * @param view The {@link View} to showcase.
	 */
	public void setShowcaseView(final View view) {
		if (isRedundant || view == null) {
			isRedundant = true;
			return;
		}
		isRedundant = false;

		view.post(new Runnable() {
			@Override
			public void run() {
				init();
				if (mOptions.insert == INSERT_TO_VIEW) {
					showcaseX = (float) (view.getLeft() + view.getWidth() / 2);
					showcaseY = (float) (view.getTop() + view.getHeight() / 2);
				} else {
					int[] coordinates = new int[2];
					view.getLocationInWindow(coordinates);
					showcaseX = (float) (coordinates[0] + view.getWidth() / 2);
					showcaseY = (float) (coordinates[1] + view.getHeight() / 2);
				}
				invalidate();
			}
		});
	}

	/**
	 * Set a specific position to showcase
	 *
	 * @param x X co-ordinate
	 * @param y Y co-ordinate
	 */
	public void setShowcasePosition(float x, float y) {
		if (isRedundant) {
			return;
		}
		showcaseX = x;
		showcaseY = y;
		init();
		invalidate();
	}

    public void setShowcaseItem(final int itemType, final int actionItemId, final Activity activity) {
        post(new Runnable() {
            @Override
            public void run() {
                View homeButton = activity.findViewById(android.R.id.home);
                if (homeButton == null)
                    throw new RuntimeException("insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar");
                ViewParent p = homeButton.getParent().getParent(); //ActionBarView

                Class abv = p.getClass(); //ActionBarView class
                Class absAbv = abv.getSuperclass(); //AbsActionBarView class

                switch (itemType) {

                    case ITEM_ACTION_HOME:
                        setShowcaseView(homeButton);
                        break;

                    case ITEM_TITLE_OR_SPINNER:
                            try {
                                Field mTitleViewField = abv.getDeclaredField("mTitleView");
                                mTitleViewField.setAccessible(true);
                                View titleView = (View) mTitleViewField.get(p);
                                if (titleView != null) {
                                    setShowcaseView(titleView);
                                    break;
                                }
                                Field mSpinnerField = abv.getDeclaredField("mSpinner");
                                mSpinnerField.setAccessible(true);
                                View mSpinnerView = (View) mSpinnerField.get(p);
                                if (mSpinnerView != null) {
                                    setShowcaseView(mSpinnerView);
                                    break;
                                }
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        break;

                    case ITEM_ACTION_ITEM:
                    case ITEM_ACTION_OVERFLOW:
                        try {
                            Field mAmpField = absAbv.getDeclaredField("mActionMenuPresenter");
                            mAmpField.setAccessible(true);
                            Object mAmp = mAmpField.get(p);
                            if (itemType == ITEM_ACTION_OVERFLOW) {
                                // Finds the overflow button associated with the ActionMenuPresenter
                                Field mObField = mAmp.getClass().getDeclaredField("mOverflowButton");
                                mObField.setAccessible(true);
                                View mOb = (View) mObField.get(mAmp);
                                if (mOb != null)
                                    setShowcaseView(mOb);
                            } else {
                                // Want an ActionItem, so find it
                                Field mAmvField = mAmp.getClass().getSuperclass().getDeclaredField("mMenuView");
                                mAmvField.setAccessible(true);
                                Object mAmv = mAmvField.get(mAmp);

                                Field mChField = mAmv.getClass().getSuperclass().getSuperclass().getDeclaredField("mChildren");
                                mChField.setAccessible(true);
                                Object[] mChs = (Object[]) mChField.get(mAmv);
                                for (Object mCh : mChs) {
                                    if (mCh != null) {
                                        View v = (View) mCh;
                                        if (v.getId() == actionItemId)
                                            setShowcaseView(v);
                                    }
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            throw new RuntimeException("insertShowcaseViewWithType() must be called " +
                                    "after or during onCreateOptionsMenu() of the host Activity");
                        }

                }
            }
        });

    }

	/**
	 * Set the shot method of the showcase - only once or no limit
	 *
	 * @param shotType either TYPE_ONE_SHOT or TYPE_NO_LIMIT
	 */
	@Deprecated
	public void setShotType(int shotType) {
		if (shotType == TYPE_NO_LIMIT || shotType == TYPE_ONE_SHOT) {
			mOptions.shotType = shotType;
		}
	}

	/**
	 * Decide whether touches outside the showcased circle should be ignored or not
	 *
	 * @param block true to block touches, false otherwise. By default, this is true.
	 */
	@Deprecated
	public void blockNonShowcasedTouches(boolean block) {
		mOptions.block = block;
	}

	/**
	 * Override the standard button click event, if there is a button available
	 *
	 * @param listener Listener to listen to on click events
	 */
	public void overrideButtonClick(OnClickListener listener) {
		if (isRedundant) {
			return;
		}
		if (mButton != null) {
			mButton.setOnClickListener(listener);
		}
	}

	public void setOnShowcaseEventListener(OnShowcaseEventListener listener) {
		mEventListener = listener;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		if (showcaseX < 0 || showcaseY < 0 || isRedundant) {
			super.dispatchDraw(canvas);
			return;
		}

		Bitmap b = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		//Draw the semi-transparent background
		c.drawColor(backColor);

		//Erase the area for the ring
		c.drawCircle(showcaseX, showcaseY, showcaseRadius, mEraser);

		makeVoidedRect();

		showcase.setBounds(voidedArea);
		showcase.draw(c);

		canvas.drawBitmap(b, 0, 0, null);

		if (!TextUtils.isEmpty(mTitleText) && !TextUtils.isEmpty(mSubText)) {
			float[] textPos = getBestTextPosition(canvas.getWidth(), canvas.getHeight());
			if (!TextUtils.isEmpty(mTitleText))
				canvas.drawText(mTitleText, textPos[0], textPos[1], mPaintTitle);
			if (!TextUtils.isEmpty(mSubText)) {
				canvas.save();
				DynamicLayout slTitle = new DynamicLayout(mSubText, mPaintSub, ((Number) textPos[2]).intValue(), Layout.Alignment.ALIGN_NORMAL,
						1.2f, 1.0f, true);
				canvas.translate(textPos[0], textPos[1] + 12 * metricScale);
				slTitle.draw(canvas);
				canvas.restore();
			}
		}

		c.setBitmap(null);
		b.recycle();

		super.dispatchDraw(canvas);

	}

	private float[] getBestTextPosition(int canvasW, int canvasH) {

		//if the width isn't much bigger than the voided area, just consider top & bottom
		float spaceTop = voidedArea.top;
		float spaceBottom = canvasH - voidedArea.bottom - 64 * metricScale; //64dip considers the OK button
		//float spaceLeft = voidedArea.left;
		//float spaceRight = canvasW - voidedArea.right;

		//TODO currently only considers above or below showcase, deal with left or right
		return new float[]{24 * metricScale, spaceTop > spaceBottom ? 128 * metricScale : 24 * metricScale + voidedArea.bottom, canvasW - 48 * metricScale};

	}

	private void makeVoidedRect() {

		int cx = (int) showcaseX, cy = (int) showcaseY;
		int dw = showcase.getIntrinsicWidth();
		int dh = showcase.getIntrinsicHeight();

		voidedArea = new Rect(cx - dw / 2, cy - dh / 2, cx + dw / 2, cy + dh / 2);

	}

    public AnimatorSet animateGesture(float offsetStartX, float offsetStartY, float offsetEndX, float offsetEndY) {

        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(mHandy, "alpha", 0f, 1f).setDuration(500);

        ObjectAnimator setUpX = ObjectAnimator.ofFloat(mHandy, "x", showcaseX + offsetStartX).setDuration(0);
        ObjectAnimator setUpY = ObjectAnimator.ofFloat(mHandy, "y", showcaseY + offsetStartY).setDuration(0);

        ObjectAnimator moveX = ObjectAnimator.ofFloat(mHandy, "x", showcaseX + offsetEndX).setDuration(1000);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(mHandy, "y", showcaseY + offsetEndY).setDuration(1000);
        moveX.setStartDelay(1000);
        moveY.setStartDelay(1000);

        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(mHandy, "alpha", 0f).setDuration(500);
        alphaOut.setStartDelay(2500);

        AnimatorSet as = new AnimatorSet();
        as.play(setUpX).with(setUpY).before(alphaIn).before(moveX).with(moveY).before(alphaOut);
        return as;

    }

	@Override
	public void onClick(View view) {
		// If the type is set to one-shot, store that it has shot
		if (mOptions.shotType == TYPE_ONE_SHOT) {
			SharedPreferences internal = getContext().getSharedPreferences("showcase_internal", Context.MODE_PRIVATE);
			internal.edit().putBoolean("hasShot", true).commit();
		}
        hide();
	}

	public void hide() {
		if (mEventListener != null) {
			mEventListener.onShowcaseViewHide(this);
		}
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
	}

	public void show() {
		if (mEventListener != null) {
			mEventListener.onShowcaseViewShow(this);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ObjectAnimator oa = ObjectAnimator.ofFloat(this, "alpha", 1f);
			oa.setDuration(300).addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {
					setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationEnd(Animator animator) {
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
			setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (!mOptions.block) {
			float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
			float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
			double distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));
			if (mOptions.hideOnClickOutside && !(distanceFromFocus > showcaseRadius)) {
			    this.hide();
			}
			return false;
		} else {
			float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
			float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
			double distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));
			if (mOptions.hideOnClickOutside && !(distanceFromFocus > showcaseRadius)) {
			    this.hide();
			}
			return distanceFromFocus > showcaseRadius;
		}
	}

	public interface OnShowcaseEventListener {

		public void onShowcaseViewHide(ShowcaseView showcaseView);

		public void onShowcaseViewShow(ShowcaseView showcaseView);

	}

	public void setText(String titleText, String subText) {

        //TODO allow dynamic text changing
		mTitleText = titleText;
		mSubText = subText;

	}
	
	public void setText(int titleText, int subText) {

        //TODO allow dynamic text changing
        	mTitleText = mContext.getResources().getString(titleText);
        	mSubText = mContext.getResources().getString(subText);

    	}

    /**
     * Get the ghostly gesture hand for custom gestures
     * @return a View respresenting the ghostly hand
     */
    public View getHand() {
        return mHandy;
    }

    /**
     * Point to a specific view
     * @param view
     */
    public void pointTo(View view) {

        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(mHandy, "alpha", 0f, 1f).setDuration(500);

        ObjectAnimator setUpX = ObjectAnimator.ofFloat(mHandy, "x", view.getX() + view.getWidth() / 2).setDuration(0);
        ObjectAnimator setUpY = ObjectAnimator.ofFloat(mHandy, "y", view.getY() + view.getHeight() / 2).setDuration(0);

        AnimatorSet as = new AnimatorSet();
        as.play(setUpX).with(setUpY).before(alphaIn);
        as.start();
    }

    /**
     * Point to a specific point on the screen
     * @param x
     * @param y
     */
    public void pointTo(float x, float y) {

        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(mHandy, "alpha", 0f, 1f).setDuration(500);

        ObjectAnimator setUpX = ObjectAnimator.ofFloat(mHandy, "x", x).setDuration(0);
        ObjectAnimator setUpY = ObjectAnimator.ofFloat(mHandy, "y", y).setDuration(0);

        AnimatorSet as = new AnimatorSet();
        as.play(setUpX).with(setUpY).before(alphaIn);
        as.start();

    }

	public void setConfigOptions(ConfigOptions options) {
		mOptions = options;
	}

	public ConfigOptions getConfigOptions() {
		return mOptions;
	}

	/**
	 * Quick method to insert a ShowcaseView into an Activity
	 *
	 * @param viewToShowcase View to showcase
	 * @param activity       Activity to insert into
	 * @param title          Text to show as a title. Can be null.
	 * @param detailText     More detailed text. Can be null.
	 * @param options        A set of options to customise the ShowcaseView
	 * @return the created ShowcaseView instance
	 */
	public static ShowcaseView insertShowcaseView(View viewToShowcase, Activity activity, String title,
	                                              String detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity, null, 0);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
		}
		sv.setShowcaseView(viewToShowcase);
		sv.setText(title, detailText);
		return sv;
	}
	
	/**
	 * Quick method to insert a ShowcaseView into an Activity
	 *
	 * @param viewToShowcase View to showcase
	 * @param activity       Activity to insert into
	 * @param title          Text to show as a title. Can be null.
	 * @param detailText     More detailed text. Can be null.
	 * @param options        A set of options to customise the ShowcaseView
	 * @return the created ShowcaseView instance
	 */
	public static ShowcaseView insertShowcaseView(View viewToShowcase, Activity activity, int title,
	                                              int detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity, null, 0);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
		}
		sv.setShowcaseView(viewToShowcase);
		sv.setText(title, detailText);
		return sv;
	}

	public static ShowcaseView insertShowcaseView(int showcaseViewId, Activity activity, String title,
	                                              String detailText, ConfigOptions options) {
		View v = activity.findViewById(showcaseViewId);
		if (v != null) return insertShowcaseView(v, activity, title, detailText, options);
		return null;
	}
	
	public static ShowcaseView insertShowcaseView(int showcaseViewId, Activity activity, int title,
	                                              int detailText, ConfigOptions options) {
		View v = activity.findViewById(showcaseViewId);
		if (v != null) return insertShowcaseView(v, activity, title, detailText, options);
		return null;
	}

	public static ShowcaseView insertShowcaseView(float x, float y, Activity activity, String title,
	                                              String detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity, null, 0);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
		}
		sv.setShowcasePosition(x, y);
		sv.setText(title, detailText);
		return sv;
	}
	
	public static ShowcaseView insertShowcaseView(float x, float y, Activity activity, int title,
	                                              int detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity, null, 0);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
		}
		sv.setShowcasePosition(x, y);
		sv.setText(title, detailText);
		return sv;
	}

	public static ShowcaseView insertShowcaseView(View showcase, Activity activity) {
		return insertShowcaseView(showcase, activity, null, null, null);
	}

    /**
     * Quickly insert a ShowcaseView into an Activity, highlighting an item.
     * @param type          the type of item to showcase (can be ITEM_ACTION_HOME, ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or ITEM_ACTION_OVERFLOW)
     * @param itemId        the ID of an Action item to showcase (only required for ITEM_ACTION_ITEM
     * @param activity      Activity to insert the ShowcaseView into
     * @param title         Text to show as a title. Can be null.
     * @param detailText    More detailed text. Can be null.
     * @param options       A set of options to customise the ShowcaseView
     * @return the created ShowcaseView instance
     */
    public static ShowcaseView insertShowcaseViewWithType(int type, int itemId, Activity activity, String title, String detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity, null, 0);
        if (options != null)
            sv.setConfigOptions(options);
        if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
        } else {
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
        }
        sv.setShowcaseItem(type, itemId, activity);
        sv.setText(title, detailText);
        return sv;
    }
    
    /**
     * Quickly insert a ShowcaseView into an Activity, highlighting an item.
     * @param type          the type of item to showcase (can be ITEM_ACTION_HOME, ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or ITEM_ACTION_OVERFLOW)
     * @param itemId        the ID of an Action item to showcase (only required for ITEM_ACTION_ITEM
     * @param activity      Activity to insert the ShowcaseView into
     * @param title         Text to show as a title. Can be null.
     * @param detailText    More detailed text. Can be null.
     * @param options       A set of options to customise the ShowcaseView
     * @return the created ShowcaseView instance
     */
    public static ShowcaseView insertShowcaseViewWithType(int type, int itemId, Activity activity, int title, int detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity, null, 0);
        if (options != null)
            sv.setConfigOptions(options);
        if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
        } else {
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
        }
        sv.setShowcaseItem(type, itemId, activity);
        sv.setText(title, detailText);
        return sv;
    }

	public static ShowcaseView insertShowcaseView(float x, float y, Activity activity) {
		return insertShowcaseView(x, y, activity, null, null, null);
	}

	public static class ConfigOptions {
		public boolean block = true, noButton = false;
		public int shotType = TYPE_NO_LIMIT;
		public int insert = INSERT_TO_DECOR;
		public boolean hideOnClickOutside = false;
	}

}
