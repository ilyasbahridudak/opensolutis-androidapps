package lift.maintenance.android.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lift.maintenance.android.ActivityMain;
import lift.maintenance.android.R;
import lift.maintenance.android.dal.DataBaseManager;
import lift.maintenance.android.dal.Synchro;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ServiceSync extends Service {

	private static final int NOTIFICATION = R.string.title;
	private NotificationManager mNM;
	private int freq;
	private int next;
	private Boolean isSync;
	private SharedPreferences prefs;
	private Timer timer = new Timer();
	private static Handler handler;
	private DataBaseManager manager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		freq = prefs.getInt("freq", 0);

		if(freq == 0)
			freq = 10;
		
		next = freq * 60;
		isSync = false;
		
		handler = new Handler(Looper.getMainLooper());
		
		manager = new DataBaseManager(getApplicationContext());
		
		timer = new Timer((String) getText(NOTIFICATION)  + " timer");
	    timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!isSync){
					if(next > 0){
						next--;
					}
					else{
						isSync = true;
						SharedPreferences.Editor editor=prefs.edit();
						editor.putBoolean("can_sync", false);
						editor.commit();
						showMessage(getApplicationContext(), (String) getText(R.string.BeginSync));
						showMessage(getApplicationContext(), Synchro.synchro(prefs, getApplicationContext(), manager));
						next = freq *60;
						editor.putBoolean("can_sync", true);
						editor.commit();
						isSync = false;
					}
				}
			}
		}, 1000, 1000);
		
		showNotification();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mNM.cancel(NOTIFICATION);
		timer.cancel();
	}
	
	private void showNotification() {
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.lift, getText(R.string.StartService), System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        Intent main = new Intent(this, ActivityMain.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, main, 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(NOTIFICATION) + " " + freq, getText(R.string.StartService), contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
	
	public static void showMessage(final Context context, final String Message){
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public static Boolean isStarted(Context context){
		Boolean active = false;
		
		ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
		         
		for (int i=0; i<rs.size(); i++) {
		  ActivityManager.RunningServiceInfo
		  rsi = rs.get(i);
		  if(rsi.service.getClassName().equals(ServiceSync.class.getName()))
			  active = true;
		}
		
		return active;
	}
}
