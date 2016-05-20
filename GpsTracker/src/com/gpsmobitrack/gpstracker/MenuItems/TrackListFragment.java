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

import com.gpstracker.pro.R;

public class TrackListFragment extends Fragment{


	//FragmentTabHost tabHost;
	TabHost tabHost;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_page, null);
        
    	tabHost = (TabHost)view.findViewById(android.R.id.tabhost);
    	tabHost.setup();
    	
    	View trackListTabView = LayoutInflater.from(getActivity()).inflate(R.layout.tab_bg, null);
     	final ImageView trackListTabTitleTxt = (ImageView)trackListTabView.findViewById(R.id.tab_title_txt);
     	
     	View blockListTabView = LayoutInflater.from(getActivity()).inflate(R.layout.tab_bg, null);
     	final ImageView blockListTabTitleTxt = (ImageView)blockListTabView.findViewById(R.id.tab_title_txt);
    	
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        	 
            @Override
            public void onTabChanged(String tabId) {
                FragmentManager fm =   getChildFragmentManager();
                TrackListPage trackListFragment = (TrackListPage) fm.findFragmentByTag("track list");
                BlockListPage blockListFrag = (BlockListPage)fm.findFragmentByTag("block list");
                FragmentTransaction ft = fm.beginTransaction();
                
 
                /** Detaches the androidfragment if exists */
                if(trackListFragment!=null)
                    ft.detach(trackListFragment);
 
                /** Detaches the applefragment if exists */
                if(blockListFrag!=null)
                    ft.detach(blockListFrag);
                
 
                /** If current tab is invite */
                if(tabId.equalsIgnoreCase("track list")){
                	trackListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.tracklist_tab_click_btn));
                	blockListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.blocklist_tab_btn));
 
                    if(trackListFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new TrackListPage(), "track list");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(trackListFragment);
                    }
 
                }
                else if(tabId.equalsIgnoreCase("block list")){
                	trackListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.tracklist_tab_btn));
					blockListTabTitleTxt.setImageDrawable(getResources().getDrawable(R.drawable.blocklist_tab_click_btn));
                	
                    if(blockListFrag==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new BlockListPage(), "block list");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(blockListFrag);
                    }
 
                }
                
                ft.commit();
            }
        };
 
        /** Setting Tabchangelistener for the tab */
        tabHost.setOnTabChangedListener(tabChangeListener);
    	TabHost.TabSpec inviteTSpec = tabHost.newTabSpec("track list");
    	inviteTSpec.setIndicator(trackListTabView);
    	inviteTSpec.setContent(new TabContent(getActivity()));
    	tabHost.addTab(inviteTSpec);
    	tabHost.getTabWidget().getChildTabViewAt(0).setBackgroundColor(Color.WHITE);
    	
        
    	TabHost.TabSpec reqRecTSpec = tabHost.newTabSpec("block list");
    	reqRecTSpec.setIndicator(blockListTabView);
    	reqRecTSpec.setContent(new TabContent(getActivity()));
    	tabHost.addTab(reqRecTSpec);
    	tabHost.getTabWidget().getChildTabViewAt(1).setBackgroundColor(Color.WHITE);
    	
        return view;
    }
    
    public class TabContent implements TabContentFactory {

    	private Context mContext;

    	public TabContent(Context mContext) {
    		this.mContext = mContext;
    	}

    	@Override
    	public View createTabContent(String tag) {
    		// TODO Auto-generated method stub

    		View v = new View(mContext);
    		v.setMinimumWidth(0);
    		v.setMinimumHeight(0);
    		return v;
    	}

    }

}
