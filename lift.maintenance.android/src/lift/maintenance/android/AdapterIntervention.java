package lift.maintenance.android;

import java.util.List;

import lift.maintenance.android.R;
import lift.maintenance.android.dal.InterventionModel;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterIntervention extends BaseAdapter {

	List<InterventionModel> lst;
	LayoutInflater inflater;
	Resources res;
	
	public AdapterIntervention(Context context, List<InterventionModel> lst){
		inflater = LayoutInflater.from(context);
		this.lst = lst;
		res = context.getResources();
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
		TextView tvDescription;
		TextView tvAddress;
		TextView tvDate;
		TextView tvState;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
				 
			convertView = inflater.inflate(R.layout.itemintervention, null);
			 
			holder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
			holder.tvAddress = (TextView)convertView.findViewById(R.id.tvAddress);
			holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
			holder.tvState = (TextView)convertView.findViewById(R.id.tvState);
			 
			convertView.setTag(holder);
			 
		} else {
			 
			holder = (ViewHolder) convertView.getTag();
			 
		}
		
		holder.tvDescription.setText(lst.get(position).getInformation());
		holder.tvAddress.setText(lst.get(position).getMachineName());
		if(lst.get(position).getType().equals("incident"))
			if(lst.get(position).getCallDay() != null)
				holder.tvDate.setText(lst.get(position).getCallDay().toLocaleString());
		else if(lst.get(position).getMaintenanceDeadline()!=null)
			holder.tvDate.setText(lst.get(position).getMaintenanceDeadline().toLocaleString());
		
		holder.tvState.setText(res.getString(res.getIdentifier(lst.get(position).getState(), "string", "lift.maintenance.android")));
		
		return convertView;
	}

}
