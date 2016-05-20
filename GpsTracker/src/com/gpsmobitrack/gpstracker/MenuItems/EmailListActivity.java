package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gpsmobitrack.gpstracker.Adapter.EmailContactsAdap;
import com.gpsmobitrack.gpstracker.Bean.ContactBean;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;


public class EmailListActivity extends Activity implements OnClickListener, OnItemClickListener {

	private ListView emailListFromPhone;
	Button doneBtn,selectBtn,unselectBtn;
	public ArrayList<ContactBean> contactBeanList = new ArrayList<ContactBean>();
	public ArrayList<ContactBean> contactBeanFinal = new ArrayList<ContactBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_from_contacts);

		emailListFromPhone = (ListView) findViewById(R.id.list_from_contacts);
		doneBtn = (Button)findViewById(R.id.done_email_cont_page);
		selectBtn = (Button)findViewById(R.id.select_all_email_cont_page);
		unselectBtn = (Button)findViewById(R.id.unselect_all_email_cont_page);
		selectBtn.setOnClickListener(this);
		unselectBtn.setOnClickListener(this);
		doneBtn.setOnClickListener(this);
		emailListFromPhone.setOnItemClickListener(this);


		Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cur.getCount() > 0){
			Utils.printLog("", ""+cur.getCount());
			while(cur.moveToNext()){
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				Utils.printLog("id value", id);
				Cursor email = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, 
						ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);
				Utils.printLog("email", ""+email.getCount());
				while(email.moveToNext()){
					String emailData = email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					Utils.printLog("Email from", ""+emailData);
					ContactBean objContact = new ContactBean();
					objContact.setEmail(emailData);

					Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[] { id }, null);
					if(pCur.getCount()>0) {
						while (pCur.moveToNext()) {
							// Do something with phones
							String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

							objContact.setPhoneNo(phoneNo); // Here you can list of contact.

						}// Here you will get list of phone number.    
					} else {

						objContact.setPhoneNo("0");

					}
					pCur.close();
					contactBeanList.add(objContact);
				}

				email.close();
			}
		}
		contactBeanFinal = contactBeanList;
		/*for(int i=0;i<Mail.emailFinalList.size();i++){
			for(int j=0;j<contactBeanList.size();j++){
				if(contactBeanList.get(j).getEmail().contains(Mail.emailFinalList.get(i)))
					contactBeanFinal.remove(j);
			}
		}*/

		emailListFromPhone.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		EmailContactsAdap objAdapter = new EmailContactsAdap(EmailListActivity.this, contactBeanFinal, false, false);
		emailListFromPhone.setAdapter(objAdapter);
       try{
		if (null != contactBeanList && contactBeanList.size() != 0) {
			Collections.sort(contactBeanList, new Comparator<ContactBean>() {

				public int compare(ContactBean lhs, ContactBean rhs) {
					return lhs.getEmail().compareTo(rhs.getEmail());
				}
			});

		} else {
			Utils.showToast("No Contact Found!");
		}
    }catch(Exception ex){
	ex.printStackTrace();
	try{
    ArrayList<String>allEmails=new ArrayList<String>();
	for(ContactBean bean:contactBeanFinal){
		if(bean.getEmail()!=null)
		allEmails.add(bean.getEmail());
	}
	Collections.sort(allEmails);
	ArrayList<ContactBean> list=new ArrayList<ContactBean>();
	for(String str:allEmails){
		ContactBean b=new ContactBean();
		b.setEmail(str);
		list.add(b);
	}
	EmailContactsAdap adapter = new EmailContactsAdap(EmailListActivity.this, list, false, false);
	emailListFromPhone.setAdapter(adapter);
	}catch(Exception exx){}
}
	}

	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		Utils.printLog("Activity Pos===", ""+position);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == doneBtn){
			Utils.printLog("Done Onclick", ""+EmailContactsAdap.emailArrayList);
			Intent i = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable(AppConstants.EMAIL_KEY_INTENT, EmailContactsAdap.emailArrayList);
			i.putExtras(bundle);
			setResult(4, i);
			finish();
		}

		if(v == selectBtn){
			EmailContactsAdap adapter = new EmailContactsAdap(EmailListActivity.this, contactBeanList, true, false);
			emailListFromPhone.setAdapter(adapter);
		}

		if(v == unselectBtn){
			EmailContactsAdap adapter = new EmailContactsAdap(EmailListActivity.this, contactBeanList, false, true);
			emailListFromPhone.setAdapter(adapter);
		}
	}


}
