package lift.maintenance.android;

import java.sql.Timestamp;
import java.util.Date;

import lift.maintenance.android.dal.DataBaseManager;
import lift.maintenance.android.dal.InterventionModel;
import lift.maintenance.android.dal.MachineModel;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class ActivityInterTabInfo extends Activity implements OnClickListener{
	private int inter_id;
	
	private Context context;
	private DataBaseManager manager;
	private Resources res;
	private InterventionModel intervention;
	private MachineModel machine;
	
	private TextView tvSector;
	private TextView tvDept;
	private TextView tvCity;
	private TextView tvMachineAddress;
	private TextView tvIndex;
	private TextView tvContract;
	private TextView tvType;
	private TextView tvDateLabel;
	private TextView tvDate;
	private TextView tvInfo;
	private TextView tvState;
	private CheckBox cbCable;
	private CheckBox cbParachute;
	private TextView tvLast;
	private TextView tvNext;
	
	private Button bToExecute;
	private Button bToWait;
	private Button bToStop;
	private Button bToBeDone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		inter_id = this.getIntent().getExtras().getInt("inter_id");
		
		context = getApplicationContext();
        manager = new DataBaseManager(context);
        res = context.getResources();
		
        manager.open();
        intervention = (InterventionModel) manager.intervention.getWithBaseId(inter_id).get(0);
        manager.close();
        
        if(intervention.getType().equals("maintenance")){
        	setContentView(R.layout.intertabinfomnt);
        	cbCable = (CheckBox)findViewById(R.id.cbCable);
        	cbCable.setOnClickListener(this);
        	
        	cbParachute = (CheckBox)findViewById(R.id.cbParachute);
        	cbParachute.setOnClickListener(this);
        }
        else{
        	setContentView(R.layout.intertabinfopanne);
        	tvLast = (TextView)findViewById(R.id.tvLast);
        	tvNext = (TextView)findViewById(R.id.tvNext);
        }
        
        tvSector = (TextView)findViewById(R.id.tvSetcor);
        tvDept = (TextView)findViewById(R.id.tvDept);
        tvCity = (TextView)findViewById(R.id.tvCity);
        tvMachineAddress = (TextView)findViewById(R.id.tvMachineAdresse);
        tvIndex = (TextView)findViewById(R.id.tvIndex);
        tvContract = (TextView)findViewById(R.id.tvContract);
        tvType = (TextView)findViewById(R.id.tvType);
    	tvDateLabel = (TextView)findViewById(R.id.tvDateLabel);
    	tvDate = (TextView)findViewById(R.id.tvDate);
    	tvInfo = (TextView)findViewById(R.id.tvInfo);
    	tvState = (TextView)findViewById(R.id.tvState);
    	
    	bToExecute = (Button)findViewById(R.id.bToExecute);
    	bToExecute.setOnClickListener(this);
    	
    	bToWait = (Button)findViewById(R.id.bToWait);
    	bToWait.setOnClickListener(this);
    	
    	bToStop = (Button)findViewById(R.id.bToStop);
    	bToStop.setOnClickListener(this);
    	
    	bToBeDone = (Button)findViewById(R.id.bToBeDone);
    	bToBeDone.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		manager.open();
        intervention = (InterventionModel) manager.intervention.getWithBaseId(inter_id).get(0);
        machine = (MachineModel) manager.machine.getWithBaseId(intervention.getMachineId()).get(0);
        manager.close();
        
        tvSector.setText(machine.getArea());
        String zip = machine.getZip().substring(0, 2);
        if(zip != "75")
        	tvDept.setText(zip);
        else{
        	int district = Integer.parseInt(machine.getZip().substring(3));
        	String ext;
        	switch(district){
        	case 1:
        		ext = getString(R.string.first);
        		break;
        	case 2:
        		ext = getString(R.string.second);
        		break;
        	case 3:
        		ext = getString(R.string.third);
        		break;
        	default:
        		ext = getString(R.string.authers);
        		break;
        	}
        	tvDept.setText(district + ext);
        }
        tvCity.setText(machine.getCity());
        String[] mn = intervention.getMachineName().split("[/,]");
        tvIndex.setText(mn[1]);
        tvContract.setText(mn[0]);
        tvType.setText(res.getString(res.getIdentifier(machine.getGenre(),"string","lift.maintenance.android")));
        tvMachineAddress.setText(machine.getMachineAddress());
        tvInfo.setText(intervention.getInformation());
        tvState.setText(res.getString(res.getIdentifier(intervention.getState(), "string","lift.maintenance.android")));
    	
        if(intervention.getType().equals("incident")){
    		tvDateLabel.setText(context.getText(R.string.callDateLabel));
    		if(intervention.getCallDay() != null)
    			tvDate.setText(intervention.getCallDay().toLocaleString());
    		else
    			tvDate.setText(R.string.NA);
    		
    		if(machine.getLastInter()!=null)
    			tvLast.setText(machine.getLastInter().toLocaleString());
    		else
    			tvLast.setText(R.string.NA);
    		
    		if(machine.getNextInter()!=null)
    			tvNext.setText(machine.getNextInter().toLocaleString());
    		else
    			tvNext.setText(R.string.NA);
    	}
    	else{
    		tvDateLabel.setText(context.getText(R.string.deadlineDate));
    		tvDate.setText(intervention.getMaintenanceDeadline().toLocaleString());
    		
    		cbCable.setChecked(intervention.getCable());
    		cbParachute.setChecked(intervention.getParachute());
    	}
        
        setButtonsAccess();
	}
	
	private void setButtonsAccess() {
		if(intervention.getState().equals(res.getResourceEntryName(R.string.considering))){
			bToExecute.setEnabled(true);
			bToExecute.setText(R.string.bToExecute);
			bToStop.setEnabled(true);
			bToWait.setEnabled(true);
			bToBeDone.setEnabled(false);
		}
		else if(intervention.getState().equals(res.getResourceEntryName(R.string.progress))){
			bToExecute.setEnabled(false);
			bToExecute.setText(R.string.bToExecute);
			bToStop.setEnabled(true);
			bToWait.setEnabled(true);
			bToBeDone.setEnabled(true);
		}
		else if(intervention.getState().equals(res.getResourceEntryName(R.string.waiting))){
			bToExecute.setEnabled(true);
			bToExecute.setText(R.string.bToExecute);
			bToStop.setEnabled(false);
			bToWait.setEnabled(false);
			bToBeDone.setEnabled(false);
		}
		else if(intervention.getState().equals(res.getResourceEntryName(R.string.stop))){
			bToExecute.setEnabled(true);
			bToExecute.setText(R.string.bToRestart);
			bToStop.setEnabled(false);
			bToWait.setEnabled(false);
			bToBeDone.setEnabled(false);
		}
		else if(intervention.getState().equals(res.getResourceEntryName(R.string.done))){
			bToExecute.setEnabled(false);
			bToExecute.setText(R.string.bToExecute);
			bToStop.setEnabled(false);
			bToWait.setEnabled(false);
			bToBeDone.setEnabled(false);
		}
		
		if(!intervention.getType().equals("incident")){
			bToStop.setEnabled(false);
    		bToWait.setEnabled(false);
		}
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bToExecute:
			if(intervention.getState().equals(res.getResourceEntryName(R.string.stop))){
				machine.setBreakdownStop(false);
			}
			intervention.setState(res.getResourceEntryName(R.string.progress));
			if(intervention.getInterventionStart() == null)
			{
				intervention.setInterventionStart(new Timestamp(new Date().getTime()));
			}
			break;
		case R.id.bToWait:
			intervention.setState(res.getResourceEntryName(R.string.waiting));
			break;
		case R.id.bToStop:
			intervention.setState(res.getResourceEntryName(R.string.stop));
			machine.setBreakdownStop(true);
			break;
		case R.id.bToBeDone:
			intervention.setState(res.getResourceEntryName(R.string.done));
			if(intervention.getInterventionEnd() == null)
			{
				intervention.setInterventionEnd(new Timestamp(new Date().getTime()));
			}
			break;
		case R.id.cbCable:
			intervention.setCable(cbCable.isChecked());
			break;
		case R.id.cbParachute:
			intervention.setParachute(cbParachute.isChecked());
			break;
		}
		manager.open();
		manager.intervention.update(inter_id, intervention);
		manager.close();
		
		onResume();
	}
}
