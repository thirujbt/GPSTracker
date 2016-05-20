package com.pickzy.moresdk.moreviews;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class MoreTextView extends TextView {
	private Context cont;
	public static int count=0;
	private LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	public MoreTextView(Context context) {
		super(context);
		try{
			this.setText(""+count);
		}catch(Exception e){
			this.setText("0");
		}
		this.setTextColor(Color.BLACK);
		this.setTextSize(8);
		params.gravity=Gravity.CENTER;
		this.setLayoutParams(params);
	}
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			if(count>=0){
				this.setText(""+count);
			}else{
				this.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}
}
