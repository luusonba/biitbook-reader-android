/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.fbreader;

import lmquan1990.biitbook.BuyBookActivity;
import lmquan1990.biitbook.GolbalFunction;

import org.geometerplus.android.fbreader.FBReaderApplication;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.fbreader.options.PageTurningOptions;
import org.geometerplus.zlibrary.text.view.ZLTextView;

import android.content.Intent;

class VolumeKeyTurnPageAction extends FBAction {
	private final boolean myForward;
	private FBReaderApp myFBReaderApp;
	GolbalFunction golbal = new GolbalFunction();

	VolumeKeyTurnPageAction(FBReaderApp fbreader, boolean forward) {
		super(fbreader);
		myForward = forward;
		myFBReaderApp = (FBReaderApp)FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(new BookCollectionShadow());
		}
	}

	@Override
	protected void run(Object ... params) {
		final PageTurningOptions preferences = Reader.PageTurningOptions;
		final ZLTextView textView = myFBReaderApp.getTextView();
		final ZLTextView.PagePosition pagePosition = textView.pagePosition();
		String pass = golbal.loadSavedPreferences("KEY_PASSBOOK");
		
		if(pass.equals("1")){
			Reader.getViewWidget().startAnimatedScrolling(
				myForward ? FBView.PageIndex.next : FBView.PageIndex.previous,
				preferences.Horizontal.getValue()
					? FBView.Direction.rightToLeft : FBView.Direction.up,
				preferences.AnimationSpeed.getValue()
			);
		}else if(pass.equals("2")){
			if(myForward){
				if(pagePosition.Current>pagePosition.Total/10){
					if(BuyBookActivity.fa==null){
						Intent i = new Intent(FBReaderApplication.getAppContext(),BuyBookActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						FBReaderApplication.getAppContext().startActivity(i);
					}else{
						BuyBookActivity.fa.finish();
						Intent i = new Intent(FBReaderApplication.getAppContext(),BuyBookActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						FBReaderApplication.getAppContext().startActivity(i);
					}					
				}else{
					Reader.getViewWidget().startAnimatedScrolling(
							myForward ? FBView.PageIndex.next : FBView.PageIndex.previous,
							preferences.Horizontal.getValue()
								? FBView.Direction.rightToLeft : FBView.Direction.up,
							preferences.AnimationSpeed.getValue()
					);
				}
			}else{
				Reader.getViewWidget().startAnimatedScrolling(
						myForward ? FBView.PageIndex.next : FBView.PageIndex.previous,
						preferences.Horizontal.getValue()
							? FBView.Direction.rightToLeft : FBView.Direction.up,
						preferences.AnimationSpeed.getValue()
				);
			}
		}
	} 
}