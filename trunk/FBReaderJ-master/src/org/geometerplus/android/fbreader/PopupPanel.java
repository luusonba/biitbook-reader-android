package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.geometerplus.zlibrary.core.application.ZLApplication;

import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

abstract class PopupPanel extends ZLApplication.PopupPanel {
	public ZLTextWordCursor StartPosition;

	protected volatile PopupWindow myWindow;
	private volatile FBReader myActivity;
	private volatile RelativeLayout myRoot;

	PopupPanel(FBReaderApp fbReader) {
		super(fbReader);
	}

	protected final FBReaderApp getReader() {
		return (FBReaderApp)Application;
	}

	@Override
	protected void show_() {
		if (myActivity != null) {
			createControlPanel(myActivity, myRoot);
		}
		if (myWindow != null) {
			myWindow.show();
		}
	}

	@Override
	protected void hide_() {
		if (myWindow != null) {
			myWindow.hide();
		}
	}

	private final void removeWindow(Activity activity) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			ViewGroup root = (ViewGroup)myWindow.getParent();
			myWindow.hide();
			root.removeView(myWindow);
			myWindow = null;
		}
	}

	public static void removeAllWindows(ZLApplication application, Activity activity) {
		for (ZLApplication.PopupPanel popup : application.popupPanels()) {
			((PopupPanel)popup).removeWindow(activity);
		}
	}

	public static void restoreVisibilities(ZLApplication application) {
		final PopupPanel popup = (PopupPanel)application.getActivePopup();
		if (popup != null) {
			popup.show_();
		}
	}

	public final void initPosition() {
		if (StartPosition == null) {
			StartPosition = new ZLTextWordCursor(getReader().getTextView().getStartCursor());
		}
	}

	public final void storePosition() {
		if (StartPosition != null &&
			!StartPosition.equals(getReader().getTextView().getStartCursor())) {
			getReader().addInvisibleBookmark(StartPosition);
		}
	}

	public void setPanelInfo(FBReader activity, RelativeLayout root) {
		myActivity = activity;
		myRoot = root;
	}

	public abstract void createControlPanel(FBReader activity, RelativeLayout root);
}
