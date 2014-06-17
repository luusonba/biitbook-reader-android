package lmquan1990.biitbook;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.biitbook.android.R;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class DialogFragmentJr extends SherlockDialogFragment  {

	 public static DialogFragmentJr newInstance(int title) {
		 DialogFragmentJr frag = new DialogFragmentJr();
	        Bundle args = new Bundle();
	        args.putInt("title", title);
	        frag.setArguments(args);
	        return frag;
	    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		OnClickListener positiveClick = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
	    	  	Intent intent = new Intent(getActivity().getApplication(), LibsActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
			}
		};

		OnClickListener negativeClick = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getActivity().getResources().getString(R.string.yesnoquit));
		builder.setNegativeButton(getActivity().getResources().getString(R.string.later), negativeClick);
		builder.setPositiveButton(getActivity().getResources().getString(R.string.agree), positiveClick);
		builder.setTitle(getActivity().getResources().getString(R.string.notice));
		Dialog dialog = builder.create();
		return dialog;
	}
}
