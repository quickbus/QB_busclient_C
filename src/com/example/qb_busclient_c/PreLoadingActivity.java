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
        		//SharedPreferenceÿ�ε���edit()�������ص���ʵ�ǲ�ͬ��Editor��������һ��Ҫͨ��editor_config�������޸�
        		Share.editor_config=Share.preferences_config.edit();
        		Share.editor_config.commit();
        		 
        		//��¼״ָ̬ʾ��LauchReiveiverʹ�á�
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