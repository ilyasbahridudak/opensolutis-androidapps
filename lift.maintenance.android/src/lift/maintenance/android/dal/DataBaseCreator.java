package lift.maintenance.android.dal;

import com.sqlite.management.Types;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseCreator extends SQLiteOpenHelper {

	private static final String CREATE_TABLE_CHECK_LIST = Types.create + CheckListModel.tableName + Types.begin
			+ CheckListModel.listFields[0] + Types.pkey + Types.next + CheckListModel.listFields[1] + Types.intnotnull + Types.next
			+ CheckListModel.listFields[2] + Types.textnotnull + Types.end;
	
	private static final String  CREATE_TABLE_CHECK_LINE = Types.create + CheckLineModel.tableName + Types.begin
			+ CheckLineModel.listFields[0] + Types.pkey + Types.next + CheckLineModel.listFields[1] + Types.intnotnull + Types.next
			+ CheckLineModel.listFields[2] + Types.textnotnull + Types.next + CheckLineModel.listFields[3] + Types.textnotnull + Types.next
			+ CheckLineModel.listFields[4] + Types.intnotnull + Types.end;
	
	private static final String CREATE_TABLE_CODIFICATION = Types.create + CodificationModel.tableName + Types.begin
			+ CodificationModel.listFields[0] + Types.pkey + Types.next + CodificationModel.listFields[1] + Types.intnotnull + Types.next
			+ CodificationModel.listFields[2] + Types.integer + Types.next + CodificationModel.listFields[3] + Types.textnotnull + Types.next
			+ CodificationModel.listFields[4] + Types.textnotnull + Types.end;
	
	private static final String CREATE_TABLE_MACHINE = Types.create + MachineModel.tableName + Types.begin
			+ MachineModel.listFields[0] + Types.pkey + Types.next + MachineModel.listFields[1] + Types.intnotnull + Types.next
			+ MachineModel.listFields[2] + Types.textnotnull + Types.next + MachineModel.listFields[3] + Types.textnotnull + Types.next
			+ MachineModel.listFields[4] + Types.textnotnull + Types.next + MachineModel.listFields[5] + Types.text + Types.next
			+ MachineModel.listFields[6] + Types.intnotnull + Types.next + MachineModel.listFields[7] + Types.text + Types.next
			+ MachineModel.listFields[8] + Types.text + Types.next + MachineModel.listFields[9] + Types.text + Types.next
			+ MachineModel.listFields[10] + Types.text + Types.next + MachineModel.listFields[11] + Types.text + Types.next
			+ MachineModel.listFields[12] + Types.text + Types.end;
	
	private static final String CREATE_TABLE_INTERVENTION = Types.create + InterventionModel.tableName + Types.begin
			+ InterventionModel.listFields[0] + Types.pkey + Types.next + InterventionModel.listFields[1] + Types.intnotnull + Types.next
			+ InterventionModel.listFields[2] + Types.textnotnull + Types.next + InterventionModel.listFields[3] + Types.integer + Types.next
			+ InterventionModel.listFields[4] + Types.integer + Types.next + InterventionModel.listFields[5] + Types.integer + Types.next
			+ InterventionModel.listFields[6] + Types.text + Types.next + InterventionModel.listFields[7] + Types.text + Types.next
			+ InterventionModel.listFields[8] + Types.text + Types.next + InterventionModel.listFields[9] + Types.text + Types.next
			+ InterventionModel.listFields[10] + Types.text + Types.next + InterventionModel.listFields[11] + Types.text + Types.next
			+ InterventionModel.listFields[12] + Types.textnotnull + Types.next + InterventionModel.listFields[13] + Types.textnotnull + Types.next
			+ InterventionModel.listFields[14] + Types.intnotnull + Types.next + InterventionModel.listFields[15] + Types.text + Types.next
			+ InterventionModel.listFields[16] + Types.none + Types.next + InterventionModel.listFields[17] + Types.intnotnull + Types.next
			+ InterventionModel.listFields[18] + Types.intnotnull + Types.next + InterventionModel.listFields[19] + Types.textnotnull + Types.next
			+ InterventionModel.listFields[20] + Types.intnotnull + Types.next + InterventionModel.listFields[21] + Types.intnotnull + Types.next
			+ InterventionModel.listFields[22] + Types.text + Types.next + InterventionModel.listFields[23] + Types.text + Types.next 
			+ InterventionModel.listFields[24] + Types.text + Types.end;
	
	public DataBaseCreator(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	//création des tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_CHECK_LIST);
		db.execSQL(CREATE_TABLE_CHECK_LINE);
		db.execSQL(CREATE_TABLE_CODIFICATION);
		db.execSQL(CREATE_TABLE_MACHINE);
		db.execSQL(CREATE_TABLE_INTERVENTION);
	}

	//procédure de mise à jour des tables
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			//suppression des tables existantes
			db.execSQL("DROP TABLE " + CheckListModel.tableName + ";");
			db.execSQL("DROP TABLE " + CheckLineModel.tableName + ";");
			db.execSQL("DROP TABLE " + CodificationModel.tableName + ";");
			db.execSQL("DROP TABLE " + MachineModel.tableName + ";");
			db.execSQL("DROP TABLE " + InterventionModel.tableName + ";");
		}catch(SQLException e){
			e.printStackTrace();
		}
		//recréation de la base
		onCreate(db);
	}

}
