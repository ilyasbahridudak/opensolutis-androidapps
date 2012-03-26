package lift.maintenance.android.dal;

import java.sql.Timestamp;
import java.util.List;

import com.sqlite.management.TableModel;

public class MachineModel extends TableModel {
	
	String contract_name;
	String machine_address;
	String caretaker_address;
	int check_list_id;
	String area_id;
	String genre;
	String zip;
	String city;
	private Timestamp last_inter;
	private Timestamp next_inter;
	
	public static final String modelName = "contract.machine";
	public static final String tableName = modelName.replace(".", "_");
	public static final String[] listFields = TableModel.MakeFields(new String[] {"contract_id", "machine_address_id", "caretaker_address_id", "check_list_id",
			"area_id", "genre", "zip", "city", "last_inter", "next_inter"});
	
	public MachineModel(){
		super.modelName = MachineModel.modelName;
    	super.tableName = MachineModel.tableName;
    	super.listFields = MachineModel.listFields;
	}
	public MachineModel(int base_id, String contract_name, String machine_address, String caretaker_address, int check_list_id, String name,
			String area_id, String genre, String zip, String city, Timestamp last_inter, Timestamp next_inter){
		super.modelName = MachineModel.modelName;
    	super.tableName = MachineModel.tableName;
    	super.listFields = MachineModel.listFields;
		this.base_id = base_id;
		this.contract_name = contract_name;
		this.machine_address = machine_address;
		this.caretaker_address = caretaker_address;
		this.check_list_id = check_list_id;
		this.name = name;
		this.area_id =  area_id;
		this.genre = genre;
		this.zip = zip;
		this.city = city;
		this.last_inter = last_inter;
		this.next_inter = next_inter;
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
	public String getArea(){
		return area_id;
	}
	public String getGenre(){
		return genre;
	}
	public String getZip(){
		return zip;
	}
	public String getCity(){
		return city;
	}
	public Timestamp getLastInter(){
    	return last_inter;
    }
    public Timestamp getNextInter(){
    	return next_inter;
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
	public void setGenre(String genre){
		this.genre = genre;
	}
	public void setArea(String area_id){
		this.area_id = area_id;
	}
	public void setZip(String zip){
		this.zip = zip;
	}
	public void setCity(String city){
		this.city = city;
	}
	public void setLastInter(Timestamp last_inter){
		this.last_inter = last_inter;
	}
	public void setNextInter(Timestamp next_inter){
		this.next_inter = next_inter;
	}

	@Override
	public List<Object> toArray() {
		List<Object> list = super.toArrayBase();
		list.add(contract_name);
		list.add(machine_address);
		list.add(caretaker_address);
		list.add(check_list_id);
		list.add(area_id);
		list.add(genre);
		list.add(zip);
		list.add(city);
		list.add(last_inter);
		list.add(next_inter);
		return list;
	}
}
