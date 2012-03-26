package lift.maintenance.android;

import java.util.ArrayList;
import java.util.List;

import lift.maintenance.android.dal.CodificationModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterCodification extends BaseAdapter {

	List<CodificationModel> lst;
	LayoutInflater inflater;
	Context context;
	
	public AdapterCodification(Context context, List<CodificationModel> lst, int SelectType)
	{
		this.lst = new ArrayList<CodificationModel>();
		this.lst.add(new CodificationModel(0, 0, "", context.getString(SelectType)));
		this.lst.addAll(lst);
		inflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public int getCount() {
		return lst.size();
	}

	public Object getItem(int position) {
		return lst.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemIdByBaseId(int base_id){
		for(int i=0; i<lst.size(); i++){
			if(lst.get(i).getBaseId() == base_id)
				return i;
		}
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout rowLayout;
		
		CodificationModel element = lst.get(position);
		
		if(convertView == null){
			rowLayout = (LinearLayout) inflater.inflate(R.layout.itemcodification, parent, false);
		}
		else {
			rowLayout = (LinearLayout) convertView; 
		}
		
		TextView tvName = (TextView) rowLayout.findViewById(R.id.tvCodeName);
		tvName.setText(element.getName());
		
		return rowLayout;
	}

}
