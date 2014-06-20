package iit.android.settings;

import iit.android.swarachakra.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class UserSettings extends PreferenceFragment {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);
    }
}
