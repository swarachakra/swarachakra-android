package iit.android.settings;

import iit.android.language.hindi.MainLanguage;
import iit.android.swarachakra.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
	private boolean isFirstRun;
	private MainLanguage language = new MainLanguage();

	SharedPreferences settings;
	SharedPreferences.Editor editor;

	CustomPageAdapter pageAdapter;
	CustomViewPager pager;
	Button b;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CustomFragment.mMainActivity = this;
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		editor = settings.edit();
		String key = this.getResources().getString(R.string.first_run_tutorial);
		isFirstRun = settings.getBoolean(key, true);
		if (!isFirstRun) {
			startSetUp = true;
		}

		List<Fragment> fragments = getFragments();
		pageAdapter = new CustomPageAdapter(getSupportFragmentManager(),
				fragments);
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
		b.setText(getStringResourceByName("welcome_button"));
	}

	public String getStringResourceByName(String aString) {
		String packageName = getPackageName();
		int resId = getResources().getIdentifier(language.name + "_" + aString,
				"string", packageName);
		if (resId == 0) {
			resId = getResources()
					.getIdentifier(aString, "string", packageName);
		}
		return getString(resId);
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
		if (isFirstRun) {
			if (startSetUp && !endSetUp && hasFocus) {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						setCorrectView();
					}
				}, 500);
			}
		} else {
			setCorrectView();
		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	private List<Fragment> getFragments() {
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
		switch (stageNo) {
		case 0:
			b.setText(getStringResourceByName("welcome_button"));
			break;
		case 1:
			b.setText(getStringResourceByName("enable_button"));
			break;
		case 2:
			b.setText(getStringResourceByName("default_button"));
			break;
		case 3:
			b.setText(getStringResourceByName("congrats_button"));
			;
			break;
		}
	}

	public void buttonClick(View v) {
		int stageNo = pager.getCurrentItem();
		Log.d("settings", "stageNo = " + stageNo);
		switch (stageNo) {
		case 0:
			setCorrectView();
			startSetUp = true;
			break;
		case 1:
			showInputEnableSettings();
			break;
		case 2:
			showInputDefaultSettings();
			break;
		case 3:
			openSettingsApp();
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
			Log.d("main", "resultCode = " + resultCode);
			if (resultCode == RESULT_OK) {
				Log.d("main", "data = " + data);
			}
		}
	}

	public void showInputEnableSettings() {
		startActivityForResult(new Intent(
				android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
	}

	public void showInputDefaultSettings() {
		InputMethodManager mgr = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showInputMethodPicker();
	}

	public void setCorrectView() {
		Log.d("settings", "getStageNumber = " + getStageNumber());
		int stageNumber = getStageNumber();
		switch (stageNumber) {
		case 0:
			pager.setCurrentItem(1, true);
			break;
		case 1:
			pager.setCurrentItem(2, true);
			break;
		case 2:
			if (isFirstRun) {
				String key = this.getResources().getString(
						R.string.first_run_tutorial);
				editor.putBoolean(key, false);
				editor.commit();
				pager.setCurrentItem(3, true);
			} else {
				openSettingsApp();
			}
			break;
		default:
			Log.d("main", "I'm defaulting");
			pager.setCurrentItem(0);
		}
	}

	public void moveTo(int destFragment) {
		int curFragment = pager.getCurrentItem();
		while (destFragment != curFragment) {
			if (destFragment > curFragment) {
				pager.setCurrentItem(curFragment + 1, true);
			} else {
				pager.setCurrentItem(curFragment - 1, true);
			}
			curFragment = pager.getCurrentItem();
		}
	}

	public void openSettingsApp() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

}