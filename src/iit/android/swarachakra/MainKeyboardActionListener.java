package iit.android.swarachakra;

import iit.android.language.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

public class MainKeyboardActionListener implements OnKeyboardActionListener,
		OnTouchListener {
	private SoftKeyboard mSoftKeyboard;
	private static MainKeyboardView mKeyboardView;
	private static PopupWindow mChakraPopup;
	private static SwaraChakra mSwaraChakra;
	private ExceptionHandler mExceptionHandler;
	private static View mPopupParent;
	private int touchDownX;
	private int touchDownY;
	private static boolean isChakraVisible;
	private boolean inHalantMode;
	private boolean isShifted;
	private boolean inSymbolMode;
	private boolean isPersistent;
	private boolean inExceptionMode;
	private boolean spaceHandled;
	private boolean shiftHandled;
	private int exceptionCode;
	private String preText;
	private HashMap<Integer, KeyAttr> mKeys;
	private HashMap<Integer, KeyAttr> sKeys;
	private int ENTER;
	private int BACKSPACE;
	private int SPACE;
	private int ENGLISH;
	private int SYMBOLS;
	private int SHIFT;
	private int MOVE_THRESHOLD = 0;
	private InputConnection mInputConnection;
	private int halantEnd;
	private static final int MSG_SHOW_CHAKRA = 1;
	private static final int MSG_REMOVE_CHAKRA = 2;
	private static final int DELAY_BEFORE_SHOW = 70;
	
	static Handler mHandler = new Handler(){
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MSG_SHOW_CHAKRA:
				mSwaraChakra.setVisibility(View.VISIBLE);
				mKeyboardView.setAlpha(0.75f);
				break;
			case MSG_REMOVE_CHAKRA:
				removeChakra();
				break;
			}
		}
	};

	public void initialize(MainKeyboardView mKeyboardView) {
		MainKeyboardActionListener.mKeyboardView = mKeyboardView;

		mChakraPopup = mKeyboardView.mChakraPopup;
		mSwaraChakra = mKeyboardView.mSwaraChakra;
		mPopupParent = mKeyboardView.mPopupParent;
		isChakraVisible = false;

		BACKSPACE = mKeyboardView.BACKSPACE;
		ENTER = mKeyboardView.ENTER;
		SPACE = mKeyboardView.SPACE;
		ENGLISH = mKeyboardView.ENGLISH;
		SYMBOLS = mKeyboardView.SYMBOLS;
		SHIFT = mKeyboardView.SHIFT;
		

		inHalantMode = false;
		inExceptionMode = false;
		exceptionCode = 0;
		preText = "";
		MOVE_THRESHOLD = (int) mSwaraChakra.getInnerRadius();
	}

	public void setInputConnection(InputConnection ic) {
		mInputConnection = ic;
	}

	public void setExceptionHandler(ExceptionHandler eh) {
		mExceptionHandler = eh;
	}

	public void setKeysMap(HashMap<Integer, KeyAttr> mKeys) {
		this.mKeys = mKeys;
	}

	public void setHalantEnd(int h) {
		halantEnd = h;
	}

	public void setTouchDownPoint(int x, int y) {
		touchDownX = x;
		touchDownY = y;
	}

	public void setSoftKeyboard(SoftKeyboard sk) {
		this.mSoftKeyboard = sk;
	}
	

	@SuppressLint("NewApi")
	private static void showChakraAt(int posX, int posY) {
		final PopupWindow chakraPopup = mChakraPopup;
		mSwaraChakra.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		float offset = 2*mSwaraChakra.getOuterRadius();
		
		int w = mSwaraChakra.getMeasuredWidth();
		int h = mSwaraChakra.getMeasuredHeight();
		int x = (int) (posX - offset);
		int y = (int) (posY - offset);
		
		if(chakraPopup.isShowing()){
			chakraPopup.update(x, y, w, h);
		}
		else{
			chakraPopup.setWidth(w);
			chakraPopup.setHeight(h);
			chakraPopup.showAtLocation(mPopupParent, Gravity.NO_GRAVITY, x, y);
		}
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SHOW_CHAKRA), DELAY_BEFORE_SHOW);
		isChakraVisible = true;
	}

	@SuppressLint("NewApi")
	private static void removeChakra() {
		mHandler.removeMessages(MSG_SHOW_CHAKRA);
		mSwaraChakra.desetArc();
		mSwaraChakra.setVisibility(View.GONE);
		isChakraVisible = false;
		mKeyboardView.setAlpha(1);
	}

	public void onLongPress(Key key) {
		if (key.codes[0] == SHIFT) {
			if (isShifted && isPersistent) {
				changeLayout("default");
				isPersistent = false;
				isShifted = false;
			} else {
				changeLayout("shift");
				isPersistent = true;
				isShifted = true;
			}
			shiftHandled = true;
			
		} else if (key.codes[0] == SPACE) {
			InputMethodManager imm = (InputMethodManager) mSoftKeyboard
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showInputMethodPicker();
			spaceHandled = true;
		}
	}

	@Override
	public void onKey(int arg0, int[] arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPress(int keyCode) {
		spaceHandled = false;
		shiftHandled = false;
		KeyAttr key = null;
		boolean showChakra = false;
		if(inExceptionMode && sKeys.containsKey(keyCode)){
			showChakra = sKeys.get(keyCode).showChakra && !(isChakraVisible);
			key = sKeys.get(keyCode);
		}
		else if (mKeys.containsKey(keyCode)) {
			showChakra = mKeys.get(keyCode).showChakra && !(isChakraVisible);
			key = mKeys.get(keyCode);
		}
		if (showChakra) {
			mSwaraChakra.setCurrentKey(key);
			mSwaraChakra.setKeyLabel(getKeyLabel(keyCode));
			showChakraAt(touchDownX, touchDownY);
		}
	}

	@Override
	public void onRelease(int keyCode) {
		if (mKeys.containsKey(keyCode)) {
			if (isShifted && !(isPersistent)) {
				changeLayout("default");
				isShifted = false;
			}
		}

		if (inExceptionMode) {
			inExceptionMode = false;
			updateKeyLabels();
			if (mKeys.containsKey(keyCode)) {
				if (mKeys.get(keyCode).isException
						&& (exceptionCode == keyCode)) {
					exceptionCode = 0;
					return;
				}
			}
			exceptionCode = 0;
		}

		if (isChakraVisible) {
			String text = mSwaraChakra.getText();
			if (mSwaraChakra.isHalant()) {
				setHalant(text);
			} else {
				removeHalantMode();
				commitText(text);
			}
			removeChakra();
		} else if (mKeys.containsKey(keyCode)) {
			KeyAttr key = mKeys.get(keyCode);
			if (key.isException) {
				handleException(keyCode);
			} else if (key.changeLayout) {
				changeLayout(key.layout);
			} else {
				commitText(key.label);
			}
		} else {
			removeHalantMode();
			handleSpecialInput(keyCode);
		}
	}

	private void handleException(int keyCode) {
		inExceptionMode = true;
		exceptionCode = keyCode;
		sKeys = mExceptionHandler.handleException(keyCode);
		updateKeyLabels();
	}

	private void removeHalantMode() {
		if (inHalantMode) {
			inHalantMode = false;
			preText = "";
			updateKeyLabels();
		}
	}

	/**
	 * Sets halant mode and changes the labels of the keyboard accrding to the
	 * halant text
	 * 
	 * @param text
	 *            The string to be added to the labels of the keyboard when
	 *            halant is selected in swarachakra
	 */
	private void setHalant(String text) {
		if (inHalantMode) {
			preText = text;
			updateKeyLabels();
		} else {
			preText = text;
			inHalantMode = true;
			updateKeyLabels();
		}
		mInputConnection.setComposingText("", 1);
	}

	/**
	 * Changes the layout of the keyboard
	 * 
	 * @param layout
	 *            name string of the layout resource
	 */
	private void changeLayout(String layout) {
		mSoftKeyboard.changeKeyboard(layout);
	}

	/**
	 * Changes the language of the keyboard
	 * 
	 * @param layout
	 *            name string of the layout resource
	 */
	private void changeLanguage() {
		mSoftKeyboard.changeLanguage();
	}

	/**
	 * Handles what to be done when keys like ENTER, SPACE, BACKSPACE, EN,
	 * SHIFT, SYM
	 * 
	 * @param keyCode
	 *            key code of the key whose functionality is to be handled
	 */
	private void handleSpecialInput(int keyCode) {
		if (keyCode == SHIFT && !shiftHandled) {
			if (!inSymbolMode) {
				if (isShifted) {
					isShifted = false;
					isPersistent = false;
					changeLayout("default");
				} else {
					isShifted = true;
					changeLayout("shift");
				}
			}
		}

		else if (keyCode == BACKSPACE) {
			CharSequence selection = mInputConnection.getSelectedText(0);
			commitText("");

			if (selection == null) {
				mInputConnection.deleteSurroundingText(1, 0);
			}
		}

		else if (keyCode == SPACE) {
			if (!spaceHandled) {
				mInputConnection.finishComposingText();
				commitText(" ");
			}
		}

		else if (keyCode == SYMBOLS) {
			if (inSymbolMode) {
				inSymbolMode = !(inSymbolMode);
				if (isPersistent && isShifted) {
					changeLayout("shift");
				} else {
					changeLayout("default");
				}
			} else {
				inSymbolMode = !(inSymbolMode);
				changeLayout("symbols");
			}
		}

		else if (keyCode == ENGLISH) {
			changeLanguage();
		}

		else if (keyCode == ENTER) {
			handleEnter();
		}

	}

	private void handleEnter() {
		// TODO Auto-generated method stub
		mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_ENTER));
		mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
				KeyEvent.KEYCODE_ENTER));
	}

	/**
	 * Send text to the EditText
	 * 
	 * @param text
	 *            string to be sent to EditText
	 */
	private void commitText(String text) {
		mInputConnection.setComposingText(text, 1);
		mInputConnection.finishComposingText();
	}

	private void updateKeyLabels() {
		List<Key> keys = mKeyboardView.getKeyboard().getKeys();
		for (Key key : keys) {
			int code = key.codes[0];
			if (code <= halantEnd) {
				String nextLabel = "";
				if (inExceptionMode && sKeys.containsKey(code)) {
					nextLabel = sKeys.get(code).label;
					Log.d("exhandle", "nextLabel = " + nextLabel);
				} else {
					nextLabel = preText + mKeys.get(code).label;
				}
				key.label = nextLabel;
			}
		}
		mKeyboardView.invalidateAllKeys();
	}

	private String getKeyLabel(int keyCode) {
		if (inExceptionMode) {
			if (sKeys.containsKey(keyCode)) {
				String label = sKeys.get(keyCode).label;
				return label;
			}
		}
		String label = preText + mKeys.get(keyCode).label;
		return label;
	}

	@Override
	public void onText(CharSequence arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View view, MotionEvent me) {
		// TODO Auto-generated method stub
		MainKeyboardView mKeyboardView = (MainKeyboardView) view;
		int action = me.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			int x = (int) me.getX();
			int y = (int) me.getY();
			setTouchDownPoint(x, y);
		}

		else if (action == MotionEvent.ACTION_UP
				|| (action == MotionEvent.ACTION_OUTSIDE)) {
		}

		else if (action == MotionEvent.ACTION_MOVE) {
			int x = (int) me.getX();
			int y = (int) me.getY();
			handleMove(x, y);

			return true;
		}

		else if (action == MotionEvent.ACTION_CANCEL) {
			mSwaraChakra.desetArc();
		}

		return mKeyboardView.onTouchEvent(me);
	}

	
	@SuppressLint("NewApi")
	private void handleMove(int x, int y){
		
		
		int touchMovementX = (int) x - touchDownX;
		int touchMovementY = (int) y - touchDownY;
		
		
		if(y==0 && touchMovementX < mSwaraChakra.getOuterRadius() && touchMovementY < mSwaraChakra.getOuterRadius()){
			float outerRadius = (float) (1.2 * mSwaraChakra.getOuterRadius());
			touchMovementY = -(int)Math.sqrt(outerRadius*outerRadius - touchMovementX*touchMovementX);
		}

		int radius = (int) Math.sqrt((touchMovementX * touchMovementX)
				+ (touchMovementY * touchMovementY));

		float theta = (float) Math.toDegrees(Math.atan2(touchMovementY,
				touchMovementX));
		
		if (radius > MOVE_THRESHOLD) {
			int arc = findArc(theta);
			if (isChakraVisible) {
				mSwaraChakra.setArc(arc);
				String text = mSwaraChakra.getText();
				mInputConnection.setComposingText(text, 1);
				if(mSwaraChakra.getVisibility() == View.VISIBLE){
					mKeyboardView.setAlpha(0.35f);
				}
			}
		} else {
			if(isChakraVisible){
				mSwaraChakra.desetArc();
				String text = mSwaraChakra.getText();
				mInputConnection.setComposingText(text, 1);
				
				if(mSwaraChakra.getVisibility() == View.VISIBLE){
					float a = 0;
					double k = (-0.4)/MOVE_THRESHOLD;
					a = (float) (0.75+k*radius);
					mKeyboardView.setAlpha(a);
				}
			}
		}
	}

	private int findArc(float theta) {
		int nArcs = SwaraChakra.getNArcs();
		float offset = (float) -(90.0 + 360.0 / (2 * nArcs));
		float relAngle = theta - offset;
		if (relAngle < 0) {
			relAngle = 360 + relAngle;
		}
		int region = (int) (relAngle * nArcs / 360);
		return region;
	}

}
