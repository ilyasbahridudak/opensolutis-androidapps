package com.android.updates;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;

public class UpdateManager {
	
	public static String getVersion(Context context){
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

	public static Boolean canUpdate(Context context){
		int allowNonMarket = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		if(allowNonMarket == 0)
			return false;
		else
			return true;
	}
	
	
}
