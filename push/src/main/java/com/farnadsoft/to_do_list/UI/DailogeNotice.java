package com.farnadsoft.to_do_list.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.farnadsoft.to_do_list.I;
import com.farnadsoft.to_do_list.LanguageHelper;
import com.farnadsoft.to_do_list.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



public class DailogeNotice extends Activity {

	public static SharedPreferences sp;
	String language;
   
	TextView date,code,msg,flink,title;
	Button readmore,close;
	ImageView image;
	private DisplayImageOptions options;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		language=sp.getString("language","fa"); //todo for english version change to "en"
		LanguageHelper.setLanguage(I.context,language);

    	setContentView(R.layout.gcm_dialog);
    	
    	date=(TextView)findViewById(R.id.date);
		image=(ImageView)findViewById(R.id.image);
		msg=(TextView)findViewById(R.id.msg);
		readmore=(Button)findViewById(R.id.readmore);
		
		final Bundle b=getIntent().getExtras();
		
		date.setText(b.getString("title"));
		msg.setText(b.getString("msg"));
		Linkify.addLinks(msg, Linkify.ALL);

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(options).build();
		ImageLoader.getInstance().init(config);

		try {
			ImageLoader.getInstance().displayImage(b.getString("image"),image);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(b.getString("link").equalsIgnoreCase("http://")){
			readmore.setVisibility(View.GONE);
		}else{
			readmore.setVisibility(View.VISIBLE);
			readmore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getApplicationContext(), CustomeWebView.class);
					
					intent.putExtra("link", b.getString("link"));
				    startActivity(intent);
				}
			});
			
		}
		
		close=(Button)findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
	
		
    }

	//* override the base context of application to update default locale for this activity
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LanguageHelper.onAttach(base));
	}
	//--------------------------------------------------*/


}