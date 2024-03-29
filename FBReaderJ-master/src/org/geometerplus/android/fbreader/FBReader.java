package org.geometerplus.android.fbreader;

import com.biitbook.android.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;

import lmquan1990.biitbook.DBAdapter;
import lmquan1990.biitbook.GolbalFunction;
import lmquan1990.biitbook.LibsActivity;
import lmquan1990.biitbook.UserDB;

import android.app.Activity;
import android.app.SearchManager;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.*;
import android.widget.RelativeLayout;

import org.geometerplus.zlibrary.core.application.ZLApplicationWindow;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.Config;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;

import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.ui.android.error.ErrorKeys;
import org.geometerplus.zlibrary.ui.android.library.*;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import org.geometerplus.fbreader.book.*;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.fbreader.*;
import org.geometerplus.fbreader.fbreader.options.CancelMenuHelper;
import org.geometerplus.fbreader.tips.TipsManager;

import org.geometerplus.android.fbreader.api.*;
import org.geometerplus.android.fbreader.httpd.DataService;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.fbreader.tips.TipsActivity;

import org.geometerplus.android.util.*;

public final class FBReader extends Activity implements ZLApplicationWindow {
	static final int ACTION_BAR_COLOR = Color.DKGRAY;

	public static final int REQUEST_PREFERENCES = 1;
	public static final int REQUEST_CANCEL_MENU = 2;

	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;
	UserDB userdb = new UserDB(this);
	DBAdapter db = new DBAdapter(this);
	GolbalFunction golbal = new GolbalFunction();
	public static Activity fa;
	public String pathbook = "";
	public String idbook = "";
	public String tmpbook = "";
	public String extbook = "";
	public boolean isOpen;

	public static void openBookActivity(Context context, Book book, Bookmark bookmark) {
		final Intent intent = new Intent(context, FBReader.class)
			.setAction(FBReaderIntents.Action.VIEW)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		FBReaderIntents.putBookExtra(intent, book);
		FBReaderIntents.putBookmarkExtra(intent, bookmark);
		context.startActivity(intent);
	}

	private static ZLAndroidLibrary getZLibrary() {
		return (ZLAndroidLibrary)ZLAndroidLibrary.Instance();
	}

	private FBReaderApp myFBReaderApp;
	private volatile Book myBook;

	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;

	private volatile boolean myShowStatusBarFlag;
	private String myMenuLanguage;

	final DataService.Connection DataConnection = new DataService.Connection();

