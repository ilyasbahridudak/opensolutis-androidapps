package com.android.updates;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import lift.maintenance.android.ActivitySettings;
import lift.maintenance.android.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class UpdateManager {
	
	
	/**
	 * Check if there is possible to install updates, if there is an update available and if user accept to install update.
	 * Download the latest version, and install update.
	 * @param context : Context of the current application.
	 * @param url : String containing the application server path.
	 * @param appName : String containing the application name on the server. 
	 * @return -1 non market applications are not allowed
	 * 		   -2 unable to read version.txt
	 * 		   -3 Download error
	 * 		   -4 Read downloaded file error
	 * 		    0 No updates
	 */
	public static int update(final Context context, final String url, final String appName){
		/*check if non market applications are allowed*/
		if(canUpdate(context)){
			try {
				//get current version number
				String curentVersion = getVersion(context);
				
				//get the latest version number
				final String lastVersion = retrieveNewVersionCode(url);
				
				//check if the versions are not the same
				if(!curentVersion.equals(lastVersion)){
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
    				builder.setMessage(R.string.UpdateMessage).setTitle(R.string.UpdateTitle)
    				.setPositiveButton(context.getString(R.string.Yes), new DialogInterface.OnClickListener()
    				{ 
    					public void onClick(DialogInterface dialog, int id){
    						String downloadedFileName = appName + "-v" + lastVersion + ".apk";
    						
    						try {
    							InputStream is = getContentInputStream(url + downloadedFileName);

    							OutputStream out = context.openFileOutput(downloadedFileName, Context.MODE_WORLD_READABLE);

    							try {
    								int byteCount = 0;
    								byte[] buffer = new byte[1024];
    								int bytesRead = -1;
    								while ((bytesRead = is.read(buffer)) != -1) {
    									out.write(buffer, 0, bytesRead);
    									byteCount += bytesRead;
    								}
    								out.flush();
    								Log.d("Update", String.valueOf(byteCount));
    							} finally {
    								try {
    									is.close();
    								} catch (IOException ex) {
    									Log.e("Update", ex.getMessage());
    								}
    								try {
    									out.close();
    								} catch (IOException ex) {
    									Log.e("Update", ex.getMessage());
    								}
    								
    								File downloadedFile = context.getFileStreamPath(downloadedFileName);
    								
    								Intent i = new Intent();
    								i.setAction(Intent.ACTION_VIEW);
    								i.setDataAndType(Uri.fromFile(downloadedFile), "application/vnd.android.package-archive");
    								context.startActivity(i);
    							}
    						} catch (ClientProtocolException e) {
    							e.printStackTrace();
    							Log.e("Update", e.getMessage());
    							//return -3; //Download error
    						} catch (IOException e) {
    							e.printStackTrace();
    							Log.e("Update", e.getMessage());
    							//return -4; //Read downloaded file error
    						}
    					}
    				})
    				.setNegativeButton(context.getString(R.string.No), new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {} })
    				.create().show();
    				return 1;
				}
				else{
					Log.i("Update", "No updates");
					return 0; //No updates
				}
			} catch (IOException e) {
				Log.e("Update", e.getMessage());
				return -2;//unable to read version.txt
			}
		}
		else{
			Log.e("Update", "Can't install non market app");
			return -1; // non market applications are not allowed
		}
	}
	
	/**
	 * Get the version of the current application
	 * @param context of the current application to get the version number
	 * @return String containing version name an version code
	 */
	private static String getVersion(Context context){
		PackageInfo packageInfo;
		
		String packageName = context.getPackageName();
		
		try {

		    packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);

		} catch (NameNotFoundException e) {

		    throw new IllegalStateException("Should not happen", e);

		}

		int versionCode = packageInfo.versionCode;

		String versionName = packageInfo.versionName;
		
		return versionName + "." + versionCode;
	}

	/**
	 * Verify if the device is configured or not to allow setup of non market applications
	 * @param context of the current application.
	 * @return true if non market applications are allowed, false if not.
	 */
	private static Boolean canUpdate(Context context){
		int allowNonMarket = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		if(allowNonMarket == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * Read version.txt on to get the last version available
	 * @param url : path where is located the version.txt corresponding
	 * @return the version name and code of the latest version available
	 * @throws IOException
	 */
	private static String retrieveNewVersionCode(String url) throws IOException {
		url += "version.txt";

		InputStream is = getContentInputStream(url);
		String versionCode = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			versionCode = br.readLine();
		} finally {
			is.close();
		}
		return versionCode;
	}
	
	/**
	 * get the content of a file
	 * @param url of the file to read
	 * @return content of the file
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private static InputStream getContentInputStream(String url) throws IOException, ClientProtocolException {
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setSoTimeout(params, 3000);
		HttpConnectionParams.setConnectionTimeout(params, 3000);

		DefaultHttpClient client = new DefaultHttpClient(params);

		HttpResponse response = client.execute(new HttpGet(url));

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new RuntimeException("Response status code was not 200 but " + statusCode);
		}

		return response.getEntity().getContent();
	}
}
