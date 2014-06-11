package iit.android.settings;

import iit.android.swarachakra.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends FragmentActivity {
	private boolean isDefault = false;
	private boolean isEnabled = false;

	CustomPageAdapter pageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Fragment> fragments = getFragments();
		pageAdapter = new CustomPageAdapter(getSupportFragmentManager(), fragments);
		CustomViewPager pager = (CustomViewPager) findViewById(R.id.viewpager);
		pager.setPagingEnabled(false);
		pager.setAdapter(pageAdapter);

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onWindowFocusChanged(final boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}
	
	private List<Fragment> getFragments(){
		  List<Fragment> fList = new ArrayList<Fragment>();
		  fList.add(CustomFragment.newInstance("Fragment 1"));
		  fList.add(CustomFragment.newInstance("Fragment 2")); 
		  fList.add(CustomFragment.newInstance("Fragment 3"));
		  return fList;
		}

	public void checkKeyboardStatus() {
		InputMethodManager mgr = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> lim = mgr.getEnabledInputMethodList();
		isEnabled = false;
		isDefault = false;

		for (InputMethodInfo im : lim) {
			// Log.d("main", ""+ im.getPackageName() + "***" + im.getId());
			if (im.getPackageName().equals(getPackageName())) {
				Log.d("main", "###################### Package names are equal");
				isEnabled = true;
				final String currentImeId = Settings.Secure.getString(
						getContentResolver(),
						Settings.Secure.DEFAULT_INPUT_METHOD);

				if (im != null && im.getId().equals(currentImeId)) {
					isDefault = true;
					Log.d("main", "################### Set isDefault to true");
				}
			}
		}

	}

	public int getStageNumber() {
		int stageNumber = 0;
		checkKeyboardStatus();
		if (!isEnabled) {
			stageNumber = 0;
		} else {
			if (!isDefault) {
				stageNumber = 1;
			} else {
				stageNumber = 2;
			}
		}

		return stageNumber;
	}

}