package iit.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import iit.android.swarachakra.R;

public class MainActivity extends FragmentActivity {
	private boolean isDefault = false;
	private boolean isEnabled = false;
	private boolean isFirstRun;
	private boolean inEnglish = false;
	private static MainActivity mainApp;

	SharedPreferences settings;
	SharedPreferences.Editor editor;

	CustomPageAdapter pageAdapter;
	CustomViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getExtras() != null) {
			inEnglish = getIntent().getExtras().getBoolean("inEnglish", false);
		}

		setContentView(R.layout.activity_main);
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);

		mainApp = this;
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		editor = settings.edit();
		String key = this.getResources().getString(R.string.first_run_tutorial);
		isFirstRun = settings.getBoolean(key, true);

		List<Fragment> fragments = getFragments();
		pageAdapter = new CustomPageAdapter(getSupportFragmentManager(),
				fragments);
		pager = (CustomViewPager) findViewById(R.id.viewpager);
		pager.setPagingEnabled(false);
		pager.setAdapter(pageAdapter);
        if(getActionBar() != null) {
            getActionBar().setTitle(getString(R.string.title));
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_language:
			if (!inEnglish) {
				inEnglish = true;
				String languageName = getResources().getString(
						R.string.language_name);
				int resId = getResources().getIdentifier(
						languageName + "_" + "menu_language", "string",
						getPackageName());
				String title = getResources().getString(resId);
				item.setTitle(title);
			} else {
				inEnglish = false;
				String title = getResources().getString(R.string.menu_language);
				item.setTitle(title);
			}
			refreshActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public String getStringResourceByName(String aString) {
		String packageName = getPackageName();
		String languageName = getResources().getString(R.string.language_name);
		int resId = getResources().getIdentifier(languageName + "_" + aString,
				"string", packageName);
		if (inEnglish)
			resId = 0;
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
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}

	@Override
	public void onWindowFocusChanged(final boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		boolean isWelcomeFragment = isFirstRun && (pager.getCurrentItem() == 0);
		boolean isCongratsFragment = isFirstRun
				&& (pager.getCurrentItem() == 3);
		if (hasFocus && !isWelcomeFragment && !isCongratsFragment) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					setCorrectView();
				}
			}, 500);
		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	public static MainActivity getMainApp() {
		return mainApp;
	}

	private List<Fragment> getFragments() {
		List<Fragment> fList = new ArrayList<Fragment>();
		if (isFirstRun)
			fList.add(new WelcomeFragment());
		fList.add(new EnableFragment());
		fList.add(new DefaultFragment());
		if (isFirstRun)
			fList.add(new CongratsFragment());
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

	public void buttonClick(View v) {
		int fragmentNo = pager.getCurrentItem();
		if (!isFirstRun)
			fragmentNo += 1;
		switch (fragmentNo) {
		case 0:
			setCorrectView();
			break;
		case 1:
			showInputEnableSettings();
			break;
		case 2:
			showInputDefaultSettings();
			break;
		case 3:
			openSettingsApp();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setCorrectView();
			}
		}, 500);
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

		int stageNumber = getStageNumber();
		switch (stageNumber) {
		case 0:
			if (isFirstRun)
				pager.setCurrentItem(1, true);
			else
				pager.setCurrentItem(0, true);
			break;
		case 1:
			if (isFirstRun)
				pager.setCurrentItem(2, true);
			else
				pager.setCurrentItem(1, true);
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

	public void openSettingsApp() {
		Intent intent = new Intent(this,
				iit.android.settings.SettingsActivity.class);
		intent.putExtra("inEnglish", inEnglish);
		startActivity(intent);
	}

	public void refreshActivity() {
		pager.setAdapter(pageAdapter);
		setCorrectView();
	}

}