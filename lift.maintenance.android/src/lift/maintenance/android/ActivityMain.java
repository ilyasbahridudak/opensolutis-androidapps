package lift.maintenance.android;

import lift.maintenance.android.dal.DataBaseManager;
import lift.maintenance.android.dal.Synchro;
import lift.maintenance.android.service.ServiceSync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ActivityMain extends Activity {
    
	private Context context;
	private SharedPreferences prefs;
	private DataBaseManager manager;
	
	private Button bCheckList;
	private Button bMaintenances;
	private Button bIncidents;
	private ProgressDialog mProgressDialog;
	final Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
	    	case 1:
	    		ServiceSync.showMessage(getApplicationContext(), (String) getText(R.string.BeginSync));
	    		break;
	    	case 2:
	    		SharedPreferences.Editor editor=prefs.edit();
	    		editor.putBoolean("can_sync", true);
	    		editor.commit();
	    		refreshButtons();
	    		mProgressDialog.dismiss();
	    		break;
	    	}
	    }
	};	
		
	//événement de création de la vue
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        manager = new DataBaseManager(context);
        
        SharedPreferences.Editor editor=prefs.edit();
		editor.putBoolean("can_sync", true);
		editor.commit();
		
        bCheckList = (Button) findViewById(R.id.bCheckList);
        bCheckList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMain.this, ActivityListView.class);
				intent.putExtra("view", 1);
				startActivity(intent);
			}
		});
        
        bIncidents = (Button) findViewById(R.id.bIncidents);
        bIncidents.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMain.this, ActivityListView.class);
				intent.putExtra("view", 3);
				startActivity(intent);
			}
        });
        bMaintenances = (Button) findViewById(R.id.bMaintenances);
        bMaintenances.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMain.this, ActivityListView.class);
				intent.putExtra("view", 4);
				startActivity(intent);
			}
        });
        
        if(!ServiceSync.isStarted(context)){
        	startService(new Intent(ActivityMain.this, ServiceSync.class));
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	refreshButtons();
    }
    
    private void refreshButtons() {
    	bMaintenances.post(new Runnable() {
			public void run() {
				manager.open();
				bMaintenances.setText(manager.intervention.getNbMaintenance(true) + " " + getString(R.string.maintenance) + " (" + manager.intervention.getNbMaintenance(false) + ")");
				manager.close();
			}
		});
    	bIncidents.post(new Runnable() {
			public void run() {
				manager.open();
				bIncidents.setText(manager.intervention.getNbIncidents(true) + " " + getString(R.string.incidents) + " (" + manager.intervention.getNbIncidents(false) + ")");
				manager.close();
			}
    	});
	}

	//création du menu contectuel
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// instanciation du menu via le fichier XML
    	new MenuInflater(getApplication()).inflate(R.layout.menu, menu);
    	
    	//theMenu = menu;
    	// Création du menu 
    	getLayoutInflater().setFactory(new Factory() {
    		public View onCreateView(String name, Context context, AttributeSet attrs) {
	
		    	if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
			    	try {
				    	LayoutInflater f = getLayoutInflater();
				    	final View view = f.createView(name, null, attrs);
				
				    	new Handler().post(new Runnable() {
					    	public void run() {
						    	// set the background drawable
						    	view.setBackgroundResource(R.drawable.menu_blue);
						    	
					    	}
				    	});
				    	return view;
				    } catch (InflateException e) {
			    	} catch (ClassNotFoundException e) {
			    	}
			    }
		    	return null;
	    	}
    	});
    	return (super.onCreateOptionsMenu(menu));
    }
    
    // Instanciation de l’action associée à la sélection d’un item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId()==R.id.itSettings){
    		if(prefs.getBoolean("can_sync", true)){
    			Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
    			startActivity(intent);
    		}
    		else {
    			ServiceSync.showMessage(getApplicationContext(), (String) getText(R.string.cantEditSetting));
    		}
    	} else if(item.getItemId()==R.id.itSync){
    		if(prefs.getBoolean("can_sync", true)){
    			mProgressDialog  = ProgressDialog.show(ActivityMain.this, getString(R.string.synchroWaitTitle), getString(R.string.synchroManWaitMessage), true);
    			
    			if(ServiceSync.isStarted(context))
	    			stopService(new Intent(ActivityMain.this, ServiceSync.class));
    			
    			SharedPreferences.Editor editor=prefs.edit();
				editor.putBoolean("can_sync", false);
				editor.commit();
				
				new Thread((new Runnable() {
			        public void run() {
			        	mHandler.sendEmptyMessage(1);
			        	
			        	String message = Synchro.synchro(prefs, context, manager);

			            mHandler.sendEmptyMessage(2);
			            
			            startService(new Intent(ActivityMain.this, ServiceSync.class));
			            ServiceSync.showMessage(getApplicationContext(), message);
			        }
				})).start();
    		}
    		else
				ServiceSync.showMessage(getApplicationContext(), (String) getText(R.string.allready_sync));
    	}
    	return (super.onOptionsItemSelected(item));
    }
    
    //fonction de contruction d'une boite de dialogue
    public static void BuildDialog(int Title, int Message, Context context){
		Builder alertDialog=new AlertDialog.Builder(context);
		alertDialog.setTitle(Title);
		alertDialog.setMessage(Message);
		
		alertDialog.setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dlg, int sumthin) {	} });
		alertDialog.show();
	}
}