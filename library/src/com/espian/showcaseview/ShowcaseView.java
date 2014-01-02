package com.espian.showcaseview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region.Op;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.espian.showcaseview.actionbar.ActionBarViewWrapper;
import com.espian.showcaseview.actionbar.reflection.BaseReflector;
import com.espian.showcaseview.anim.AnimationUtils;
import com.espian.showcaseview.drawing.ClingDrawer;
import com.espian.showcaseview.drawing.ClingDrawerImpl;
import com.espian.showcaseview.drawing.TextDrawer;
import com.espian.showcaseview.drawing.TextDrawerImpl;
import com.espian.showcaseview.targets.Target;
import com.espian.showcaseview.utils.Calculator;
import com.espian.showcaseview.utils.PointAnimator;
import com.github.espiandev.showcaseview.R;
import com.nineoldandroids.animation.Animator;

import static com.espian.showcaseview.anim.AnimationUtils.AnimationEndListener;
import static com.espian.showcaseview.anim.AnimationUtils.AnimationStartListener;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout
        implements View.OnClickListener, View.OnTouchListener {

    public static final int TYPE_NO_LIMIT = 0;
    public static final int TYPE_ONE_SHOT = 1;

    public static final int INSERT_TO_DECOR = 0;
    public static final int INSERT_TO_VIEW = 1;

    public static final int ITEM_ACTION_HOME = 0;
    public static final int ITEM_TITLE = 1;
    public static final int ITEM_SPINNER = 2;
    public static final int ITEM_ACTION_ITEM = 3;
    public static final int ITEM_ACTION_OVERFLOW = 6;

    protected static final String PREFS_SHOWCASE_INTERNAL = "showcase_internal";
    public static final int INNER_CIRCLE_RADIUS = 94;
    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private int showcaseX = -1;
    private int showcaseY = -1;
    private float showcaseRadius = -1;
    private float metricScale = 1.0f;
    private float legacyShowcaseX = -1;
    private float legacyShowcaseY = -1;
    private boolean isRedundant = false;
    private boolean hasCustomClickListener = false;
    private ConfigOptions mOptions;
    private int mBackgroundColor;
    private View mHandy;
    private final Button mEndButton;
    OnShowcaseEventListener mEventListener = OnShowcaseEventListener.NONE;
    private boolean mAlteredText = false;

    private final String buttonText;

    private float scaleMultiplier = 1f;
    private TextDrawer mTextDrawer;
    private ClingDrawer mShowcaseDrawer;

    public static final Target NONE = new Target() {
        @Override
        public Point getPoint() {
            return null;
        }
    };

    private boolean mHasNoTarget = false;

    protected ShowcaseView(Context context) {
        this(context, null, R.styleable.CustomTheme_showcaseViewStyle);
    }

    protected ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get the attributes for the ShowcaseView
        final TypedArray styled = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle,
                        R.style.ShowcaseView);
        mBackgroundColor = styled
                .getInt(R.styleable.ShowcaseView_sv_backgroundColor, Color.argb(128, 80, 80, 80));
        int showcaseColor = styled
                .getColor(R.styleable.ShowcaseView_sv_showcaseColor, Color.parseColor("#33B5E5"));

        int titleTextAppearance = styled
                .getResourceId(R.styleable.ShowcaseView_sv_titleTextAppearance,
                        R.style.TextAppearance_ShowcaseView_Title);
        int detailTextAppearance = styled
                .getResourceId(R.styleable.ShowcaseView_sv_detailTextAppearance,
                        R.style.TextAppearance_ShowcaseView_Detail);

        buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
        styled.recycle();

        metricScale = getContext().getResources().getDisplayMetrics().density;
        mEndButton = (Button) LayoutInflater.from(context).inflate(R.layout.showcase_button, null);

        mShowcaseDrawer = new ClingDrawerImpl(getResources(), showcaseColor);

        // TODO: This isn't ideal, ClingDrawer and Calculator interfaces should be separate
        mTextDrawer = new TextDrawerImpl(metricScale, mShowcaseDrawer);
        mTextDrawer.setTitleStyling(context, titleTextAppearance);
        mTextDrawer.setDetailStyling(context, detailTextAppearance);

        ConfigOptions options = new ConfigOptions();
        options.showcaseId = getId();
        setConfigOptions(options);

        init();
    }

    private void init() {
        setHardwareAccelerated(true);

        boolean hasShot = getContext()
                .getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
                .getBoolean("hasShot" + getConfigOptions().showcaseId, false);
        if (hasShot && mOptions.shotType == TYPE_ONE_SHOT) {
            // The showcase has already been shot once, so we don't need to do anything
            setVisibility(View.GONE);
            isRedundant = true;
            return;
        }

        showcaseRadius = metricScale * INNER_CIRCLE_RADIUS;
        setOnTouchListener(this);

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
            mEndButton.setText(
                    buttonText != null ? buttonText : getResources().getString(R.string.ok));
            if (!hasCustomClickListener) {
                mEndButton.setOnClickListener(this);
            }
            addView(mEndButton);
        }

    }

    /**
     * @deprecated Use setShowcase() with the target ShowcaseView.NONE
     */
    @Deprecated
    public void setShowcaseNoView() {
        setShowcasePosition(1000000, 1000000);
    }

    /**
     * Set the view to showcase
     *
     * @param view The {@link View} to showcase.
     * @deprecated Use setShowcase with a {@link com.espian.showcaseview.targets.ViewTarget}
     */
    @Deprecated
    public void setShowcaseView(final View view) {
        if (isRedundant || view == null) {
            isRedundant = true;
            return;
        }
        isRedundant = false;

        view.post(new Runnable() {
            @Override
            public void run() {
                //init();
                Point viewPoint = Calculator.getShowcasePointFromView(view, getConfigOptions());
                setShowcasePosition(viewPoint);
                invalidate();
            }
        });
    }

    /**
     * @deprecated This will soon become private. Use setShowcase with a {@link com.espian.showcaseview.targets.PointTarget}
     */
    @Deprecated
    public void setShowcasePosition(Point point) {
        setShowcasePosition(point.x, point.y);
    }

    /**
     * Set a specific position to showcase
     *
     * @param x X co-ordinate
     * @param y Y co-ordinate
     * @deprecated use setShowcase with a PointTarget
     */
    @Deprecated
    public void setShowcasePosition(int x, int y) {
        if (isRedundant) {
            return;
        }
        showcaseX = x;
        showcaseY = y;
        //init();
        invalidate();
    }

    public void setShowcase(final Target target) {
        setShowcase(target, false);
    }

    public void setShowcase(final Target target, final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Point targetPoint = target.getPoint();
                if (targetPoint != null) {
                    mHasNoTarget = false;
                    if (animate) {
                        Animator animator = PointAnimator.ofPoints(ShowcaseView.this, targetPoint);
                        animator.setDuration(getConfigOptions().fadeInDuration);
                        animator.setInterpolator(INTERPOLATOR);
                        animator.start();
                    } else {
                        setShowcasePosition(targetPoint);
                    }
                } else {
                    mHasNoTarget = true;
                    invalidate();
                }
            }
        }, 100);
    }

    public boolean hasShowcaseView() {
    	return (showcaseX != 1000000 && showcaseY != 1000000) || !mHasNoTarget;
    }

    public void setShowcaseX(int x) {
        setShowcasePosition(x, showcaseY);
    }

    public void setShowcaseY(int y) {
        setShowcasePosition(showcaseX, y);
    }

    public int getShowcaseX() {
        return showcaseX;
    }

    public int getShowcaseY() {
        return showcaseY;
    }

    @Deprecated
    public void setShowcaseItem(final int itemType, final int actionItemId,
            final Activity activity) {
        post(new Runnable() {
            @Override
            public void run() {
                BaseReflector reflector = BaseReflector.getReflectorForActivity(activity);
                ViewParent p = reflector.getActionBarView(); //ActionBarView
                ActionBarViewWrapper wrapper = new ActionBarViewWrapper(p);

                switch (itemType) {
                    case ITEM_ACTION_HOME:
                        setShowcaseView(reflector.getHomeButton());
                        break;
                    case ITEM_SPINNER:
                        setShowcaseView(wrapper.getSpinnerView());
                        break;
                    case ITEM_TITLE:
                        setShowcaseView(wrapper.getTitleView());
                        break;
                    case ITEM_ACTION_ITEM:
                        setShowcaseView(wrapper.getActionItem(actionItemId));
                        break;
                    case ITEM_ACTION_OVERFLOW:
                        View overflow = wrapper.getOverflowView();
                        // This check essentially checks if we are on a device with a legacy menu key
                        if (overflow != null) {
                            setShowcaseView(wrapper.getOverflowView());
                        } else {
                            setShowcasePosition(getLegacyOverflowPoint());
                        }
                        break;
                    default:
                        Log.e("TAG", "Unknown item type");
                }
            }
        });

    }

    /**
     * Gets the bottom centre of the screen, where a legacy menu would pop up
     */
    private Point getLegacyOverflowPoint() {
        return new Point(getLeft() + getWidth() / 2, getBottom());
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

    protected void performButtonClick() {
        mEndButton.performClick();
    }

    public void setOnShowcaseEventListener(OnShowcaseEventListener listener) {
        if (listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = OnShowcaseEventListener.NONE;
        }
    }

    public void setButtonText(CharSequence text) {
        if (mEndButton != null) {
            mEndButton.setText(text);
        }
    }

    public void setHardwareAccelerated(boolean accelerated) {
        if (accelerated) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				if (isHardwareAccelerated()) {
					Paint hardwarePaint = new Paint();
					hardwarePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
					setLayerType(LAYER_TYPE_HARDWARE, hardwarePaint);
				} else {
					setLayerType(LAYER_TYPE_SOFTWARE, null);
				}
			} else {
				setDrawingCacheEnabled(true);
			}
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            } else {
                setDrawingCacheEnabled(true);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (showcaseX < 0 || showcaseY < 0 || isRedundant) {
            super.dispatchDraw(canvas);
            return;
        }

        boolean recalculatedCling = mShowcaseDrawer.calculateShowcaseRect(showcaseX, showcaseY);
        boolean recalculateText = recalculatedCling || mAlteredText;
        mAlteredText = false;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB && !mHasNoTarget) {
        	Path path = new Path();
            path.addCircle(showcaseX, showcaseY, showcaseRadius, Path.Direction.CW);
            canvas.clipPath(path, Op.DIFFERENCE);
        }

        //Draw background color
        canvas.drawColor(mBackgroundColor);

        // Draw the showcase drawable
        if (!mHasNoTarget) {
            mShowcaseDrawer.drawShowcase(canvas, showcaseX, showcaseY, scaleMultiplier, showcaseRadius);
        }

        // Draw the text on the screen, recalculating its position if necessary
        if (recalculateText) {
            mTextDrawer.calculateTextPosition(canvas.getWidth(), canvas.getHeight(), this);
        }
        mTextDrawer.draw(canvas, recalculateText);

        super.dispatchDraw(canvas);

    }

    /**
     * Adds an animated hand performing a gesture.
     * All parameters passed to this method are relative to the center of the showcased view.
     * @param offsetStartX  x-offset of the start position
     * @param offsetStartY  y-offset of the start position
     * @param offsetEndX    x-offset of the end position
     * @param offsetEndY    y-offset of the end position
     * @see com.espian.showcaseview.ShowcaseView#animateGesture(float, float, float, float, boolean)
     */
    public void animateGesture(float offsetStartX, float offsetStartY, float offsetEndX,
            float offsetEndY) {
        animateGesture(offsetStartX, offsetStartY, offsetEndX, offsetEndY, false);
    }

    /**
     * Adds an animated hand performing a gesture.
     * @param startX                x-coordinate or x-offset of the start position
     * @param startY                y-coordinate or x-offset of the start position
     * @param endX                  x-coordinate or x-offset of the end position
     * @param endY                  y-coordinate or x-offset of the end position
     * @param absoluteCoordinates   If true, this will use absolute coordinates instead of coordinates relative to the center of the showcased view
     */
    public void animateGesture(float startX, float startY, float endX,
            float endY, boolean absoluteCoordinates) {
        mHandy = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.handy, null);
        addView(mHandy);
        moveHand(startX, startY, endX, endY, absoluteCoordinates, new AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                removeView(mHandy);
            }
        });
    }

    private void moveHand(float startX, float startY, float endX,
            float endY, boolean absoluteCoordinates, AnimationEndListener listener) {
        AnimationUtils.createMovementAnimation(mHandy, absoluteCoordinates?0:showcaseX,
                absoluteCoordinates?0:showcaseY,
                startX, startY,
                endX, endY,
                listener).start();
    }

    @Override
    public void onClick(View view) {
        // If the type is set to one-shot, store that it has shot
        if (mOptions.shotType == TYPE_ONE_SHOT) {
            SharedPreferences internal = getContext()
                    .getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                internal.edit().putBoolean("hasShot" + getConfigOptions().showcaseId, true).apply();
            } else {
                internal.edit().putBoolean("hasShot" + getConfigOptions().showcaseId, true)
                        .commit();
            }
        }
        hide();
    }

    public void hide() {
        mEventListener.onShowcaseViewHide(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && getConfigOptions().fadeOutDuration > 0) {
            fadeOutShowcase();
        } else {
            setVisibility(View.GONE);
            mEventListener.onShowcaseViewDidHide(this);
        }
    }

    private void fadeOutShowcase() {
        AnimationUtils.createFadeOutAnimation(this, new AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(View.GONE);
                mEventListener.onShowcaseViewDidHide(ShowcaseView.this);
            }
        }).start();
    }

    public void show() {
        mEventListener.onShowcaseViewShow(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && getConfigOptions().fadeInDuration > 0) {
            fadeInShowcase();
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    private void fadeInShowcase() {
        AnimationUtils.createFadeInAnimation(this, getConfigOptions().fadeInDuration,
                new AnimationStartListener() {
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

        if (MotionEvent.ACTION_UP == motionEvent.getAction() &&
            mOptions.hideOnClickOutside && distanceFromFocus > showcaseRadius) {
            this.hide();
            return true;
        }

        return mOptions.block && distanceFromFocus > showcaseRadius;
    }

    /**
     * @deprecated Use setScaleMultiplier
     */
    @Deprecated
    public void setShowcaseIndicatorScale(float scaleMultiplier) {
        setScaleMultiplier(scaleMultiplier);
    }

    public void setText(int titleTextResId, int subTextResId) {
        String titleText = getContext().getResources().getString(titleTextResId);
        String subText = getContext().getResources().getString(subTextResId);
        setText(titleText, subText);
    }

    public void setText(String titleText, String subText) {
        mTextDrawer.setTitle(titleText);
        mTextDrawer.setDetails(subText);
        mAlteredText = true;
        invalidate();
    }

    /**
     * Get the ghostly gesture hand for custom gestures
     *
     * @return a View representing the ghostly hand
     */
    public View getHand() {
        final View mHandy = ((LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.handy, null);
        addView(mHandy);
        AnimationUtils.hide(mHandy);

        return mHandy;
    }

    /**
     * Point to a specific view
     * @param view The {@link View} to Showcase
     * @deprecated use pointTo(Target)
     */
    @Deprecated
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
     * @deprecated use pointTo(Target)
     */
    @Deprecated
    public void pointTo(float x, float y) {
        mHandy = getHand();
        AnimationUtils.createMovementAnimation(mHandy, x, y).start();
    }

    /**
     * Point to a specific point on the screen
     * @param target The target to point to
     * @deprecated use pointTo(Target)
     */
    public void pointTo(final Target target) {
        post(new Runnable() {
            @Override
            public void run() {
                mHandy = getHand();
                Point targetPoint = target.getPoint();
                AnimationUtils.createMovementAnimation(mHandy, targetPoint.x,
                        targetPoint.y).start();
            }
        });
    }

    protected void setConfigOptions(ConfigOptions options) {
        mOptions = options;
    }

    public ConfigOptions getConfigOptions() {
        // Make sure that this method never returns null
        if (mOptions == null) {
            return mOptions = new ConfigOptions();
        }
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
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(View viewToShowcase, Activity activity,
            String title,
            String detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        if (options != null) {
            sv.setConfigOptions(options);
        }
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
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(View viewToShowcase, Activity activity, int title,
            int detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        if (options != null) {
            sv.setConfigOptions(options);
        }
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
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(int showcaseViewId, Activity activity,
            String title, String detailText, ConfigOptions options) {
        View v = activity.findViewById(showcaseViewId);
        if (v != null) {
            return insertShowcaseView(v, activity, title, detailText, options);
        }
        return null;
    }

    /**
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(int showcaseViewId, Activity activity, int title,
            int detailText, ConfigOptions options) {
        View v = activity.findViewById(showcaseViewId);
        if (v != null) {
            return insertShowcaseView(v, activity, title, detailText, options);
        }
        return null;
    }

    /**
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(int x, int y, Activity activity, String title,
            String detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        if (options != null) {
            sv.setConfigOptions(options);
        }
        if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
        } else {
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
        }
        sv.setShowcasePosition(x, y);
        sv.setText(title, detailText);
        return sv;
    }

    /**
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(int x, int y, Activity activity, int title,
            int detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        if (options != null) {
            sv.setConfigOptions(options);
        }
        if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
        } else {
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
        }
        sv.setShowcasePosition(x, y);
        sv.setText(title, detailText);
        return sv;
    }

    /**
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseView(View showcase, Activity activity) {
        return insertShowcaseView(showcase, activity, null, null, null);
    }

    /**
     * Quickly insert a ShowcaseView into an Activity, highlighting an item.
     *
     * @param type       the type of item to showcase (can be ITEM_ACTION_HOME,
     *                   ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or ITEM_ACTION_OVERFLOW)
     * @param itemId     the ID of an Action item to showcase (only required for ITEM_ACTION_ITEM
     * @param activity   Activity to insert the ShowcaseView into
     * @param title      Text to show as a title. Can be null.
     * @param detailText More detailed text. Can be null.
     * @param options    A set of options to customise the ShowcaseView
     * @return the created ShowcaseView instance
     * @deprecated use insertShowcaseView with {@link Target}
     */
    @Deprecated
    public static ShowcaseView insertShowcaseViewWithType(int type, int itemId, Activity activity,
            String title, String detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        if (options != null) {
            sv.setConfigOptions(options);
        }
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
     * @param type       the type of item to showcase (can be ITEM_ACTION_HOME,
     *                   ITEM_TITLE_OR_SPINNER, ITEM_ACTION_ITEM or ITEM_ACTION_OVERFLOW)
     * @param itemId     the ID of an Action item to showcase (only required for ITEM_ACTION_ITEM
     * @param activity   Activity to insert the ShowcaseView into
     * @param title      Text to show as a title. Can be null.
     * @param detailText More detailed text. Can be null.
     * @param options    A set of options to customise the ShowcaseView
     * @return the created ShowcaseView instance
     */
    @Deprecated
    public static ShowcaseView insertShowcaseViewWithType(int type, int itemId, Activity activity,
            int title, int detailText, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        if (options != null) {
            sv.setConfigOptions(options);
        }
        if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
        } else {
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
        }
        sv.setShowcaseItem(type, itemId, activity);
        sv.setText(title, detailText);
        return sv;
    }

    @Deprecated
    public static ShowcaseView insertShowcaseView(int x, int y, Activity activity) {
        return insertShowcaseView(x, y, activity, null, null, null);
    }

    /**
     * Internal insert method so all inserts are routed through one method
     */
    private static ShowcaseView insertShowcaseViewInternal(Target target, Activity activity, String title,
                                                           String detail, ConfigOptions options) {
        ShowcaseView sv = new ShowcaseView(activity);
        sv.setConfigOptions(options);
        if (sv.getConfigOptions().insert == INSERT_TO_DECOR) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(sv);
        } else {
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(sv);
        }
        sv.setShowcase(target);
        sv.setText(title, detail);
        return sv;
    }

    public static ShowcaseView insertShowcaseView(Target target, Activity activity) {
        return insertShowcaseViewInternal(target, activity, null, null, null);
    }

    public static ShowcaseView insertShowcaseView(Target target, Activity activity, String title, String detail) {
        return insertShowcaseViewInternal(target, activity, title, detail, null);
    }

    public static ShowcaseView insertShowcaseView(Target target, Activity activity, int title, int detail) {
        return insertShowcaseViewInternal(target, activity, activity.getString(title), activity.getString(detail), null);
    }

    public static ShowcaseView insertShowcaseView(Target target, Activity activity, String title, String detail, ConfigOptions options) {
        return insertShowcaseViewInternal(target, activity, title, detail, options);
    }

    public static ShowcaseView insertShowcaseView(Target target, Activity activity, int title, int detail, ConfigOptions options) {
        return insertShowcaseViewInternal(target, activity, activity.getString(title), activity.getString(detail), options);
    }

    public static class ConfigOptions {

        public boolean block = true, noButton = false;
        public boolean hideOnClickOutside = false;

        /**
         * Does not work with the {@link ShowcaseViews} class as it does not make sense (only with
         * {@link ShowcaseView}).
         * @deprecated not compatible with Target API
         */
        @Deprecated
        public int insert = INSERT_TO_DECOR;

        /**
         * If you want to use more than one Showcase with the {@link ConfigOptions#shotType} {@link
         * ShowcaseView#TYPE_ONE_SHOT} in one Activity, set a unique value for every different
         * Showcase you want to use.
         */
        public int showcaseId = 0;

        /**
         * If you want to use more than one Showcase with {@link ShowcaseView#TYPE_ONE_SHOT} in one
         * Activity, set a unique {@link ConfigOptions#showcaseId} value for every different
         * Showcase you want to use. If you want to use this in the {@link ShowcaseViews} class, you
         * need to set a custom showcaseId for each {@link ShowcaseView}.
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
        /**
         * Allow custom positioning of the button within the showcase view.
         */
        public LayoutParams buttonLayoutParams = null;
        
        /**
         * Whether the text should be centered or stretched in the available space
         */
        public boolean centerText = false;
    }

    public float getScaleMultiplier() {
        return scaleMultiplier;
    }

    public void setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

}
