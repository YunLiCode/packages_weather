package com.lewa.weather.view;

import com.lewa.weather.R;

import android.R.anim;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A ListView in which items can be re-ordered via dragging and dropping an item.
 *
 */
public class DraggableListView extends ListView {
    private ImageView mDragView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    /**
     * At which position is the item currently being dragged. Note that this
     * takes in to account header items.
     */
    private int mDragPos;
    private int mSrcDragPos;    // At which position was the item being dragged originally
    private int mDragPointX;    // at what x offset inside the item did the user grab it
    private int mDragPointY;    // at what y offset inside the item did the user grab it
    private int mXOffset;       // the difference between screen coordinates and coordinates in this view
    private int mYOffset;       // the difference between screen coordinates and coordinates in this view

    private DragListener mDragListener;
    private DropListener mDropListener;
    private RemoveListener mRemoveListener;

    private int mUpperBound;
    private int mLowerBound;

    private int mHeight;

    private GestureDetector mGestureDetector;
    private static final int FLING = 0;
    private static final int SLIDE = 1;
    private static final int TRASH = 2;

    private int mRemoveMode = -1;

    private Rect mTempRect = new Rect();
    private Bitmap mDragBitmap;
    private final int mTouchSlop;
    private int mItemHeightNormal;
    private int mItemHeightExpanded;
    private int mItemHeightHalf;
    private Drawable mTrashcan;

    private int mSrcDragPosition;
    private Context context;

    private static final int LAST_BUT_ONE_POSITION = 2;
    private static final int MIN_MOVE_DIV = 3;
    
    private int mFixedItem = -1;
    private Button addcity_bt;
    private ImageButton del_city_bt;
    private int windHeight;
    public DraggableListView (Context context) {
        this(context, null);
    }
    
    public void setButtons(Button button,ImageButton imageButton){
        this.addcity_bt=button;
        this.del_city_bt=imageButton;
    }

    public DraggableListView (Context context, AttributeSet attrs) {
       super(context, attrs);
       this.context=context;
       mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
       Resources res = getResources();
       mItemHeightNormal = res.getDimensionPixelSize(R.dimen.normal_height);
       mItemHeightHalf = mItemHeightNormal / 2;
       mItemHeightExpanded = res.getDimensionPixelSize(R.dimen.expanded_height);
       
       mWindowParams = new WindowManager.LayoutParams();
       mWindowParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
       mWindowParams.x = 0;
       mWindowParams.y = 0;
       
       mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
       mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
       mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
           | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
           | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
           | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
           | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
       mWindowParams.format = PixelFormat.TRANSLUCENT;
       mWindowParams.windowAnimations = 0;

       mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
       DisplayMetrics metric = new DisplayMetrics();
       mWindowManager.getDefaultDisplay().getMetrics(metric);
       windHeight = metric.heightPixels;
    }

