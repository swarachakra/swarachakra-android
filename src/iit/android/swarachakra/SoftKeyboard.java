package iit.android.swarachakra;

import iit.android.language.ExceptionHandler;
import iit.android.language.Language;
import iit.android.language.english.English;
import iit.android.language.telugu.MainLanguage;
import iit.android.language.telugu.MainLanguageExceptionHandler;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.RelativeLayout;
public class SoftKeyboard extends InputMethodService {
	private MainKeyboardView mKeyboardView;
	private Keyboard mKeyboard;
	private HashMap<Integer, KeyAttr> mKeys;
	private HashMap<Integer, KeyAttr> mainKeys;
	private HashMap<Integer, KeyAttr> englishKeys;
	private MainKeyboardActionListener mActionListener;
	private MainLanguage mainLanguage;
	private English english;
	private Language language;
	private InputConnection mInputConnection;
	private String languageName = "";
	private Context mContext;
	private String displayMode;
	private Key mEnterKey;

	@Override
	public void onInitializeInterface() {
		mainLanguage = new MainLanguage();
		mainKeys = mainLanguage.hashThis();

		english = new English();
		englishKeys = english.hashThis();

		if (languageName == "") {
			setLanguage("main");
		} else {
			setLanguage(languageName);
		}

	}

	@Override
	public View onCreateInputView() {
		mContext = this;
		if (languageName == "") {
			setLanguage("main");
		} else {
			setLanguage(languageName);
		}
		detectDisplayMode();
		int keyboardViewResourceId = getKeyboardViewResourceId();

		final RelativeLayout layout = (RelativeLayout) getLayoutInflater()
				.inflate(keyboardViewResourceId, null);
		mKeyboardView = (MainKeyboardView) layout.findViewById(R.id.keyboard);

		int resourceId = getResourceId("default");
		mKeyboard = new Keyboard(this, resourceId);
		mKeyboardView.setKeyboard(mKeyboard);

		mActionListener = new MainKeyboardActionListener();
		mKeyboardView.setOnKeyboardActionListener(mActionListener);
		initActionListener();

		updateFullscreenMode();

		setKeys();
		mKeyboardView.invalidateAllKeys();

		initSwaraChakra();

		printHash();

		return layout;
	}

	private int getKeyboardViewResourceId() {
		String file = "kview_" + displayMode + languageName;
		Log.d("chakra", "filename = " + file);
		int output = getResources().getIdentifier(file, "layout",
				getPackageName());

		return output;
	}

