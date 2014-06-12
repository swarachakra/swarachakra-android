package iit.android.settings;

import iit.android.swarachakra.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomFragment extends Fragment {

	public static final String STAGE_NO = "stageNo";
	public static final CustomFragment newInstance(int stageNo)
	{
		CustomFragment f = new CustomFragment();
		Bundle bdl = new Bundle(1);
		bdl.putInt(STAGE_NO, stageNo);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		int stageNo = getArguments().getInt(STAGE_NO);
		View v = inflater.inflate(R.layout.fragment_layout, container, false);
		TextView messageTextView = (TextView) v.findViewById(R.id.english);
		switch (stageNo) {
		case 0:	
			messageTextView.setText("Welcome Fragment");
			break;
		case 1:
			messageTextView.setText("Enable Fragment");
			break;
		case 2:
			messageTextView.setText("Default Fragment");
			break;
		case 3:
			messageTextView.setText("Congrats Fragment");
			break;
		default:
			messageTextView.setText("Something went wrong");
			break;
				
		}
		return v;
	}
}
