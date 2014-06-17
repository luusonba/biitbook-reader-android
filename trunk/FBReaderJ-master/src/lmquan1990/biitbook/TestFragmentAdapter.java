package lmquan1990.biitbook;

import com.astuetz.PagerSlidingTabStrip.IconTabProvider;
import com.biitbook.android.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TestFragmentAdapter extends FragmentPagerAdapter implements IconTabProvider{
		
	private int mCount = 3;
	private final int[] ICONS = { R.drawable.ic_tab_all, R.drawable.ic_tab_person,
			R.drawable.ic_tab_cloud};

	public TestFragmentAdapter(FragmentManager fm) {		
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position) {		
		Fragment fragment = new AllLibrary();
		switch(position){
		case 0:
			fragment = new AllLibrary();
			break;
		case 1:
			fragment = new YourLibrary();
			break;
		case 2:
			fragment = new BiitLibrary();
			break;		
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return mCount;
	}
		
	public void setCount(int count){
		//if (count > 0 && count < 10){
			//mCount = count;
			//notifyDataSetChanged();
		//}
	}
	
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return ICONS[position];
	}
}