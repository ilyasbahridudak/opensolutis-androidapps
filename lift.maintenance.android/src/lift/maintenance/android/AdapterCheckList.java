package lift.maintenance.android;

import java.util.List;

import lift.maintenance.android.R;
import lift.maintenance.android.dal.CheckListModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterCheckList extends BaseAdapter {

	List<CheckListModel> lst;
	LayoutInflater inflater;
	
	public AdapterCheckList(Context context, List<CheckListModel> lst){
		inflater = LayoutInflater.from(context);
		this.lst = lst;
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

	private class ViewHolder{
		TextView tvCheckListName;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
				 
			convertView = inflater.inflate(R.layout.itemchecklist, null);
			 
			holder.tvCheckListName = (TextView)convertView.findViewById(R.id.tvCheckListName);
			 
			convertView.setTag(holder);
			 
		} else {
			 
			holder = (ViewHolder) convertView.getTag();
			 
		}
		
		holder.tvCheckListName.setText(lst.get(position).getName());
		
		return convertView;
	}

}
