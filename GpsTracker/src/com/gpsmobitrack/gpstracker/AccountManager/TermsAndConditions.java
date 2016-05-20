package com.gpsmobitrack.gpstracker.AccountManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;


@SuppressLint("SetJavaScriptEnabled")
public class TermsAndConditions extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_codn);

		WebView wv = (WebView) findViewById(R.id.webview1);
		 final WebSettings webSettings = wv.getSettings();
         webSettings.setJavaScriptEnabled(true);
         webSettings.setSupportZoom(true);
         webSettings.setBuiltInZoomControls(true);
         wv.setWebViewClient(new MyWebViewClient());
         if(isInternetOn()){
        	 
        	 wv.loadUrl(AppConstants.TERMS_CONDTIONS_URL_GPS);
         } else {
        	 
        	 Utils.showToast("No Internet connection");
         }
         
	}



private class MyWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(final WebView view, final String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(final WebView view, final int errorCode, final String description, final String url) {
        super.onReceivedError(view, errorCode, description, url);
    }
}

//Check Internet connection
public final boolean isInternetOn() {
	ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
			|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
		return true;
	} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
			|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
		return false;
	}
	return false;
}
}