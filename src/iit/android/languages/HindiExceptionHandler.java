package iit.android.languages;

import iit.android.swarachakra.ExceptionHandler;
import iit.android.swarachakra.KeyAttr;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;

public class HindiExceptionHandler implements ExceptionHandler {
	
	private ArrayList<KeyAttr> keyArray;
	private HashMap<Integer, KeyAttr> mKeys;
	private Language hindi;
	
	private static final String RA = "\u0930";	
	private static final String HALANT = "\u094D";
	private static final String NUKTA = "\u093C";
	
	private static final int RAFARCODE = 53;
	private static final int TRAKARCODE = 52;
	private static final int NUKTACODE = 51;
	
	public HindiExceptionHandler(Language lang){
		hindi = lang;
		initializeKeyArray();
	}
	
	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, KeyAttr> handleException(int keyCode){
		HashMap<Integer, KeyAttr> sKeys = new HashMap<Integer, KeyAttr>();
		switch(keyCode){
		case NUKTACODE:
			handleNukta(sKeys);
			break;
		case TRAKARCODE:
			handleTrakar(sKeys);
			break;
		case RAFARCODE:
			handleRafar(sKeys);
			break;
		}
		initializeKeyArray();
		return sKeys;
	}
	
	private void initializeKeyArray(){
		keyArray = new ArrayList<KeyAttr>();
		int halantEnd = hindi.halantEnd;
		for(int i = 0; i < halantEnd; i++){
			KeyAttr key = new KeyAttr();
			key.code = i+1;
			keyArray.add(key);
			mKeys = hindi.hashThis();
		}
	}

	private void handleRafar(HashMap<Integer, KeyAttr> sKeys) {
		for(KeyAttr key : keyArray){
			String newLabel = RA + HALANT + mKeys.get(key.code).label;
			key.label = newLabel;
			key.showChakra = true;
			sKeys.put(key.code, key);
		}
	}

	private void handleTrakar(HashMap<Integer, KeyAttr> sKeys) {
		for(KeyAttr key : keyArray){
			String newLabel = mKeys.get(key.code).label + HALANT + RA;
			key.label = newLabel;
			key.showChakra = true;
			sKeys.put(key.code, key);
		}
	}

	private void handleNukta(HashMap<Integer, KeyAttr> sKeys) {
		int[] temp = {1,2,3,8,13,14,20,22,31,32};
		ArrayList<Integer> nuktaKeys = new ArrayList<Integer>();
		for(int i = 0; i < temp.length; i++){
			nuktaKeys.add(temp[i]);
		}
		for(KeyAttr key : keyArray){
			if(nuktaKeys.contains(key.code)){
				String newLabel = mKeys.get(key.code).label + NUKTA;
				key.label = newLabel;
				key.showChakra = true;
			}
			sKeys.put(key.code, key);
		}
	}
	
}
