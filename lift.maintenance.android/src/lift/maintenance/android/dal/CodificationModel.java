package lift.maintenance.android.dal;

public class CodificationModel {
	public int id;
	public int base_id;
	public int parent_id; 
    public String type;
    public String name;
	
    public static final String modelName = "intervention.codification";
    public static final String tableName = modelName.replace(".", "_");
    public static String[] listFields = new String[] {"id","base_id", "parent_id", "type", "name"};
    
    public CodificationModel(){}
    
    public CodificationModel(int base_id, int parent_id, String type, String name){
    	this.base_id = base_id;
    	this.parent_id = parent_id; 
        this.type = type;
        this.name = name;
    }
	
    public int getId(){
    	return id;
    }    
    public int getBaseId(){
		return base_id;
	}
	public int getParentId(){
		return parent_id; 
	}
    public String getType(){
    	return type;
    }
    public String getName(){
    	return name;
    }
    
    public void setId(int id){
    	this.id = id;
    }
    public void setBaseId(int base_id){
    	this.base_id = base_id;
    }
	public void setParentId(int parent_id){
		this.parent_id = parent_id; 
	}
    public void setType(String type){
    	this.type = type;
    }
    public void setName(String name){
    	this.name = name;
    }
    
    public String toString(){
    	return "id: "+id+",base_id: "+base_id+",parent_id: "+parent_id+",type: "+type+",name: "+name;
    }
}
