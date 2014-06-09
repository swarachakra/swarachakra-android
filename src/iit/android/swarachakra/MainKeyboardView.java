package iit.android.swarachakra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class MainKeyboardView extends KeyboardView {
	public PopupWindow mChakraPopup;
	public View mPopupParent;
	public SwaraChakra mSwaraChakra;
	private MainKeyboardActionListener mActionListener;
	
	public final int BACKSPACE = Integer.parseInt(getResources().getString(R.string.backspace));
	public final int SYMBOLS = Integer.parseInt(getResources().getString(R.string.symbols));
	public final int ENGLISH = Integer.parseInt(getResources().getString(R.string.english));
	public final int SPACE = Integer.parseInt(getResources().getString(R.string.space));
	public final int ENTER = Integer.parseInt(getResources().getString(R.string.enter));
	public final int SHIFT = Integer.parseInt(getResources().getString(R.string.shift));
	
	public MainKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
		// TODO Auto-generated constructor stub
	}
	
	public MainKeyboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
		// TODO Auto-generated constructor stub
	}
	

	@SuppressLint("NewApi")
	private void initialize(Context context) {
		super.setPreviewEnabled(false);
		
		setLayerType(View.LAYER_TYPE_HARDWARE, null);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.chakra_layout, null);
		mSwaraChakra = (SwaraChakra) v.findViewById(R.id.swarachakra);
		//TO BE DONE
		int mode = 0;//based on device and orientation
		mSwaraChakra.setMetrics(mode);
		
		mChakraPopup = new PopupWindow(context);
		mChakraPopup.setContentView(v);
		mChakraPopup.setTouchable(false);
		mChakraPopup.setClippingEnabled(false);
		mChakraPopup.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mChakraPopup.setBackgroundDrawable(null);
		mPopupParent = this;
	}
	
	public void initListener(){
		mActionListener = (MainKeyboardActionListener) this.getOnKeyboardActionListener();
		mActionListener.initialize(this);
		this.setOnTouchListener(mActionListener);
	}
	
	@Override
	protected boolean onLongPress(Key key) {
	    mActionListener.onLongPress(key);
		return super.onLongPress(key);
	}

	public void dismissChakra() {
		// TODO Auto-generated method stub
		mChakraPopup.dismiss();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me){
		return super.onTouchEvent(me);
	}

}
