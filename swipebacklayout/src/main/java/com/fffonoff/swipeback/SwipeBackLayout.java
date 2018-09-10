package com.fffonoff.swipeback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashSet;
import java.util.Set;


public class SwipeBackLayout extends FrameLayout {


    private static final boolean DEFAULT_EDGE_ONLY = true;
    private static final float DEFAULT_EDGE_SIZE = 0.12f;
    private static final float DEFAULT_PERCENT_TO_RELEASE = 0.30f;
    private static final int DEFAULT_SCRIM_COLOR = Color.parseColor("#cc000000");

    private static final int MIN_FLING_VELOCITY = 400;
    private static final float SENSITIVITY = 1f;
    private static final float DISTANCE_TO_TRIGGER = Density.dpToPx(20);

    private View dimmerView;
    private ViewDragHelper dragHelper;

    private View dragView;
    private @IdRes int dragViewId;
    private @ColorInt int scrimColor;
    private float percentToRelease;
    private boolean edgeOnly;
    private float edgeSize;

    private int edgePosition;
    private boolean isLocked = false;
    private float startX;

    private Set<Listener> listeners = new HashSet<>();


    public SwipeBackLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (dragViewId == child.getId()) {
            dragView = child;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isLocked) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceSum = 0;
                int historySize = ev.getHistorySize();
                for (int h = 0; h < historySize; h++) {
                    float hx = ev.getHistoricalX(0, h);
                    float dx = (hx - startX);
                    distanceSum += dx;
                }

                if (distanceSum < DISTANCE_TO_TRIGGER) return false;
                break;
        }

        if (edgeOnly && !canDragFromEdge(ev)) {
            return false;
        }

        boolean interceptForDrag;
        try {
            interceptForDrag = dragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            interceptForDrag = false;
        }

        return interceptForDrag;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLocked) {
            return false;
        }

        try {
            dragHelper.processTouchEvent(event);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public View getDragView() {
        return dragView;
    }

    public void setDragView(View dragView) {
        this.dragView = dragView;
    }

    public float getPercentToRelease() {
        return percentToRelease;
    }

    public void setPercentToRelease(@FloatRange(from = 0.0, to = 1.0) float percentToRelease) {
        this.percentToRelease = percentToRelease;
    }

    public boolean isEdgeOnly() {
        return edgeOnly;
    }

    public void setEdgeOnly(boolean edgeOnly) {
        this.edgeOnly = edgeOnly;
    }

    public float getEdgeSize() {
        return edgeSize;
    }

    public void setEdgeSize(@FloatRange(from = 0.0, to = 1.0) float edgeSize) {
        this.edgeSize = edgeSize;
    }

    public int getScrimColor() {
        return scrimColor;
    }

    public void setScrimColor(@ColorInt int scrimColor) {
        this.scrimColor = scrimColor;
    }

    public void lock() {
        dragHelper.abort();
        isLocked = true;
    }

    public void unlock() {
        dragHelper.abort();
        isLocked = false;
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwipeBackLayout,
                0,
                0
        );

        try {
            dragViewId = ta.getResourceId(R.styleable.SwipeBackLayout_drag_view_id, -2);
            scrimColor = ta.getColor(R.styleable.SwipeBackLayout_scrim_color, DEFAULT_SCRIM_COLOR);
            edgeOnly = ta.getBoolean(R.styleable.SwipeBackLayout_edge_only, DEFAULT_EDGE_ONLY);
            edgeSize = ta.getFloat(R.styleable.SwipeBackLayout_edge_size, DEFAULT_EDGE_SIZE);
            percentToRelease = ta.getFloat(R.styleable.SwipeBackLayout_percent_to_release, DEFAULT_PERCENT_TO_RELEASE);
        } finally {
            ta.recycle();
        }

        edgePosition = ViewDragHelper.EDGE_LEFT;
        dragHelper = ViewDragHelper.create(this, SENSITIVITY, callback);
        dragHelper.setMinVelocity(MIN_FLING_VELOCITY);
        dragHelper.setEdgeTrackingEnabled(edgePosition);

        setMotionEventSplittingEnabled(false);

        dimmerView = new View(getContext());
        dimmerView.setBackgroundColor(scrimColor);

        addView(dimmerView);
    }

    private boolean canDragFromEdge(MotionEvent ev) {
        return ev.getX() < getWidth() * edgeSize;
    }

    private void applyScrim(float percent) {
        dimmerView.setAlpha(percent);
    }


    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            boolean edgeCase = !edgeOnly || edgeSize > 0 || dragHelper.isEdgeTouched(edgePosition, pointerId);
            return child == dragView && edgeCase;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return Math.max(0, Math.min(getWidth(), left));
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getWidth();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            int left = releasedChild.getLeft();
            int leftThreshold = (int) (getWidth() * percentToRelease);
            if (xvel >= 0 && left > leftThreshold) {
                left = getWidth();
            } else {
                left = 0;
            }

            dragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            float percent = 1f - ((float) left / (float) getWidth());

            for (Listener listener : listeners) {
                listener.onSlideChange(percent);
            }

            applyScrim(percent);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (dragView == null) return;

            switch (state) {
                case ViewDragHelper.STATE_IDLE:
                    if (dragView.getLeft() < getWidth()) {
                        for (Listener listener : listeners) {
                            listener.onOpen();
                        }
                    } else {
                        for (Listener listener : listeners) {
                            listener.onClose();
                        }
                    }
                    break;
            }
        }

    };


    public interface Listener {

        default void onOpen() {
        }

        default void onClose() {
        }

        default void onSlideChange(float percent) {
        }
    }

}
