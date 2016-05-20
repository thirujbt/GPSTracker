package com.pickzy.moresdk.moreviews;

import android.content.Context;

import android.graphics.Color;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class MoreListView extends ListView {
	public MoreListView(Context context) {
		super(context);
		this.setBackgroundColor(Color.GRAY);
	}
}
