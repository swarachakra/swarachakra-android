package iit.android.settings;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

public class SettingsActivity extends PreferenceActivity {
	public static boolean isTablet;
	private boolean isDefault = false;
	private boolean isEnabled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkKeyboardStatus();
		if (isDefault && isEnabled) {
			// Display the fragment as the main content.
			isTablet = isTablet(this);
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new UserSettings()).commit();
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		checkKeyboardStatus();
		if(isDefault && isEnabled) {
			
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
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

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
