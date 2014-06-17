package org.geometerplus.zlibrary.core.view;

import org.geometerplus.zlibrary.core.application.ZLApplication;

abstract public class ZLView {
	public final ZLApplication Application;
	private ZLPaintContext myViewContext = new DummyPaintContext();

	protected ZLView(ZLApplication application) {
		Application = application;
	}

	protected final void setContext(ZLPaintContext context) {
		myViewContext = context;
	}

	public final ZLPaintContext getContext() {
		return myViewContext;
	}

	public final int getContextWidth() {
		return myViewContext.getWidth();
	}

	public final int getContextHeight() {
		return myViewContext.getHeight();
	}

	abstract public interface FooterArea {
		int getHeight();
		void paint(ZLPaintContext context);
	}

	abstract public FooterArea getFooterArea();

	public static enum PageIndex {
		previous, current, next;

		public PageIndex getNext() {
			switch (this) {
				case previous:
					return current;
				case current:
					return next;
				default:
					return null;
			}
		}

		public PageIndex getPrevious() {
			switch (this) {
				case next:
					return current;
				case current:
					return previous;
				default:
					return null;
			}
		}
	};
	public static enum Direction {
		leftToRight(true), rightToLeft(true), up(false), down(false);

		public final boolean IsHorizontal;

		Direction(boolean isHorizontal) {
			IsHorizontal = isHorizontal;
		}
	};
	public static enum Animation {
		none, curl, slide, shift
	}

	public abstract Animation getAnimationType();

	abstract public void preparePage(ZLPaintContext context, PageIndex pageIndex);
	abstract public void paint(ZLPaintContext context, PageIndex pageIndex);
	abstract public void onScrollingFinished(PageIndex pageIndex);

	public boolean onFingerPress(int x, int y) {
		return false;
	}

	public boolean onFingerRelease(int x, int y) {
		return false;
	}

	public boolean onFingerMove(int x, int y) {
		return false;
	}

	public boolean onFingerLongPress(int x, int y) {
		return false;
	}

	public boolean onFingerReleaseAfterLongPress(int x, int y) {
		return false;
	}

	public boolean onFingerMoveAfterLongPress(int x, int y) {
		return false;
	}

	public boolean onFingerSingleTap(int x, int y) {
		return false;
	}

	public boolean onFingerDoubleTap(int x, int y) {
		return false;
	}

	public boolean isDoubleTapSupported() {
		return false;
	}

	public boolean onTrackballRotated(int diffX, int diffY) {
		return false;
	}

	public abstract boolean isScrollbarShown();
	public abstract int getScrollbarFullSize();
	public abstract int getScrollbarThumbPosition(PageIndex pageIndex);
	public abstract int getScrollbarThumbLength(PageIndex pageIndex);

	public abstract boolean canScroll(PageIndex index);
}
