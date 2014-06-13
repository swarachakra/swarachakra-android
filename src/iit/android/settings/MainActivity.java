package iit.android.settings;

import iit.android.swarachakra.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
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
		b.setText(R.string.welcome_button);
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
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        setCorrectView();
			    }
			}, 500);
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
			b.setText(R.string.welcome_button);
			b.setVisibility(View.VISIBLE);
			break;
		case 1:
			b.setText(R.string.enable_button);
			b.setVisibility(View.VISIBLE);
			break;
		case 2:
			b.setText(R.string.default_button);
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
			setCorrectView();;
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request it is that we're responding to
		Log.d("main", "called onActivityResult");
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        setCorrectView();
		    }
		}, 500);
	    if (requestCode == 0) {
	        Log.d("main","resultCode = " + resultCode);
	        if (resultCode == RESULT_OK) {
	        	Log.d("main", "data = " + data);
	        }
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
			moveTo(1);
			break;
		case 1:
			moveTo(2);
			break;
		case 2:
			moveTo(3);
			break;
		case 3:
			moveTo(4);
		default:
			Log.d("main", "I'm defaulting");
			pager.setCurrentItem(0);
		}
	}
	
	public void moveTo (int destFragment) {
		int curFragment = pager.getCurrentItem();
		while (destFragment != curFragment) {
			if(destFragment > curFragment) {
				pager.setCurrentItem(curFragment+1, true);
			}
			else {
				pager.setCurrentItem(curFragment-1, true);
			}
			curFragment = pager.getCurrentItem();
		}
	}

}