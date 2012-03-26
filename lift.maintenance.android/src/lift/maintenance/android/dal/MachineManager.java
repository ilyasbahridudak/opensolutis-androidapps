package lift.maintenance.android.dal;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.sqlite.management.TableManager;
import com.sqlite.management.TableModel;

public class MachineManager extends TableManager {

	public MachineManager() {
		super(new MachineModel());
		
	}

	@Override
	protected List<TableModel> cursorToTable(Cursor c) {
		List<TableModel> machines = new ArrayList<TableModel>();
		c.moveToFirst();
		
		for(int i=0; i<c.getCount();i++){
			MachineModel machine = new MachineModel();
			machine.setId(c.getInt(0));
			machine.setBaseId(c.getInt(1));
			machine.setName(c.getString(2));
			machine.setContractName(c.getString(3));
			machine.setMachineAddress(c.getString(4));
			machine.setCaretakerAddress(c.getString(5));
			machine.setCheckListId(c.getInt(6));
			machine.setArea(c.getString(7));
			machine.setGenre(c.getString(8));
			machine.setZip(c.getString(9));
			machine.setCity(c.getString(10));
			machine.setLastInter(TableModel.stringToDate(c.getString(11), true));
			machine.setNextInter(TableModel.stringToDate(c.getString(12), true));
			machines.add(machine);
			c.moveToNext();
		}
		c.close();
		return machines;
	}

}
