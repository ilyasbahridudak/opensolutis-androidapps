package lift.maintenance.android.dal;

public class MachineModel {
	
	int id;
	int base_id;
	String contract_name;
	String machine_address;
	String caretaker_address;
	int check_list_id;
	String name;
	
	public static final String modelName = "contract.machine";
	public static final String tableName = modelName.replace(".", "_");
	public static final String[] listFields = {"id", "base_id", "contract_id", "machine_address_id",
												"caretaker_address_id", "check_list_id", "name"};
	
	public MachineModel(){}
	public MachineModel(int base_id, String contract_name, String machine_address, String caretaker_address, int check_list_id, String name){
		this.base_id = base_id;
		this.contract_name = contract_name;
		this.machine_address = machine_address;
		this.caretaker_address = caretaker_address;
		this.check_list_id = check_list_id;
		this.name = name;
	}
	
	public int getId(){
		return id;
	}
	public int getBaseId(){
		return base_id;
	}
	public String getContactName(){
		return contract_name;
	}
	public String getMachineAddress(){
		return machine_address;
	}
	public String getCaretakerAddress(){
		return caretaker_address;
	}
	public int getCheckListId(){
		return check_list_id;
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
	public void setContractName(String contract_name){
		this.contract_name = contract_name;
	}
	public void setMachineAddress(String machine_addresse){
		this.machine_address = machine_addresse;
	}
	public void setCaretakerAddress(String caretaker_address){
		this.caretaker_address = caretaker_address;
	}
	public void setCheckListId(int check_list_id){
		this.check_list_id = check_list_id;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String toString(){
		return "id: " + id + ", base_id: " + base_id + ", contract_name: " + contract_name + ", check_list_id: " + check_list_id 
				+ ", name :" + name + ", machine_address: " + machine_address + ", caretaker_address: " + caretaker_address;
	}
}
