package org.geometerplus.android.fbreader;

import java.io.File;

import com.biitbook.android.R;

import lmquan1990.biitbook.BuyBookActivity;
import lmquan1990.biitbook.DBAdapter;
import lmquan1990.biitbook.GolbalFunction;
import lmquan1990.biitbook.LibsActivity;
import lmquan1990.biitbook.DiscoveryFragment.DownloadSDCard;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.style.ZLTextBaseStyle;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ViewOptions;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;

final class NavigationPopup extends PopupPanel {
	final static String ID = "NavigationPopup";
	
	private volatile boolean myIsInProgress;
	private FBReaderApp myReader;	
	int close = 0;	
	int pop = 0;   //0 an, 1 hien	  
	String pass = "";
	String bookid = "";
	GolbalFunction golbal = new GolbalFunction();
	private static final int ID_TOC = 1;
	private static final int ID_BOOKMARK = 0;	
	int page = 0;
	DBAdapter db = new DBAdapter(FBReaderApplication.getAppContext());
	NavigationPopup(FBReaderApp fbReader) {
		super(fbReader);
		myReader = fbReader;
	}	
		
	public void runNavigation() {
		//if (myWindow == null || myWindow.getVisibility() == View.GONE) {
		if(golbal.loadSavedPreferences("KEY_NAV").equals("1")){
			myIsInProgress = false;
			initPosition();
			Application.showPopup(ID);
			FBReader.fa.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			golbal.savePreferences("KEY_NAV", "0");
		}else{
			Application.hideActivePopup();
			FBReader.fa.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			golbal.savePreferences("KEY_NAV", "1");
		}
	}	
		
	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected void show_() {
		super.show_();
		if (myWindow != null) {
			setupNavigation(myWindow);			
		}
	}

	@Override
	protected void update() {
		if (!myIsInProgress && myWindow != null) {
			setupNavigation(myWindow);
		}
	}
		
