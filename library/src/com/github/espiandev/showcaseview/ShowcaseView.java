package com.github.espiandev.showcaseview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
public class ShowcaseView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener {

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

    private static final String PREFS_SHOWCASE_INTERNAL = "showcase_internal";
    public static final int INNER_CIRCLE_RADIUS = 94;

    private ArrayList<ShowcasePosition> showcases = new ArrayList<ShowcasePosition>();
    private float metricScale = 1.0f;
    private boolean isRedundant = false;
    private boolean hasCustomClickListener = false;
    private ConfigOptions mOptions;
    private Paint  mEraser;
    private TextPaint mPaintDetail, mPaintTitle;
    private int backColor;
    private Drawable showcaseGlowOverlay;
    private View mHandy;
    private final Button mEndButton;
    private OnShowcaseEventListener mEventListener;
    private Rect voidedArea;
    private CharSequence mTitleText, mSubText;
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;
    private float[] mBestTextPosition;
    private boolean mAlteredText = false;
    private TextAppearanceSpan mDetailSpan, mTitleSpan;

    private Typeface titleTypeface;
    private Typeface detailTypeface;

    private final String buttonText;
    private float scaleMultiplier = 1f;

    private OnSetVisibilityListener mOnSetVisibilityListener;
    
    private View currentView;
    
    public interface OnSetVisibilityListener {
        public void onSetVisibility();
      }

    private Bitmap mBleachedCling;
    private int mShowcaseColor;

    
    protected ShowcaseView(Context context) {
        this(context, null, R.styleable.CustomTheme_showcaseViewStyle);
    }

