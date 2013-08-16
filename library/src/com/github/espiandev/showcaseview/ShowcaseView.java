package com.github.espiandev.showcaseview;

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
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.espiandev.showcaseview.anim.AnimationUtils;

import java.lang.reflect.Field;

import static com.github.espiandev.showcaseview.anim.AnimationUtils.AnimationEndListener;
import static com.github.espiandev.showcaseview.anim.AnimationUtils.AnimationStartListener;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener {

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

    private float showcaseX = -1;
    private float showcaseY = -1;
    private float showcaseRadius = -1;
    private float metricScale = 1.0f;
    private float legacyShowcaseX = -1;
    private float legacyShowcaseY = -1;
    private boolean isRedundant = false;
    private boolean hasCustomClickListener = false;
    private ConfigOptions mOptions;
    private Paint mPaintTitle, mEraser;
    private TextPaint mPaintDetail;
    private int backColor;
    private Drawable showcase;
    private View mHandy;
    private final Button mEndButton;
    private OnShowcaseEventListener mEventListener;
    private Rect voidedArea;
    private String mTitleText, mSubText;
    private int detailTextColor = -1;
    private int titleTextColor = -1;
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;
    private float[] mBestTextPosition;
    private boolean mAlteredText = false;

    private final String buttonText;
    private float scaleMultiplier = 1f;

    public ShowcaseView(Context context) {
        this(context, null, R.styleable.CustomTheme_showcaseViewStyle);
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_showcaseViewStyle);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get the attributes for the ShowcaseView
        final TypedArray styled = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle, R.style.ShowcaseView);
        backColor = styled.getInt(R.styleable.ShowcaseView_sv_backgroundColor, Color.argb(128, 80, 80, 80));
        detailTextColor = styled.getColor(R.styleable.ShowcaseView_sv_detailTextColor, Color.WHITE);
        titleTextColor = styled.getColor(R.styleable.ShowcaseView_sv_titleTextColor, Color.parseColor("#49C0EC"));
        buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
        styled.recycle();

        metricScale = getContext().getResources().getDisplayMetrics().density;
        mEndButton = (Button) LayoutInflater.from(context).inflate(R.layout.showcase_button, null);

        ConfigOptions options = new ConfigOptions();
        options.showcaseId = getId();
        setConfigOptions(options);
    }

    private void init() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE,null);
        }
        else {
            setDrawingCacheEnabled(true);
        }

        boolean hasShot = getContext().getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
                .getBoolean("hasShot" + getConfigOptions().showcaseId, false);
        if (hasShot && mOptions.shotType == TYPE_ONE_SHOT) {
            // The showcase has already been shot once, so we don't need to do anything
            setVisibility(View.GONE);
            isRedundant = true;
            return;
        }
        showcase = getContext().getResources().getDrawable(R.drawable.cling);

        showcaseRadius = metricScale * INNER_CIRCLE_RADIUS;
        PorterDuffXfermode mBlender = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        setOnTouchListener(this);

        mPaintTitle = new Paint();
        mPaintTitle.setColor(titleTextColor);
        mPaintTitle.setShadowLayer(2.0f, 0f, 2.0f, Color.DKGRAY);
        mPaintTitle.setTextSize(24 * metricScale);
        mPaintTitle.setAntiAlias(true);

        mPaintDetail = new TextPaint();
        mPaintDetail.setColor(detailTextColor);
        mPaintDetail.setShadowLayer(2.0f, 0f, 2.0f, Color.DKGRAY);
        mPaintDetail.setTextSize(16 * metricScale);
        mPaintDetail.setAntiAlias(true);

        mEraser = new Paint();
        mEraser.setColor(0xFFFFFF);
        mEraser.setAlpha(0);
        mEraser.setXfermode(mBlender);
        mEraser.setAntiAlias(true);

        if (!mOptions.noButton && mEndButton.getParent() == null) {
            RelativeLayout.LayoutParams lps = (LayoutParams) generateDefaultLayoutParams();
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = ((Number) (metricScale * 12)).intValue();
            lps.setMargins(margin, margin, margin, margin);
            lps.height = LayoutParams.WRAP_CONTENT;
            lps.width = LayoutParams.WRAP_CONTENT;
            mEndButton.setLayoutParams(lps);
            mEndButton.setText(buttonText != null ? buttonText : getResources().getString(R.string.ok));
            if (!hasCustomClickListener) mEndButton.setOnClickListener(this);
            addView(mEndButton);
        }

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

        //Draw the semi-transparent background
        canvas.drawColor(backColor);

        //Draw to the scale specified
        Matrix mm = new Matrix();
        mm.postScale(scaleMultiplier, scaleMultiplier, showcaseX, showcaseY);
        canvas.setMatrix(mm);

        //Erase the area for the ring
        canvas.drawCircle(showcaseX, showcaseY, showcaseRadius, mEraser);

        boolean recalculateText = makeVoidedRect() || mAlteredText;
        mAlteredText = false;

        showcase.setBounds(voidedArea);
        showcase.draw(canvas);

        canvas.setMatrix(new Matrix());


        if (!TextUtils.isEmpty(mTitleText) || !TextUtils.isEmpty(mSubText)) {
            if (recalculateText)
                mBestTextPosition = getBestTextPosition(canvas.getWidth(), canvas.getHeight());

            if (!TextUtils.isEmpty(mTitleText)) {
                //TODO: use a dynamic detail layout
                canvas.drawText(mTitleText, mBestTextPosition[0], mBestTextPosition[1], mPaintTitle);
            }

            if (!TextUtils.isEmpty(mSubText)) {
                canvas.save();
                if (recalculateText)
                    mDynamicDetailLayout = new DynamicLayout(mSubText, mPaintDetail,
                            ((Number) mBestTextPosition[2]).intValue(), Layout.Alignment.ALIGN_NORMAL,
                            1.2f, 1.0f, true);
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
        return new float[]{24 * metricScale, spaceTop > spaceBottom ? 128 * metricScale : 24 * metricScale + voidedArea.bottom, canvasW - 48 * metricScale};

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
        if (voidedArea == null || (showcaseX != legacyShowcaseX || showcaseY != legacyShowcaseY)) {

            int cx = (int) showcaseX, cy = (int) showcaseY;
            int dw = showcase.getIntrinsicWidth();
            int dh = showcase.getIntrinsicHeight();

            voidedArea = new Rect(cx - dw / 2, cy - dh / 2, cx + dw / 2, cy + dh / 2);

            legacyShowcaseX = showcaseX;
            legacyShowcaseY = showcaseY;

            return true;

        }
        return false;

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
        this.titleTextColor = titleTextColor;
        this.detailTextColor = detailTextColor;
        if (mPaintTitle != null) {
            mPaintTitle.setColor(titleTextColor);
        }
        if (mPaintDetail != null) {
            mPaintDetail.setColor(detailTextColor);
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
        mTitleText = titleText;
        mSubText = subText;
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
        AnimationUtils.createMovementAnimation(mHandy, x, y).start();
    }

    private void setConfigOptions(ConfigOptions options) {
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
        public int showcaseId = 0;
        public int shotType = TYPE_NO_LIMIT;
        public int insert = INSERT_TO_DECOR;
        public boolean hideOnClickOutside = false;

        /**
         * Default duration for fade in animation. Set to 0 to disable.
         */
        public int fadeInDuration = AnimationUtils.DEFAULT_DURATION;

        /**
         * Default duration for fade out animation. Set to 0 to disable.
         */
        public int fadeOutDuration = AnimationUtils.DEFAULT_DURATION;
    }

}
