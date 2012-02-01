package lift.maintenance.android;

import java.util.List;

import lift.maintenance.android.dal.CheckLineModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterCheckLine extends BaseAdapter {

	List<CheckLineModel> lst;
	LayoutInflater inflater;
	
	public AdapterCheckLine(Context context, List<CheckLineModel> lst){
		inflater = LayoutInflater.from(context);
		this.lst = lst;
	}
	
	@Override
	public int getCount() {
		return lst.size();
	}

	@Override
	public Object getItem(int position) {
		return lst.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder{
		TextView tvCheckLineName;
		TextView tvCheckLineFrequency;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
				 
			convertView = inflater.inflate(R.layout.itemcheckline, null);
			 
			holder.tvCheckLineName = (TextView)convertView.findViewById(R.id.tvCheckLineName);
			holder.tvCheckLineFrequency = (TextView)convertView.findViewById(R.id.tvCheckLineFrequency);
			 
			convertView.setTag(holder);
			 
		} else {
			 
			holder = (ViewHolder) convertView.getTag();
			 
		}
		
		holder.tvCheckLineName.setText(lst.get(position).getName());
		holder.tvCheckLineFrequency.setText(lst.get(position).getFrequency());
		
		return convertView;
	}

}
