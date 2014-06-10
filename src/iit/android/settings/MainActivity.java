package iit.android.settings;

import iit.android.swarachakra.R;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private boolean isDefault = false;
	private boolean isEnabled = false;
	private boolean doneSettingUp = false;
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		doneSettingUp = false;
		Log.d("textBox", "from onCreate");
		setTextBoxes();		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
    public void onWindowFocusChanged(final boolean hasFocus) {
		if(!doneSettingUp) {
			Log.d("textBox", "from onWindowFocueChanged");
			setTextBoxes();
		}
        super.onWindowFocusChanged(hasFocus);
    }
	
	public void buttonClick(View v) {
		checkKeyboardStatus();
		changeKeyboardStatus();
		if(!doneSettingUp) {
			setTextBoxes();
		}
	}
	
	public void checkKeyboardStatus() {
		InputMethodManager mgr =  (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> lim = mgr.getEnabledInputMethodList();
		isEnabled = false;
		isDefault = false;
		
		for(InputMethodInfo im : lim) {
			// Log.d("main", ""+ im.getPackageName() + "***" + im.getId());
			if(im.getPackageName().equals(getPackageName())) {
				Log.d("main","###################### Package names are equal" );
				isEnabled = true;
				final String currentImeId = Settings.Secure.getString(
		                getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		        
				if(im!=null && im.getId().equals(currentImeId)) {
					isDefault = true;
					Log.d("main", "################### Set isDefault to true");
				}
			}
		}
		
	}
	
	public int getStageNumber() {
		int stageNumber = 0;
		checkKeyboardStatus();
		if(!isEnabled) {
			stageNumber = 0;
		}
		else {
			if(!isDefault) {
				stageNumber = 1;
			}
			else {
				stageNumber = 2;
			}
		}
		
		return stageNumber;
	}
	
	public void setTextBoxes() {
		int stageNumber = getStageNumber();
		setTextBoxes(stageNumber);
	}
	
	public void setTextBoxes(int stageNumber) {	
		Log.d("textBox", "Called with stageNumber: " + stageNumber);
		TextView v1 = (TextView) findViewById(R.id.textView1);
		TextView v2 = (TextView) findViewById(R.id.textView2);
		EditText e = (EditText) findViewById(R.id.editText1);
		Button b = (Button) findViewById(R.id.button1);
		switch(stageNumber) {
		case 0:
			v1.setText("If you can read the following text in Hindi, proceed with the set up. If you see boxes instead, your device is not compatible. ");
			v2.setText("आप यदि ये पढ़ पा रहे है तो कृपया नेक्स्ट दबायें।");
			v1.setVisibility(View.VISIBLE);
			v2.setVisibility(View.VISIBLE);
			b.setVisibility(View.VISIBLE);
			e.setVisibility(View.GONE);
			break;
		case 1:
			v1.setText("You have Enabled Swarachakra, you have to set it to default to use it. Please press next.");
			v2.setText("आपने स्वरचक्र को इनेबल किया है। उसका इस्तेमाल करने के लिए एक और बार नेक्स्ट दबायें। ");
			v1.setVisibility(View.VISIBLE);
			v2.setVisibility(View.VISIBLE);
			e.setVisibility(View.GONE);
			b.setVisibility(View.VISIBLE);
			break;
		case 2:
			v1.setText("Congratulations! Enjoy typing in our National Language. ");
			v2.setText("बधाईयाँ। स्वरचक्र प्रयोग करने के लिए तैयार है। ");
			v1.setVisibility(View.VISIBLE);
			v2.setVisibility(View.VISIBLE);
			e.setVisibility(View.GONE);
			b.setVisibility(View.VISIBLE);
			break;
		case 3:
			v1.setVisibility(View.GONE);
			v2.setVisibility(View.GONE);
			e.setVisibility(View.VISIBLE);
			b.setVisibility(View.GONE);
			break;
		default:
			v1.setText("If you can read the following text in Hindi, proceed with the set up. If you see boxes instead, your device is not compatible. ");
			v2.setText("आप यदि ये पढ़ पा रहे है तो कृपया नेक्स्ट दबायें।");
			e.setVisibility(View.GONE);
			b.setVisibility(View.VISIBLE);
			v1.setVisibility(View.VISIBLE);
			v2.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	public void changeKeyboardStatus() {
		if (isEnabled) {
			if(isDefault) {
				Context context = getApplicationContext();
				CharSequence text = "Swarachakra is now default. It takes a little while for it's first launch. Please click on the Textbox.";
				int duration = Toast.LENGTH_LONG;

				toast = Toast.makeText(context, text, duration);
				toast.show();
				doneSettingUp = true;
				setTextBoxes(3);
			} 
			else {
				makeDefaultDialog();
			}
		}
		else {
			enableDialog();
		}
	}
	
	public void enableDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage("In the next screen check the checkbox corresponding to swarachakra and press back.")
	       .setTitle("Enable Swarachakra");
		
		// Add the buttons
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		        	   startActivityForResult( new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
		           }
		       });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               dialog.dismiss();
		           }
		       });

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void makeDefaultDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage("Make Swarachakra default by choosing it on the next screen")
	       .setTitle("Make Swarachakra default");
		
		// Add the buttons
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   // dialog.dismiss();
		        	   InputMethodManager mgr =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		        	   mgr.showInputMethodPicker();
		           }
		       });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               dialog.dismiss();
		           }
		       });

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}