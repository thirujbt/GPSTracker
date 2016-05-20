package com.pickzy.moresdk.moreviews;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;

public class MoreFrameLayout extends FrameLayout {
	private MoreTextView textview;

	public MoreFrameLayout(Context context) {
		super(context);
		textview = new MoreTextView(context);
		this.addView(textview);
	}

	
}
