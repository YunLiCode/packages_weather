package com.lewa.weather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class PageIndicatorView extends View {
	private int mCurrentPage = -1;
	private int mTotalPage = 0;
	
	public PageIndicatorView(Context context) {
		super(context);
	}

	public PageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTotalPage(int nPageNum) {
		mTotalPage = nPageNum;
		if (mCurrentPage >= mTotalPage)
			mCurrentPage = mTotalPage - 1;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int nPageIndex) {
		if (nPageIndex < 0 || nPageIndex >= mTotalPage)
			return;

		if (mCurrentPage != nPageIndex) {
			mCurrentPage = nPageIndex;
			this.invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		Rect r = new Rect();
		this.getDrawingRect(r);
		int iconWidth = getHeight()/2;
		int iconHeight = getHeight()/2;
		int space = getHeight()/2;
		
		int x = (r.width() - (iconWidth * mTotalPage + space * (mTotalPage - 1))) / 2;
		int y = (r.height() - iconHeight) ;

		for (int i = 0; i < mTotalPage; i++) {
			paint.setColor(Color.parseColor("#3a3a3a"));
			if (i == mCurrentPage) {
				paint.setColor(Color.WHITE);
			}
			canvas.drawCircle(x, y, getHeight()/4, paint);
			x += iconWidth + space;

		}

	}

//	public void DrawImage(Canvas canvas, Bitmap mBitmap, int x, int y, int w, int h, int bx, int by) {
//		Rect src = new Rect();
//		Rect dst = new Rect();
//		src.left = bx;
//		src.top = by;
//		src.right = bx + w;
//		src.bottom = by + h;
//
//		dst.left = x;
//		dst.top = y;
//		dst.right = x + w;
//		dst.bottom = y + h;
//
//		// canvas.drawBitmap(mBitmap, src, dst, mPaint);
//		src = null;
//		dst = null;
//	}

}