	@SuppressLint("NewApi")
	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		mInputConnection = getCurrentInputConnection();
		mActionListener.setInputConnection(mInputConnection);
		mKeyboardView.setAlpha(1);
		setImeOptions();
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		return false;
	}

	// @Override
	// public void onUpdateExtractingVisibility(EditorInfo ei) {
	// setExtractViewShown(false);
	// }

	private void initSwaraChakra() {
		String[] swaras = language.defaultChakra;
		boolean halantExists = language.halantExists;
		SwaraChakra.setHalantExists(halantExists);
		SwaraChakra.setDefaultChakra(swaras);
	}

	private void setKeys() {
		List<Key> keys = mKeyboard.getKeys();
		for (Key key : keys) {
			if (mKeys.containsKey(key.codes[0])) {
				KeyAttr tempKey = mKeys.get(key.codes[0]);
				key.label = tempKey.label;
				if (tempKey.showIcon) {
					int id = getDrawableId(tempKey.icon);
					if (id != 0) {
						key.icon = getResources().getDrawable(id);
						key.label = null;
						Log.d("Location", "set icon " + key.icon);
					}
				}
			}
		}
		setImeOptions();
	}

	private void initActionListener() {
		mKeyboardView.initListener();
		mActionListener.setKeysMap(mKeys);
		mActionListener.setHalantEnd(language.halantEnd);
		mActionListener.setSoftKeyboard(this);
		mInputConnection = getCurrentInputConnection();
		mActionListener.setInputConnection(mInputConnection);

		if (languageName == "main") {
			ExceptionHandler exceptionHandler = new MainLanguageExceptionHandler(mainLanguage);
			mActionListener.setExceptionHandler(exceptionHandler);
		}
	}

	public void changeKeyboard(String layoutFile) {
		int resourceId = getResourceId(layoutFile);
		if (resourceId != 0) {
			mKeyboard = new Keyboard(mContext, resourceId);
			setKeys();
			mKeyboardView.setKeyboard(mKeyboard);
		} else {
			Log.d("layout", "you suck");
		}
	}

	public void setLanguage(String name) {
		languageName = name;
		if (name == "english") {
			language = english;
			mKeys = englishKeys;
		} else {
			language = mainLanguage;
			mKeys = mainKeys;
		}
	}

	public void changeLanguage() {
		if (languageName == "main") {
			language = english;
			languageName = "english";
			setLanguage("english");
		} else {
			language = mainLanguage;
			languageName = "main";
			setLanguage("main");
		}
		setInputView(onCreateInputView());
	}

	public void restartKeyboard() {
		int resourceId = getResourceId("default");
		mKeyboard = new Keyboard(this, resourceId);
		mKeyboardView.setKeyboard(mKeyboard);

		initActionListener();

		setKeys();
		mKeyboardView.invalidateAllKeys();
		initSwaraChakra();
	}

	public void detectDisplayMode() {
		int dispMode = getResources().getConfiguration().orientation;

		if (dispMode == 1) {
			displayMode = "";
		} else {
			displayMode = "land_";
		}
	}

	public int getResourceId(String layoutFile) {
		int resourceId = 0;
		resourceId = getResources().getIdentifier(
				languageName + "_" + displayMode + layoutFile, "layout",
				getPackageName());
		return resourceId;
	}

	public int getDrawableId(String layoutFile) {
		int resourceId = 0;
		resourceId = getResources().getIdentifier(layoutFile, "drawable",
				getPackageName());
		Log.d("Location", "R id " + resourceId);
		return resourceId;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mInputConnection != null) {
			mInputConnection.setComposingText("", 1);
			mInputConnection.finishComposingText();
			mKeyboardView.dismissChakra();
		}
		super.onConfigurationChanged(newConfig);
	}

	void setImeOptions() {
		Resources res = getResources();
		EditorInfo ei = getCurrentInputEditorInfo();
		int options = ei.imeOptions;
		for(Key k: mKeyboard.getKeys()) {
			if(k.codes[0]==mKeyboardView.ENTER){
				mEnterKey = k;
			}		
		}
		if (mEnterKey == null) {
			return;
		}

		switch (options
				& (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
		case EditorInfo.IME_ACTION_GO:
			mEnterKey.iconPreview = null;

			mEnterKey.label = "Go";
			// mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
			break;
		case EditorInfo.IME_ACTION_NEXT:
			mEnterKey.iconPreview = null;
			mEnterKey.icon = null;
			// mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
			mEnterKey.label = "Next";
			break;
		case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT:
			mEnterKey.iconPreview = null;
			mEnterKey.icon = null;
			mEnterKey.label = "NEXT";
			break;
		case EditorInfo.IME_ACTION_DONE:
			mEnterKey.iconPreview = null;
			mEnterKey.icon = null;
			mEnterKey.label = "Done";

			break;
		case EditorInfo.IME_ACTION_SEARCH:
			mEnterKey.icon = res.getDrawable(R.drawable.ic_action_search);
			break;
		case EditorInfo.IME_ACTION_SEND:
			mEnterKey.iconPreview = null;
			mEnterKey.label = "Send";
			break;
		default:
			mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
			mEnterKey.label = null;
			break;
		}
		mKeyboardView.invalidateAllKeys();
	}

	private void printHash() {
		for (KeyAttr key : mKeys.values()) {
			int code = key.code;
			Log.d("Parser", Integer.toString(code) + " " + key.isException);
		}
	}
}
