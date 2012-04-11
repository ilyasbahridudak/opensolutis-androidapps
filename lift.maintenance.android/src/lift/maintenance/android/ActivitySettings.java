package lift.maintenance.android;

import com.xmlrpc.access.xmlrpcAccess;

import java.lang.Integer;

import lift.maintenance.android.dal.DataBaseManager;
import lift.maintenance.android.dal.Synchro;
import lift.maintenance.android.service.ServiceSync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivitySettings extends Activity {
	
	private Button bValider;
	private EditText etLogin;
	private EditText etPass;
	private EditText etURL;
	private EditText etBase;
	private EditText etFreq;
	private Context context;
	private SharedPreferences prefs;
	private xmlrpcAccess access;
	
	private ProgressDialog mProgressDialog;
	
	//événement de création de la vue
	public void onCreate(Bundle savedInstanceState) {
		//affichage de la vue
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        //lecture des préférences
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        //liason avec les EditText de la vue
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPass = (EditText) findViewById(R.id.etPass);
        etURL = (EditText) findViewById(R.id.etURL);
        etBase = (EditText) findViewById(R.id.etBase);
        etFreq = (EditText) findViewById(R.id.etFreq);
        
        //afficher les préférences dans les EditText
        etLogin.setText(prefs.getString("login", ""));
		etPass.setText(prefs.getString("pass", ""));
		etURL.setText(prefs.getString("URL", ""));
		etBase.setText(prefs.getString("base", ""));
		int freq = prefs.getInt("freq", 0);
		if(freq != 0)
		    etFreq.setText(Integer.toString(freq));
		
		//création de l'évenement pour le click sur le bouton valider
        bValider = (Button) findViewById(R.id.bValidSettings);
        bValider.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		if(Connect()){
        			if(ServiceSync.isStarted(context)){
        	        	stopService(new Intent(ActivitySettings.this, ServiceSync.class));
        	        }
        			
        			if(prefs.getString("login", "") == "" || prefs.getString("login", "").equals(etLogin.getText().toString())){
        				Save();
        				finish();
        				startService(new Intent(ActivitySettings.this, ServiceSync.class));
        			}
        			else{
        				AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySettings.this);
        				builder.setMessage(R.string.newUserMessage).setTitle(R.string.newUserTitle)
        				.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener()
        				{
        					public void onClick(DialogInterface dialog, int id)
        					{
        						mProgressDialog  = ProgressDialog.show(ActivitySettings.this, getString(R.string.synchroWaitTitle), getString(R.string.synchroNewWaitMessage), true);
        						
        						new Thread((new Runnable() {
        					        public void run() {
        					        	Synchro.supressInterventions(prefs, context,  new DataBaseManager(context));
                						
        					            mHandler.sendEmptyMessage(1);
        					            
        					            Synchro.synchroInterventions(prefs, context, new DataBaseManager(context));
        					            
        					            mHandler.sendEmptyMessage(2);
        					            
        					            startService(new Intent(ActivitySettings.this, ServiceSync.class));
        					            finish();
        					        }
        					        
        						})).start();
        						
        						
        					}
        				})
        				.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Save();
								startService(new Intent(ActivitySettings.this, ServiceSync.class));
								finish();
							}
						})
						.setNeutralButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								startService(new Intent(ActivitySettings.this, ServiceSync.class));
							}})
						.create().show();
        			}
        		}
        	}
        });
        
        //instanciation de l'access xmlRPC
        access = new xmlrpcAccess();
	}
	
	//enregistrement des valeurs saisies par l'utilisateur
	private void Save(){
		SharedPreferences.Editor editor=prefs.edit();
		editor.putString("login", etLogin.getText().toString());
		editor.putString("pass", etPass.getText().toString());
		editor.putString("URL", etURL.getText().toString());
		editor.putString("base", etBase.getText().toString());
		
		//verification de la valeur saisie pour frequency 
		if(etFreq.getText().toString() != ""){
			try{
				editor.putInt("freq", Integer.parseInt(etFreq.getText().toString()));
			}catch(NumberFormatException e){
				etFreq.setText("");
				editor.putInt("freq", 0);
			}
		}
		else
			editor.putInt("freq", 0);
		editor.commit();
	}
	
	//tests de connection
	private Boolean Connect(){
		//tentative de connection
		int rep = access.Connect(etURL.getText().toString(), etBase.getText().toString(),
				etLogin.getText().toString(), etPass.getText().toString());
		//gestion de la réponse
		switch(rep){
			case 0:
				return true;
			case -1:
					ActivityMain.BuildDialog(R.string.title_refused, R.string.msg_refused, this);					
					return false;
			case -2:
					ActivityMain.BuildDialog(R.string.title_badurl, R.string.msg_badurl, this);
					return false;
			case -3:
					ActivityMain.BuildDialog(R.string.title_error, R.string.msg_error, this);
					return false;
			default:
				return false;
		}
	}
	
	final Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
	    	case 1:
	    		Save();
	    		break;
	    	case 2:
	    		mProgressDialog.dismiss();
	    	}
	    }
	    
	    
	};
}
