package com.example.qb_busclient_c;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONException;










//import android.R.color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
//import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

//import android.content.SharedPreferences;

public class MainActivity extends Activity implements OnClickListener 
{
/*	SharedPreferences preferences_config;
	SharedPreferences.Editor editor_config;*/
	
	Map<String,String> params;
	EditText txt1;
	EditText txt2;
//	EditText txtUrl;
	Button btn;
	Button btn_q;
	String res;
	ProgressBar pb;
	List<Map<String,Object>> ml;
	Button btn_r;
	String username_initial="";
	boolean checkInIndex=false;
	
	MainHandler hdl;
	Timer timer;
   // private static final int UPDATE_UI = 0;
   //采用内部类和弱引用
	static class MainHandler extends Handler 
	{
		
		WeakReference<MainActivity> mActivity;  
		  
		public MainHandler(MainActivity activity) {  
                mActivity = new WeakReference<MainActivity>(activity);  
        }  

		@Override
		public void handleMessage(Message msg) 
		{
			MainActivity theActivity = mActivity.get(); 
			theActivity.pb.setVisibility(-1);
			theActivity.btn.setEnabled(true);
			switch (msg.what) 
			{
             	
				case Share.ST_VALID_ROUTES: 
				{
					//StringBuilder sb=new StringBuilder();
					theActivity.ml=Share.lstRoutes;
					
	                Intent intent = new Intent(theActivity, QBMapActivity.class); 
	                theActivity.startActivity(intent); 
	                //成功登陆后就删除该activity
	                theActivity.finish();
	                break;
                }
                case Share.ST_NO_ROUTES:
                {
                	new AlertDialog.Builder(theActivity)
                	.setTitle("快巴士")
                	.setIcon(R.drawable.qb_busclient)
                	.setMessage("请首先创建路线!")
                	.setPositiveButton("确定" ,  null )  
                	.show();
                	String url="http://115.29.204.94/Users/login";
			    	Uri u = Uri.parse(url); 
			    	Intent it = new Intent(Intent.ACTION_VIEW, u); 
			    	theActivity.startActivity(it);
                	break;
                }
                case Share.ST_REGISTER_ENABLE:
                {
                	if(TextUtils.isEmpty(theActivity.txt1.getText()) || TextUtils.isEmpty(theActivity.txt2.getText()))
                	{
                		theActivity.checkInIndex = false;
                		theActivity.btn.setTextColor(theActivity.getResources().getColor(R.color.register_disable_gray));
                	}
                	else
                	{
                		theActivity.checkInIndex = true;
                		theActivity.btn.setTextColor(theActivity.getResources().getColor(R.color.register_enable_gray));
                	}
                	break;
                }
                default:
                {
                	new AlertDialog.Builder(theActivity)
                 	.setTitle("快巴士")
                 	.setIcon(R.drawable.qb_busclient)
                 	.setMessage("登陆失败![ERR=]"+msg.what)
                 	.setPositiveButton("确定" ,  null )  
                 	.show();
                }
                     break;
			}
		}
	}

	private void initShare()
	{
		params.put("data[User][username]",txt1.getText().toString());
		params.put("data[User][password]", txt2.getText().toString());
		Share.login_info=params;
		Share.url="http://115.29.204.94/Users/client_login";//txtUrl.getText().toString();
		Share.mAct=this;
		PostData.httpc=new DefaultHttpClient();//clear sessions
	}

     
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AppManager.getAppManager().addActivity(this);
		
/*		Share.preferences_config=getSharedPreferences("QB_busclient_config", MODE_PRIVATE);
		//SharedPreference每次调用edit()方法返回的其实是不同的Editor对象，所以一定要通过editor_config对象来修改
		Share.editor_config=Share.preferences_config.edit();
		Share.editor_config.commit();*/

		txt1=(EditText) findViewById(R.id.editText1);
		txt2=(EditText) findViewById(R.id.editText2);
	//	txtUrl=(EditText) findViewById(R.id.editText3);
		pb=(ProgressBar)findViewById(R.id.progressBar1);
		btn_r=(Button)findViewById(R.id.button3);
		params=new HashMap<String,String>();
	
		pb.setVisibility(-1);
		hdl=new MainHandler(this);
		
		btn=(Button) findViewById(R.id.button1);
		btn_q=(Button) findViewById(R.id.button2);
		//btn.setBackgroundColor(Color.rgb(60, 200, 150));
		btn.setOnClickListener(this);
		btn_q.setOnClickListener(this);
		btn_r.setOnClickListener(this);
		
		username_initial=Share.preferences_config.getString("username", "");
		
		txt1.setText(username_initial);
		txt2.setText("");
		
		timer = new Timer(true);
		timer.schedule(new TimerTask()
        {  
            @Override  
            public void run()
            {  
            	hdl.sendEmptyMessageDelayed(Share.ST_REGISTER_ENABLE, 0);
            }  
        }, 300, 300);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
	        {
	        	//标记登录状态为退出
	        	Share.editor_config.putBoolean("loginstatusindex", false);
	    		Share.editor_config.commit();
	        	timer.cancel();
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

	@Override
	public void onClick(View arg0) 
	{
		// TODO Auto-generated method stub
		   
		switch (arg0.getId())
		{
			case R.id.button1:
		    {
		    	if(checkInIndex == true)
		    	{
		    		checkInIndex=false;
		    		initShare();
		    		Share.editor_config.putString("username", params.get("data[User][username]"));
		    		Share.editor_config.putString("password", params.get("data[User][password]"));
		    		Share.editor_config.commit();
		    		Share.getLoginStatus();
		    		pb.setVisibility(1);
		    		//pb.animate();
		    		this.btn.setEnabled(false);
		    	}
		    	break;
		    }
			case R.id.button2:
		    {
		    	//标记登录状态为退出
	        	Share.editor_config.putBoolean("loginstatusindex", false);
	    		Share.editor_config.commit();
		    	timer.cancel();
		    	AppManager.getAppManager().AppExit(this);
				break;
		    }
		     //Toast.makeText(MainActivity.this, "按钮1被单击", Toast.LENGTH_LONG).show();
		    case R.id.button3:
		    {/*
		    	Intent intent = new Intent();
		    	intent.setData(Uri.parse("http://115.29.204.94/Users/"));
		    	intent.setAction(Intent.ACTION_VIEW);
		    	this.startActivity(intent); //启动浏览器
		    	*/
		    	String url="http://115.29.204.94/Users/register";
		    	Uri u = Uri.parse(url); 
		    	Intent it = new Intent(Intent.ACTION_VIEW, u); 
		    	MainActivity.this.startActivity(it);
		    	break;
		    }
		    default:
		    break;
		}
	}

}