	private static final String PLUGIN_ACTION_PREFIX = "___";
	private final List<PluginApi.ActionInfo> myPluginActions =
		new LinkedList<PluginApi.ActionInfo>();
	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(true).<PluginApi.ActionInfo>getParcelableArrayList(PluginApi.PluginInfo.KEY);
			if (actions != null) {
				synchronized (myPluginActions) {
					int index = 0;
					while (index < myPluginActions.size()) {
						myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions) {
						myFBReaderApp.addAction(
							PLUGIN_ACTION_PREFIX + index++,
							new RunPluginAction(FBReader.this, myFBReaderApp, info.getId())
						);
					}
				}
			}
		}
	};

	private synchronized void openBook(Intent intent, Runnable action, boolean force) {		
		String onoff = golbal.loadSavedPreferences("KEY_ONOFF");
		idbook = golbal.loadSavedPreferences("KEY_IDBOOK");
		golbal.savePreferences("KEY_PASSBOOK", returnPass(idbook));
		golbal.savePreferences("KEY_FIRSTLOAD", "1");	
		if (!force && myBook != null) {
			return;
		}
		final Bookmark bookmark = FBReaderIntents.getBookmarkExtra(intent);						
		if(onoff.equals("1")){
			String paththe = golbal.loadSavedPreferences("KEY_PATHBOOK");			
			extbook = golbal.loadSavedPreferences("KEY_EXTBOOK");
			tmpbook = golbal.loadSavedPreferences("KEY_PATHTMP");
			File tmpSD = new File(Environment.getDataDirectory() + "/data/com.biitbook.android/tmps/");
	    	if(!tmpSD.exists()){
	    		tmpSD.mkdirs();            	
	        }
			File from = new File(paththe,idbook);
	   		File to = new File(tmpbook,idbook+extbook);
	   		try {
				copy(from,to);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myBook = createBookForFile(ZLFile.createFileByPath(tmpbook+idbook+extbook));
			golbal.savePreferences("KEY_ONOFF", "2");				
		}if(onoff.equals("0")){
			pathbook = golbal.loadSavedPreferences("KEY_PATHOFFBOOK");
			myBook = createBookForFile(ZLFile.createFileByPath(pathbook));
			golbal.savePreferences("KEY_ONOFF", "2");
		}if(onoff.equals("2")){
			//////////////
		}
		
		if (myBook == null) {
			final Uri data = intent.getData();
			if (data != null) {				
				myBook = createBookForFile(ZLFile.createFileByPath(data.getPath()));				
			}
		}
		myFBReaderApp.openBook(myBook, bookmark, action);								
    	try{
    		String tit = returnBook(idbook);
    		if(!tit.equals("")){
				golbal.savePreferences("KEY_TITLE", tit);
    		}else
    			golbal.savePreferences("KEY_TITLE", myBook.getTitle());
    		
    		String author = returnAuthor(idbook);
    		if(!author.equals("")){
				golbal.savePreferences("KEY_AUTHOR", author);
    		}else
    			golbal.savePreferences("KEY_AUTHOR", getResources().getString(R.string.updating));		    		
    	}catch(Exception ex){
    		golbal.savePreferences("KEY_TITLE", getResources().getString(R.string.updating));
    		golbal.savePreferences("KEY_AUTHOR", getResources().getString(R.string.updating));
    	}	
	}
	
	public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
public boolean isOnline() {
    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        return true;
    }
    return false;
}
private String returnBook(String id){
	
    db.open();
    String path = "";
    Cursor c = db.getTitle(id);
    if (c.moveToFirst())
        path = returnPath(c);
    c.close();
    db.close();
    return path;
}        
public String returnPath(Cursor c){
	return c.getString(2);	                
}

 private String returnAuthor(String id){
    	
        db.open();
        String path = "";
        Cursor c = db.getTitle(id);
        if (c.moveToFirst())
            path = returnAuthor(c);
        c.close();
        db.close();
        return path;
	}        
	public String returnAuthor(Cursor c){
		return c.getString(3);	                
	}

