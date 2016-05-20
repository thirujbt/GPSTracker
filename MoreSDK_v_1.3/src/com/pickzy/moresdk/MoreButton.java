package com.pickzy.moresdk;

import org.xml.sax.Attributes;



import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pickzy.moresdk.moreviews.MoreFrameLayout;
import com.pickzy.moresdk.moreviews.MoreTextView;

public class MoreButton extends FrameLayout implements OnClickListener {
	private static Context cont;
	private Drawable d, d1;
	private MoreFrameLayout framelayout;
	public static Attributes width, height;
    public static boolean notified=false;
    public static boolean processindicator=false;
    public static Dialog dialog;
    private ProgressBar spinner;
	public MoreButton(Context context, AttributeSet attributes) {
		super(context, attributes);
		setClickable(true);
		this.setOnClickListener(this);
		int[] arr = null;
		cont = context;
		try{
		if(!isInEditMode()){
		spinner=new ProgressBar(cont);
		dialog=new Dialog(cont);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		try{
		spinner.setBackgroundColor(Color.LTGRAY);
		}catch(Exception color){
		}
		dialog.setContentView(spinner);
		dialog.setCancelable(true);
		}
		}
		catch(Exception e){
			
		}
		framelayout = new MoreFrameLayout(cont);
		LayoutParams params1 = new LayoutParams(25, 25);
		params1.gravity = Gravity.RIGHT;
		params1.topMargin=2;
		params1.rightMargin=2;
		framelayout.setLayoutParams(params1);
		try {
			d = this.getForeground();
			if (d != null) {
				this.setForeground(null);
				framelayout.setBackgroundDrawable(d);
			} else {
				framelayout.setBackgroundDrawable(new BitmapDrawable(makeRadGrad()));
			}
		} catch (Exception e) {
		}
		try {
			d1 = this.getBackground();
			if (d1 != null) {
				this.setBackgroundDrawable(null);
			}
		} catch (Exception e) {
		}
		ImageView backimage = new ImageView(cont);
		LayoutParams params2 = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		backimage.setLayoutParams(params2);
		backimage.setImageDrawable(d1);
		this.addView(backimage);
		this.addView(framelayout);
		 try{
			 if(MoreTextView.count<=0){
				 framelayout.setVisibility(View.INVISIBLE);
				 this.removeView(framelayout);
			 }else{
				 this.removeView(framelayout);
				 this.addView(framelayout);
				 notified=false;
			 }
			 }catch(Exception e){
				 
			 }
	}

	 public void onDraw(Canvas canvas) {
	 super.onDraw(canvas);
	 try{
	 if(MoreTextView.count<=0){
		 framelayout.setVisibility(View.INVISIBLE);
		 this.removeView(framelayout);
	 }else{
		 this.removeView(framelayout);
		 this.addView(framelayout);
	 }
	 }catch(Exception e){
		 
	 }
	 }
	// int w=this.getWidth();
	// int h=this.getHeight();
	// int cir=0;
	// try{
	// if (h>=w){
	// cir=w/2;
	// }else{
	// cir=h/2;
	// }
	// while(cir>=h){
	// cir=cir/2;
	// }
	// int mar=cir/10;
	// LayoutParams params1=new LayoutParams(cir,cir);
	// params1.gravity=Gravity.RIGHT;
	// params1.topMargin=mar;
	// params1.leftMargin=mar;
	// params1.rightMargin=mar;
	// params1.bottomMargin=mar;
	// try{
	// int w1=framelayout.getWidth();
	// int h1=framelayout.getHeight();
	// MoreTextView.width=w1;
	// MoreTextView.height=h1;
	// }catch(Exception e){
	//
	// }
	// try{
	// if(d!=null){
	// framelayout.setBackgroundDrawable(d);
	// }else{
	// Drawable d1=new BitmapDrawable(makeRadGrad());
	// framelayout.setBackgroundDrawable(d1);
	// }
	// }catch(Exception e){
	//
	// }
	// // this.removeView(framelayout);
	// // this.addView(framelayout);
	// }catch(Exception e){
	// }
	// }
	
	@Override
	public void onClick(View v) {
		notified=true;
		PZYAppPromoSDK more =new PZYAppPromoSDK();
		try{
		if(PZYAppPromoSDK.strings!=null && PZYAppPromoSDK.passimage!=null ){
		Intent moreactivity = new Intent(cont.getApplicationContext(),MoreActivity.class);
		cont.startActivity(moreactivity);
		PZYAppPromoSDK.startingactivity=true;
		}else if(PZYAppPromoSDK.strings!=null && PZYAppPromoSDK.passimage==null){
			more.keynodeposition();
		}
		else{
			if(isInternetOn(cont)){
			if(PZYAppPromoSDK.running=false){
				PZYAppPromoSDK.startingactivity=true;
			}else{
				ShowProcess();
			}
			}else{
				more.databaseexecuter();
			}
		}
		}catch(Exception e){
			
		}
	}
	private void ShowProcess(){
		processindicator=true;
		dialog.show();
	}
	public static void stopprocess(){
		processindicator=false;
		dialog.dismiss();
		try{
			Intent moreactivity = new Intent(cont,MoreActivity.class);
			cont.startActivity(moreactivity);
		}catch(Exception ca){
			
		}
	}
	private boolean isInternetOn(Context context1) {
		try{
		ConnectivityManager connec = (ConnectivityManager) context1	.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} 
		else 
		{
			return false;
		}
		}catch(Exception e){
			
		}
		return false;
	}
	private Bitmap makeRadGrad() {
		RadialGradient gradient = new RadialGradient(20, 20, 20, 0xFFFFFF00,
				0xFFFF6600, android.graphics.Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setDither(true);
		p.setShader(gradient);
		Bitmap bitmap = Bitmap.createBitmap(40, 40, Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		c.drawCircle(20, 20, 20, p);
		return bitmap;
	}
}
