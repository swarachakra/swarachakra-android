package iit.android.settings;

import iit.android.swarachakra.R;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	public static boolean isTablet;
	private boolean isDefault = false;
	private boolean isEnabled = false;
	private EditText previewEditText;
	private TextView instructionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.settings_layout, null);
		previewEditText = (EditText) layout.findViewById(R.id.preview_edit_text);
		instructionTextView = (TextView) layout.findViewById(R.id.instruction);
		String instruction = getStringResourceByName("settings_instruction");
		instructionTextView.setText(instruction);
		setContentView(layout);
		checkKeyboardStatus();
		
		SharedPreferences prefs = UserSettings.getPrefs();
		prefs.registerOnSharedPreferenceChangeListener(this);
		if (isDefault && isEnabled) {
			// Display the fragment as the main content.
			isTablet = isTablet(this);
			previewEditText.requestFocus();
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
	
	public String getStringResourceByName(String aString) {
		String packageName = getPackageName();
		String languageName = getResources().getString(R.string.language_name);
		int resId = getResources().getIdentifier(languageName + "_" + aString,
				"string", packageName);
		if (resId == 0) {
			resId = getResources()
					.getIdentifier(aString, "string", packageName);
		}
		return getString(resId);
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
	
	private void showPreview() {
		previewEditText.requestFocus();
		previewEditText.setText(null);
		
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	      
	    imm.showSoftInput(previewEditText, 0);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		showPreview();		
	}
}
