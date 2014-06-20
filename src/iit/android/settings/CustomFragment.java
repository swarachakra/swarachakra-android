package iit.android.settings;

import iit.android.swarachakra.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomFragment extends Fragment {

	public static final String STAGE_NO = "stageNo";
	public static MainActivity mMainActivity;

	public static final CustomFragment newInstance(int stageNo) {
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
		int containHeight;
		int containWidth;
		View v = inflater.inflate(R.layout.fragment_layout, container, false);
		TextView title = (TextView) v.findViewById(R.id.title);
		TextView instruction = (TextView) v.findViewById(R.id.instruction);
		GIFView gif = (GIFView) v.findViewById(R.id.gif);
		FrameLayout gifcontain = (FrameLayout) v
				.findViewById(R.id.main_container);
		containHeight = gifcontain.getHeight();
		containWidth = gifcontain.getWidth();
		switch (stageNo) {
		case 0:
			instruction.setText(mMainActivity
					.getStringResourceByName("welcome_instruction"));
			title.setText(mMainActivity
					.getStringResourceByName("welcome_title"));
			gif.setVisibility(View.GONE);
			break;
		case 1:
			instruction.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			gif.loadGIFResource(getActivity(), R.drawable.enable,
					containHeight, containWidth);
			MarginLayoutParams m = (MarginLayoutParams) gif.getLayoutParams();
			m.setMargins(gif.getMarginLeft(), gif.getMarginTop(),
					gif.getMarginLeft(), 0);
			gif.setLayoutParams(m);
			gif.setVisibility(View.VISIBLE);
			break;
		case 2:
			instruction.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			gif.loadGIFResource(getActivity(), R.drawable.defaults,
					containHeight, containWidth);
			MarginLayoutParams m2 = (MarginLayoutParams) gif.getLayoutParams();
			m2.setMargins(gif.getMarginLeft(), gif.getMarginTop(),
					gif.getMarginLeft(), 0);
			gif.setLayoutParams(m2);
			gif.setVisibility(View.VISIBLE);
			break;
		case 3:
			/*instruction.setText(mMainActivity
					.getStringResourceByName("congrats_instruction"));
			instruction.setVisibility(View.VISIBLE);
			title.setText(mMainActivity
					.getStringResourceByName("congrats_title"));
			title.setVisibility(View.VISIBLE);
			gif.setVisibility(View.GONE);*/
			Toast toast = Toast.makeText(mMainActivity, "called open settings App", Toast.LENGTH_SHORT);
			toast.show();
			mMainActivity.openSettingsApp();
			break;
		default:
			title.setText("Something went wrong");
			break;

		}
		return v;
	}

}