    public DraggableListView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.context=context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Resources res = getResources();
        mItemHeightNormal = res.getDimensionPixelSize(R.dimen.normal_height);
        mItemHeightHalf = mItemHeightNormal / 2;
        mItemHeightExpanded = res.getDimensionPixelSize(R.dimen.expanded_height);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = 0;
        mWindowParams.y = 0;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        windHeight = metric.heightPixels;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mRemoveListener != null && mGestureDetector == null) {
            if (mRemoveMode == FLING) {
                mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                        float velocityY) {
                        if (mDragView != null) {
                            if (velocityX > 1000) {
                                Rect r = mTempRect;
                                mDragView.getDrawingRect(r);
                                if ( e2.getX() > r.right * 2 / 3) {
                                    // fast fling right with release near the right edge of the screen
                                    stopDragging();
                                    mRemoveListener.remove(mSrcDragPos);
                                    unExpandViews(true);
                                }
                            }
                            // flinging while dragging should have no effect
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        if (mDragListener != null || mDropListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    int itemnum = pointToPosition(x, y);
                    if (itemnum == AdapterView.INVALID_POSITION) {
                        break;
                    }
                    ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
                    mDragPointX = x - item.getLeft();
                    mDragPointY = y - item.getTop();
                    mXOffset = ((int)ev.getRawX()) - x;
                    mYOffset = ((int)ev.getRawY()) - y;
//                    View dragger = item.findViewById(lewa.R.id.grabber);
                    // The left side of the item is the grabber for dragging the item if the grabber
                    // doesn't exist; otherwise the touch area is where the grabber icon located
                    // plus some padding on the left or right side (20)
                    View dragger = item.findViewById(R.id.grabber);
                    Rect r = mTempRect;
                    r.top = dragger.getTop();
                    r.bottom = dragger.getBottom();
                    r.left = dragger.getLeft();
                    r.right = dragger.getRight();
                    if (x > r.left - 10 && x < r.right + 10) {
                        // clear the double buffer at first.
                        item.setDrawingCacheEnabled(false);
                        item.setDrawingCacheEnabled(true);
                        // Create a copy of the drawing cache so that it does not get recycled
                        // by the framework when the list tries to clean up memory
                        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
                        mSrcDragPosition = y;
                        startDragging(bitmap, x, y);
                        mDragPos = itemnum;
                        mSrcDragPos = mDragPos;
                        mHeight = getHeight();
                        int touchSlop = mTouchSlop;
                        mUpperBound = Math.min(y - touchSlop, mHeight / 3);
                        mLowerBound = Math.max(y + touchSlop, mHeight * 2 /3);
//                        Intent intent=new Intent("com.lewa.player.startdraging");
//                        context.sendBroadcast(intent);
                        del_city_bt.setVisibility(View.VISIBLE);
                        return true;
                    }
                    stopDragging();
                    break;
                    
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
     * pointToPosition() doesn't consider invisible views, but we
     * need to, so implement a slightly different version.
     */
    private int myPointToPosition(int x, int y) {

        if (y < 0) {
            // when dragging off the top of the screen, calculate position
            // by going back from a visible item
            int pos = myPointToPosition(x, y + mItemHeightNormal);
            if (pos > 0) {
                return pos - 1;
            }
        }

        Rect frame = mTempRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);

            if (frame.contains(x + getListPaddingLeft() , y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }

    private int getItemForPosition(int y) {
        int adjustedy = y - mDragPointY - mItemHeightHalf;
        int pos = myPointToPosition(0, adjustedy);
        if (pos >= 0) {
            if (pos <= mSrcDragPos) {
                pos += 1;
            }
        } else if (adjustedy < 0) {
            // this shouldn't happen anymore now that myPointToPosition deals
            // with this situation
            pos = 0;
        }
        // For move to bottom easy, add the bottom case check
        // if the position is in last item area, move position is larger then Item height /3, and  the number is last but one, we add the mumber
        if ((y > mHeight - mItemHeightNormal)
                && (pos == getCount() - LAST_BUT_ONE_POSITION)
                && (y >= mSrcDragPosition + mItemHeightNormal / MIN_MOVE_DIV)) {
            pos += 1;
                }
        return pos;
    }

    private void adjustScrollBounds(int y) {
        if (y >= mHeight / 3) {
            mUpperBound = mHeight / 3;
        }
        if (y <= mHeight * 2 / 3) {
            mLowerBound = mHeight * 2 / 3;
        }
    }

    /*
     * Restore size and visibility for all listitems
     */
    private void unExpandViews(boolean deletion) {
        for (int i = 0;; i++) {
            View v = getChildAt(i);
            if (v == null) {
                if (deletion) {
                    // HACK force update of mItemCount
                    int position = getFirstVisiblePosition();
                    int y = getChildAt(0).getTop();
                    setAdapter(getAdapter());
                    setSelectionFromTop(position, y);
                    // end hack
                }
                try {
                    layoutChildren(); // force children to be recreated where needed
                    v = getChildAt(i);
                } catch (IllegalStateException ex) {
                    // layoutChildren throws this sometimes, presumably because we're
                    // in the process of being torn down but are still getting touch
                    // events
                }
                if (v == null) {
                    return;
                }
            }
            // Fixed item need not Expansion add by shenqi
            if (v.getHeight() > 1 && v.getHeight() <= mItemHeightHalf) {
                continue;
            }
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = mItemHeightNormal;
            v.setLayoutParams(params);
            v.setVisibility(View.VISIBLE);
        }
    }

    /* Adjust visibility and size to make it appear as though
     * an item is being dragged around and other items are making
     * room for it:
     * If dropping the item would result in it still being in the
     * same place, then make the dragged listitem's size normal,
     * but make the item invisible.
     * Otherwise, if the dragged listitem is still on screen, make
     * it as small as possible and expand the item below the insert
     * point.
     * If the dragged item is not on screen, only expand the item
     * below the current insertpoint.
     */
    private void doExpansion() {
        int childnum = mDragPos - getFirstVisiblePosition();
        if (mDragPos > mSrcDragPos) {
            childnum++;
        }
        int numheaders = getHeaderViewsCount();

        View first = getChildAt(mSrcDragPos - getFirstVisiblePosition());
        for (int i = 0;; i++) {
            View vv = getChildAt(i);
            if (vv == null) {
                break;
            }

            // Fixed item need not Expansion add by shenqi
            if (vv.getHeight() > 1 && vv.getHeight() <= mItemHeightHalf) {
                continue;
            }

            int height = mItemHeightNormal;
            int visibility = View.VISIBLE;
            if (mDragPos < numheaders && i == numheaders) {
                // dragging on top of the header item, so adjust the item below
                // instead
                if (vv.equals(first)) {
                    visibility = View.INVISIBLE;
                } else {
                    height = mItemHeightExpanded;
                }
            } else if (vv.equals(first)) {
                // processing the item that is being dragged
                if (mDragPos == mSrcDragPos || getPositionForView(vv) == getCount() - 1) {
                    // hovering over the original location
                    visibility = View.INVISIBLE;
                } else {
                    // not hovering over it
                    // Ideally the item would be completely gone, but neither
                    // setting its size to 0 nor settings visibility to GONE
                    // has the desired effect.
                    height = 1;
                }
            } else if (i == childnum) {
                if (mDragPos >= numheaders && mDragPos < getCount() - 1) {
                    height = mItemHeightExpanded;
                }
            }
            ViewGroup.LayoutParams params = vv.getLayoutParams();
            params.height = height;
            vv.setLayoutParams(params);
            vv.setVisibility(visibility);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        if ((mDragListener != null || mDropListener != null) && mDragView != null) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
//                    Intent intent=new Intent("com.lewa.player.stopdraging");
//                    context.sendBroadcast(intent);
                    del_city_bt.setVisibility(View.GONE);
                case MotionEvent.ACTION_CANCEL:
                    Rect r = mTempRect;
                    mDragView.getDrawingRect(r);
                   
                    stopDragging();
                    if (mRemoveMode == SLIDE && ev.getX() > r.right * 3 / 4) {
                        if (mRemoveListener != null) {
                            mRemoveListener.remove(mSrcDragPos);
                        }
                        unExpandViews(true);
                    } else {
                        if (mDropListener != null && mDragPos >= -1 && mDragPos < getCount()) {
                            mDropListener.drop(mSrcDragPos, mDragPos);
                        }
                        unExpandViews(false);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    
                case MotionEvent.ACTION_MOVE:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    dragView(x, y);
                    int itemnum = getItemForPosition(y);
                    if (itemnum >= 0) {
                        if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
                            if (mDragListener != null) {
                                mDragListener.drag(mDragPos, itemnum);
                            }
                            mDragPos = itemnum;
                            doExpansion();
                        }
                        int speed = 0;
                        adjustScrollBounds(y);
                        if (y > mLowerBound) {
                            // scroll the list up a bit
                            if (getLastVisiblePosition() < getCount() - 1) {
                                speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
                            } else {
                                speed = 1;
                            }
                        } else if (y < mUpperBound) {
                            // scroll the list down a bit
                            speed = y < mUpperBound / 2 ? -16 : -4;
                            if (getFirstVisiblePosition() == 0
                                    && getChildAt(0).getTop() >= getPaddingTop()) {
                                // if we're already at the top, don't try to scroll, because
                                // it causes the framework to do some extra drawing that messes
                                // up our animation
                                speed = 0;
                            }
                        }
                        if (speed != 0) {
                            smoothScrollBy(speed, 30);
                        }
                    }
                    if(ev.getRawY()>=(windHeight-del_city_bt.getHeight())){
                        del_city_bt.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        mDragPos=-1;
                    }else{
                        del_city_bt.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void startDragging(Bitmap bm, int x, int y) {
        stopDragging();
        mWindowParams.x = x - mDragPointX + mXOffset;
        mWindowParams.y = y - mDragPointY + mYOffset;
        Context context = getContext();
        ImageView v = new ImageView(context);
        //int backGroundColor = context.getResources().getColor(R.color.dragndrop_background);
        //v.setBackgroundColor(backGroundColor);
        v.setBackgroundResource(R.drawable.dnd_list_drag);
        v.setPadding(0,0, 0, 0);
        v.setImageBitmap(bm);
        mDragBitmap = bm;

        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    private void dragView(int x, int y) {
        if (mRemoveMode == SLIDE) {
            float alpha = 1.0f;
            int width = mDragView.getWidth();
            if (x > width / 2) {
                alpha = ((float)(width - x)) / (width / 2);
            }
            mWindowParams.alpha = alpha;
        }

        if (mRemoveMode == FLING || mRemoveMode == TRASH) {
            mWindowParams.x = x - mDragPointX + mXOffset;
        } else {
            mWindowParams.x = 0 + getListPaddingLeft();
        }

        mWindowParams.y = y - mDragPointY + mYOffset;
        mWindowManager.updateViewLayout(mDragView, mWindowParams);

        if (mTrashcan != null) {
            int width = mDragView.getWidth();
            if (y > getHeight() * 3 / 4) {
                mTrashcan.setLevel(2);
            } else if (width > 0 && x > width / 4) {
                mTrashcan.setLevel(1);
            } else {
                mTrashcan.setLevel(0);
            }
        }
    }

    private void stopDragging() {
        if (mDragView != null) {
            mDragView.setVisibility(GONE);
            mWindowManager.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        if (mDragBitmap != null) {
            mDragBitmap.recycle();
            mDragBitmap = null;
        }
        if (mTrashcan != null) {
            mTrashcan.setLevel(0);
        }
    }

    public void setTrashcan(Drawable trash) {
        mTrashcan = trash;
        mRemoveMode = TRASH;
    }

    /**
     * Set the drag listener to be used with this DraggableListView.
     * This can be null if the caller doesn't care about an item being dragged.
     */
    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    /**
     * Set the drop listener to be used with this DraggableListView.
     */
    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    /**
     * Fired when an item is being dragged.
     */
    public interface DragListener {
        void drag(int from, int to);
    }

    /**
     * Fired when an item is being dropped.
     * Implement the drop method to do the actual re-ordering.
     */
    public interface DropListener {
        void drop(int from, int to);
    }

    public interface RemoveListener {
        void remove(int which);
    }

    /**
     * @deprecated This now does nothing.
     */
    @Deprecated
    public void setItemHeight(int height) {
        // mItemHeightNormal = height;
        // mItemHeightHalf = mItemHeightNormal / 2;
        // mItemHeightExpanded = mItemHeightNormal  * 2;
    }

    /**
     * @deprecated This now does nothing.
     */
    @Deprecated
    public void setWindowType(int type) {
        // mWindowParams.type = type;
    }

    /**
     * Set the index of the item which cannot be dragged.
     */
    public void setFixedItem(int index) {
        mFixedItem = index;
    }
}
