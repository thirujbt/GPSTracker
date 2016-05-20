package com.pickzy.moresdk.moreviews;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MoreLayout extends LinearLayout{

	private static int wid,hei;
	public static final LayoutParams layoutparams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
	public MoreLayout(Context context,int width,int height) {
		super(context);
		wid=width;
		hei=height;
		layoutparams.width=(width);
		layoutparams.height=(height);
		this.setLayoutParams(layoutparams);
		this.setBackgroundColor(Color.TRANSPARENT);
	}
}
