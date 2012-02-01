package lift.maintenance.android.dal;

public class CheckLineModel {
	private int id;
	private int base_id;
	private String name;
	private String frequency;
	private int list_id;
	
	public static final String modelName = "contract.check.line";
	public static final String tableName = modelName.replace(".", "_");
	public static String[] listFields = new String[] {"id","base_id","name","frequency","list_id"};
	
	public CheckLineModel(){}
	
	public CheckLineModel(int base_id, String name, String frequency, int list_id){
		this.base_id = base_id;
		this.name = name;
		this.frequency = frequency;
		this.list_id = list_id;
	}
 
	public int getId() {
		return id;
	}
 
	public void setId(int id) {
		this.id = id;
	}
 
	public int getBaseId() {
		return base_id;
	}
 
	public void setBaseId(int base_id) {
		this.base_id = base_id;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFrequency()
	{
		return frequency;
	}
	
	public void setFrequency(String frequency){
		this.frequency = frequency;
	}
	
	public int getListId(){
		return list_id;
	}
	
 	public void setListId(int list_id)
	{
		this.list_id = list_id;
	}
 
	public String toString(){
		return "id : "+id+"\nbase_id : "+base_id+"\nName : "+name+"\nFrequency : "+frequency+"\nlist_id : "+list_id;
	}
}
