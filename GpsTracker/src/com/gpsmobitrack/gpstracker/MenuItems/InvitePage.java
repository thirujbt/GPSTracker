package com.gpsmobitrack.gpstracker.MenuItems;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSSharedPreference;
import com.gpstracker.pro.R;

public class InvitePage extends Fragment {
	@SuppressWarnings("unused")
	private static final String TAG = "Invite Fragment";
	public static boolean isInviteClicked=false;
	//FragmentTabHost tabHost;
	TabHost tabHost;
	//TextView for request received badge count  
	TextView badgeReqRecCountTxt;
	//Store Request Received count
	@SuppressWarnings("unused")
	private int reqRecBadgeCount = 0;
	String msg = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			msg = bundle.getString(AppConstants.GCM_INVITE_INVITE_PAGE_BUNDLE_KEY);
		}else{
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.invite_page, null);
		tabHost = (TabHost)view.findViewById(android.R.id.tabhost);
		tabHost.setup();
		View inviteTabView = LayoutInflater.from(getActivity()).inflate(R.layout.tab_bg, null);
		final ImageView inviteTabTitleTxt = (ImageView)inviteTabView.findViewById(R.id.tab_title_txt);
		View reqRecTabView = LayoutInflater.from(getActivity()).inflate(R.layout.tab_bg, null);
		final ImageView reqRecTabTitleTxt = (ImageView)reqRecTabView.findViewById(R.id.tab_title_txt);   
		badgeReqRecCountTxt = (TextView) reqRecTabView.findViewById(R.id.badge_rr_count_txt);
		//Check for request received count and show badge
		TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				int inviteCount=GPSSharedPreference.getInviteCountSharePreference();
				if(inviteCount > 0 && !isInviteClicked){
					badgeReqRecCountTxt.setVisibility(View.VISIBLE);
					badgeReqRecCountTxt.setText(""+inviteCount);
				}
				//setTabColor(tabHost);
				FragmentManager fm =   getChildFragmentManager();
				InviteFragment inviteFragment = (InviteFragment) fm.findFragmentByTag("invite");
				// RequestSendFragment requestSendFragment = (RequestSendFragment) fm.findFragmentByTag("request send");
				RequestReceivedFragment reqReceiveFrag = (RequestReceivedFragment)fm.findFragmentByTag("request receive");
				// TrackListFragment trackListFrag = (TrackListFragment)fm.findFragmentByTag("track list");
				FragmentTransaction ft = fm.beginTransaction();
				/** Detaches the androidfragment if exists */
				if(inviteFragment!=null)
					ft.detach(inviteFragment);
				/** Detaches the applefragment if exists */
				/* if(requestSendFragment!=null)
                    ft.detach(requestSendFragment);*/
				if(reqReceiveFrag!=null)
					ft.detach(reqReceiveFrag);
				/*if(trackListFrag!=null)
                    ft.detach(trackListFrag);*/
				/** If current tab is invite */
				if(tabId.equalsIgnoreCase("invite")){
					/*	inviteTabTitleTxt.setBackgroundColor(inviteTabTitleTxt.getContext().getResources().getColor(R.color.tab_on));
                	reqRecTabTitleTxt.setBackgroundColor(reqRecTabTitleTxt.getContext().getResources().getColor(R.color.tab_off));
					 */	
					inviteTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.invite_tab_click_btn));
					if(inviteCount > 0 && !isInviteClicked)
						reqRecTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.request_received_badge));
					else
						reqRecTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqrec_tab_btn));
					//reqSendTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqsent_tab_btn));
					//trackListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.tracklist_tab_btn));
					if(inviteFragment==null){
						/** Create AndroidFragment and adding to fragmenttransaction */
						ft.add(R.id.realtabcontent,new InviteFragment(), "invite");
					}else{
						/** Bring to the front, if already exists in the fragmenttransaction */
						ft.attach(inviteFragment);
					}

				}
				/* else if(tabId.equalsIgnoreCase("request send")){
				 *//** If current tab is req send *//*
                	inviteTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.invite_tab_top_btn));
                	reqSendTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqsent_tab_click_btn));
                	reqRecTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqrec_tab_btn));
                	//trackListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.tracklist_tab_btn));
                    if(requestSendFragment==null){
				  *//** Create AppleFragment and adding to fragmenttransaction *//*
                        ft.add(R.id.realtabcontent,new RequestSendFragment(), "request");
                     }else{
				   *//** Bring to the front, if already exists in the fragmenttransaction *//*
                        ft.attach(requestSendFragment);
                    }
                }*/
				else if(tabId.equalsIgnoreCase("request receive")){
					/*                	inviteTabTitleTxt.setBackgroundColor(inviteTabTitleTxt.getContext().getResources().getColor(R.color.tab_off));
                	reqRecTabTitleTxt.setBackgroundColor(reqRecTabTitleTxt.getContext().getResources().getColor(R.color.tab_on));
					 */                	inviteTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.invite_tab_top_btn));
					 reqRecTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqrec_tab_click_btn));
					 //reqSendTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqsent_tab_btn));
					 //trackListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.tracklist_tab_btn));
					 //Hide badge count
					 badgeReqRecCountTxt.setVisibility(View.INVISIBLE);
					 isInviteClicked=true;
					 if(reqReceiveFrag==null){
						 /** Create AndroidFragment and adding to fragmenttransaction */
						 ft.add(R.id.realtabcontent,new RequestReceivedFragment(), "request receive");
					 }else{
						 /** Bring to the front, if already exists in the fragmenttransaction */
						 ft.attach(reqReceiveFrag);
					 }
				}
				/*else{    *//** If current tab is track list *//*
                	inviteTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.invite_tab_btn));
                	reqSendTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqsent_tab_btn));
                	reqRecTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.reqrec_tab_btn));
                	trackListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.tracklist_tab_click_btn));
                    if(trackListFrag==null){
				 *//** Create AppleFragment and adding to fragmenttransaction *//*
                        ft.add(R.id.realtabcontent,new TrackListFragment(), "track list");
                     }else{
				  *//** Bring to the front, if already exists in the fragmenttransaction *//*
                        ft.attach(trackListFrag);
                    }
                }*/
				ft.commit();
			}
		};
		/** Setting Tabchangelistener for the tab */
		tabHost.setOnTabChangedListener(tabChangeListener);
		TabHost.TabSpec inviteTSpec = tabHost.newTabSpec("invite");
		inviteTSpec.setIndicator(inviteTabView);
		inviteTSpec.setContent(new TabContent(getActivity()));
		tabHost.addTab(inviteTSpec);
		tabHost.getTabWidget().getChildTabViewAt(0).setBackgroundColor(Color.WHITE);
		TabHost.TabSpec reqRecTSpec = tabHost.newTabSpec("request receive");
		reqRecTSpec.setIndicator(reqRecTabView);
		reqRecTSpec.setContent(new TabContent(getActivity()));
		tabHost.addTab(reqRecTSpec);
		tabHost.getTabWidget().getChildTabViewAt(1).setBackgroundColor(Color.WHITE);
		if(msg!=null){
			tabHost.setCurrentTab(1);
		}else{
		}
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		/*int inviteCount=GPSSharedPreference.getInviteCountSharePreference();
     	if(inviteCount > 0){
     		badgeReqRecCountTxt.setVisibility(View.VISIBLE);
     		badgeReqRecCountTxt.setText(""+inviteCount);
     	}*/
	}
	public class TabContent implements TabContentFactory {
		private Context mContext;
		public TabContent(Context mContext) {
			this.mContext = mContext;
		}
		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
}
