package iit.android.settings;

import java.util.List;

import iit.android.swarachakra.R;
import iit.android.swarachakra.SoftKeyboard;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
	private boolean isDefault = false;
	private boolean isEnabled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkKeyboardStatus();
		if (isDefault && isEnabled) {
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(SoftKeyboard.appContext());
			SharedPreferences.Editor editor = settings.edit();
			String key = this.getResources().getString(
					R.string.tablet_layout_setting_key);
			boolean isFirstRun = settings.getBoolean("is_first_run", true);
			if (isFirstRun) {
				editor.putBoolean("is_first_run", false);
				editor.putBoolean(key, isTablet(this));
				editor.commit();
			}

			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new UserSettings()).commit();
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			Toast toast = Toast.makeText(this, "called open MainActivity App", Toast.LENGTH_SHORT);
			toast.show();
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