private String returnPass(String id){
    db.open();
    String path = "";
    Cursor c = db.getTitle(id);
    if (c.moveToFirst())
        path = returnPass(c);
    c.close();
    db.close();
    return path;
}        
public String returnPass(Cursor c){
	return c.getString(11);	                
}

	private Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myFBReaderApp.Collection.getBookByFile(file);
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myFBReaderApp.Collection.getBookByFile(child);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	private Runnable getPostponedInitAction() {
		return new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						//new TipRunner().start();/////////
						DictionaryUtil.init(FBReader.this, null);
						final Intent intent = getIntent();
						if (intent != null && FBReaderIntents.Action.PLUGIN.equals(intent.getAction())) {
							new RunPluginAction(FBReader.this, myFBReaderApp, intent.getData()).run();
						}
					}
				});
			}
		};
	}

	@Override
	protected void onCreate(Bundle icicle) {
		fa = this;
		super.onCreate(icicle);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));

		bindService(
			new Intent(this, DataService.class),
			DataConnection,
			DataService.BIND_AUTO_CREATE
		);

		final Config config = Config.Instance();
		config.runOnStart(new Runnable() {
			public void run() {
				config.requestAllValuesForGroup("Options");
				config.requestAllValuesForGroup("Style");
				config.requestAllValuesForGroup("LookNFeel");
				config.requestAllValuesForGroup("Fonts");
				config.requestAllValuesForGroup("Colors");
				config.requestAllValuesForGroup("Files");
			}
		});

		final ZLAndroidLibrary zlibrary = getZLibrary();
		myShowStatusBarFlag = zlibrary.ShowStatusBarOption.getValue();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		myRootView = (RelativeLayout)findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget)findViewById(R.id.main_view);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		zlibrary.setActivity(this);

		myFBReaderApp = (FBReaderApp)FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		myBook = null;

		myFBReaderApp.setWindow(this);
		myFBReaderApp.initWindow();

		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			myShowStatusBarFlag ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN
		);

		if (myFBReaderApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(NavigationPopup.ID) == null) {
			new NavigationPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myFBReaderApp);
		}

		myFBReaderApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOK_INFO, new ShowBookInfoAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOKMARKS, new ShowBookmarksAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_NETWORK_LIBRARY, new ShowNetworkLibraryAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_MENU, new ShowMenuAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_NAVIGATION, new ShowNavigationAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SEARCH, new SearchAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHARE_BOOK, new ShareBookAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SELECTION_SHOW_PANEL, new SelectionShowPanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_HIDE_PANEL, new SelectionHidePanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD, new SelectionCopyAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_SHARE, new SelectionShareAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_TRANSLATE, new SelectionTranslateAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_BOOKMARK, new SelectionBookmarkAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.PROCESS_HYPERLINK, new ProcessHyperlinkAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.OPEN_VIDEO, new OpenVideoAction(this, myFBReaderApp));

		//myFBReaderApp.addAction(ActionCode.SHOW_CANCEL_MENU, new ShowCancelMenuAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_SYSTEM));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_SENSOR));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
		if (ZLibrary.Instance().supportsAllOrientations()) {
			myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
			myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
		}
		myFBReaderApp.addAction(ActionCode.OPEN_WEB_HELP, new OpenWebHelpAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.INSTALL_PLUGINS, new InstallPluginsAction(this, myFBReaderApp));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		setStatusBarVisibility(true);
		setupMenu(menu);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		setStatusBarVisibility(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		setStatusBarVisibility(false);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		final String action = intent.getAction();
		final Uri data = intent.getData();

		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action)
				   && data != null && "fbreader-action".equals(data.getScheme())) {
			myFBReaderApp.runAction(data.getEncodedSchemeSpecificPart(), data.getFragment());
		} else if (Intent.ACTION_VIEW.equals(action) || FBReaderIntents.Action.VIEW.equals(action)) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					openBook(intent, null, true);
				}
			});
		} else if (FBReaderIntents.Action.PLUGIN.equals(action)) {
			new RunPluginAction(this, myFBReaderApp, data).run();
		} else if (Intent.ACTION_SEARCH.equals(action)) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable() {
				public void run() {
					final TextSearchPopup popup = (TextSearchPopup)myFBReaderApp.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					myFBReaderApp.MiscOptions.TextSearchPattern.setValue(pattern);
					if (myFBReaderApp.getTextView().search(pattern, true, false, false, false) != 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								myFBReaderApp.showPopup(popup.getId());
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								UIUtil.showErrorMessage(FBReader.this, "textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						openBook(getIntent(), getPostponedInitAction(), false);
						myFBReaderApp.getViewWidget().repaint();
					}
				}.start();

				myFBReaderApp.getViewWidget().repaint();
			}
		});

		initPluginActions();

		final ZLAndroidLibrary zlibrary = getZLibrary();

		Config.Instance().runOnStart(new Runnable() {
			public void run() {
				final boolean showStatusBar = zlibrary.ShowStatusBarOption.getValue();
				if (showStatusBar != myShowStatusBarFlag) {
					finish();
					startActivity(new Intent(FBReader.this, FBReader.class));
				}
				zlibrary.ShowStatusBarOption.saveSpecialValue();
				myFBReaderApp.ViewOptions.ColorProfileName.saveSpecialValue();
				SetScreenOrientationAction.setOrientation(FBReader.this, zlibrary.getOrientationOption().getValue());
			}
		});

		((PopupPanel)myFBReaderApp.getPopupById(TextSearchPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myFBReaderApp.getPopupById(NavigationPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myFBReaderApp.getPopupById(SelectionPopup.ID)).setPanelInfo(this, myRootView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		switchWakeLock(hasFocus &&
			getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() <
			myFBReaderApp.getBatteryLevel()
		);
	}

	private void initPluginActions() {
		synchronized (myPluginActions) {
			int index = 0;
			while (index < myPluginActions.size()) {
				myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
			}
			myPluginActions.clear();
		}

		sendOrderedBroadcast(
			new Intent(PluginApi.ACTION_REGISTER),
			null,
			myPluginInfoReceiver,
			null,
			RESULT_OK,
			null,
			null
		);
	}

	private class TipRunner extends Thread {
		TipRunner() {
			setPriority(MIN_PRIORITY);
		}

		public void run() {
			final TipsManager manager = TipsManager.Instance();
			switch (manager.requiredAction()) {
				case Initialize:
					startActivity(new Intent(
						TipsActivity.INITIALIZE_ACTION, null, FBReader.this, TipsActivity.class
					));
					break;
				case Show:
					startActivity(new Intent(
						TipsActivity.SHOW_TIP_ACTION, null, FBReader.this, TipsActivity.class
					));
					break;
				case Download:
					manager.startDownloading();
					break;
				case None:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		myStartTimer = true;
		Config.Instance().runOnStart(new Runnable() {
			public void run() {
				final int brightnessLevel =
					getZLibrary().ScreenBrightnessLevelOption.getValue();
				if (brightnessLevel != 0) {
					setScreenBrightness(brightnessLevel);
				} else {
					setScreenBrightnessAuto();
				}
				if (getZLibrary().DisableButtonLightsOption.getValue()) {
					setButtonLight(false);
				}

				getCollection().bindToService(FBReader.this, new Runnable() {
					public void run() {
						final BookModel model = myFBReaderApp.Model;
						if (model == null || model.Book == null) {
							return;
						}
						onPreferencesUpdate(myFBReaderApp.Collection.getBookById(model.Book.getId()));
					}
				});
			}
		});

		registerReceiver(myBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		PopupPanel.restoreVisibilities(myFBReaderApp);
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_OPENED);
	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means myBatteryInfoReceiver was not registered
		}
		myFBReaderApp.stopTimer();
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(true);
		}
		myFBReaderApp.onWindowClosing();
		super.onPause();
	}

	@Override
	protected void onStop() {
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		unbindService(DataConnection);
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		myFBReaderApp.onWindowClosing();
		super.onLowMemory();
	}

	@Override
	public boolean onSearchRequested() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		myFBReaderApp.hideActivePopup();
		if (DeviceType.Instance().hasStandardSearchDialog()) {
			final SearchManager manager = (SearchManager)getSystemService(SEARCH_SERVICE);
			manager.setOnCancelListener(new SearchManager.OnCancelListener() {
				public void onCancel() {
					if (popup != null) {
						myFBReaderApp.showPopup(popup.getId());
					}
					manager.setOnCancelListener(null);
				}
			});
			startSearch(myFBReaderApp.MiscOptions.TextSearchPattern.getValue(), true, null, false);
		} else {
			SearchDialogUtil.showDialog(
				this, FBReader.class, myFBReaderApp.MiscOptions.TextSearchPattern.getValue(), new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface di) {
						if (popup != null) {
							myFBReaderApp.showPopup(popup.getId());
						}
					}
				}
			);
		}
		return true;
	}

	public void showSelectionPanel() {
		final ZLTextView view = myFBReaderApp.getTextView();
		((SelectionPopup)myFBReaderApp.getPopupById(SelectionPopup.ID))
			.move(view.getSelectionStartY(), view.getSelectionEndY());
		myFBReaderApp.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		if (popup != null && popup.getId() == SelectionPopup.ID) {
			myFBReaderApp.hideActivePopup();
		}
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myFBReaderApp.onBookUpdated(book);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_PREFERENCES:
				if (resultCode != RESULT_DO_NOTHING && data != null) {
					final Book book = FBReaderIntents.getBookExtra(data);
					if (book != null) {
						getCollection().bindToService(this, new Runnable() {
							public void run() {
								onPreferencesUpdate(book);
							}
						});
					}
				}
				break;
			case REQUEST_CANCEL_MENU:
				runCancelAction(data);
				break;
		}
	}

	private void runCancelAction(Intent intent) {
		final CancelMenuHelper.ActionType type;
		try {
			type = CancelMenuHelper.ActionType.valueOf(
				intent.getStringExtra(FBReaderIntents.Key.TYPE)
			);
		} catch (Exception e) {
			// invalid (or null) type value
			return;
		}
		Bookmark bookmark = null;
		if (type == CancelMenuHelper.ActionType.returnTo) {
			bookmark = FBReaderIntents.getBookmarkExtra(intent);
			if (bookmark == null) {
				return;
			}
		}
		finish();
		this.startActivity(new Intent(this, LibsActivity.class));
		//myFBReaderApp.runCancelAction(type, bookmark);
	}

	public void navigate() {
		((NavigationPopup)myFBReaderApp.getPopupById(NavigationPopup.ID)).runNavigation();
	}

	private Menu addSubmenu(Menu menu, String id) {
		return menu.addSubMenu(ZLResource.resource("menu").getResource(id).getValue());
	}

	private void addMenuItem(Menu menu, String actionId, Integer iconId, String name) {
		if (name == null) {
			name = ZLResource.resource("menu").getResource(actionId).getValue();
		}
		final MenuItem menuItem = menu.add(name);
		if (iconId != null) {
			menuItem.setIcon(iconId);
		}
		menuItem.setOnMenuItemClickListener(myMenuListener);
		myMenuItemMap.put(menuItem, actionId);
	}

	private void addMenuItem(Menu menu, String actionId, String name) {
		addMenuItem(menu, actionId, null, name);
	}

	private void addMenuItem(Menu menu, String actionId, int iconId) {
		addMenuItem(menu, actionId, iconId, null);
	}

	private void addMenuItem(Menu menu, String actionId) {
		addMenuItem(menu, actionId, null, null);
	}

	private void fillMenu(Menu menu, List<MenuNode> nodes) {
		for (MenuNode n : nodes) {
			if (n instanceof MenuNode.Item) {
				final Integer iconId = ((MenuNode.Item)n).IconId;
				if (iconId != null) {
					addMenuItem(menu, n.Code, iconId);
				} else {
					addMenuItem(menu, n.Code);
				}
			} else /* if (n instanceof MenuNode.Submenu) */ {
				final Menu submenu = addSubmenu(menu, n.Code);
				fillMenu(submenu, ((MenuNode.Submenu)n).Children);
			}
		}
	}

	private void setupMenu(Menu menu) {
		final String menuLanguage = ZLResource.getLanguageOption().getValue();
		if (menuLanguage.equals(myMenuLanguage)) {
			return;
		}
		myMenuLanguage = menuLanguage;

		menu.clear();
		fillMenu(menu, MenuData.topLevelNodes());
		synchronized (myPluginActions) {
			int index = 0;
			for (PluginApi.ActionInfo info : myPluginActions) {
				if (info instanceof PluginApi.MenuActionInfo) {
					addMenuItem(
						menu,
						PLUGIN_ACTION_PREFIX + index++,
						((PluginApi.MenuActionInfo)info).MenuItemName
					);
				}
			}
		}

		refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		setupMenu(menu);

		return true;
	}

	private void setStatusBarVisibility(boolean visible) {
		if (DeviceType.Instance() != DeviceType.KINDLE_FIRE_1ST_GENERATION && !myShowStatusBarFlag) {
			if (visible) {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}
	}

	@Override
	public void onBackPressed() {
		File file = new File(tmpbook+idbook+extbook);
		if(file.exists()){
			@SuppressWarnings("unused")
			boolean deleted = file.delete();
		}
		finish();
		this.startActivity(new Intent(this, LibsActivity.class));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
            case KeyEvent.KEYCODE_MENU:
            	((NavigationPopup)myFBReaderApp.getPopupById(NavigationPopup.ID)).runNavigation();
            	break;
            case KeyEvent.KEYCODE_BACK:
            	if(golbal.loadSavedPreferences("KEY_NAV").equals("0"))
            		((NavigationPopup)myFBReaderApp.getPopupById(NavigationPopup.ID)).runNavigation();
            	else{            		
	        		File file = new File(tmpbook+idbook+extbook);
	        		if(file.exists()){
	        			@SuppressWarnings("unused")
	        			boolean deleted = file.delete();
	        		}
	        		finish();
	        		this.startActivity(new Intent(this, LibsActivity.class));
            	}
            	break;
            }
            return true;
        }        
		return (myMainView != null && myMainView.onKeyDown(keyCode, event)) || super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyUp(keyCode, event)) || super.onKeyUp(keyCode, event);
	}

	private void setButtonLight(boolean enabled) {
		try {
			final WindowManager.LayoutParams attrs = getWindow().getAttributes();
			final Class<?> cls = attrs.getClass();
			final Field fld = cls.getField("buttonBrightness");
			if (fld != null && "float".equals(fld.getType().toString())) {
				fld.setFloat(attrs, enabled ? -1.0f : 0.0f);
				getWindow().setAttributes(attrs);
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;
	private boolean myStartTimer;

	@SuppressWarnings("deprecation")
	public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock =
						((PowerManager)getSystemService(POWER_SERVICE))
							.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "FBReader");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			myFBReaderApp.startTimer();
			myStartTimer = false;
		}
	}

	private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}

	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			setBatteryLevel(level);
			switchWakeLock(
				hasWindowFocus() &&
				getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() < level
			);
		}
	};

	private void setScreenBrightnessAuto() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = -1.0f;
		getWindow().setAttributes(attrs);
	}

	public void setScreenBrightness(int percent) {
		if (percent < 1) {
			percent = 1;
		} else if (percent > 100) {
			percent = 100;
		}
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = percent / 100.0f;
		getWindow().setAttributes(attrs);
		getZLibrary().ScreenBrightnessLevelOption.setValue(percent);
	}

	public int getScreenBrightness() {
		final int level = (int)(100 * getWindow().getAttributes().screenBrightness);
		return (level >= 0) ? level : 50;
	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow)myFBReaderApp.Collection;
	}

	// methods from ZLApplicationWindow interface
	@Override
	public void showErrorMessage(String key) {
		UIUtil.showErrorMessage(this, key);
	}

	@Override
	public void showErrorMessage(String key, String parameter) {
		UIUtil.showErrorMessage(this, key, parameter);
	}

	@Override
	public void runWithMessage(String key, Runnable action, Runnable postAction) {
		UIUtil.runWithMessage(this, key, action, postAction, false);
	}

	private int myBatteryLevel;
	@Override
	public int getBatteryLevel() {
		return myBatteryLevel;
	}
	private void setBatteryLevel(int percent) {
		myBatteryLevel = percent;
	}

	@Override
	public void close() {
		((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).finish();
	}

	@Override
	public ZLViewWidget getViewWidget() {
		return myMainView;
	}

	private final HashMap<MenuItem,String> myMenuItemMap = new HashMap<MenuItem,String>();

	private final MenuItem.OnMenuItemClickListener myMenuListener =
		new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				myFBReaderApp.runAction(myMenuItemMap.get(item));
				return true;
			}
		};

	@Override
	public void refresh() {
		runOnUiThread(new Runnable() {
			public void run() {
				for (Map.Entry<MenuItem,String> entry : myMenuItemMap.entrySet()) {
					final String actionId = entry.getValue();
					final MenuItem menuItem = entry.getKey();
					menuItem.setVisible(myFBReaderApp.isActionVisible(actionId) && myFBReaderApp.isActionEnabled(actionId));
					switch (myFBReaderApp.isActionChecked(actionId)) {
						case B3_TRUE:
							menuItem.setCheckable(true);
							menuItem.setChecked(true);
							break;
						case B3_FALSE:
							menuItem.setCheckable(true);
							menuItem.setChecked(false);
							break;
						case B3_UNDEFINED:
							menuItem.setCheckable(false);
							break;
					}
				}
			}
		});
	}

	@Override
	public void processException(Exception exception) {
		exception.printStackTrace();

		final Intent intent = new Intent(
			FBReaderIntents.Action.ERROR,
			new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
		);
		intent.setPackage(FBReaderIntents.DEFAULT_PACKAGE);
		intent.putExtra(ErrorKeys.MESSAGE, exception.getMessage());
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		intent.putExtra(ErrorKeys.STACKTRACE, stackTrace.toString());
		/*
		if (exception instanceof BookReadingException) {
			final ZLFile file = ((BookReadingException)exception).File;
			if (file != null) {
				intent.putExtra("file", file.getPath());
			}
		}
		*/
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// ignore
			e.printStackTrace();
		}
	}

	@Override
	public void setWindowTitle(final String title) {
		runOnUiThread(new Runnable() {
			public void run() {
				setTitle(title);
			}
		});
	}
}
