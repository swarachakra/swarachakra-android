package iit.android.settings;

import iit.android.swarachakra.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class MainActivity extends FragmentActivity {
	private boolean isDefault = false;
	private boolean isEnabled = false;
	private boolean startSetUp = false;
	private boolean endSetUp = false;

	CustomPageAdapter pageAdapter;
	CustomViewPager pager;
	Button b;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Fragment> fragments = getFragments();
		pageAdapter = new CustomPageAdapter(getSupportFragmentManager(), fragments);
		pager = (CustomViewPager) findViewById(R.id.viewpager);
		pager.setPagingEnabled(false);
		pager.setAdapter(pageAdapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setButtonText();
            }
        });
		
		b = (Button) findViewById(R.id.Button);
		b.setText("Start");
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
		if(startSetUp && !endSetUp && hasFocus) {
			setCorrectView();
		}
	}
	
	private List<Fragment> getFragments(){
		  List<Fragment> fList = new ArrayList<Fragment>();
		  fList.add(CustomFragment.newInstance(0));
		  fList.add(CustomFragment.newInstance(1)); 
		  fList.add(CustomFragment.newInstance(2));
		  fList.add(CustomFragment.newInstance(3));
		  return fList;
		}

	public void checkKeyboardStatus() {
		InputMethodManager mgr = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> lim = mgr.getEnabledInputMethodList();
		isEnabled = false;
		isDefault = false;

		for (InputMethodInfo im : lim) {
			if (im.getPackageName().equals(getPackageName())) {
				isEnabled = true;
				final String currentImeId = Settings.Secure.getString(
						getContentResolver(),
						Settings.Secure.DEFAULT_INPUT_METHOD);

				if (im != null && im.getId().equals(currentImeId)) {
					isDefault = true;
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
	
	private void setButtonText() {
		int stageNo = pager.getCurrentItem();
		switch(stageNo) {
		case 0:
			b.setText("Start");
			b.setVisibility(View.VISIBLE);
			break;
		case 1:
			b.setText("Enable");
			b.setVisibility(View.VISIBLE);
			break;
		case 2:
			b.setText("Default");
			b.setVisibility(View.VISIBLE);
			break;
		case 3:
			b.setVisibility(View.GONE);
			break;
		}
	}
	
	public void buttonClick(View v) {
		int stageNo = pager.getCurrentItem();
		switch (stageNo) {
		case 0:
			pager.setCurrentItem(1);
			startSetUp = true;
			break;
		case 1:
			showInputEnableSettings();
			break;
		case 2:
			showInputDefaultSettings();
			break;
		case 3:
			endSetUp = true;
			break;
		default:
			break;
		}
	}
	
	public void showInputEnableSettings() {
		startActivityForResult( new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
	}
	
	public void showInputDefaultSettings() {
		InputMethodManager mgr =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showInputMethodPicker();
	}
	
	public void setCorrectView() {
		int stageNumber = getStageNumber();
		switch(stageNumber) {
		case 0:
			pager.setCurrentItem(1, true);
			break;
		case 1:
			pager.setCurrentItem(2, true);
			break;
		case 2:
			pager.setCurrentItem(3, true);
			break;
		case 3:
			pager.setCurrentItem(4, true);
		default:
			Log.d("main", "I'm defaulting");
			pager.setCurrentItem(0);
		}
	}

}