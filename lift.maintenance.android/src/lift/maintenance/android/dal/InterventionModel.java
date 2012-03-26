package lift.maintenance.android.dal;

import java.sql.Timestamp;
import java.util.List;

import com.sqlite.management.TableModel;

public class InterventionModel extends TableModel {
	private int chapter_id; 
	private int localization_id;
	private int cause_id;
	private String information;
	private Timestamp call_day;
	private Timestamp take_into_account_day;
	private Timestamp intervention_start;
	private Timestamp intervention_end;
	private Timestamp maintenance_deadline;
	private String requestor_name;
	private String requestor_phone;
	private String state;
	private String type;
	private Boolean someone;
	private String signature_name;
	private byte[] signature_picture;
	private Boolean invoiceable;
	private int machine_id;
	private String machine_name;
	private Boolean parachute;
	private Boolean cable;
	private String last_incidents;    
	
    public static final String modelName = "intervention.intervention";
    public static final String tableName = modelName.replace(".", "_");
    public static final String[] listFields = TableModel.MakeFields(new String[] {"chapter_id", "localization_id", "cause_id", //5
    	"information", "call_day", "intervention_start", "intervention_end", "requestor_name", "requestor_phone", "state", "type", //13
    	"someone", "signature_name", "signature_picture", "invoiceable", "machine_id", "machine_name", "parachute", "cable",//21
    	"maintenance_deadline", "take_into_account_day", "last_incidents"}); //24
    
    public InterventionModel(){
    	super.modelName = InterventionModel.modelName;
    	super.tableName = InterventionModel.tableName;
    	super.listFields = InterventionModel.listFields;
    }
    
    public int getChapterId(){
    	return chapter_id; 
    }
    public int getLocalizationId(){
    	return localization_id;
    }
    public int getCauseId(){
    	return cause_id;
    }
    public String getInformation(){
    	return information;
    }
    public Timestamp getCallDay(){
    	return call_day;
    }
    public Timestamp getTakeIntoAccountDay(){
    	return take_into_account_day;
    }
    public Timestamp getInterventionStart(){
    	return intervention_start;
    }
    public Timestamp getInterventionEnd(){
    	return intervention_end;
    }
    public Timestamp getMaintenanceDeadline(){
    	return maintenance_deadline;
    }
    public String getRequestorName(){
    	return requestor_name;
    }
    public String getRequestorPhone(){
    	return requestor_phone;
    }
    public String getState(){
    	return state;
    }
    public String getType(){
    	return type;
    }
    public Boolean getSomeone(){
    	return someone;
    }
    public String getSignatureName(){
    	return signature_name;
    }
    public byte[] getSignaturePicture(){
    	return signature_picture;
    }
    public Boolean getInvoiceable(){
    	return invoiceable;
    }
    public int getMachineId(){
    	return machine_id;
    }
    public String getMachineName(){
    	return machine_name;
    }
    public Boolean getParachute(){
    	return parachute;
    }
    public Boolean getCable(){
    	return cable;
    }
    public String getLastIncidents(){
    	return last_incidents;
    }
    
    public void setChapterId(int chapter_id){
    	this.chapter_id = chapter_id; 
    }
    public void setLocalizationId(int localization_id){
    	this.localization_id = localization_id;
    }
    public void setCauseId(int cause_id){
    	this.cause_id = cause_id;
    }
    public void setInformation(String information){
    	this.information = information;
    }
    public void setCallDay(Timestamp call_day){
    	this.call_day = call_day;
    }
    public void setTakeIntoAccountDay(Timestamp take_into_account_day){
    	this.take_into_account_day = take_into_account_day;
    }
    public void setInterventionStart(Timestamp intervention_start){
    	this.intervention_start = intervention_start;
    }
    public void setInterventionEnd(Timestamp intervention_end){
    	this.intervention_end = intervention_end;
    }
    public void setMaintenanceDeadline(Timestamp maintenance_deadline){
    	this.maintenance_deadline = maintenance_deadline;
    }
    public void setRequestorName(String requestor_name){
    	this.requestor_name = requestor_name;
    }
    public void setRequestorPhone(String requestor_phone){
    	this.requestor_phone = requestor_phone;
    }
    public void setState(String state){
    	this.state = state;
    }
    public void setType(String type){
    	this.type = type;
    }
    public void setSomeone(Boolean someone){
    	this.someone = someone;
    }
    public void setSignatureName(String signature_name){
    	this.signature_name = signature_name;
    }
    public void setSignaturePicture(byte[] signature_picture){
    	this.signature_picture =signature_picture;
    }
    public void setInvoiceable(Boolean invoiceable){
    	this.invoiceable = invoiceable;
    }
    public void setMachineId(int machine_id){
    	this.machine_id = machine_id;
    }
    public void setMachineName(String machine_name){
    	this.machine_name = machine_name;
    }
    public void setParachute(Boolean parachute){
    	this.parachute = parachute;
    }
    public void setCable(Boolean cable){
    	this.cable = cable;
    }
	public void setLastIncidents(String last_incidents){
		this.last_incidents = last_incidents;
	}
		
	@Override
	public  List<Object> toArray() {
		List<Object> list = super.toArrayBase();
		list.add(chapter_id); 
		list.add(localization_id);
		list.add(cause_id);
		list.add(information);
		list.add(call_day);
		list.add(intervention_start);
		list.add(intervention_end);
		list.add(requestor_name);
		list.add(requestor_phone);
		list.add(state);
		list.add(type);
		list.add(someone);
		list.add(signature_name);
		list.add(signature_picture);
		list.add(invoiceable);
		list.add(machine_id);
		list.add(machine_name);
		list.add(parachute);
		list.add(cable);
		list.add(maintenance_deadline);
		list.add(take_into_account_day);
		list.add(last_incidents);
		return list;
	}
    
}
