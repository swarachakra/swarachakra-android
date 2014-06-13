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
		TextView title = (TextView) v.findViewById(R.id.title);
		TextView instruction = (TextView) v.findViewById(R.id.instruction);
		GIFView gif = (GIFView) v.findViewById(R.id.gif);
		switch (stageNo) {
		case 0:	
			instruction.setText(R.string.welcome_instruction);
			title.setText(R.string.welcome_title);
			gif.setVisibility(View.GONE);
			break;
		case 1:
			instruction.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			gif.loadGIFResource(getActivity(),R.drawable.enable);;
			gif.setVisibility(View.VISIBLE);
			break;
		case 2:
			instruction.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			gif.loadGIFResource(getActivity(), R.drawable.defaults);;
			gif.setVisibility(View.VISIBLE);
			break;
		case 3:
			instruction.setVisibility(View.GONE);
			title.setText("Congrats Fragment");
			title.setVisibility(View.VISIBLE);
			gif.setVisibility(View.GONE);
			break;
		default:
			title.setText("Something went wrong");
			break;
				
		}
		return v;
	}
}
