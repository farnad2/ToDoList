package com.farnadsoft.to_do_list.UI;


import com.farnadsoft.to_do_list.Database.NoZoomControllWebView;
import com.farnadsoft.to_do_list.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class CustomeWebView extends AppCompatActivity{

	public NoZoomControllWebView webView;
	ProgressDialog mProgressDialog;

	ProgressDialog mSpinner;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.webview);
		
		mProgressDialog=new ProgressDialog(CustomeWebView.this);
		
		mProgressDialog.setMessage("Loading...");
		webView = new NoZoomControllWebView(this);
		webView = (NoZoomControllWebView) findViewById(R.id.webView1);
		webView.setWebViewClient(new CustomClient());
		webView.getSettings().setJavaScriptEnabled(true);
		String url;
		try{
		Bundle where = getIntent().getExtras();
		url = where.getString("link");
		}catch (NullPointerException e) {
			url="http://www.icanstudioz.com";
		}
		webView.loadUrl(url);
//		AdView adView = (AdView) findViewById(R.id.adView);
//		if (getResources().getString(R.string.ad_unit_id).length() == 0) {
//			adView.setVisibility(View.GONE);
//		} else {
//			adView.setVisibility(View.VISIBLE);
//			AdRequest adRequest = new AdRequest.Builder()
//					.addTestDevice("5AD47C03FEA90E3222051A8F076F8976")
//					.addTestDevice("3A56423DE328DF0B2B09E6B157C2CC32").build();
//			adView.loadAd(adRequest);
//		}
		
	}
	private class CustomClient extends WebViewClient {
		
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// Log.d(TAG, "Loading URL: " + url);
			
			mProgressDialog.show();
			
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mProgressDialog.cancel();
		
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			
		}
		
		 @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        if (Uri.parse(url).getScheme().equals("market")) {
		            try {
		                Intent intent = new Intent(Intent.ACTION_VIEW);
		                intent.setData(Uri.parse(url));
		                Activity host = (Activity) view.getContext();
		                host.startActivity(intent);
		                return true;
		            } catch (ActivityNotFoundException e) {
		                // Google Play app is not installed, you may want to open the app store link
		                Uri uri = Uri.parse(url);
		                view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
		                return false;
		            }

		        }
		        return false;
		    }
		

}

	public void onBackPressed() {
		if (webView.isFocused() && webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.webview_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_reload:
			Toast.makeText(getApplicationContext(), "Reload page...",
					Toast.LENGTH_SHORT).show();
			webView.reload();
			break;
		case R.id.action_exit:
			finish();
			break;
		case R.id.action_back:
			if(webView.canGoBack()){
				webView.goBack();
			}else{
				finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}

}