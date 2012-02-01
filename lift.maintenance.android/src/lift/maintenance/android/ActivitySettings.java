package lift.maintenance.android;

import com.xmlrpc.access.xmlrpcAccess;

import java.lang.Integer;

import lift.maintenance.android.service.ServiceSync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        		Save();
        		if(Connect())
    				finish();
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
		if(ServiceSync.isStarted(context)){
        	stopService(new Intent(ActivitySettings.this, ServiceSync.class));
        }
		startService(new Intent(ActivitySettings.this, ServiceSync.class));
	}
	
	//tests de connection
	private Boolean Connect(){
		//tentative de connection
		int rep = access.Connect(prefs.getString("URL", ""), prefs.getString("base", ""),
				prefs.getString("login", ""), prefs.getString("pass", ""));
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
	
}
