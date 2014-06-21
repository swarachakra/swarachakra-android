package iit.android.settings;

import iit.android.swarachakra.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DefaultFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
MainActivity mainApp = MainActivity.getMainApp();
		
		View v = inflater.inflate(R.layout.default_fragment, container,  false);
		TextView instructionsHeadTextView = (TextView)v.findViewById(R.id.default_instructions_head);
		TextView step1TextView = (TextView)v.findViewById(R.id.default_step_1);
		Button defaultButton = (Button)v.findViewById(R.id.default_button);
		
		String instructionsHeadText = mainApp.getStringResourceByName("default_instructions_head");
		String step1Text = mainApp.getStringResourceByName("default_step_1");
		String defaultButtonText = mainApp.getStringResourceByName("default_button");
		
		instructionsHeadTextView.setText(instructionsHeadText);
		step1TextView.setText(step1Text);
		defaultButton.setText(defaultButtonText);
		return v;
	}

}
