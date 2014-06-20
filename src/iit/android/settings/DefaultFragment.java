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

public class DefaultFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
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
		instruction.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
		gif.loadGIFResource(getActivity(), R.drawable.defaults, containHeight,
				containWidth);
		MarginLayoutParams m2 = (MarginLayoutParams) gif.getLayoutParams();
		m2.setMargins(gif.getMarginLeft(), gif.getMarginTop(),
				gif.getMarginLeft(), 0);
		gif.setLayoutParams(m2);
		gif.setVisibility(View.VISIBLE);

		return v;
	}

}
