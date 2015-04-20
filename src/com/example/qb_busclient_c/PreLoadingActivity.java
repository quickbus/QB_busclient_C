package com.example.qb_busclient_c;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;



public class PreLoadingActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		
		new Timer().schedule(new TimerTask() {  
            @Override  
            public void run() { 
            	
        		Share.preferences_config=getSharedPreferences("QB_busclient_config", MODE_PRIVATE);
        		//SharedPreference每次调用edit()方法返回的其实是不同的Editor对象，所以一定要通过editor_config对象来修改
        		Share.editor_config=Share.preferences_config.edit();
        		Share.editor_config.commit();
        		 
        		//登录状态指示，LauchReiveiver使用。
        		boolean loginStatusIndex=Share.preferences_config.getBoolean("loginstatusindex", false);
        		
        		if(loginStatusIndex == false)
        		{
/*        			Intent intent = new Intent();
        			intent.setAction("com.example.qb_busclient.action.AUTOLAUNCH");
        			intent.putExtra("loginstatus", loginStatusIndex);
        			
        			sendBroadcast(intent);*/
        			startActivity(new Intent(PreLoadingActivity.this, MainActivity.class)); 
        		}
        		else
        		{
        			/*Intent intent = new Intent();
        			intent.setAction("com.example.qb_busclient.action.AUTOLAUNCH");
        			intent.putExtra("loginstatus", loginStatusIndex);
        			
        			sendBroadcast(intent);*/
        			
        			startActivity(new Intent(PreLoadingActivity.this, AutoLaunchActivity.class)); 
        		}
                cancel();
                finish();  
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);  
            }  
        }, 2000); 
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
		getMenuInflater().inflate(R.menu.preloading, menu);
		return true;
	}

}