package org.geometerplus.zlibrary.core.application;

import org.geometerplus.zlibrary.core.view.ZLViewWidget;

public interface ZLApplicationWindow {
	void setWindowTitle(String title);
	void showErrorMessage(String resourceKey);
	void showErrorMessage(String resourceKey, String parameter);
	void runWithMessage(String key, Runnable runnable, Runnable postAction);
	void processException(Exception e);

	void refresh();

	ZLViewWidget getViewWidget();

	void close();

	int getBatteryLevel();
}
