package com.artifex.mupdfdemo;
import java.io.File;
import java.util.ArrayList;

import com.biitbook.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class MuPDFCore
{
	/* load our native library */
	static {
		System.loadLibrary("mupdf");
	}

	/* Readable members */
	private int numPages = -1;	
	private float pageWidth;
	private int pageNum = -1;
	private float pageHeight;
	private long globals;
	private byte fileBuffer[];
	private String file_format;
	private String mFileName;
	private int displayPages = 1;

	/* The native functions */
	private native long openFile(String filename);
	private native long openBuffer();
	private native String fileFormatInternal();
	private native int countPagesInternal();
	private native void gotoPageInternal(int localActionPageNum);
	private native float getPageWidth();
	private native float getPageHeight();
	private native void drawPage(Bitmap bitmap,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH);
	private native void updatePageInternal(Bitmap bitmap,
			int page,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH);
	private native RectF[] searchPage(String text);
	private native TextChar[][][][] text();
	private native byte[] textAsHtml();
	private native void addMarkupAnnotationInternal(PointF[] quadPoints, int type);
	private native void addInkAnnotationInternal(PointF[][] arcs);
	private native void deleteAnnotationInternal(int annot_index);
	private native int passClickEventInternal(int page, float x, float y);
	private native void setFocusedWidgetChoiceSelectedInternal(String [] selected);
	private native String [] getFocusedWidgetChoiceSelected();
	private native String [] getFocusedWidgetChoiceOptions();
	private native int setFocusedWidgetTextInternal(String text);
	private native String getFocusedWidgetTextInternal();
	private native int getFocusedWidgetTypeInternal();
	private native LinkInfo [] getPageLinksInternal(int page);
	private native RectF[] getWidgetAreasInternal(int page);
	private native Annotation[] getAnnotationsInternal(int page);
	private native OutlineItem [] getOutlineInternal();
	private native boolean hasOutlineInternal();
	private native boolean needsPasswordInternal();
	private native boolean authenticatePasswordInternal(String password);
	private native MuPDFAlertInternal waitForAlertInternal();
	private native void replyToAlertInternal(MuPDFAlertInternal alert);
	private native void startAlertsInternal();
	private native void stopAlertsInternal();
	private native void destroying();
	private native boolean hasChangesInternal();
	private native void saveInternal();

	public static native boolean javascriptSupported();

	public MuPDFCore(Context context, String filename) throws Exception
	{
		globals = openFile(filename);
		if (globals == 0)
		{
			throw new Exception(String.format(context.getString(R.string.cannot_open_file_Path), filename));
		}
		file_format = fileFormatInternal();
	}

	public MuPDFCore(Context context, byte buffer[]) throws Exception
	{
		fileBuffer = buffer;
		globals = openBuffer();
		if (globals == 0)
		{
			throw new Exception(context.getString(R.string.cannot_open_buffer));
		}
		file_format = fileFormatInternal();
	}
	
	public String getFileName() {
		return mFileName;
	}

	public String getFileDirectory() {
		return (new File(getFileName())).getParent();
	}

	public  int countPages()
	{
		if (numPages < 0)
			numPages = countPagesSynchronized();

		return numPages;
	}

	public int getDisplayPages() {
		return displayPages;
	}
	
	public String fileFormat()
	{
		return file_format;
	}

	private synchronized int countPagesSynchronized() {
		return countPagesInternal();
	}

	/* Shim function */
	public void gotoPage(int page) {
		
		if (page > numPages - 1)
			page = numPages - 1;
		else if (page < 0)
			page = 0;
		gotoPageInternal(page);
		this.pageNum = page;
		this.pageWidth = getPageWidth();
		this.pageHeight = getPageHeight();
	}
	
	public synchronized PointF getPageSize(int page) {
		gotoPage(page);
		return new PointF(pageWidth, pageHeight);
	}

	public MuPDFAlert waitForAlert() {
		MuPDFAlertInternal alert = waitForAlertInternal();
		return alert != null ? alert.toAlert() : null;
	}

	public void replyToAlert(MuPDFAlert alert) {
		replyToAlertInternal(new MuPDFAlertInternal(alert));
	}

	public void stopAlerts() {
		stopAlertsInternal();
	}

	public void startAlerts() {
		startAlertsInternal();
	}

	public synchronized void onDestroy() {
		destroying();
		globals = 0;
	}

	/**public synchronized Bitmap drawPage(int page,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH) {
		gotoPage(page);
		Bitmap bm = Bitmap.createBitmap(patchW, patchH, Config.ARGB_8888);
		drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH);
		return bm;
	}*/
	
	private Config getBitmapConfig(){
		return Config.ARGB_8888;
	} 
	
	public synchronized Bitmap drawPage(final int page,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH) {
		
		Canvas canvas = null;
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(patchW, patchH, Config.ARGB_8888);
			canvas = new Canvas(bitmap);
			canvas.drawColor(Color.TRANSPARENT);
			// If we have only one page (portrait), or if is the first, we show only one page (centered).
			if (displayPages == 1 || page==0) {
				gotoPage(page);
				drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
				return bitmap;
				// If we are on two pages mode (landscape), and at the last page, we show only one page (centered).
			} else if (displayPages==2 && page == numPages/2) {
				gotoPage(page*2+1); // need to multiply per 2, because page counting is being divided by 2 (landscape mode)
				drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
				return bitmap;
			} else {
				final int drawPage = (page == 0) ? 0 : page * 2 - 1;
				int leftPageW = pageW / 2;
				int rightPageW = pageW - leftPageW;

				// If patch overlaps both bitmaps (left and right) - return the
				// width of overlapping left bitpam part of the patch
				// or return full patch width if it's fully inside left bitmap
				int leftBmWidth = Math.min(leftPageW, leftPageW - patchX);

				// set left Bitmap width to zero if patch is fully overlay right
				// Bitmap
				leftBmWidth = (leftBmWidth < 0) ? 0 : leftBmWidth;

				// set the right part of the patch width, as a rest of the patch
				int rightBmWidth = patchW - leftBmWidth;

				if (drawPage == numPages - 1) {
					// draw only left page
					canvas.drawColor(Color.BLACK);
					if (leftBmWidth > 0) {
						Bitmap leftBm = Bitmap.createBitmap(leftBmWidth, patchH,
								getBitmapConfig());
						gotoPage(drawPage);
						drawPage(leftBm, leftPageW, pageH,
								(leftBmWidth == 0) ? patchX - leftPageW : 0,
								patchY, leftBmWidth, patchH);
						Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
						canvas.drawBitmap(leftBm, 0, 0, paint);
						leftBm.recycle();
					}
				} else if (drawPage == 0) {
					// draw only right page
					canvas.drawColor(Color.BLACK);
					if (rightBmWidth > 0) {
						Bitmap rightBm = Bitmap.createBitmap(rightBmWidth, patchH,
								getBitmapConfig());
						gotoPage(drawPage);
						drawPage(rightBm, rightPageW, pageH,
								(leftBmWidth == 0) ? patchX - leftPageW : 0,
								patchY, rightBmWidth, patchH);
						Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
						canvas.drawBitmap(rightBm, leftBmWidth, 0, paint);
						rightBm.recycle();
					}
				} else {
					// Need to draw two pages one by one: left and right
					Log.d("bitmap width", "" + bitmap.getWidth());
//					canvas.drawColor(Color.BLACK);
					Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
					if (leftBmWidth > 0) {
						Bitmap leftBm = Bitmap.createBitmap(leftBmWidth,
								patchH, getBitmapConfig());
						gotoPage(drawPage);
						drawPage(leftBm, leftPageW, pageH, patchX, patchY,
								leftBmWidth, patchH);
						canvas.drawBitmap(leftBm, 0, 0, paint);
						leftBm.recycle();
					}
					if (rightBmWidth > 0) {
						Bitmap rightBm = Bitmap.createBitmap(rightBmWidth,
								patchH, getBitmapConfig());
						gotoPage(drawPage+1);
						drawPage(rightBm, rightPageW, pageH,
								(leftBmWidth == 0) ? patchX - leftPageW : 0,
								patchY, rightBmWidth, patchH);

						canvas.drawBitmap(rightBm, (float) leftBmWidth, 0,
								paint);
						rightBm.recycle();
					}

				}
				return bitmap;
			}
		} catch (OutOfMemoryError e) {
			if(canvas != null) canvas.drawColor(Color.TRANSPARENT);
			return bitmap;
		}
	}

	public synchronized Bitmap updatePage(BitmapHolder h, int page,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH) {
		Bitmap bm = null;
		Bitmap old_bm = h.getBm();

		if (old_bm == null)
			return null;

		bm = old_bm.copy(Bitmap.Config.ARGB_8888, false);
		old_bm = null;

		updatePageInternal(bm, page, pageW, pageH, patchX, patchY, patchW, patchH);
		return bm;
	}

	public synchronized PassClickResult passClickEvent(int page, float x, float y) {
		boolean changed = passClickEventInternal(page, x, y) != 0;

		switch (WidgetType.values()[getFocusedWidgetTypeInternal()])
		{
		case TEXT:
			return new PassClickResultText(changed, getFocusedWidgetTextInternal());
		case LISTBOX:
		case COMBOBOX:
			return new PassClickResultChoice(changed, getFocusedWidgetChoiceOptions(), getFocusedWidgetChoiceSelected());
		default:
			return new PassClickResult(changed);
		}

	}

	public synchronized boolean setFocusedWidgetText(int page, String text) {
		boolean success;
		gotoPage(page);
		success = setFocusedWidgetTextInternal(text) != 0 ? true : false;

		return success;
	}

	public synchronized void setFocusedWidgetChoiceSelected(String [] selected) {
		setFocusedWidgetChoiceSelectedInternal(selected);
	}

	public synchronized LinkInfo [] getPageLinks(int page) {
		return getPageLinksInternal(page);
	}

	public synchronized RectF [] getWidgetAreas(int page) {
		return getWidgetAreasInternal(page);
	}

	public synchronized Annotation [] getAnnoations(int page) {
		return getAnnotationsInternal(page);
	}

	public synchronized RectF [] searchPage(int page, String text) {
		gotoPage(page);
		return searchPage(text);
	}

	public synchronized byte[] html(int page) {
		gotoPage(page);
		return textAsHtml();
	}

	public synchronized TextWord [][] textLines(int page) {
		gotoPage(page);
		TextChar[][][][] chars = text();

		// The text of the page held in a hierarchy (blocks, lines, spans).
		// Currently we don't need to distinguish the blocks level or
		// the spans, and we need to collect the text into words.
		ArrayList<TextWord[]> lns = new ArrayList<TextWord[]>();

		for (TextChar[][][] bl: chars) {
			for (TextChar[][] ln: bl) {
				ArrayList<TextWord> wds = new ArrayList<TextWord>();
				TextWord wd = new TextWord();

				for (TextChar[] sp: ln) {
					for (TextChar tc: sp) {
						if (tc.c != ' ') {
							wd.Add(tc);
						} else if (wd.w.length() > 0) {
							wds.add(wd);
							wd = new TextWord();
						}
					}
				}

				if (wd.w.length() > 0)
					wds.add(wd);

				if (wds.size() > 0)
					lns.add(wds.toArray(new TextWord[wds.size()]));
			}
		}

		return lns.toArray(new TextWord[lns.size()][]);
	}

	public synchronized void addMarkupAnnotation(int page, PointF[] quadPoints, Annotation.Type type) {
		gotoPage(page);
		addMarkupAnnotationInternal(quadPoints, type.ordinal());
	}

	public synchronized void addInkAnnotation(int page, PointF[][] arcs) {
		gotoPage(page);
		addInkAnnotationInternal(arcs);
	}

	public synchronized void deleteAnnotation(int page, int annot_index) {
		gotoPage(page);
		deleteAnnotationInternal(annot_index);
	}

	public synchronized boolean hasOutline() {
		return hasOutlineInternal();
	}

	public synchronized OutlineItem [] getOutline() {
		return getOutlineInternal();
	}

	public synchronized boolean needsPassword() {
		return needsPasswordInternal();
	}

	public synchronized boolean authenticatePassword(String password) {
		return authenticatePasswordInternal(password);
	}

	public synchronized boolean hasChanges() {
		return hasChangesInternal();
	}

	public synchronized void save() {
		saveInternal();
	}
	
	public int countSinglePages() {
		// TODO Auto-generated method stub
		return numPages;
	}
	
	public synchronized PointF getSinglePageSize(int page) {
		gotoPage(page);
		return new PointF(pageWidth, pageHeight);
	}
	
	public synchronized void drawPageSynchrinized(int page, Bitmap bitmap, int pageW,
			int pageH, int patchX, int patchY, int patchW, int patchH) {
		gotoPage(page);		
		drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
	}
	
	public synchronized void drawSinglePage(int page, Bitmap bitmap, int pageW,
			int pageH) {
				drawPageSynchrinized(page, bitmap, pageW, pageH, 0, 0, pageW, pageH);
	}
}