package iit.android.swarachakra;

import iit.android.language.Language;

import java.util.HashMap;

import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.view.inputmethod.InputConnection;

public class EnglishKeyboardView extends CustomKeyboardView {
	
	private HashMap<Integer, KeyAttr> mKeys;
	private EnglishKeyboardActionListener mActionListener;

	public EnglishKeyboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public EnglishKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(SoftKeyboard sk, Language lang, HashMap<Integer, KeyAttr> keys){
		mKeys = keys;
		
		mActionListener = new EnglishKeyboardActionListener();
		this.setOnKeyboardActionListener(mActionListener);
		this.setOnTouchListener(mActionListener);
		mActionListener.initialize(this);
		mActionListener.setSoftKeyboard(sk);
		mActionListener.setKeysMap(mKeys);
		InputConnection mInputConnection = sk.getCurrentInputConnection();
		mActionListener.setInputConnection(mInputConnection);
	}
	
	@Override
	protected boolean onLongPress(Key key) {
	    mActionListener.onLongPress(key);
		return super.onLongPress(key);
	}
	
	@Override
	public void resetInputConnection(InputConnection ic){
		mActionListener.setInputConnection(ic);
	}
}
