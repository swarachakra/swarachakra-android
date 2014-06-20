package iit.android.settings;

import iit.android.swarachakra.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class UserSettings extends PreferenceFragment {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);
        SharedPreferences settings = getPreferenceManager().getSharedPreferences();
		SharedPreferences.Editor editor = settings.edit();
		String key = this.getResources().getString(
				R.string.tablet_layout_setting_key);
		boolean isFirstRun = settings.getBoolean("is_first_run", true);
		if (isFirstRun) {
			editor.putBoolean("is_first_run", false);
			editor.putBoolean(key, SettingsActivity.isTablet);
			editor.commit();
		}
    }
}
