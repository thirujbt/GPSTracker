package com.pickzy.moresdk.moreviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class MoreImage extends ImageView implements OnClickListener {
	private Context cont;

	public MoreImage(Context context) {
		super(context);
		setClickable(true);
		this.setOnClickListener(this);
		cont = context;
		this.setImageBitmap(makeRadGrad());
	}

	public MoreImage(Context context, AttributeSet attributes) {
		super(context, attributes);
		setClickable(true);
		this.setOnClickListener(this);
		cont = context;
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w=this.getWidth();
		int h=this.getHeight();
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(cont, "more button", Toast.LENGTH_SHORT).show();
	}

	private Bitmap makeRadGrad() {
		RadialGradient gradient = new RadialGradient(200, 200, 200, 0xFFFFFF00,
				0xFFFF6600, android.graphics.Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setDither(true);
		p.setShader(gradient);
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		c.drawCircle(200, 200, 200, p);
		return bitmap;
	}
}
