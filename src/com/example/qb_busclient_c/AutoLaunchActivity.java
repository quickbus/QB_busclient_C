package com.example.qb_busclient_c;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONException;












//import android.R.color;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
import android.content.Intent;
//import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
//import android.graphics.Color;
import android.view.Menu;
import android.widget.Button;
//import android.widget.ImageButton;

//import android.widget.Toast;

import java.lang.ref.WeakReference;

//import android.content.SharedPreferences;

public class AutoLaunchActivity extends Activity
{
//	SharedPreferences preferences_config;
//	SharedPreferences.Editor editor_config;
	
	Map<String,String> params;
	String res;
	List<Map<String,Object>> ml;
	Button btn_r;
	AmainHandler hdl;
   // private static final int UPDATE_UI = 0;
   //采用内部类和弱引用
	static class AmainHandler extends Handler 
	{
		
		WeakReference<AutoLaunchActivity> mActivity;  
		  
		public AmainHandler(AutoLaunchActivity activity) {  
                mActivity = new WeakReference<AutoLaunchActivity>(activity);  
        }  

		@Override
		public void handleMessage(Message msg) 
		{
			AutoLaunchActivity theActivity = mActivity.get(); 

			switch (msg.what) 
			{
             	
				case Share.ST_VALID_ROUTES: 
				{
					//StringBuilder sb=new StringBuilder();
					theActivity.ml=Share.lstRoutes;
					
	                Intent intent = new Intent(theActivity, QBMapActivity.class); 
	                theActivity.startActivity(intent); 
	                theActivity.finish();
	                break;
                }
                /*case Share.ST_NO_ROUTES:
                {
                	Intent intent = new Intent(theActivity, MainActivity.class); 
	                theActivity.startActivity(intent); 
                	
                	AlertDialog.Builder builder = new AlertDialog.Builder(theActivity)
                	.setTitle("快巴士")
                	.setIcon(R.drawable.qb_busclient)
                	.setMessage("请首先创建路线!");
                	
                	theActivity.setPositiveButton(builder)
                	.create()
					.show();
                	
                	String url="http://115.29.204.94/Users/login";
			    	Uri u = Uri.parse(url); 
			    	Intent it = new Intent(Intent.ACTION_VIEW, u); 
			    	theActivity.startActivity(it);
                	break;
                }
                default:
                {
	                
                	AlertDialog.Builder builder = new AlertDialog.Builder(theActivity)
                 	.setTitle("快巴士")
                 	.setIcon(R.drawable.qb_busclient)
                 	.setMessage("登陆失败![ERR=]"+msg.what);
                	
                	theActivity.setPositiveButton(builder)
                	.create()
					.show();
                	
                	Intent intent = new Intent(theActivity, MainActivity.class); 
	                theActivity.startActivity(intent); 
                }*/
				default:
                {      
                	Share.editor_config.putBoolean("loginstatusindex", false);
    	    		Share.editor_config.commit();
                	Intent intent = new Intent(theActivity, MainActivity.class); 
	                theActivity.startActivity(intent);
	                theActivity.finish();
                }
                     break;
			}
		}
	}
	
/*	private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder)
	{
		return builder.setPositiveButton("确定", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Share.editor_config.putBoolean("loginstatusindex", false);
	    		Share.editor_config.commit();
				Toast.makeText(getBaseContext(), "确定", Toast.LENGTH_SHORT).show();
				AutoLaunchActivity.this.finish();
				//AppManager.getAppManager().AppExit(AutoLaunchActivity.this);
			}
		});
	}*/

	private void autoInitShare()
	{
		params.put("data[User][username]",Share.preferences_config.getString("username", ""));
		params.put("data[User][password]", Share.preferences_config.getString("password", ""));
		Share.login_info=params;
		Share.url="http://115.29.204.94/Users/client_login";//txtUrl.getText().toString();
		Share.aAct=this;
		PostData.httpc=new DefaultHttpClient();//clear sessions
	}

     
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);

		params=new HashMap<String,String>();
	
		hdl=new AmainHandler(this);
		
		autoInitShare();
		Share.getAutoLoginStatus();
		
//		finish();
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
	        {
	        	AppManager.getAppManager().AppExit(this);
	        	return true;
	        }
//	        else if(keyCode==KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0)
//	        {
//	        	return true;
//	        }
	        return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}