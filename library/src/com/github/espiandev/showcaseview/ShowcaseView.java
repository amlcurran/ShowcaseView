package com.github.espiandev.showcaseview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.espiandev.showcaseview.anim.AnimationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.github.espiandev.showcaseview.anim.AnimationUtils.AnimationEndListener;
import static com.github.espiandev.showcaseview.anim.AnimationUtils.AnimationStartListener;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout implements
		View.OnClickListener, View.OnTouchListener {

	public static final String TAG = "ShowcaseView";

	public static final int TYPE_NO_LIMIT = 0;
	public static final int TYPE_ONE_SHOT = 1;

	public static final int INSERT_TO_DECOR = 0;
	public static final int INSERT_TO_VIEW = 1;

	public static final int ITEM_ACTION_HOME = 0;
	public static final int ITEM_TITLE = 1;
	public static final int ITEM_SPINNER = 2;
	public static final int ITEM_ACTION_ITEM = 3;
	public static final int ITEM_ACTION_OVERFLOW = 6;

	private static final int OVERLAY_TYPE_DEFAULT = -1;
	public static final int OVERLAY_TYPE_NONE = 0;
	public static final int OVERLAY_TYPE_HAND = 1;
	public static final int OVERLAY_TYPE_ARROW = 2;
	public static final int OVERLAY_TYPE_SHOWCASE = 3;
	public static final int TEXT_POSITION_DEFAULT = 0;
	public static final int TEXT_POSITION_HORZ = 1;
	public static final int TEXT_POSITION_VERT = 2;

	public static final int INNER_CIRCLE_RADIUS = 94;
	private static final String PREFS_SHOWCASE_INTERNAL = "showcase_internal";
	private static final String DEFAULT_SHOWCASE_COLOR = "#33B5E5";
	private static final int OK_BUTTON_HEIGHT = 12;

	private static final int DEVICE_DPI_WITH_TRANSPARENT_MENU = 320;
	private static final int DEVICE_HEIGHT_WITH_TRANSPARENT_MENU = 1202;
	private static final int DEVICE_WIDTH_WITH_TRANSPARENT_MENU = 720;

	private ArrayList<ShowcasePosition> showcases = new ArrayList<ShowcasePosition>();
	private static float metricScale = 1.0f;
	private boolean isRedundant = false;
	private boolean hasCustomClickListener = false;
	private ConfigOptions mOptions;

	private Paint mEraser;
	private TextPaint mPaintDetail, mPaintTitle;
	private View mHandy;
	private final Button mEndButton;
	private OnShowcaseEventListener mEventListener;
	private DynamicLayout mDynamicTitleLayout;
	private DynamicLayout mDynamicDetailLayout;
	private Object[] mBestTextPosition;
	private TextAppearanceSpan mDetailSpan, mTitleSpan;

	private Typeface titleTypeface;
	private Typeface detailTypeface;

	private final String buttonText;
	private float scaleMultiplier = 1f;

	private OnSetVisibilityListener mOnSetVisibilityListener;

	public interface OnSetVisibilityListener {
		public void onSetVisibility();
	}

	private int mShowcaseColor;

	protected ShowcaseView(Context context) {
		this(context, null, R.styleable.CustomTheme_showcaseViewStyle);
	}

	public ShowcaseView(Context context, AttributeSet attrs) {
		this(context, attrs, R.styleable.CustomTheme_showcaseViewStyle);
	}

	public ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// Get the attributes for the ShowcaseView
		final TypedArray styled = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle,
				R.style.ShowcaseView);
		mShowcaseColor = styled.getColor(
				R.styleable.ShowcaseView_sv_showcaseColor,
				Color.parseColor(DEFAULT_SHOWCASE_COLOR));

		int titleTextAppearance = styled.getResourceId(
				R.styleable.ShowcaseView_sv_titleTextAppearance,
				R.style.TextAppearance_ShowcaseView_Title);
		int detailTextAppearance = styled.getResourceId(
				R.styleable.ShowcaseView_sv_detailTextAppearance,
				R.style.TextAppearance_ShowcaseView_Detail);
		mTitleSpan = new TextAppearanceSpan(context, titleTextAppearance);
		mDetailSpan = new TextAppearanceSpan(context, detailTextAppearance);

		buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
		styled.recycle();

		metricScale = getContext().getResources().getDisplayMetrics().density;
		mEndButton = (Button) LayoutInflater.from(context).inflate(
				R.layout.showcase_button, null);

		ConfigOptions options = new ConfigOptions();
		options.showcaseId = getId();
		setConfigOptions(options);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void init() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		} else {
			setDrawingCacheEnabled(true);
		}

		// See if this showcase is eligible for display
		boolean hasShot = getContext().getSharedPreferences(
				PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE).getBoolean(
				"hasShot" + getConfigOptions().showcaseId, false);
		if (hasShot && mOptions.shotType == TYPE_ONE_SHOT) {
			// The showcase has already been shot once, so we don't need to do
			// anything
			setVisibility(View.GONE);
			isRedundant = true;
			return;
		}

		// Load the custom title font
		try {
			titleTypeface = Typeface.createFromAsset(getContext().getAssets(),
					mOptions.titleFontAssetName);
		} catch (Exception e) {
			Log.d(TAG, "TITLE FONT: default font");
		}

		// Load the custom detail font
		try {
			detailTypeface = Typeface.createFromAsset(getContext().getAssets(),
					mOptions.detailFontAssetName);
		} catch (Exception e) {
			Log.d(TAG, "DETAIL FONT: default font");
		}

		// Set a listener
		setOnTouchListener(this);

		// Create title painter
		mPaintTitle = new TextPaint();
		mPaintTitle.setAntiAlias(true);
		if (null != titleTypeface) {
			mPaintTitle.setTypeface(titleTypeface);
		}

		// Create detail painter
		mPaintDetail = new TextPaint();
		mPaintDetail.setAntiAlias(true);
		if (null != detailTypeface) {
			mPaintDetail.setTypeface(detailTypeface);
		}

		// Draw OK button
		if (!mOptions.noButton && mEndButton.getParent() == null) {
			RelativeLayout.LayoutParams lps = getConfigOptions().buttonLayoutParams;
			if (lps == null) {
				lps = (LayoutParams) generateDefaultLayoutParams();

				// Position the OK button on the screen
				if (mOptions.okButtonOnRight) {
					lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				} else {
					lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}
				
				int margin = ((Number) (metricScale * OK_BUTTON_HEIGHT)).intValue();

				// Fix for certain newer devices with transparent Android control buttons
				if (isDeviceWithTransparentToolbar(false, 0)) {
					margin += 50;
				}

				lps.setMargins(margin, margin, margin, margin);
			}
			mEndButton.setLayoutParams(lps);
			mEndButton.setText(buttonText != null ? buttonText : getResources()
					.getString(R.string.ok));
			if (!hasCustomClickListener)
				mEndButton.setOnClickListener(this);
			addView(mEndButton);
		}

	}

	public void setVisibilityListener(
			OnSetVisibilityListener onSetvisibilityListener) {
		mOnSetVisibilityListener = onSetvisibilityListener;
	}

	public void setShowcaseNoView() {
		setShowcasePosition(1000000, 1000000);
	}

	public void setVisibility(int visibility) {
		if (View.GONE != this.getVisibility()) {
			super.setVisibility(visibility);
			if (mOnSetVisibilityListener != null
					&& View.GONE == this.getVisibility())
				mOnSetVisibilityListener.onSetVisibility();
		}
	}

	/**
	 * Set the view to showcase
	 * 
	 * @param view
	 *            The {@link View} to showcase.
	 */
	public void setShowcaseView(final View view) {
		if (isRedundant || view == null) {
			isRedundant = true;
			return;
		}
		isRedundant = false;

		// Multiple showcase support
		ShowcasePosition showcase;
		if (null == showcases) {
			// Sanity checking
			showcases = new ArrayList<ShowcasePosition>();

		} else if (showcases.size() <= 0) {
			// Add a new showcase if there aren't any
			showcases.add(new ShowcasePosition());
		}

		// Add the view to the showcase
		showcase = showcases.get(showcases.size() - 1);
		showcase.view = view;

		view.post(new Runnable() {
			@Override
			public void run() {
				init();

				// Compute showcase positions for each showcase
				for (int ii = 0; ii < showcases.size(); ++ii) {
					ShowcasePosition thisShowcase = showcases.get(ii);
					if (mOptions.insert == INSERT_TO_VIEW) {
						thisShowcase.legacyShowcaseX = thisShowcase.showcaseX;
						thisShowcase.legacyShowcaseY = thisShowcase.showcaseY;
						thisShowcase.showcaseX = (float) (view.getLeft() + view.getWidth() / 2);
						thisShowcase.showcaseY = (float) (view.getTop() + view.getHeight() / 2);
					} else {
						int[] coordinates = new int[2];
						if (null != thisShowcase.view) {
							thisShowcase.view.getLocationInWindow(coordinates);
							thisShowcase.legacyShowcaseX = thisShowcase.showcaseX;
							thisShowcase.legacyShowcaseY = thisShowcase.showcaseY;
							thisShowcase.showcaseX = (float) (coordinates[0] + view.getWidth() / 2);
							thisShowcase.showcaseY = (float) (coordinates[1] + view.getHeight() / 2);
						}
					}
					
					// Fix for newer LG devices
					if (isDeviceWithTransparentToolbar(true, Configuration.ORIENTATION_PORTRAIT) 
							&& thisShowcase.legacyShowcaseY != -1 && thisShowcase.showcaseY < thisShowcase.legacyShowcaseY) {
						thisShowcase.showcaseY = thisShowcase.legacyShowcaseY;
					}
					if (isDeviceWithTransparentToolbar(true, Configuration.ORIENTATION_LANDSCAPE) 
							&& thisShowcase.legacyShowcaseX != -1 && thisShowcase.showcaseX < thisShowcase.legacyShowcaseX) {
						thisShowcase.showcaseX = thisShowcase.legacyShowcaseX;
					}

				}

				invalidate();
			}
		});
	}

	/**
	 * Set a specific position to showcase
	 * 
	 * @param x
	 *            X co-ordinate
	 * @param y
	 *            Y co-ordinate
	 */
	public void addShowcasePosition(float x, float y) {
		// Check for redundant coordinates
		for (ShowcasePosition showcase : showcases) {
			if (showcase.showcaseX == x && showcase.showcaseY == y) {
				return;
			}
		}

		ShowcasePosition thisPosition = new ShowcasePosition();
		thisPosition.showcaseX = x;
		thisPosition.showcaseY = y;
		showcases.add(thisPosition);

		init();
		invalidate();
	}

	/**
	 * Set a specific position to showcase. Clears out all other showcases for
	 * this view
	 * 
	 * @param x
	 *            X co-ordinate
	 * @param y
	 *            Y co-ordinate
	 */
	public void setShowcasePosition(float x, float y) {
		if (isRedundant) {
			return;
		}

		// Multiple showcase support
		ShowcasePosition showcase;
		if (null == showcases) {
			// Sanity checking
			showcases = new ArrayList<ShowcasePosition>();

		} else if (showcases.size() <= 0) {
			// Add a new showcase if there aren't any
			showcases.add(new ShowcasePosition());
		}

		// Get the last showcase in the list
		showcase = showcases.get(showcases.size() - 1);

		// Set the position
		showcase.legacyShowcaseX = showcase.showcaseX;
		showcase.legacyShowcaseY = showcase.showcaseY;
		showcase.showcaseX = x;
		showcase.showcaseY = y;

		init();
		invalidate();
	}

	/**
	 * Set the showcase position offset
	 * 
	 * @param x
	 *            X co-ordinate
	 * @param y
	 *            Y co-ordinate
	 */
	public void setShowcaseOffset(float x, float y) {
		if (isRedundant) {
			return;
		}

		// Multiple showcase support
		ShowcasePosition showcase;
		if (null == showcases) {
			// Sanity checking
			showcases = new ArrayList<ShowcasePosition>();

		} else if (showcases.size() <= 0) {
			// Add a new showcase if there aren't any
			showcases.add(new ShowcasePosition());
		}

		// Get the last showcase in the list
		showcase = showcases.get(showcases.size() - 1);
		showcase.showcaseXOffset = x;
		showcase.showcaseYOffset = y;

		init();
		invalidate();
	}

	public void setShowcaseItem(final int itemType, final int actionItemId,
			final Activity activity) {
		post(new Runnable() {
			@Override
			public void run() {
				View homeButton = activity.findViewById(android.R.id.home);
				if (homeButton == null) {
					// Thanks to @hameno for this
					int homeId = activity.getResources().getIdentifier(
							"abs__home", "id", activity.getPackageName());
					if (homeId != 0) {
						homeButton = activity.findViewById(homeId);
					}
				}
				if (homeButton == null)
					throw new RuntimeException(
							"insertShowcaseViewWithType cannot be used when the theme "
									+ "has no ActionBar");
				ViewParent p = homeButton.getParent().getParent(); // ActionBarView

				if (!p.getClass().getName().contains("ActionBarView")) {
					String previousP = p.getClass().getName();
					p = p.getParent();
					String throwP = p.getClass().getName();
					if (!p.getClass().getName().contains("ActionBarView"))
						throw new IllegalStateException(
								"Cannot find ActionBarView for "
										+ "Activity, instead found "
										+ previousP + " and " + throwP);
				}

				Class abv = p.getClass(); // ActionBarView class
				Class absAbv = abv.getSuperclass(); // AbsActionBarView class

				switch (itemType) {
				case ITEM_ACTION_HOME:
					setShowcaseView(homeButton);
					break;
				case ITEM_SPINNER:
					showcaseSpinner(p, abv);
					break;
				case ITEM_TITLE:
					showcaseTitle(p, abv);
					break;
				case ITEM_ACTION_ITEM:
				case ITEM_ACTION_OVERFLOW:
					showcaseActionItem(p, absAbv, itemType, actionItemId);
					break;
				default:
					Log.e("TAG", "Unknown item type");
				}
			}
		});

	}

	private void showcaseActionItem(ViewParent p, Class absAbv, int itemType,
			int actionItemId) {
		try {
			Field mAmpField = absAbv.getDeclaredField("mActionMenuPresenter");
			mAmpField.setAccessible(true);
			Object mAmp = mAmpField.get(p);
			if (itemType == ITEM_ACTION_OVERFLOW) {
				// Finds the overflow button associated with the
				// ActionMenuPresenter
				Field mObField = mAmp.getClass().getDeclaredField(
						"mOverflowButton");
				mObField.setAccessible(true);
				View mOb = (View) mObField.get(mAmp);
				if (mOb != null)
					setShowcaseView(mOb);
			} else {
				// Want an ActionItem, so find it
				Field mAmvField = mAmp.getClass().getSuperclass()
						.getDeclaredField("mMenuView");
				mAmvField.setAccessible(true);
				Object mAmv = mAmvField.get(mAmp);

				Field mChField;
				if (mAmv.getClass().toString()
						.contains("com.actionbarsherlock")) {
					// There are thousands of superclasses to traverse up
					// Have to get superclasses because mChildren is private
					mChField = mAmv.getClass().getSuperclass().getSuperclass()
							.getSuperclass().getSuperclass()
							.getDeclaredField("mChildren");
				} else
					mChField = mAmv.getClass().getSuperclass().getSuperclass()
							.getDeclaredField("mChildren");
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
			throw new RuntimeException(
					"insertShowcaseViewWithType() must be called "
							+ "after or during onCreateOptionsMenu() of the host Activity");
		}
	}

	private void showcaseSpinner(ViewParent p, Class abv) {
		try {
			Field mSpinnerField = abv.getDeclaredField("mSpinner");
			mSpinnerField.setAccessible(true);
			View mSpinnerView = (View) mSpinnerField.get(p);
			if (mSpinnerView != null) {
				setShowcaseView(mSpinnerView);
			}
		} catch (NoSuchFieldException e) {
			Log.e("TAG", "Failed to find actionbar spinner", e);
		} catch (IllegalAccessException e) {
			Log.e("TAG", "Failed to access actionbar spinner", e);

		}
	}

	private void showcaseTitle(ViewParent p, Class abv) {
		try {
			Field mTitleViewField = abv.getDeclaredField("mTitleView");
			mTitleViewField.setAccessible(true);
			View titleView = (View) mTitleViewField.get(p);
			if (titleView != null) {
				setShowcaseView(titleView);
			}
		} catch (NoSuchFieldException e) {
			Log.e("TAG", "Failed to find actionbar title", e);
		} catch (IllegalAccessException e) {
			Log.e("TAG", "Failed to access actionbar title", e);

		}
	}

	/**
	 * Set the shot method of the showcase - only once or no limit
	 * 
	 * @param shotType
	 *            either TYPE_ONE_SHOT or TYPE_NO_LIMIT
	 * @deprecated Use the option in {@link ConfigOptions} instead.
	 */
	@Deprecated
	public void setShotType(int shotType) {
		if (shotType == TYPE_NO_LIMIT || shotType == TYPE_ONE_SHOT) {
			mOptions.shotType = shotType;
		}
	}

	/**
	 * Decide whether touches outside the showcased circle should be ignored or
	 * not
	 * 
	 * @param block
	 *            true to block touches, false otherwise. By default, this is
	 *            true.
	 * @deprecated Use the option in {@link ConfigOptions} instead.
	 */
	@Deprecated
	public void blockNonShowcasedTouches(boolean block) {
		mOptions.block = block;
	}

	/**
	 * Override the standard button click event
	 * 
	 * @param listener
	 *            Listener to listen to on click events
	 */
	public void overrideButtonClick(OnClickListener listener) {
		if (isRedundant) {
			return;
		}
		if (mEndButton != null) {
			mEndButton.setOnClickListener(listener != null ? listener : this);
		}
		hasCustomClickListener = true;
	}

	public void setOnShowcaseEventListener(OnShowcaseEventListener listener) {
		mEventListener = listener;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		for (ShowcasePosition showcase : showcases) {
			if (showcase.getShowcaseX() < 0 || showcase.getShowcaseY() < 0 || isRedundant) {
				super.dispatchDraw(canvas);
				return;
			}
		}

		// Draw the semi-transparent background
		int[] attrs = { R.attr.sv_overlayBackgroundColor };
		TypedArray styled = getContext().obtainStyledAttributes(
				R.style.ShowcaseView, attrs);
		int overlayColor = styled.getColor(0, Color.BLACK);
		canvas.drawColor(overlayColor);
		styled.recycle();

		// Cycle through all the showcases on this screen
		for (ShowcasePosition showcase : showcases) {
			if (mOptions.insert == INSERT_TO_VIEW) {
				showcase.legacyShowcaseX = showcase.showcaseX;
				showcase.legacyShowcaseY = showcase.showcaseY;
				showcase.showcaseX = (float) (showcase.view.getLeft() + showcase.view.getWidth() / 2);
				showcase.showcaseY = (float) (showcase.view.getTop() + showcase.view.getHeight() / 2);
			} else {
				int[] coordinates = new int[2];
				if (null != showcase.view) {
					showcase.view.getLocationInWindow(coordinates);
					showcase.legacyShowcaseX = showcase.showcaseX;
					showcase.legacyShowcaseY = showcase.showcaseY;
					showcase.showcaseX = (float) (coordinates[0] + showcase.view
							.getWidth() / 2);
					showcase.showcaseY = (float) (coordinates[1] + showcase.view
							.getHeight() / 2);
					
					
					// Fix for newer LG devices
					if (isDeviceWithTransparentToolbar(true, Configuration.ORIENTATION_PORTRAIT) 
							&& showcase.legacyShowcaseY != -1 && showcase.showcaseY < showcase.legacyShowcaseY) {
						showcase.showcaseY = showcase.legacyShowcaseY;
					}
					if (isDeviceWithTransparentToolbar(true, Configuration.ORIENTATION_LANDSCAPE) 
							&& showcase.legacyShowcaseX != -1 && showcase.showcaseX < showcase.legacyShowcaseX) {
						showcase.showcaseX = showcase.legacyShowcaseX;
					}
				}
			}
		
			invalidate();

			// Draw to the scale specified
			Matrix mm = new Matrix();
			mm.postScale(scaleMultiplier, scaleMultiplier, showcase.getShowcaseX(), showcase.getShowcaseY());
			canvas.setMatrix(mm);

			if (mOptions.showcaseHighlightHasHardEdge) {
				// Create eraser
				PorterDuffXfermode mBlender = new PorterDuffXfermode(
						PorterDuff.Mode.MULTIPLY);
				mEraser = new Paint();
				mEraser.setColor(0xFFFFFF);
				mEraser.setAlpha(0);
				mEraser.setXfermode(mBlender);
				mEraser.setAntiAlias(true);

				canvas.drawCircle(showcase.getShowcaseX(), showcase.getShowcaseY(),
						showcase.getShowcaseRadius(), mEraser);

			} else {
				// Create glowy-maker (radial gradient)
				RadialGradient gradient = new android.graphics.RadialGradient(
						showcase.getShowcaseX(), showcase.getShowcaseY(),
						showcase.getShowcaseRadius(),
						new int[] { 0xFF888888, 0xFF888888, 0xFF888888,
								0xFF000000, 0x00000000 }, null,
						android.graphics.Shader.TileMode.CLAMP);

				// Draw transparent circle into tempBitmap
				Paint mGlowyater = new Paint();
				mGlowyater.setShader(gradient);
				mGlowyater.setColor(0xFF888888);
				mGlowyater.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));

				canvas.drawCircle(showcase.getShowcaseX(), showcase.getShowcaseY(),
						showcase.getShowcaseRadius(), mGlowyater);
			}

			// Draw overlay
			switch (mOptions.overlayType) {
			case OVERLAY_TYPE_ARROW:
				showcase.overlayArrowRotation = determineArrowOrientation(showcase, canvas.getWidth(), canvas.getHeight());
				showcase.overlay = getArrow(showcase.overlayArrowRotation);
				break;

			case OVERLAY_TYPE_HAND:
				showcase.overlay = getHandDrawable();
				break;

			case OVERLAY_TYPE_DEFAULT:
			case OVERLAY_TYPE_SHOWCASE:
			default:
				showcase.overlay = getContext().getResources().getDrawable(R.drawable.cling_bleached);
				showcase.overlay.setColorFilter(mShowcaseColor, PorterDuff.Mode.MULTIPLY);
				break;

			}

			showcase.voidedOverlayArea = makeVoidedRect(showcase);
			showcase.overlay.setBounds(showcase.voidedOverlayArea);
			showcase.overlay.draw(canvas);

			canvas.setMatrix(new Matrix());

			if (!TextUtils.isEmpty(showcase.mTitleText)
					|| !TextUtils.isEmpty(showcase.mSubText)) {

				if (showcase.mAlteredText) {
					if (TEXT_POSITION_HORZ == mOptions.textPositioning) {
						mBestTextPosition = getBestTextPositionHorz(showcase,
								canvas.getWidth(), canvas.getHeight());
					} else if (TEXT_POSITION_VERT == mOptions.textPositioning) {
						mBestTextPosition = getBestTextPositionVert(showcase,
								canvas.getWidth(), canvas.getHeight());
					} else {
						mBestTextPosition = getBestTextPosition(showcase,
								canvas.getWidth(), canvas.getHeight());
					}
				}

				if (!TextUtils.isEmpty(showcase.mTitleText)) {
					canvas.save();
					if (showcase.mAlteredText) {
						mDynamicTitleLayout = new DynamicLayout(
								showcase.mTitleText, mPaintTitle,
								((Float) mBestTextPosition[2]).intValue(),
								Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);
					}
				}

				if (!TextUtils.isEmpty(showcase.mSubText)) {
					canvas.save();

					int detailWidth = ((Float) mBestTextPosition[2]).intValue();

					// Fix for certain newer devices with transparent Android
					// control buttons
					if (isDeviceWithTransparentToolbar()) {
						detailWidth -= 200;
					}

					if (showcase.mAlteredText) {
						mDynamicDetailLayout = new DynamicLayout(
								showcase.mSubText, mPaintDetail, detailWidth,
								Layout.Alignment.ALIGN_NORMAL, 1.2f, 1.0f, true);
					}
				}

				// Zip up above OK button
				int vertOffset = 0;
				if (((Float) mBestTextPosition[1]).intValue()
						+ OK_BUTTON_HEIGHT >= mEndButton.getTop()
						- OK_BUTTON_HEIGHT) {
					vertOffset = mEndButton.getHeight();
				}

				if (!TextUtils.isEmpty(showcase.mTitleText)) {
					canvas.translate((Float) mBestTextPosition[0],
							(Float) mBestTextPosition[1] - vertOffset
									- OK_BUTTON_HEIGHT * metricScale);
					mDynamicTitleLayout.draw(canvas);
					canvas.restore();
				}

				if (!TextUtils.isEmpty(showcase.mSubText)) {
					canvas.translate((Float) mBestTextPosition[0],
							(Float) mBestTextPosition[1] - vertOffset
									+ OK_BUTTON_HEIGHT * metricScale);
					mDynamicDetailLayout.draw(canvas);
					canvas.restore();
				}
			}
		}

		super.dispatchDraw(canvas);

	}

	/**
	 * Indicates that this device is one of the newer devices with transparent
	 * Android control buttons. They throw off screen dimensions for some
	 * reason, and overlay on top of the OK button and description text.
	 * 
	 * @return
	 */
	private boolean isDeviceWithTransparentToolbar() {
		return isDeviceWithTransparentToolbar(true, Configuration.ORIENTATION_LANDSCAPE);
	}

	/**
	 * Indicates that this device is one of the newer devices with transparent
	 * Android control buttons. They throw off screen dimensions for some
	 * reason, and overlay on top of the OK button and description text.
	 * 
	 * @param considerScreenOrientation Consider the orientation of the device when making this true?
	 * @param orientation Configuration.ORIENTATION_LANDSCAPE or ORIENTATION_PORTRAIT
	 * @return
	 */
	public boolean isDeviceWithTransparentToolbar(boolean considerScreenOrientation, int orientation) {
		int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		int screenOrient = getResources().getConfiguration().orientation;
		
		Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		
		int width=0, height=0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
			width = size.x;
			height = size.y;
		}
		
		int screenDensity = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			screenDensity = getResources().getConfiguration().densityDpi;
		}

		if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
				&& screenDensity == DEVICE_DPI_WITH_TRANSPARENT_MENU
				&& ( (width == DEVICE_WIDTH_WITH_TRANSPARENT_MENU && height == DEVICE_HEIGHT_WITH_TRANSPARENT_MENU) 
						|| (width == DEVICE_HEIGHT_WITH_TRANSPARENT_MENU && height == DEVICE_WIDTH_WITH_TRANSPARENT_MENU) )
				&& (!considerScreenOrientation || screenOrient == orientation)) {
			return true;
		}

		return false;

	}

	/**
	 * Calculates the best place to position text
	 * 
	 * @param canvasW
	 *            width of the screen
	 * @param canvasH
	 *            height of the screen
	 * @return
	 */
	private Object[] getBestTextPosition(ShowcasePosition showcase,
			int canvasW, int canvasH) {

		// if the width isn't much bigger than the voided area, just consider
		// top & bottom
		float spaceTop = showcase.voidedOverlayArea.top;
		float spaceBottom = canvasH - showcase.voidedOverlayArea.bottom - 64
				* metricScale; // 64dip considers the OK button

		return new Object[] {
				Float.valueOf(mOptions.titleTextSize * metricScale),
				Float.valueOf(spaceTop > spaceBottom ? 128 * metricScale
						: mOptions.titleTextSize * metricScale
								+ showcase.voidedOverlayArea.bottom),
				Float.valueOf(canvasW - 48 * metricScale) };

	}

	/**
	 * Calculates where to position text horizontally
	 * 
	 * @param canvasW
	 *            width of the screen
	 * @param canvasH
	 *            height of the screen
	 * @return
	 */
	private Object[] getBestTextPositionHorz(ShowcasePosition showcase,
			int canvasW, int canvasH) {

		// if the width isn't much bigger than the voided area, just consider
		// top & bottom
		Rect voidedOverlay = showcase.voidedOverlayArea;

		// init
		Float textX = (float) voidedOverlay.left;
		Float textY = (float) voidedOverlay.top;
		Float textWidth = (canvasW - 48) * metricScale;
		Alignment textAlignment = Layout.Alignment.ALIGN_NORMAL;

		if ((voidedOverlay.right + voidedOverlay.left) / 2 <= canvasW / 2) {
			// Left
			textX = (float) showcase.voidedOverlayArea.right;
			textY = (float) ((voidedOverlay.bottom + voidedOverlay.top) / 2);
			textWidth = canvasW - textX;

		} else {
			// Right
			textX = mOptions.titleTextSize * metricScale;
			textY = (float) ((voidedOverlay.bottom + voidedOverlay.top) / 2);
			textWidth = (float) (voidedOverlay.left);
			textAlignment = Layout.Alignment.ALIGN_CENTER;
		}

		return new Object[] { textX, textY, textWidth, textAlignment };
	}

	/**
	 * Calculates where to position text vertically
	 * 
	 * @param canvasW
	 *            width of the screen
	 * @param canvasH
	 *            height of the screen
	 * @return
	 */
	private Object[] getBestTextPositionVert(ShowcasePosition showcase,
			int canvasW, int canvasH) {

		// if the width isn't much bigger than the voided area, just consider
		// top & bottom
		Rect voidedOverlay = showcase.voidedOverlayArea;

		// init
		Float textX = (float) voidedOverlay.left;
		Float textY = (float) voidedOverlay.top;
		Float textWidth = (canvasW - 48) * metricScale;
		Alignment textAlignment = Layout.Alignment.ALIGN_NORMAL;

		if ((voidedOverlay.top + voidedOverlay.bottom) / 2 <= canvasH / 2) {
			// Top
			textX = ((float) voidedOverlay.right - voidedOverlay.left) / 2;
			if (showcase.overlayArrowRotation >= 202.5 && showcase.overlayArrowRotation <= 337.5) {
				textY = (float) voidedOverlay.top - (voidedOverlay.height() / 3);
				textY = (textY >= (OK_BUTTON_HEIGHT * metricScale)) ? textY : (OK_BUTTON_HEIGHT * metricScale);
			} else {
				textY = (float) voidedOverlay.bottom;
			}
			textWidth = canvasW - textX;

		} else {
			// Bottom
			textX = ((float) voidedOverlay.right - voidedOverlay.left) / 2;
			if (showcase.overlayArrowRotation >= 202.5 && showcase.overlayArrowRotation <= 337.5) {
				textY = (float) voidedOverlay.top - (voidedOverlay.height() / 3);
				textY = (textY >= (OK_BUTTON_HEIGHT * metricScale)) ? textY : (OK_BUTTON_HEIGHT * metricScale);
			} else {
				textY = (float) (2 * voidedOverlay.top) - voidedOverlay.bottom;
			}
			textWidth = canvasW - textX;
		}

		return new Object[] { textX, textY, textWidth, textAlignment };
	}

	/**
	 * Creates a {@link Rect} which represents the area the showcase covers.
	 * Used to calculate where best to place the text
	 * 
	 * @return Rect of "voided" area of the overlay
	 */
	private Rect makeVoidedRect(ShowcasePosition showcase) {

		int cx = (int) showcase.getShowcaseX();
		int cy = (int) showcase.getShowcaseY();
		int dw, dh;

		Rect voidedArea = null;

		switch (mOptions.overlayType) {
		case OVERLAY_TYPE_ARROW:
			dw = showcase.overlay.getIntrinsicWidth();
			dh = showcase.overlay.getIntrinsicHeight();

			int halfRadius = (int) (showcase.getShowcaseRadius() / 2);

			if (showcase.overlayArrowRotation < 45
					|| showcase.overlayArrowRotation > 315) {
				// right
				//  ___________
				// |           |
				// |          _|_
				// |_________/_| \
				//          |     |
				//           \___/
				voidedArea = new Rect(cx - dw - halfRadius, cy - dh
						- halfRadius, cx - halfRadius, cy - halfRadius);

			} else if (showcase.overlayArrowRotation >= 45
					&& showcase.overlayArrowRotation <= 135) {
				// up
				//            ___
				//  _________/_  \
				// |        |  |  |
				// |         \_|_/
				// |           |
				// |___________|
				voidedArea = new Rect(cx - dw - halfRadius, cy + halfRadius, cx
						- halfRadius, cy + dh + halfRadius);

			} else if (showcase.overlayArrowRotation >= 225
					&& showcase.overlayArrowRotation <= 315) {
				// down
				//  _____
				// |     |
				// |     |
				// |     |
				// |     |
				// | ___ |
				// |/___\|
				// |     |
				//  \___/
				voidedArea = new Rect(cx - (dw / 2), cy - dh - halfRadius, cx
						+ (dw / 2), cy - halfRadius);

			} else {
				// left
				//     ___________
				//    |           |
				//   _|_          |
				//  / |_\_________|
				// |     |
				//  \___/
				voidedArea = new Rect(cx + halfRadius, cy - dh - halfRadius, cx
						+ dw + halfRadius, cy - halfRadius);
			}

			break;

		case OVERLAY_TYPE_HAND:
			//   ___
			//  /___\
			// |     |
			// |\___/|
			// |     |
			// |     |
			// |     |
			// |     |
			// |_____|
			dw = showcase.overlay.getIntrinsicWidth();
			dh = showcase.overlay.getIntrinsicHeight();
			voidedArea = new Rect(cx - (dw / 2), cy, cx + (dw / 2), cy + dh);
			break;

		default:
			//  _________
			// |   ___   |
			// |  /   \  |
			// | |     | |
			// |  \___/  |
			// |_________|
			dw = showcase.overlay.getIntrinsicWidth();
			dh = showcase.overlay.getIntrinsicHeight();
			voidedArea = new Rect(cx - dw / 2, cy - dh / 2, cx + dw / 2, cy
					+ dh / 2);
			break;
		}

		// Draw a box around the showcase overlay
		return voidedArea;

	}

	public void animateGesture(float offsetStartX, float offsetStartY,
			float offsetEndX, float offsetEndY) {
		mHandy = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
		addView(mHandy);
		moveHand(showcases.get(0), offsetStartX, offsetStartY, offsetEndX,
				offsetEndY, new AnimationEndListener() {
					@Override
					public void onAnimationEnd() {
						removeView(mHandy);
					}
				});
	}

	private void moveHand(ShowcasePosition showcase, float offsetStartX,
			float offsetStartY, float offsetEndX, float offsetEndY,
			AnimationEndListener listener) {
		AnimationUtils.createMovementAnimation(mHandy, showcase.getShowcaseX(),
				showcase.getShowcaseY(), offsetStartX, offsetStartY, offsetEndX,
				offsetEndY, listener).start();
	}

	private void moveOverlay(ShowcasePosition showcase, View overlay, float offsetStartX,
			float offsetStartY, float offsetEndX, float offsetEndY,
			AnimationEndListener listener) {
		AnimationUtils.createMovementAnimation(overlay, showcase.getShowcaseX(),
				showcase.getShowcaseY(), offsetStartX, offsetStartY, offsetEndX,
				offsetEndY, listener).start();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onClick(View view) {
		// If the type is set to one-shot, store that it has shot
		if (mOptions.shotType == TYPE_ONE_SHOT) {
			SharedPreferences internal = getContext().getSharedPreferences(
					PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				internal.edit()
						.putBoolean("hasShot" + getConfigOptions().showcaseId,
								true).apply();
			} else {
				internal.edit()
						.putBoolean("hasShot" + getConfigOptions().showcaseId,
								true).commit();
			}
		}
		hide();
	}

	public void hide() {
		if (mEventListener != null) {
			mEventListener.onShowcaseViewHide(this);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
				&& getConfigOptions().fadeOutDuration > 0) {
			fadeOutShowcase();
		} else {
			setVisibility(View.GONE);
		}
	}

	private void fadeOutShowcase() {
		AnimationUtils.createFadeOutAnimation(this,
				getConfigOptions().fadeOutDuration, new AnimationEndListener() {
					@Override
					public void onAnimationEnd() {
						setVisibility(View.GONE);
					}
				}).start();
	}

	public void show() {
		if (mEventListener != null) {
			mEventListener.onShowcaseViewShow(this);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
				&& getConfigOptions().fadeInDuration > 0) {
			fadeInShowcase();
		} else {
			setVisibility(View.VISIBLE);
		}
	}

	private void fadeInShowcase() {
		AnimationUtils.createFadeInAnimation(this,
				getConfigOptions().fadeInDuration,
				new AnimationStartListener() {
					@Override
					public void onAnimationStart() {
						setVisibility(View.VISIBLE);
					}
				}).start();
	}

	
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		return onTouchEvent(motionEvent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {

		double distanceFromFocus;

		int action = motionEvent.getAction();
		if (action != MotionEvent.ACTION_UP) {
			return true;
		}
		
		for (ShowcasePosition showcase : showcases) {
			float xDelta = Math.abs(motionEvent.getRawX() - showcase.getShowcaseX());
			float yDelta = Math.abs(motionEvent.getRawY() - showcase.getShowcaseY());
			distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2)
					+ Math.pow(yDelta, 2));

			if (mOptions.hideOnClickOutside
					&& distanceFromFocus > showcase.getShowcaseRadius()) {
				// A touch to the overlay screen is equivalent to pressing the OK button
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
					mEndButton.callOnClick();
				} else {
					mEndButton.performClick();
				}
				
				this.hide();
				return true;
			}

			if (mOptions.block
					&& distanceFromFocus > showcase.getShowcaseRadius()) {
				return true;
			}
		}

		return false;
	}

	public void setShowcaseIndicatorScale(float scaleMultiplier) {
		this.scaleMultiplier = scaleMultiplier;
	}

	public interface OnShowcaseEventListener {

		public void onShowcaseViewHide(ShowcaseView showcaseView);

		public void onShowcaseViewShow(ShowcaseView showcaseView);

	}

	public void setText(int titleTextResId, int subTextResId) {
		String titleText = getContext().getResources()
				.getString(titleTextResId);
		String subText = getContext().getResources().getString(subTextResId);
		setText(titleText, subText);
	}

	public void setText(String titleText, String subText) {
		// Multiple showcase support
		ShowcasePosition showcase;
		if (null == showcases) {
			// Sanity checking
			showcases = new ArrayList<ShowcasePosition>();

		} else if (showcases.size() <= 0) {
			// Add a new showcase if there aren't any
			showcases.add(new ShowcasePosition());
		}

		// Add the view to the showcase
		showcase = showcases.get(showcases.size() - 1);

		SpannableString ssbTitle = new SpannableString(titleText);
		ssbTitle.setSpan(mTitleSpan, 0, ssbTitle.length(), 0);
		showcase.mTitleText = ssbTitle;

		SpannableString ssbDetail = new SpannableString(subText);
		ssbDetail.setSpan(mDetailSpan, 0, ssbDetail.length(), 0);
		showcase.mSubText = ssbDetail;

		showcase.mAlteredText = true;
		invalidate();
	}

	/**
	 * Get the ghostly gesture hand for custom gestures
	 * 
	 * @return a View representing the ghostly hand
	 */
	public View getHand() {
		final View mHandy = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
		addView(mHandy);
		AnimationUtils.hide(mHandy);

		return mHandy;
	}

	public Drawable getHandDrawable() {
		return getContext().getResources().getDrawable(R.layout.handy);
	}

	/**
	 * Get the cartoony arrow for custom gestures
	 * 
	 * @param directionPolarCoordinate
	 *            direction the arrow should point (e.g. 0 = right, 90 = up, 180
	 *            = left, 270 = down)
	 * @return
	 */
	public Drawable getArrow(int directionPolarCoordinate) {
		// round and round and round
		directionPolarCoordinate %= 360;

		// Orient the arrow
		int arrowGlyph = R.drawable.arrow_left;
		if (directionPolarCoordinate < 45 || directionPolarCoordinate > 337) {
			arrowGlyph = R.drawable.arrow_right;
		} else if (directionPolarCoordinate >= 45
				&& directionPolarCoordinate <= 135) {
			arrowGlyph = R.drawable.arrow_right_up;
		} else if (directionPolarCoordinate >= 202
				&& directionPolarCoordinate <= 270) {
			arrowGlyph = R.drawable.arrow_down2;
		} else if (directionPolarCoordinate > 270
				&& directionPolarCoordinate <= 337) {
			arrowGlyph = R.drawable.arrow_down;
		}

		return getContext().getResources().getDrawable(arrowGlyph);
	}

	/**
	 * Pass in a showcase using an arrow, and this method will determine how the
	 * arrow should be oriented.
	 * 
	 * @param showcase
	 * @return int polar coordinate
	 */
	private int determineArrowOrientation(ShowcasePosition showcase,
			int canvasX, int canvasY) {
		int oriented = 0;

		if (TEXT_POSITION_HORZ == mOptions.textPositioning) {
			// horz
			oriented = (showcase.getShowcaseX() > (canvasX / 2)) ? 0 : 180;

		} else if (TEXT_POSITION_VERT == mOptions.textPositioning) {
			// vert
			oriented = (showcase.getShowcaseY() > (canvasY / 2)) ? 270 : 90;

		} else {
			// TEXT_POSITION_DEFAULT
			// any direction
			// http://www.engineeringtoolbox.com/converting-cartesian-polar-coordinates-d_1347.html
			float originAdjustmentX = canvasX / 2; // center of screen
			float originAdjustmentY = canvasY / 2; // center of screen

			// Adjust Android coordinates to Cartesian coordinates
			double x = showcase.getShowcaseX() - originAdjustmentX;
			double y = showcase.getShowcaseY() - originAdjustmentY;

			// Convert Cartesian coordinates to polar coordinate (angle in
			// degrees)
			double theta = Math.atan(y / x);

			oriented = (int) theta;
		}

		return oriented;
	}

	/**
	 * Point to a specific view
	 * 
	 * @param view
	 *            The {@link View} to Showcase
	 */
	public void pointTo(View view) {
		float x = AnimationUtils.getX(view) + view.getWidth() / 2;
		float y = AnimationUtils.getY(view) + view.getHeight() / 2;
		pointTo(x, y);
	}

	/**
	 * Point to a specific point on the screen
	 * 
	 * @param x
	 *            X-coordinate to point to
	 * @param y
	 *            Y-coordinate to point to
	 */
	public void pointTo(float x, float y) {
		final View mHandy = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
		AnimationUtils.createMovementAnimation(mHandy, x, y).start();
	}

	/**
	 * Set the showcase's radius. Default: ShowcaseView.INNER_CIRCLE_RADIUS
	 * 
	 * @param r
	 */
	public void setRadius(float r) {
		// Multiple showcase support
		ShowcasePosition showcase;
		if (null == showcases) {
			// Sanity checking
			showcases = new ArrayList<ShowcasePosition>();

		}

		if (showcases.size() <= 0) {
			// Add a new showcase if there aren't any
			showcases.add(new ShowcasePosition());
		}

		// Set the showcase radius
		showcase = showcases.get(showcases.size() - 1);
		showcase.setShowcaseRadius(r);
	}

	public void setConfigOptions(ConfigOptions options) {
		mOptions = options;
	}

	private ConfigOptions getConfigOptions() {
		// Make sure that this method never returns null
		if (mOptions == null)
			return mOptions = new ConfigOptions();
		return mOptions;
	}

	/**
	 * Quick method to insert a ShowcaseView into an Activity
	 * 
	 * @param viewToShowcase
	 *            View to showcase
	 * @param activity
	 *            Activity to insert into
	 * @param title
	 *            Text to show as a title. Can be null.
	 * @param detailText
	 *            More detailed text. Can be null.
	 * @param options
	 *            A set of options to customise the ShowcaseView
	 * @return the created ShowcaseView instance
	 */
	public static ShowcaseView insertShowcaseView(View viewToShowcase,
			Activity activity, String title, String detailText,
			ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content))
					.addView(sv);
		}
		sv.setShowcaseView(viewToShowcase);
		sv.setText(title, detailText);
		return sv;
	}

	/**
	 * Quick method to insert a ShowcaseView into an Activity
	 * 
	 * @param viewToShowcase
	 *            View to showcase
	 * @param activity
	 *            Activity to insert into
	 * @param title
	 *            Text to show as a title. Can be null.
	 * @param detailText
	 *            More detailed text. Can be null.
	 * @param options
	 *            A set of options to customise the ShowcaseView
	 * @return the created ShowcaseView instance
	 */
	public static ShowcaseView insertShowcaseView(View viewToShowcase,
			Activity activity, int title, int detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content))
					.addView(sv);
		}
		sv.setShowcaseView(viewToShowcase);
		sv.setText(title, detailText);
		return sv;
	}

	public static ShowcaseView insertShowcaseView(int showcaseViewId,
			Activity activity, String title, String detailText,
			ConfigOptions options) {
		View v = activity.findViewById(showcaseViewId);
		if (v != null) {
			return insertShowcaseView(v, activity, title, detailText, options);
		}
		return null;
	}

	public static ShowcaseView insertShowcaseView(int showcaseViewId,
			Activity activity, int title, int detailText, ConfigOptions options) {
		View v = activity.findViewById(showcaseViewId);
		if (v != null) {
			return insertShowcaseView(v, activity, title, detailText, options);
		}
		return null;
	}

	public static ShowcaseView insertShowcaseView(float x, float y,
			Activity activity, String title, String detailText,
			ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content))
					.addView(sv);
		}
		sv.setShowcasePosition(x, y);
		sv.setText(title, detailText);
		return sv;
	}

	public static ShowcaseView insertShowcaseView(float x, float y,
			Activity activity, int title, int detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content))
					.addView(sv);
		}
		sv.setShowcasePosition(x, y);
		sv.setText(title, detailText);
		return sv;
	}

	public static ShowcaseView insertShowcaseView(View showcase,
			Activity activity) {
		return insertShowcaseView(showcase, activity, null, null, null);
	}

	/**
	 * Quickly insert a ShowcaseView into an Activity, highlighting an item.
	 * 
	 * @param type
	 *            the type of item to showcase (can be ITEM_ACTION_HOME,
	 *            ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or
	 *            ITEM_ACTION_OVERFLOW)
	 * @param itemId
	 *            the ID of an Action item to showcase (only required for
	 *            ITEM_ACTION_ITEM
	 * @param activity
	 *            Activity to insert the ShowcaseView into
	 * @param title
	 *            Text to show as a title. Can be null.
	 * @param detailText
	 *            More detailed text. Can be null.
	 * @param options
	 *            A set of options to customise the ShowcaseView
	 * @return the created ShowcaseView instance
	 */
	public static ShowcaseView insertShowcaseViewWithType(int type, int itemId,
			Activity activity, String title, String detailText,
			ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content))
					.addView(sv);
		}
		sv.setShowcaseItem(type, itemId, activity);
		sv.setText(title, detailText);
		return sv;
	}

	/**
	 * Quickly insert a ShowcaseView into an Activity, highlighting an item.
	 * 
	 * @param type
	 *            the type of item to showcase (can be ITEM_ACTION_HOME,
	 *            ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or
	 *            ITEM_ACTION_OVERFLOW)
	 * @param itemId
	 *            the ID of an Action item to showcase (only required for
	 *            ITEM_ACTION_ITEM
	 * @param activity
	 *            Activity to insert the ShowcaseView into
	 * @param title
	 *            Text to show as a title. Can be null.
	 * @param detailText
	 *            More detailed text. Can be null.
	 * @param options
	 *            A set of options to customise the ShowcaseView
	 * @return the created ShowcaseView instance
	 */
	public static ShowcaseView insertShowcaseViewWithType(int type, int itemId,
			Activity activity, int title, int detailText, ConfigOptions options) {
		ShowcaseView sv = new ShowcaseView(activity);
		if (options != null)
			sv.setConfigOptions(options);
		if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
			((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
		} else {
			((ViewGroup) activity.findViewById(android.R.id.content))
					.addView(sv);
		}
		sv.setShowcaseItem(type, itemId, activity);
		sv.setText(title, detailText);
		return sv;
	}

	public static ShowcaseView insertShowcaseView(float x, float y,
			Activity activity) {
		return insertShowcaseView(x, y, activity, null, null, null);
	}

	/**
	 * Adds a new ShowcasePosition to this ShowcaseView.
	 * <ul>
	 * <li>If you are putting multiple show cases on the same screen (the same
	 * ShowcaseView) you need to run this between each ItemViewProperties
	 * object.</li>
	 * <li>Or you can put them all in an ArrayList and run ShowcaseViews.addView
	 * on that.</li>
	 * </ul>
	 * <p>
	 * All setting operations will now apply to this new ShowcasePosition.
	 * </p>
	 */
	public void addShowcase() {
		showcases.add(new ShowcasePosition());
	}

	/**
	 * Remove the last showcase added to this ShowcaseView object.
	 * 
	 * @return ShowcasePosition the last object to be added is popped off the
	 *         stack. <br/>
	 *         Returns null if the Showcase list is empty.
	 */
	public ShowcasePosition removeShowcase() {
		if (showcases.size() <= 0) {
			return null;
		}

		// Pop the last showcase off the end of the list
		ShowcasePosition thisShowcase = showcases.get(showcases.size() - 1);
		showcases.remove(thisShowcase);

		return thisShowcase;
	}

	public static class ConfigOptions {
		public boolean block = true, noButton = false;
		public boolean okButtonOnRight = true;
		/**
		 * If you want to use more than one Showcase with the
		 * {@link ConfigOptions#shotType} {@link ShowcaseView#TYPE_ONE_SHOT} in
		 * one Activity, set a unique value for every different Showcase you
		 * want to use.
		 */
		public int showcaseId = 0;

		/**
		 * If you want to use more than one Showcase with
		 * {@link ShowcaseView#TYPE_ONE_SHOT} in one Activity, set a unique
		 * {@link ConfigOptions#showcaseId} value for every different Showcase
		 * you want to use.
		 */
		public int shotType = TYPE_NO_LIMIT;
		public int insert = INSERT_TO_DECOR;
		public boolean hideOnClickOutside = false;
		/**
		 * This is the path/name of the TTF font asset you want to use with the
		 * Title text. The asset must be in the main project in
		 * /git/projectname/assets/... The path root is the assets directory.
		 */
		public String titleFontAssetName = null;
		/**
		 * This is the path/name of the TTF font asset you want to use with the
		 * Detail text. The asset must be in the main project in
		 * /git/projectname/assets/... The path root is the assets directory.
		 */
		public String detailFontAssetName = null;

		/**
		 * Default duration for fade in animation. Set to 0 to disable.
		 */
		public int fadeInDuration = AnimationUtils.DEFAULT_DURATION;

		/**
		 * Default duration for fade out animation. Set to 0 to disable.
		 */
		public int fadeOutDuration = AnimationUtils.DEFAULT_DURATION;
		/**
		 * Allow custom positioning of the button within the showcase view.
		 */
		public LayoutParams buttonLayoutParams = null;
		public int titleTextSize = 24;
		public int detailTextSize = 16;
		/**
		 * Type of graphic overlay to display. e.g. OVERLAY_TYPE_NONE,
		 * OVERLAY_TYPE_HAND, OVERLAY_TYPE_ARROW, OVERLAY_TYPE_SHOWCASE
		 */
		public int overlayType = OVERLAY_TYPE_DEFAULT;
		/**
		 * Type of text positioning to use in relation to showcases e.g.
		 * TEXT_POSITION_HORZ, TEXT_POSITION_DEFAULT
		 */
		public int textPositioning = TEXT_POSITION_DEFAULT;
		public boolean showcaseHighlightHasHardEdge = true;
	}

	/**
	 * Specifies position of a showcase.
	 */
	public static class ShowcasePosition {
		View view = null;
		private float showcaseX = -1;
		private float showcaseY = -1;
		public float showcaseXOffset = 0;
		public float showcaseYOffset = 0;
		public float legacyShowcaseX = -1;
		public float legacyShowcaseY = -1;
		public int innerCircleRadius = -1;
		Drawable overlay = null;
		Rect voidedOverlayArea;

		/**
		 * Polar coordinate of direction arrow should point. (e.g. 0 = right, 90
		 * = up, 180 = left, 270 = down)
		 */
		private float showcaseRadius = INNER_CIRCLE_RADIUS;
		private int overlayArrowRotation = 0;
		private CharSequence mTitleText, mSubText;
		private boolean mAlteredText = false;

		public ShowcasePosition() {
			voidedOverlayArea = new Rect();
		}

		public void setShowcaseRadius(float r) {
			this.showcaseRadius = r;
		}

		public float getShowcaseRadius() {
			return this.showcaseRadius * ShowcaseView.metricScale;
		}
		
		public float getShowcaseX() {
			return this.showcaseX + this.showcaseXOffset;
		}
		
		public void setShowcaseX(float sx) {
			this.showcaseX = sx;
		}

		public float getShowcaseY() {
			return this.showcaseY + this.showcaseYOffset;
		}
		
		public void setShowcaseY(float sy) {
			this.showcaseY = sy;
		}
}
}
