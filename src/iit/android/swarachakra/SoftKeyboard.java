package iit.android.swarachakra;

import iit.android.language.Language;
import iit.android.language.english.English;
import iit.android.language.hindi.MainLanguage;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.RelativeLayout;
/**
 * Input Method Service that runs when the keyboard is up and manages the whole life cycle of the keyboard 
 * @author Manideep Polireddi, Madhu Kiran
 *
 */
public class SoftKeyboard extends InputMethodService {
	
	private CustomKeyboardView mKeyboardView;
	private Keyboard mKeyboard;
	private HashMap<Integer, KeyAttr> mKeys;
	private HashMap<Integer, KeyAttr> mainKeys;
	private HashMap<Integer, KeyAttr> englishKeys;
	private MainLanguage mainLanguage;
	public String mainLanguageSymbol;
	private English english;
	private Language language;
	private InputConnection mInputConnection;
	private String languageName = "";
	private Context mContext;
	private String displayMode;
	private Key mEnterKey;
	private KeyLogger mKeyLogger;

	@Override
	public void onCreate() {
		super.onCreate();
		Installation.id(getApplicationContext());
		mKeyLogger = new KeyLogger();
		mKeyLogger.setSoftKeyboard(this);
        mKeyLogger.extractedText="";
	}
	
	@Override
	public void onInitializeInterface() {
		mainLanguage = new MainLanguage();
		mainKeys = mainLanguage.hashThis();
		mainLanguageSymbol = mainLanguage.symbol;

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
		
		if(languageName == "main"){
			mKeyboardView = (MainKeyboardView) layout.findViewById(R.id.keyboard);
		}
		else{
			mKeyboardView = (EnglishKeyboardView) layout.findViewById(R.id.keyboard);
		}

		int resourceId = getResourceId("default");
		mKeyboard = new Keyboard(this, resourceId);
		mKeyboardView.setKeyboard(mKeyboard);

		mKeyboardView.init(this, language, mKeys);

		updateFullscreenMode();

		setKeys();
		mKeyboardView.invalidateAllKeys();


		return layout;
	}
	
	@Override
	public void onFinishInputView(boolean finishingInput) {
		super.onFinishInputView(finishingInput);
		
		mKeyLogger.writeToLocalStorage();
		mKeyLogger.extractedText="";
	}
	
	/**
	 * Generates the layout resource id for the keyboard view based on the displayMode and current language
	 * @return layout resource id of the keyboard view to be shown 
	 */
	private int getKeyboardViewResourceId() {
		String file = "kview_" + displayMode + languageName;
		Log.d("chakra", "filename = " + file);
		int output = getResources().getIdentifier(file, "layout",
				getPackageName());

		return output;
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		mInputConnection = getCurrentInputConnection();
		mKeyboardView.resetInputConnection(mInputConnection);
		mKeyboardView.setAlpha(1);
		setImeOptions();
		
		String curDisplayMode = displayMode;
		detectDisplayMode();
		if(displayMode != curDisplayMode){
			setInputView(onCreateInputView());
		}
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		return false;
	}

	/**
	 * sets the labels to the keys on the keyboard 
	 */
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

	/**
	 * changes the keyboard in the keyboardView
	 * @param layoutFile	layout id of the layout to be loaded into the keyboardView
	 */
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
	
	/**
	 * sets the current language and keys hashmap according to the language
	 * @param name	name of the language
	 */
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
	
	/**
	 * Changes the language of the keyboard from english to main language and vice-versa
	 */
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
	
	public static boolean isTablet(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String key = context.getResources().getString(R.string.tablet_layout_setting_key);
		boolean showTabletLayout = settings.getBoolean(key, false);
		return showTabletLayout;
	}
	
	/**
	 * Detects the display config(landscape or portrait) and sets the displayMode accordingly
	 */
	public void detectDisplayMode() {
		int dispMode = getResources().getConfiguration().orientation;

		if (dispMode == 1) {
			displayMode = "";
			if(isTablet(mContext)){displayMode = "tablet_";}
		} else {
			displayMode = "land_";
		}
	}

	/**
	 * Gets the layout file resource id of the keyboard based on displayMode and languageName 
	 * @param layoutFile	layout of the keyboard whose resource id is to be returned
	 * @return Resource id of the layout file of the keyboard to be shown 
	 */
	public int getResourceId(String layoutFile) {
		int resourceId = 0;
		resourceId = getResources().getIdentifier(
				languageName + "_" + displayMode + layoutFile, "layout",
				getPackageName());
		return resourceId;
	}

	/**
	 * Gets the resource id of the drawable
	 * @param drawable	drawable name of whose resource id is to be returned
	 * @return Drawable id in the resources 
	 */
	public int getDrawableId(String drawable) {
		int resourceId = 0;
		resourceId = getResources().getIdentifier(drawable, "drawable",
				getPackageName());
		Log.d("Location", "R id " + resourceId);
		return resourceId;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mInputConnection != null) {
			mInputConnection.setComposingText("", 1);
			mInputConnection.finishComposingText();
			mKeyboardView.configChanged();;
		}
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Changes the appearance of the enter key based on IME options
	 */
	void setImeOptions() {
		Resources res = getResources();
		EditorInfo ei = getCurrentInputEditorInfo();
		int options = ei.imeOptions;
		for(Key k: mKeyboard.getKeys()) {
			if(k.codes[0]==mKeyboardView.getEnterKeyCode()){
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
	
	/**
	 * Gets the KeyLogger of this SoftKeyboard service
	 * @return KeyLogger of this SoftKeyboard service
	 */
	public KeyLogger getKeyLogger() {
		return mKeyLogger;
	}
}
