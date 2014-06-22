package iit.android.settings;

import iit.android.swarachakra.R;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends PreferenceActivity {
	public static boolean isTablet;
	private boolean isDefault = false;
	private boolean isEnabled = false;
	private EditText previewEditText;
	private TextView instructionTextView;
	private RadioGroup radioGroup;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.settings_layout, null);
		previewEditText = (EditText) layout
				.findViewById(R.id.preview_edit_text);
		instructionTextView = (TextView) layout.findViewById(R.id.instruction);
		radioGroup = (RadioGroup) layout.findViewById(R.id.layoutRadioGroup);
		
		RadioButton smallRadio = (RadioButton) layout.findViewById(R.id.smallRadioButton);
		RadioButton bigRadio = (RadioButton) layout.findViewById(R.id.bigRadioButton);
		
		String smallRadioText = getStringResourceByName("settings_layout_small");
		String bigRadioText = getStringResourceByName("settings_layout_big");
		
		smallRadio.setText(smallRadioText);
		bigRadio.setText(bigRadioText);

		String instruction = getStringResourceByName("settings_instruction");
		instructionTextView.setText(instruction);
		setContentView(layout);

		checkKeyboardStatus();

		prefs = UserSettings.getPrefs();
		
		String key = getString(R.string.tablet_layout_setting_key);		
		Boolean isSmall = prefs.getBoolean(key, false);
		if(isSmall){
			smallRadio.setChecked(true);
			bigRadio.setChecked(false);
		}
		else{
			smallRadio.setChecked(false);
			bigRadio.setChecked(true);
		}

		OnCheckedChangeListener radioGroupOnCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				editor = prefs.edit();
				String key = getResources().getString(
						R.string.tablet_layout_setting_key);
	

				if (checkedId == R.id.smallRadioButton) {
					editor.putBoolean(key, true);
					editor.commit();
					showPreview();
				} else {
					editor.putBoolean(key, false);
					editor.commit();
					showPreview();
				}
			}
		};

		radioGroup
				.setOnCheckedChangeListener(radioGroupOnCheckedChangeListener);

		if (isDefault && isEnabled) {
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
		if (isDefault && isEnabled) {

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

		InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(previewEditText, 0);
	}
}