	@Override
	public void createControlPanel(FBReader activity, RelativeLayout root) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			return;
		}

		myWindow = new PopupWindow(activity, root, PopupWindow.Location.Bottom);

		final View layout = activity.getLayoutInflater().inflate(R.layout.navigate, myWindow, false);
		
		final SeekBar seek_bright = (SeekBar)layout.findViewById(R.id.seek_popup_bright);
		final SeekBar seek_font = (SeekBar)layout.findViewById(R.id.seek_popup_font);
		final SeekBar slider = (SeekBar)layout.findViewById(R.id.book_position_slider);
		final TextView text = (TextView)layout.findViewById(R.id.book_position_text);
		final TextView txtTitle = (TextView)layout.findViewById(R.id.txtTitleNavbar);
		//final TextView txtAuthor = (TextView)layout.findViewById(R.id.txtActorNavbar);
		final RelativeLayout linear = (RelativeLayout)layout.findViewById(R.id.layoutTran);
		final ImageButton btnHome = (ImageButton)layout.findViewById(R.id.btnBiitbook);
		final ImageButton btnSearch = (ImageButton)layout.findViewById(R.id.btnSearch);
		final ImageButton btnCont = (ImageButton)layout.findViewById(R.id.btnCont);
		final ImageButton btnView = (ImageButton)layout.findViewById(R.id.btnView);
		final ImageButton btnFont = (ImageButton)layout.findViewById(R.id.btnFont);
		final Button btnWhite = (Button)layout.findViewById(R.id.btnWhite);
		final Button btnBlack = (Button)layout.findViewById(R.id.btnBlack);
		final Button btnSepia = (Button)layout.findViewById(R.id.btnSepia);
		final Button btnMono = (Button)layout.findViewById(R.id.btnMono);
		final Button btnSans = (Button)layout.findViewById(R.id.btnSans);
		final Button btnSerif = (Button)layout.findViewById(R.id.btnSerif);
		final ImageButton btnSet = (ImageButton)layout.findViewById(R.id.btnSet);		
		//final RelativeLayout layoutTop = (RelativeLayout)layout.findViewById(R.id.layoutTop);
		final LinearLayout layoutView = (LinearLayout)layout.findViewById(R.id.layout_popup_top);
		final LinearLayout layoutFont = (LinearLayout)layout.findViewById(R.id.layout_popup_font);
		final ImageButton btnClosePopupView = (ImageButton)layout.findViewById(R.id.close_popup_view);
		final ImageButton btnClosePopupFont= (ImageButton)layout.findViewById(R.id.close_popup_font);
				
		bookid = golbal.loadSavedPreferences("KEY_IDBOOK");
		pass = golbal.loadSavedPreferences("KEY_PASSBOOK");
		
		seek_bright.setProgress(ZLibrary.Instance().getScreenBrightness());
		seek_bright.incrementProgressBy(1);
		seek_bright.setMax(100);
		
		seek_bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub				
				ZLibrary.Instance().setScreenBrightness(progress);				
			}
		});
			
		final ViewOptions viewOptions = new ViewOptions();
		final ZLTextStyleCollection collection = viewOptions.getTextStyleCollection();
		final ZLTextBaseStyle baseStyle = collection.getBaseStyle();
		
		final ZLIntegerRangeOption option = baseStyle.FontSizeOption;
		final ZLStringOption optionFont =  baseStyle.FontFamilyOption;
		
		seek_font.setProgress(option.getValue());
		seek_font.incrementProgressBy(1);
		seek_font.setMax(72);
		
		seek_font.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub				
					option.setValue(progress);
					myReader.clearTextCaches();
					myReader.getViewWidget().repaint();		
			}
		});
		
		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {			
			private void gotoPage(int page) {
				final ZLTextView view = getReader().getTextView();
				if (page == 1) {
					view.gotoHome();
				} else {
					view.gotoPage(page);
				}
				storePosition();
				StartPosition = null;
				getReader().getViewWidget().reset();
				getReader().getViewWidget().repaint();		
				
			}			
					
			public void onStopTrackingTouch(SeekBar seekBar) {				
				myIsInProgress = false;				
				final int pagesNumber = seekBar.getMax() + 1;
				if(pass.equals("2")){
					if(page>pagesNumber/10){
						gotoPage(pagesNumber/10);							
						Intent i = new Intent(FBReaderApplication.getAppContext(),BuyBookActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						FBReaderApplication.getAppContext().startActivity(i);
					}else{
						gotoPage(page);
					}
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				myIsInProgress = true;
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					page = progress + 1;
					final int pagesNumber = seekBar.getMax() + 1;
					if(pass.equals("1")){
						if(golbal.loadSavedPreferences("KEY_FIRSTLOAD").equals("1")){
							myReader.runWithMessage("loadingPage", 
								new Runnable() {
							public void run() {
								gotoPage(page);
							}
							}
							, null);
						}else
							gotoPage(page);
						golbal.savePreferences("KEY_FIRSTLOAD", "0");
						text.setText(makeProgressText(page, pagesNumber));
					}else{						
						if(page<=pagesNumber/10){						
							if(golbal.loadSavedPreferences("KEY_FIRSTLOAD").equals("1")){
								myReader.runWithMessage("loadingPage", 
									new Runnable() {
								public void run() {
									gotoPage(page);
								}
								}
								, null);
							}else
								gotoPage(page);
							golbal.savePreferences("KEY_FIRSTLOAD", "0");
							text.setText(makeProgressText(page, pagesNumber));
						}
					}					
				}
			}
		});
		
		btnHome.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				FBReader.fa.onBackPressed();				
				Application.hideActivePopup();				
			}
		});
		
		btnSerif.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				optionFont.setValue("Droid Serif");
				myReader.clearTextCaches();
				myReader.getViewWidget().repaint();						
			}
		});
		
		btnSans.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				optionFont.setValue("Droid Sans");
				myReader.clearTextCaches();
				myReader.getViewWidget().repaint();
			}
		});
		
		btnMono.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				optionFont.setValue("Droid Mono");
				myReader.clearTextCaches();
				myReader.getViewWidget().repaint();
			}
		});
		
		/**btnCont.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myReader.runAction(ActionCode.SELECTION_BOOKMARK);
				Application.hideActivePopup();
			}
		});*/
		
		ActionItem tocItem 	= new ActionItem(ID_TOC, "Table of contents", null);
        ActionItem bookmarkItem = new ActionItem(ID_BOOKMARK, "Bookmark", null);        
        		
        final QuickAction quickContent = new QuickAction(layout.getContext(), QuickAction.VERTICAL);
        
        quickContent.addActionItem(tocItem);
        quickContent.addActionItem(bookmarkItem);
        
        quickContent.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				if (actionId == ID_TOC) {
					myReader.runAction(ActionCode.SHOW_TOC);					
					Application.hideActivePopup();
				} else if (actionId == ID_BOOKMARK) {
					myReader.runAction(ActionCode.SHOW_BOOKMARKS);
					Application.hideActivePopup();
				}
			}
		});
        
        btnCont.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {	
        		if(layoutView.getVisibility()==View.VISIBLE||layoutFont.getVisibility()==View.VISIBLE){
        			layoutView.setVisibility(View.GONE);
        			layoutFont.setVisibility(View.GONE);
        		}
				quickContent.show(v);
				quickContent.setAnimStyle(QuickAction.ANIM_REFLECT);
				close = 1;
			}
		});
						
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				myReader.runAction(ActionCode.SEARCH);				
				Application.hideActivePopup();
			} 
		});
		txtTitle.setText(golbal.loadSavedPreferences("KEY_TITLE"));
		//txtAuthor.setText(golbal.loadSavedPreferences("KEY_AUTHOR"));
		
		btnWhite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myReader.runAction(ActionCode.SWITCH_TO_DAY_PROFILE);
			}
		});
		
		btnSepia.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myReader.runAction(ActionCode.SWITCH_TO_SEPIA_PROFILE);
			}
		});

		btnBlack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myReader.runAction(ActionCode.SWITCH_TO_NIGHT_PROFILE);
			}
		});
			
		btnSet.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myReader.runAction(ActionCode.SHOW_PREFERENCES);
				Application.hideActivePopup();
			}
		});
		
		btnFont.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				if(layoutFont.getVisibility() == View.GONE){
					if(layoutView.getVisibility()== View.VISIBLE)
						layoutView.setVisibility(View.GONE);
					layoutFont.setVisibility(View.VISIBLE);
					pop = 1;
				}else{
					layoutFont.setVisibility(View.GONE);
					pop = 0;
				}		
			}
		});
		
        
        btnView.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {				
				if(layoutView.getVisibility() == View.GONE){
					if(layoutFont.getVisibility()== View.VISIBLE)
						layoutFont.setVisibility(View.GONE);
					layoutView.setVisibility(View.VISIBLE);
					pop = 1;
				}else{
					layoutView.setVisibility(View.GONE);
					pop = 0;
				}		
			}
		});
        
        btnClosePopupView.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {				
				layoutView.setVisibility(View.GONE);
				pop = 0;
			}
		});
        
        btnClosePopupFont.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {				
				layoutFont.setVisibility(View.GONE);
				pop = 0;
			}
		});
						
		linear.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
				if(pop != 1){			
					runNavigation();
					close = 0;
				}				
				else{
					layoutView.setVisibility(View.GONE);
					layoutFont.setVisibility(View.GONE);
					pop = 0;
				}				
			}
		});
		
		myWindow.addView(layout);
	}
			
	private void setupNavigation(PopupWindow panel) {
		final SeekBar slider = (SeekBar)panel.findViewById(R.id.book_position_slider);
		final TextView text = (TextView)panel.findViewById(R.id.book_position_text);

		final ZLTextView textView = getReader().getTextView();
		final ZLTextView.PagePosition pagePosition = textView.pagePosition();

		if (slider.getMax() != pagePosition.Total - 1 || slider.getProgress() != pagePosition.Current - 1) {
			slider.setMax(pagePosition.Total - 1);
			slider.setProgress(pagePosition.Current - 1);
			text.setText(makeProgressText(pagePosition.Current, pagePosition.Total));
		}
	}
			
	private String makeProgressText(int page, int pagesNumber) {
		final StringBuilder builder = new StringBuilder();
		builder.append(page);
		builder.append("/");
		builder.append(pagesNumber);
		/**final TOCTree tocElement = getReader().getCurrentTOCElement();
		if (tocElement != null) {
			builder.append("  ");
			builder.append(tocElement.getText());
		}**/
		return builder.toString();
	}
}