    protected ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get the attributes for the ShowcaseView
        final TypedArray styled = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle, R.style.ShowcaseView);
        mOptions.backColor = styled.getInt(R.styleable.ShowcaseView_sv_backgroundColor, mOptions.backColor);
        mOptions.detailTextColor = styled.getColor(R.styleable.ShowcaseView_sv_detailTextColor, mOptions.detailTextColor);
        mOptions.titleTextColor = styled.getColor(R.styleable.ShowcaseView_sv_titleTextColor, mOptions.titleTextColor);

        int titleTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_titleTextAppearance, R.style.TextAppearance_ShowcaseView_Title);
        int detailTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_detailTextAppearance, R.style.TextAppearance_ShowcaseView_Detail);
        mTitleSpan = new TextAppearanceSpan(context, titleTextAppearance);
        mDetailSpan = new TextAppearanceSpan(context, detailTextAppearance);

        buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
        styled.recycle();

        metricScale = getContext().getResources().getDisplayMetrics().density;
        mEndButton = (Button) LayoutInflater.from(context).inflate(R.layout.showcase_button, null);

        ConfigOptions options = new ConfigOptions();
        options.showcaseId = getId();
        setConfigOptions(options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void init() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE,null);
        }
        else {
            setDrawingCacheEnabled(true);
        }

        
        // See if this showcase is eligible for display
        boolean hasShot = getContext().getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
                .getBoolean("hasShot" + getConfigOptions().showcaseId, false);
        if (hasShot && mOptions.shotType == TYPE_ONE_SHOT) {
            // The showcase has already been shot once, so we don't need to do anything
    		setVisibility(View.GONE);
            isRedundant = true;
            return;
        }

        showcaseGlowOverlay = getContext().getResources().getDrawable(R.drawable.cling);
		showcaseGlowOverlay.setColorFilter(mShowcaseColor, PorterDuff.Mode.MULTIPLY);
        
        
        // Load the custom title font
        try { titleTypeface = Typeface.createFromAsset(getContext().getAssets(), mOptions.titleFontAssetName); } 
        catch (Exception e) { Log.d(TAG, "TITLE FONT: default font"); }
        
        // Load the custom detail font
        try { detailTypeface = Typeface.createFromAsset(getContext().getAssets(), mOptions.detailFontAssetName); } 
        catch (Exception e) { Log.d(TAG, "DETAIL FONT: default font"); }
        

        // Compute each showcase radius
        for (ShowcasePosition showcase : showcases) {
	        if (-1 == showcase.showcaseRadius) {
	        	showcase.showcaseRadius = metricScale * INNER_CIRCLE_RADIUS;
	        }
        }
        
        // Set a listener
        setOnTouchListener(this);

        // Create title painter
        mPaintTitle = new TextPaint();
        mPaintTitle.setColor(mOptions.titleTextColor);
        mPaintTitle.setShadowLayer(2.0f, 0f, 2.0f, mOptions.shadowColor);
        mPaintTitle.setTextSize(mOptions.titleTextSize * metricScale);
        mPaintTitle.setAntiAlias(true);
        if (null != titleTypeface) {
        	mPaintTitle.setTypeface(titleTypeface);
        } 
        
        // Create detail painter
        mPaintDetail = new TextPaint();
        mPaintDetail.setColor(mOptions.detailTextColor);
        mPaintDetail.setShadowLayer(2.0f, 0f, 2.0f, mOptions.shadowColor);
        mPaintDetail.setTextSize(mOptions.detailTextSize * metricScale);
        mPaintDetail.setAntiAlias(true);
        if (null != detailTypeface) {
        	mPaintDetail.setTypeface(detailTypeface);
        } 

        // Create eraser
        PorterDuffXfermode mBlender = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFF);
        mEraser.setAlpha(0);
        mEraser.setXfermode(mBlender);
        mEraser.setAntiAlias(true);
        
        
        // Draw OK button
        if (!mOptions.noButton && mEndButton.getParent() == null) {
            RelativeLayout.LayoutParams lps = getConfigOptions().buttonLayoutParams;
            if (lps == null) {
                lps = (LayoutParams) generateDefaultLayoutParams();
                lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                int margin = ((Number) (metricScale * 12)).intValue();
                lps.setMargins(margin, margin, margin, margin);
            }
            mEndButton.setLayoutParams(lps);
            mEndButton.setText(buttonText != null ? buttonText : getResources().getString(R.string.ok));
            if (!hasCustomClickListener) mEndButton.setOnClickListener(this);
            addView(mEndButton);
        }

    }
    
    public void setVisibilityListener(OnSetVisibilityListener onSetvisibilityListener) {
    	mOnSetVisibilityListener = onSetvisibilityListener;
    }
    
    public void setVisibility (int visibility) {
    	if (View.GONE != this.getVisibility()) {
	    	super.setVisibility(visibility);
	    	if (mOnSetVisibilityListener != null && View.GONE == this.getVisibility())
	    		mOnSetVisibilityListener.onSetVisibility();
    	}
    }

    public void setShowcaseNoView() {
        setShowcasePosition(1000000, 1000000);
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
        currentView = view;

        // If there is more than one showcase, overwrite
        if (showcases.size() > 1) {
        	showcases.clear();
        	showcases.add(new ShowcasePosition());
        }
        
        view.post(new Runnable() {
            @Override
            public void run() {
                init();
                
                // Compute showcase positions for each showcase
                for (int ii = 0; ii < showcases.size(); ++ii) {
	                if (mOptions.insert == INSERT_TO_VIEW) {
	                    showcases.get(ii).showcaseX = (float) (view.getLeft() + view.getWidth() / 2);
	                    showcases.get(ii).showcaseY = (float) (view.getTop() + view.getHeight() / 2);
	                } else {
	                    int[] coordinates = new int[2];
	                    view.getLocationInWindow(coordinates);
	                    showcases.get(ii).showcaseX = (float) (coordinates[0] + view.getWidth() / 2);
	                    showcases.get(ii).showcaseY = (float) (coordinates[1] + view.getHeight() / 2);
	                }
	                showcases.get(ii).showcaseX += showcases.get(ii).showcaseXOffset;
	                showcases.get(ii).showcaseY += showcases.get(ii).showcaseYOffset;
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
     * Set a specific position to showcase. Clears out all other showcases for this view
     *
     * @param x X co-ordinate
     * @param y Y co-ordinate
     */
    public void setShowcasePosition(float x, float y) {
        if (isRedundant) {
            return;
        }

        // Clear out all the other showcases
        showcases.clear();
        
        // Add this showcase in
    	ShowcasePosition thisPosition = new ShowcasePosition();
    	thisPosition.showcaseX = x;
    	thisPosition.showcaseY = y;
    	showcases.add(thisPosition);
        
        init();
        invalidate();
    }
    
    /**
     * Set the showcase position offset //TODO: Roll into an object representing the ShowcasePosition coordinates
     *
     * @param x X co-ordinate
     * @param y Y co-ordinate
     */
    public void setShowcaseOffset(float x, float y) {
    	if (isRedundant) {
    		return;
    	}
    	showcaseXOffset = x;
    	showcaseYOffset = y;
    	init();
    	invalidate();
    }

    public void setShowcaseItem(final int itemType, final int actionItemId, final Activity activity) {
        post(new Runnable() {
            @Override
            public void run() {
                View homeButton = activity.findViewById(android.R.id.home);
                if (homeButton == null) {
                    // Thanks to @hameno for this
                    int homeId = activity.getResources().getIdentifier("abs__home", "id", activity.getPackageName());
                    if (homeId != 0) {
                        homeButton = activity.findViewById(homeId);
                    }
                }
                if (homeButton == null)
                    throw new RuntimeException("insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar");
                ViewParent p = homeButton.getParent().getParent(); //ActionBarView

                if (!p.getClass().getName().contains("ActionBarView")) {
                    String previousP = p.getClass().getName();
                    p = p.getParent();
                    String throwP = p.getClass().getName();
                    if (!p.getClass().getName().contains("ActionBarView"))
                        throw new IllegalStateException("Cannot find ActionBarView for " +
                                "Activity, instead found " + previousP + " and " + throwP);
                }

                Class abv = p.getClass(); //ActionBarView class
                Class absAbv = abv.getSuperclass(); //AbsActionBarView class

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

    private void showcaseActionItem(ViewParent p, Class absAbv, int itemType, int actionItemId) {
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

                Field mChField;
                if (mAmv.getClass().toString().contains("com.actionbarsherlock")) {
                    // There are thousands of superclasses to traverse up
                    // Have to get superclasses because mChildren is private
                    mChField = mAmv.getClass().getSuperclass().getSuperclass()
                            .getSuperclass().getSuperclass().getDeclaredField("mChildren");
                } else
                    mChField = mAmv.getClass().getSuperclass().getSuperclass().getDeclaredField("mChildren");
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
     * @param shotType either TYPE_ONE_SHOT or TYPE_NO_LIMIT
     * @deprecated Use the option in {@link ConfigOptions} instead.
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
     * @deprecated Use the option in {@link ConfigOptions} instead.
     */
    @Deprecated
    public void blockNonShowcasedTouches(boolean block) {
        mOptions.block = block;
    }

    /**
     * Override the standard button click event
     *
     * @param listener Listener to listen to on click events
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
    	if (showcaseX < 0 || showcaseY < 0 || isRedundant) {
            super.dispatchDraw(canvas);
            return;
        }

        
        if (mOptions.insert == INSERT_TO_VIEW) {
            showcaseX = (float) (currentView.getLeft() + currentView.getWidth() / 2);
            showcaseY = (float) (currentView.getTop() + currentView.getHeight() / 2);
        } else {
            int[] coordinates = new int[2];
            currentView.getLocationInWindow(coordinates);
            showcaseX = (float) (coordinates[0] + currentView.getWidth() / 2);
            showcaseY = (float) (coordinates[1] + currentView.getHeight() / 2);
        }
        showcaseX += showcaseXOffset;
        showcaseY += showcaseYOffset;
        invalidate();
        

        //Draw the semi-transparent background
        canvas.drawColor(mOptions.backColor);

        //Draw to the scale specified
        Matrix mm = new Matrix();
        mm.postScale(scaleMultiplier, scaleMultiplier, showcaseX, showcaseY);
        canvas.setMatrix(mm);

        //Erase the area for the ring
        canvas.drawCircle(showcaseX, showcaseY, showcaseRadius, mEraser);

        makeVoidedRect();
        boolean recalculateText = true;
        mAlteredText = false;

        showcaseGlowOverlay.setBounds(voidedArea);
        showcaseGlowOverlay.draw(canvas);

        canvas.setMatrix(new Matrix());


        if (!TextUtils.isEmpty(mTitleText) || !TextUtils.isEmpty(mSubText)) {
            if (recalculateText)
                mBestTextPosition = getBestTextPosition(canvas.getWidth(), canvas.getHeight());

            if (!TextUtils.isEmpty(mTitleText)) {
                canvas.save();
                if (recalculateText) {
                mDynamicTitleLayout = new DynamicLayout(mTitleText, mPaintTitle,
                        (int) mBestTextPosition[2], Layout.Alignment.ALIGN_NORMAL,
                        1.0f, 1.0f, true);
                }
                canvas.translate(mBestTextPosition[0], mBestTextPosition[1] - 24 * metricScale);
                mDynamicTitleLayout.draw(canvas);
                canvas.restore();
            }

            if (!TextUtils.isEmpty(mSubText)) {
                canvas.save();
                if (recalculateText) {
                    mDynamicDetailLayout = new DynamicLayout(mSubText, mPaintDetail,
                            ((Number) mBestTextPosition[2]).intValue(), Layout.Alignment.ALIGN_NORMAL,
                            1.2f, 1.0f, true);
                }
                canvas.translate(mBestTextPosition[0], mBestTextPosition[1] + 12 * metricScale);
                mDynamicDetailLayout.draw(canvas);
                canvas.restore();

            }
        }

        super.dispatchDraw(canvas);

    }

    /**
     * Calculates the best place to position text
     *
     * @param canvasW width of the screen
     * @param canvasH height of the screen
     * @return
     */
    private float[] getBestTextPosition(int canvasW, int canvasH) {

        //if the width isn't much bigger than the voided area, just consider top & bottom
        float spaceTop = voidedArea.top;
        float spaceBottom = canvasH - voidedArea.bottom - 64 * metricScale; //64dip considers the OK button
        //float spaceLeft = voidedArea.left;
        //float spaceRight = canvasW - voidedArea.right;

        //TODO: currently only considers above or below showcase, deal with left or right
        return new float[]{	mOptions.titleTextSize * metricScale, 
        					spaceTop > spaceBottom ? 128 * metricScale : mOptions.titleTextSize * metricScale + voidedArea.bottom, 
        					canvasW - 48 * metricScale};

    }

    /**
     * Creates a {@link Rect} which represents the area the showcase covers. Used to calculate
     * where best to place the text
     *
     * @return true if voidedArea has changed, false otherwise.
     */
    private boolean makeVoidedRect() {

        // This if statement saves resources by not recalculating voidedArea
        // if the X & Y coordinates haven't changed
//        if (voidedArea == null || (showcaseX != legacyShowcaseX || showcaseY != legacyShowcaseY)) {

            int cx = (int) showcaseX, cy = (int) showcaseY; 
            int dw = showcaseGlowOverlay.getIntrinsicWidth();
            int dh = showcaseGlowOverlay.getIntrinsicHeight();

            voidedArea = new Rect(cx - dw / 2, cy - dh / 2, cx + dw / 2, cy + dh / 2);
            
            // TODO Subtract area of OK button so it doesn't overlap it
            
            legacyShowcaseX = showcaseX;
            legacyShowcaseY = showcaseY;

            return true;

//        }
//        return false;

    }

    public void animateGesture(float offsetStartX, float offsetStartY, float offsetEndX, float offsetEndY) {
        mHandy = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
        addView(mHandy);
        moveHand(offsetStartX, offsetStartY, offsetEndX, offsetEndY, new AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                removeView(mHandy);
            }
        });
    }

    private void moveHand(float offsetStartX, float offsetStartY, float offsetEndX, float offsetEndY, AnimationEndListener listener) {
        AnimationUtils.createMovementAnimation(mHandy, showcaseX, showcaseY,
                offsetStartX, offsetStartY,
                offsetEndX, offsetEndY,
                listener).start();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
    public void onClick(View view) {
        // If the type is set to one-shot, store that it has shot
        if (mOptions.shotType == TYPE_ONE_SHOT) {
            SharedPreferences internal = getContext().getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                internal.edit().putBoolean("hasShot" + getConfigOptions().showcaseId, true).apply();
            } else {
                internal.edit().putBoolean("hasShot" + getConfigOptions().showcaseId, true).commit();
            }
        }
        hide();
    }

    public void hide() {
        if (mEventListener != null) {
            mEventListener.onShowcaseViewHide(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getConfigOptions().fadeOutDuration > 0) {
            fadeOutShowcase();
        } else {
            setVisibility(View.GONE);
        }
    }

    private void fadeOutShowcase() {
        AnimationUtils.createFadeOutAnimation(this, getConfigOptions().fadeOutDuration, new AnimationEndListener() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getConfigOptions().fadeInDuration > 0) {
            fadeInShowcase();
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    private void fadeInShowcase() {
        AnimationUtils.createFadeInAnimation(this, getConfigOptions().fadeInDuration, new AnimationStartListener() {
            @Override
            public void onAnimationStart() {
                setVisibility(View.VISIBLE);
            }
        }).start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
        float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
        double distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));

        if (mOptions.hideOnClickOutside && distanceFromFocus > showcaseRadius) {
            this.hide();
            return true;
        }

        return mOptions.block && distanceFromFocus > showcaseRadius;
    }

    public void setShowcaseIndicatorScale(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    public interface OnShowcaseEventListener {

        public void onShowcaseViewHide(ShowcaseView showcaseView);

        public void onShowcaseViewShow(ShowcaseView showcaseView);

    }

    public ShowcaseView setTextColors(int titleTextColor, int detailTextColor) {
        mOptions.titleTextColor = titleTextColor;
        mOptions.detailTextColor = detailTextColor;
        if (mPaintTitle != null) {
            mPaintTitle.setColor(mOptions.titleTextColor);
        }
        if (mPaintDetail != null) {
            mPaintDetail.setColor(mOptions.detailTextColor);
        }
        invalidate();
        return this;
    }

    public void setText(int titleTextResId, int subTextResId) {
        String titleText = getContext().getResources().getString(titleTextResId);
        String subText = getContext().getResources().getString(subTextResId);
        setText(titleText, subText);
    }

    public void setText(String titleText, String subText) {
        SpannableString ssbTitle = new SpannableString(titleText);
        ssbTitle.setSpan(mTitleSpan, 0, ssbTitle.length(), 0);
        mTitleText = ssbTitle;
        SpannableString ssbDetail = new SpannableString(subText);
        ssbDetail.setSpan(mDetailSpan, 0, ssbDetail.length(), 0);
        mSubText = ssbDetail;
        mAlteredText = true;
        invalidate();
    }

    /**
     * Get the ghostly gesture hand for custom gestures
     *
     * @return a View representing the ghostly hand
     */
    public View getHand() {
        final View mHandy = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
        addView(mHandy);
        AnimationUtils.hide(mHandy);

        return mHandy;
    }

    /**
     * Get the cartoony arrow for custom gestures
     * @param directionPolarCoordinate direction the arrow should point (e.g. 0 = right, 90 = up, 180 = left, 270 = down)
     * @return
     */
    public View getArrow(int directionPolarCoordinate) {
    	// round and round and round
    	directionPolarCoordinate %= 360;

    	// Orient the arrow
    	int arrowGlyph = R.layout.arrow_left;
    	if (directionPolarCoordinate < 45 || directionPolarCoordinate > 315) {
    		arrowGlyph = R.layout.arrow_right;
    	} else if (directionPolarCoordinate >= 45 && directionPolarCoordinate <= 135) {
    		arrowGlyph = R.layout.arrow_up;
    	} else if (directionPolarCoordinate >= 225 && directionPolarCoordinate <= 315) {
    		arrowGlyph = R.layout.arrow_down;
    	} 
    	
        final View mArrowy = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(arrowGlyph, null);
        addView(mArrowy);
        AnimationUtils.hide(mArrowy);

        return mArrowy;
    }

    /**
     * Point to a specific view
     *
     * @param view The {@link View} to Showcase
     */
    public void pointTo(View view) {
        float x = AnimationUtils.getX(view) + view.getWidth() / 2;
        float y = AnimationUtils.getY(view) + view.getHeight() / 2;
        pointTo(x, y);
    }

    /**
     * Point to a specific point on the screen
     *
     * @param x X-coordinate to point to
     * @param y Y-coordinate to point to
     */
    public void pointTo(float x, float y) {
		final View mHandy = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
        AnimationUtils.createMovementAnimation(mHandy, x, y).start();
    }

    public void setConfigOptions(ConfigOptions options) {
        mOptions = options;
    }

    private ConfigOptions getConfigOptions() {
        // Make sure that this method never returns null
        if (mOptions == null) return mOptions = new ConfigOptions();
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
        ShowcaseView sv = new ShowcaseView(activity);
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
        ShowcaseView sv = new ShowcaseView(activity);
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
        if (v != null) {
            return insertShowcaseView(v, activity, title, detailText, options);
        }
        return null;
    }

    public static ShowcaseView insertShowcaseView(int showcaseViewId, Activity activity, int title,
                                                  int detailText, ConfigOptions options) {
        View v = activity.findViewById(showcaseViewId);
        if (v != null) {
            return insertShowcaseView(v, activity, title, detailText, options);
        }
        return null;
    }

    public static ShowcaseView insertShowcaseView(float x, float y, Activity activity, String title,
                                                  String detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
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
        ShowcaseView sv = new ShowcaseView(activity);
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
     *
     * @param type       the type of item to showcase (can be ITEM_ACTION_HOME, ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or ITEM_ACTION_OVERFLOW)
     * @param itemId     the ID of an Action item to showcase (only required for ITEM_ACTION_ITEM
     * @param activity   Activity to insert the ShowcaseView into
     * @param title      Text to show as a title. Can be null.
     * @param detailText More detailed text. Can be null.
     * @param options    A set of options to customise the ShowcaseView
     * @return the created ShowcaseView instance
     */
    public static ShowcaseView insertShowcaseViewWithType(int type, int itemId, Activity activity, String title, String detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
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
     *
     * @param type       the type of item to showcase (can be ITEM_ACTION_HOME, ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or ITEM_ACTION_OVERFLOW)
     * @param itemId     the ID of an Action item to showcase (only required for ITEM_ACTION_ITEM
     * @param activity   Activity to insert the ShowcaseView into
     * @param title      Text to show as a title. Can be null.
     * @param detailText More detailed text. Can be null.
     * @param options    A set of options to customise the ShowcaseView
     * @return the created ShowcaseView instance
     */
    public static ShowcaseView insertShowcaseViewWithType(int type, int itemId, Activity activity, int title, int detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
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
        public int insert = INSERT_TO_DECOR;
        public boolean hideOnClickOutside = false;
        /**
         * This is the path/name of the TTF font asset you want to use with the Title text.
         * The asset must be in the main project in /git/projectname/assets/...
         * The path root is the assets directory.
         */
        public String titleFontAssetName = null;
        /**
         * This is the path/name of the TTF font asset you want to use with the Detail text.
         * The asset must be in the main project in /git/projectname/assets/...
         * The path root is the assets directory.
         */
        public String detailFontAssetName = null;

        /**
         * If you want to use more than one Showcase with the {@link ConfigOptions#shotType} {@link ShowcaseView#TYPE_ONE_SHOT} in one Activity, set a unique value for every different Showcase you want to use.
         */
        public int showcaseId = 0;

        /**
         * If you want to use more than one Showcase with {@link ShowcaseView#TYPE_ONE_SHOT} in one Activity, set a unique {@link ConfigOptions#showcaseId} value for every different Showcase you want to use.
         */
        public int shotType = TYPE_NO_LIMIT;
        
        /**
         * Default duration for fade in animation. Set to 0 to disable.
         */
        public int fadeInDuration = AnimationUtils.DEFAULT_DURATION;

        /**
         * Default duration for fade out animation. Set to 0 to disable.
         */
        public int fadeOutDuration = AnimationUtils.DEFAULT_DURATION;
        public int shadowColor = Color.DKGRAY;
        public int backColor = Color.argb(128, 80, 80, 80);
        public int detailTextColor = Color.WHITE;
        public int titleTextColor = Color.parseColor("#49C0EC");
        public int titleTextSize = 24;
        public int detailTextSize = 16;
        /** Allow custom positioning of the button within the showcase view.
         */
        public LayoutParams buttonLayoutParams = null;
    }

    /**
     * Specifies position of a showcase.
     * @author masyukun@gmail.com
     */
    public static class ShowcasePosition {
    	View view = null;
    	public float showcaseX = -1;
        public float showcaseY = -1;
        public float showcaseXOffset = 0;
        public float showcaseYOffset = 0;
        public float showcaseRadius = -1;
        public float legacyShowcaseX = -1;
        public float legacyShowcaseY = -1;
        public int innerCircleRadius = -1;
    }

}
