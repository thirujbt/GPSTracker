package com.gpsmobitrack.gpstracker.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.Bean.ContactBean;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class EmailContactsAdap extends BaseAdapter {

	private static ArrayList<ContactBean> emailBeanLists = new ArrayList<ContactBean>();
	private Context ctn;
	private LayoutInflater inflater;
	static public ArrayList<ContactBean> emailArrayList = new ArrayList<ContactBean>();
	ContactBean beanObj;
	private boolean[] mstateChecked;
	boolean isSelect = false, isUnselect = false;

	@SuppressWarnings("static-access")
	public EmailContactsAdap(Context context, ArrayList<ContactBean> items, boolean select, boolean unselect){
		if(!unselect){
			emailArrayList.clear();
		}
		this.emailBeanLists = items;
		this.ctn = context;
		this.mstateChecked = new boolean[items.size()];
		this.isSelect = select;
		this.isUnselect = unselect;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return emailBeanLists.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		TextView emailTxt,phoneTxt;
		final CheckBox checkEmail;
		inflater = (LayoutInflater)ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.user_email_contacts_list, parent, false);

		if(emailBeanLists == null || (position + 1) > emailBeanLists.size()) return itemView;

		beanObj = emailBeanLists.get(position);

		emailTxt = (TextView)itemView.findViewById(R.id.email_txt);
		phoneTxt = (TextView)itemView.findViewById(R.id.textview);
		checkEmail = (CheckBox)itemView.findViewById(R.id.check_email);

		if(emailTxt != null && beanObj.getEmail() != null && beanObj.getEmail().trim().length() > 0){
			emailTxt.setText(Html.fromHtml(beanObj.getEmail()));
			if(beanObj.getPhoneNo()!="0"){
				phoneTxt.setText(beanObj.getPhoneNo());
			}
		}else{
			Utils.printLog("Email beanObj", ""+beanObj.getEmail());
		}

		if(isSelect){
			Utils.printLog("In Emailadap", "in Select");
			for(int i=0;i<emailBeanLists.size();i++){
				mstateChecked[i] = true;

				if(!emailArrayList.contains(emailBeanLists.get(i)))
					emailArrayList.add(emailBeanLists.get(i));
			}

			isSelect = false;
		}

		if(isUnselect){
			Utils.printLog("In Emailadap", "in Unselect");

			for(int i=0;i<emailBeanLists.size();i++){
				mstateChecked[i] = false;


				if(emailArrayList.contains(emailBeanLists.get(i)))
					emailArrayList.remove(emailBeanLists.get(i));
			}

			isUnselect = false;
		}

		checkEmail.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkEmail.isChecked()){
					mstateChecked[position] = true;


					if(!emailArrayList.contains(emailBeanLists.get(position)))
						emailArrayList.add(emailBeanLists.get(position));
				}else{
					mstateChecked[position] = false;
					Utils.printLog("emailArray List count", ""+emailArrayList.size());

					if(emailArrayList.contains(emailBeanLists.get(position)))
						emailArrayList.remove(emailBeanLists.get(position));
				}
			}
		});

		Utils.printLog("array", ""+mstateChecked);

		checkEmail.setChecked(mstateChecked[position]);

		return itemView;

	}


	public boolean checkArrayValue(String Value){

		for ( ContactBean conBaan : emailArrayList) {

			if(conBaan.getEmail().equalsIgnoreCase(Value))
				return true;
		}

		return false;
	}

}
