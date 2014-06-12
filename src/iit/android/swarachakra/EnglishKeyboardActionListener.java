package iit.android.swarachakra;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class EnglishKeyboardActionListener implements OnKeyboardActionListener,
		OnTouchListener {
	
	private int ENTER;
	private int BACKSPACE;
	private int SPACE;
	private int ENGLISH;
	private int SYMBOLS;
	private int SHIFT;
	private EnglishKeyboardView mKeyboardView;
	private SoftKeyboard mSoftKeyboard;
	private HashMap<Integer, KeyAttr> mKeys;
	private InputConnection mInputConnection;
	
	private boolean isShifted;
	private boolean inSymbolMode;
	private boolean isPersistent;
	private boolean spaceHandled;
	private boolean shiftHandled;
	
	public void initialize(EnglishKeyboardView kv){
		mKeyboardView = kv;
		
		BACKSPACE = mKeyboardView.BACKSPACE;
		ENTER = mKeyboardView.ENTER;
		SPACE = mKeyboardView.SPACE;
		ENGLISH = mKeyboardView.ENGLISH;
		SYMBOLS = mKeyboardView.SYMBOLS;
		SHIFT = mKeyboardView.SHIFT;
		
		isShifted = false;
		inSymbolMode = false;
		isPersistent = false;
	}
	
	public void setSoftKeyboard(SoftKeyboard sk) {
		this.mSoftKeyboard = sk;
	}
	
	public void setKeysMap(HashMap<Integer, KeyAttr> mKeys) {
		this.mKeys = mKeys;
	}
	
	public void setInputConnection(InputConnection ic) {
		mInputConnection = ic;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onKey(int arg0, int[] arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPress(int keyCode) {
		// TODO Auto-generated method stub
		spaceHandled = false;
		shiftHandled = false;
	}

	@Override
	public void onRelease(int keyCode) {
		if(mKeys.containsKey(keyCode)){
			if (isShifted && !(isPersistent)) {
				changeLayout("default");
				isShifted = false;
			}
			commitText(mKeys.get(keyCode).label);
		}
		else{
			handleSpecialInput(keyCode);
		}
		
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
	
	private String getLabel(int keyCode){
		if(isShifted){
			return mKeys.get(keyCode + 26).label;
		}
		else if(inSymbolMode){
			if(keyCode == 53){
				return "'";
			}
			return mKeys.get(keyCode + 54).label;
		}
		return mKeys.get(keyCode).label;
	}
	
	/**
	 * Changes the layout of the keyboard
	 * 
	 * @param layout
	 *            name string of the layout resource
	 */
	private void changeLayout(int keyCode) {
		List<Key> keys = mKeyboardView.getKeyboard().getKeys();
		for(Key key: keys){
			if(mKeys.containsKey(keyCode)){
				key.label = getLabel(keyCode);
			}
		}
		mKeyboardView.invalidateAllKeys();
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

}
