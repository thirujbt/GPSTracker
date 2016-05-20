package com.gpsmobitrack.gpstracker.chat;

import java.util.ArrayList;

import com.gpstracker.pro.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 *
 */
@SuppressLint("ResourceAsColor")
public class AwesomeAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Message> mMessages;



	public AwesomeAdapter(Context context, ArrayList<Message> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}
	@Override
	public int getCount() {
		return mMessages.size();
	}
	@Override
	public Object getItem(int position) {		
		return mMessages.get(position);
	}
	@SuppressWarnings("deprecation")
	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = (Message) this.getItem(position);

		ViewHolder holder; 
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.dateTimeRight = (TextView) convertView.findViewById(R.id.dateTimeRight);
			holder.msgLinearLayout = (LinearLayout) convertView.findViewById(R.id.msg_layout);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
	
		String dateTime=message.getDateTime();
		String dates[]=dateTime.split("@");
		String date="";
		String time="";
		
		
		date=dates[0];
		//time=dates[1];
		holder.message.setText(message.getMessage());
		
		holder.dateTimeRight.setText(date/*+" "+time*/);
		
		LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
		LayoutParams dt = (LayoutParams) holder.dateTimeRight.getLayoutParams();
		LayoutParams ll = (LayoutParams) holder.msgLinearLayout.getLayoutParams();
		
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			lp.gravity = Gravity.LEFT;
			dt.gravity = Gravity.LEFT;
			ll.gravity = Gravity.LEFT;
			//holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{		
			//Check whether message is mine to show green background and align to right
			if(message.isMine())
			{
				
				holder.msgLinearLayout.setBackgroundResource(R.drawable.chat_speech_blue);
				holder.msgLinearLayout.setPadding(5, 0, 20, 0);
				lp.gravity = Gravity.RIGHT;
				//dt.gravity = Gravity.LEFT;
				ll.gravity = Gravity.RIGHT;
				
				//holder.message.setTextColor(R.color.textColor);
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				holder.msgLinearLayout.setBackgroundResource(R.drawable.chat_speech_white);
				holder.msgLinearLayout.setPadding(20, 0, 5, 0);
				lp.gravity = Gravity.LEFT;
				dt.gravity = Gravity.RIGHT;
				ll.gravity = Gravity.LEFT;
				//holder.message.setTextColor(R.color.textFieldColor);
			}
			holder.message.setLayoutParams(lp);
			//holder.message.setTextColor(R.color.textColor);	
		}
		return convertView;
	}
	private static class ViewHolder
	{
		TextView message;
		TextView dateTimeRight;
		LinearLayout msgLinearLayout;
		//TextView dateTimeLeft;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
