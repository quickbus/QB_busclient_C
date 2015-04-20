package com.example.qb_busclient_c;

//import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import org.json.JSONException;


import com.baidu.mapapi.model.LatLng;

//import android.app.Activity;
import android.content.SharedPreferences;

//import com.example.qb_busclient.MainActivity.MainHandler;

public class Share {
	public static SharedPreferences preferences_config;
	public static SharedPreferences.Editor editor_config;
	//List线程不安全，需要Handler来传递信息
	public static List<Map<String,Object>> lstRoutes=null;
	public static List<LatLng> lstRoutePoints=null;
	public static List<Map<String,Object>> desLoc=null;
	public static Map<String,String> login_info=null;
	public static String pswd="";
	public static String url="";
	public static int status=-1;
	
	public static final int ST_NET_ERR=-1;
	public static final int ST_PWD_ERR=0;
	public static final int ST_NO_ROUTES=1;
	public static final int ST_VALID_ROUTES=2;
	public static final int ST_REGISTER_ENABLE=3;
	
	public static final int ST_GPS_UPDATE=10;
	public static final int ST_GPS_NOUPDATE=13;
	public static final int ST_GPS_DESTINATIONCHECK=12;
	public static final int ST_POST_ERR=11;
	//终点检测倒计时
	public static final int ST_DESCHECKDC_UPDATE=14;
	
	public static final int QB_DRAW_ROUTE_STATION=101;
	public static final int QB_DRAW_ROUTE_POINT=102;
	
	public static double ST_DES_LO=121.60090322;
	public static double ST_DES_LA=31.25370703;
//	public static final double ST_DES_LO=121.60090322;
//	public static final double ST_DES_LA=31.25370703;
//	public static final double ST_DES_LO=121.44116464;
//	public static final double ST_DES_LA=31.27055624;
	
	public static final double PI=3.1415926;
	public static final double EARTH_RADIUS=6378.137;
	public static final double DES_RADIUS=0.2;
	public static final double DES_RADIUS_BIG=0.4;
	public static final int DESCNTDCDEFAULT=20;
	
	
//	public static String resp_tmp="";
	public static String route_sel_id="";
	public static MainActivity mAct;
//	public static FuncSelectTabActivity fAct;
	public static AutoLaunchActivity aAct;
	public static int desCntDc=DESCNTDCDEFAULT;

	/*//登录状态指示，LauchReiveiver使用。
	public static boolean loginStatusIndex=false;*/
	
	//GPS坐标GPRS上报使能
	public static boolean gpsUpdateByGprsIndex=false;
	//终点检测使能寄存器
	public static boolean destinationCheckIndex=false;
	//终点坐标有效寄存器
	public static boolean destinationLocIndex=false;
	
	//get current login status;
	public static void getLoginStatus()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
		
				try 
				{
							
					String resp=PostData.postFormsToURL(url,login_info);
					if(resp=="")
					{	//network error, check network or passwd
						status=Share.ST_NET_ERR;
					}
					else if(resp.length()>2 && resp.matches(".*(?i)HTML.*BODY.*"))
					{
						status=Share.ST_PWD_ERR;
					}
					else if (resp.length()<4)
					{
						status=Share.ST_NO_ROUTES;			
					}
					else
					{
						lstRoutes=JSONPaser.putJSONToArray(resp);
						status=Share.ST_VALID_ROUTES;
								
					}
					if(mAct!=null)
					{
						mAct.hdl.sendEmptyMessageDelayed(status, 0);
					}
							
							
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try 
				{
					Thread.sleep(5*1000);
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}).start();
	}
	
	//
/*	public static void getDestinationLocation()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
		
				try 
				{
							
					String resp=PostData.postFormsToURL(url,login_info);
					if(resp=="")
					{	//network error, check network or passwd
						status=Share.ST_NET_ERR;
					}
					else if(resp.length()>2 && resp.matches(".*(?i)HTML.*BODY.*"))
					{
						status=Share.ST_PWD_ERR;
					}
					else if (resp.length()<4)
					{
						status=Share.ST_NO_ROUTES;			
					}
					else
					{
						desLoc=JSONPaser.putDesJSONToArray(resp);
						status=Share.ST_VALID_ROUTES;
								
					}
					if(fAct!=null)
					{
						fAct.hdl.sendEmptyMessageDelayed(status, 0);
					}
							
							
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try 
				{
					Thread.sleep(5*1000);
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}).start();
	}*/
	
	//get current login status;
	public static void getAutoLoginStatus()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
				try 
				{
									
					String resp=PostData.postFormsToURL(url,login_info);
					if(resp=="")
					{	//network error, check network or passwd
						status=Share.ST_NET_ERR;
					}
					else if(resp.length()>2 && resp.matches(".*(?i)HTML.*BODY.*"))
					{
						status=Share.ST_PWD_ERR;
					}
					else if (resp.length()<4)
					{
						status=Share.ST_NO_ROUTES;			
					}
					else
					{
						lstRoutes=JSONPaser.putJSONToArray(resp);
						status=Share.ST_VALID_ROUTES;
										
					}
					if(aAct!=null)
					{
						aAct.hdl.sendEmptyMessageDelayed(status, 0);
					}
									
									
				}
				catch (JSONException e) 
				{
							// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try 
				{
					Thread.sleep(5*1000);
				} 
				catch (InterruptedException e) 
				{
							// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
		}).start();
	}

//计算弧度
	public static double radian(double d)
	{
	    return d * PI / 180.0;   //角度1? = π / 180
	}
	
//计算距离
	public static double get_distance(double lat1, double lng1, double lat2, double lng2)
	{
	    double radLat1 = radian(lat1);
	    double radLat2 = radian(lat2);
	    double a = radLat1 - radLat2;
	    double b = radian(lng1) - radian(lng2);
	     
	    double dst = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2) ));
	     
	    dst = dst * EARTH_RADIUS;
	    dst= Math.round(dst * 10000) / 10000.0;
	    return dst;
	}
	

}
