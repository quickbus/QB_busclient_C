package com.example.qb_busclient_c;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.json.JSONException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
//import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
//import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
//import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
//import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.utils.CoordinateConverter;
//import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
//import android.widget.Toast;


public class QBMapActivity extends Activity {
	
	MapView mMapView = null; 
	BaiduMap mBaiduMap;
	List<Marker> mMarkerlist= new ArrayList<Marker>();
	//Marker mMarker[] = new Marker[26];
	//OverlayOptions[] ooA = new OverlayOptions[26];
	//Marker mMarkerA;
	//Marker mMarkerB;
	//Marker mMarkerC;
	InfoWindow mInfoWindow;
	
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor[] mbd;
	//BitmapDescriptor bdA;
	//BitmapDescriptor bdB;
	//BitmapDescriptor bdC;
	
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	
	boolean isFirstLoc = true;// 是否首次定位
	
	Map<String,String> paramsStation;
	Map<String,String> paramsPoint;
	List<LatLng> routePointlist= new ArrayList<LatLng>();
	List<Map<String,Object>> routeStationlist= new ArrayList<Map<String,Object>>();
	String resp = null;
	
	int lineColor = Color.argb ( 126,  0,  0,  255 );  // 半透明的紫色
	
	QBMapHandler hdl;
	
	static class QBMapHandler extends Handler 
	{
		
		WeakReference<QBMapActivity> mActivity;  
		  
		public QBMapHandler(QBMapActivity activity) {  
                mActivity = new WeakReference<QBMapActivity>(activity);  
        }  

		@Override
		public void handleMessage(Message msg) 
		{
			QBMapActivity theActivity = mActivity.get(); 
			switch (msg.what) 
			{
				case Share.QB_DRAW_ROUTE_STATION: 
				{
					//Toast.makeText(theActivity.getApplicationContext(), "resp"+":"+theActivity.resp, Toast.LENGTH_SHORT).show();
					// add marker overlay	
					//Toast.makeText(theActivity.getApplicationContext(), "n"+":"+String.valueOf(theActivity.routeStationlist.size()), Toast.LENGTH_SHORT).show();
					//Toast.makeText(theActivity.getApplicationContext(), theActivity.routeStationlist.get(0).toString()+":"+theActivity.routeStationlist.get(1).toString()+":"+theActivity.routeStationlist.get(2).toString(), Toast.LENGTH_SHORT).show();
					for(int i=0; i<theActivity.routeStationlist.size(); i++)
					{
						ArrayList<BitmapDescriptor> giflistA = new ArrayList<BitmapDescriptor>();
						giflistA.add(theActivity.mbd[i]);
						giflistA.add(theActivity.mbd[i]);
						giflistA.add(theActivity.mbd[i]);
						//BitmapDescriptor giflistA = theActivity.mbd[i];
						OverlayOptions ooA = new MarkerOptions().position((LatLng) theActivity.routeStationlist.get(i).get("LatLng")).icons(giflistA)
								.zIndex(0).period(10).draggable(true).visible(true);	
						Marker mMarkerA = (Marker) (theActivity.mBaiduMap.addOverlay(ooA));
						theActivity.mMarkerlist.add(mMarkerA);
					}
					
					break;
				}
				case Share.QB_DRAW_ROUTE_POINT: 
				{
					//Toast.makeText(theActivity.getApplicationContext(), "resp"+":"+theActivity.mlist.get(0).toString(), Toast.LENGTH_SHORT).show();
					//Toast.makeText(theActivity.getApplicationContext(), "resp"+":"+theActivity.resp, Toast.LENGTH_SHORT).show();
					OverlayOptions mpolylineOptions = new PolylineOptions()
						.points(theActivity.routePointlist).color(theActivity.lineColor).width(10);
					//在地图上添加折线Option，用于显示  
					theActivity.mBaiduMap.addOverlay(mpolylineOptions);
	                break;
                }
                default:
                {
                	new AlertDialog.Builder(theActivity)
                 	.setTitle("快巴士")
                 	.setIcon(R.drawable.qb_busclient)
                 	.setMessage("无线路![ERR=]"+msg.what)
                 	.setPositiveButton("确定" ,  null )  
                 	.show();
                }
                     break;
			}
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);   
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_qbmap); 
        /*bitmap 描述信息工厂类，在使用该类方法之前请确保已经调用了 SDKInitializer.initialize(Context) 函数以提供全局 Context 信息*/
        mbd = new BitmapDescriptor[] {BitmapDescriptorFactory.fromResource(R.drawable.icon_marka), 
    			BitmapDescriptorFactory.fromResource(R.drawable.icon_markb),
    			BitmapDescriptorFactory.fromResource(R.drawable.icon_markc)};
        //bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        //bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
        //bdC = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
        
        //BaiduMapOptions为mMapView初始化选项，控制mMapView的显示参数
/*                mMapView = new MapView(this,
		new BaiduMapOptions().mapStatus(new MapStatus.Builder()
				.target(cenpt).build()));*/
        
        hdl=new QBMapHandler(this);
        
       // mMapView = new MapView(this, new BaiduMapOptions().overlookingGesturesEnabled(false));
      //获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();  
        //普通地图  
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
        //卫星地图  
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        
        mMapView.showZoomControls(true);// 设置启用内置的缩放控件
        
        LatLng cenpt = new LatLng(31.25549,121.6087); 
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
        .target(cenpt)
        .zoom(18)
        .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        
        //MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		//mBaiduMap.setMapStatus(msu);
		
		//initOverlay();
		routeStationPointLatLngDraw();
		//routePointLatLngDraw();

		
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		
		//mCurrentMode = LocationMode.COMPASS;
		mCurrentMode = LocationMode.NORMAL;
		//mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		mCurrentMarker = null;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
				for(int i=0; i<routeStationlist.size(); i++)
				{
					if (marker == mMarkerlist.get(i)) 
					{
						button.setText((String) routeStationlist.get(i).get("stationName"));
						listener = new OnInfoWindowClickListener() {
							public void onInfoWindowClick() {
								LatLng ll = marker.getPosition();
								LatLng llNew = new LatLng(ll.latitude + 0.005, ll.longitude + 0.005);
								marker.setPosition(llNew);
								mBaiduMap.hideInfoWindow();
							}
						};
						LatLng ll = marker.getPosition();
						mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
						mBaiduMap.showInfoWindow(mInfoWindow);
					} 	
				}
				return true;
			}
		});
    }
    
    /*public void routePointLatLngDraw()
    { 
    	params=new HashMap<String,String>();
    	params.put("data[route_name]","ASB班车1B");
    	
    	new Thread(new Runnable()
		{
    		传输和JSON解析最好在子线程中完成
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
				try 
				{
									
					resp=PostData.postFormsToURL("http://115.29.204.94/ViewUserRouteMaps/wechat_route_map", params);
					if(resp=="")
					{	//network error, check network or passwd
						;
					}
					else if(resp.length()>2 && resp.matches(".*(?i)HTML.*BODY.*"))
					{
						;
					}
					else if (resp.length()<4)
					{
						;			
					}
					else
					{
						routePointlist=JSONPaser.putRoutePointLatLngJSONToArray(resp);
						
						hdl.sendEmptyMessageDelayed(Share.QB_DRAW_ROUTE_POINT, 0);
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
    	List<LatLng> mlist= new ArrayList<LatLng>();
    	LatLng srcllA = new LatLng(31.26008014,121.44515243);
		LatLng srcllB = new LatLng(31.26075881,121.53679815);
		LatLng srcllC = new LatLng(31.24794249,121.58271245);
		
		mlist.add(srcllA);
		mlist.add(srcllB);
		mlist.add(srcllC);
    	
    	OverlayOptions mpolylineOptions = new  PolylineOptions().points(mlist);
		//在地图上添加折线Option，用于显示  
	    mBaiduMap.addOverlay(mpolylineOptions);
		
    }*/
    
    public void routeStationPointLatLngDraw()
    { 
    	paramsStation=new HashMap<String,String>();
    	paramsStation.put("data[user_route_id]","30");
    	paramsPoint=new HashMap<String,String>();
    	paramsPoint.put("data[route_name]","ASB班车1B");
    	
    	new Thread(new Runnable()
		{
/*    		传输和JSON解析最好在子线程中完成,不要把post传输放到不同的线程中，会造成同时多次发送请求，导致服务器崩溃*/
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
				try 
				{
									
					resp=PostData.postFormsToURL("http://115.29.204.94/ViewUserRouteDetails/wechat_route_station_detail", paramsStation);
					if(resp=="")
					{	//network error, check network or passwd
						;
					}
					else if(resp.length()>2 && resp.matches(".*(?i)HTML.*BODY.*"))
					{
						;
					}
					else if (resp.length()<4)
					{
						;			
					}
					else
					{
						routeStationlist=JSONPaser.putRouteStationLatLngJSONToArray(resp);
						
						hdl.sendEmptyMessageDelayed(Share.QB_DRAW_ROUTE_STATION, 0);
					}
									
				}
				catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
				/*catch (JSONException e) 
				{
							// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				try 
				{
					Thread.sleep(500);
				} 
				catch (InterruptedException e) 
				{
							// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try 
				{
									
					resp=PostData.postFormsToURL("http://115.29.204.94/ViewUserRouteMaps/wechat_route_map", paramsPoint);
					if(resp=="")
					{	//network error, check network or passwd
						;
					}
					else if(resp.length()>2 && resp.matches(".*(?i)HTML.*BODY.*"))
					{
						;
					}
					else if (resp.length()<4)
					{
						;			
					}
					else
					{
						routePointlist=JSONPaser.putRoutePointLatLngJSONToArray(resp);
						
						hdl.sendEmptyMessageDelayed(Share.QB_DRAW_ROUTE_POINT, 0);
					}
									
				}
				catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
/*				catch (JSONException e) 
				{
							// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
					
			}
		}).start();   	
    	/*List<LatLng> mlist= new ArrayList<LatLng>();
    	LatLng srcllA = new LatLng(31.26008014,121.44515243);
		LatLng srcllB = new LatLng(31.26075881,121.53679815);
		LatLng srcllC = new LatLng(31.24794249,121.58271245);
		
		mlist.add(srcllA);
		mlist.add(srcllB);
		mlist.add(srcllC);
    	
    	OverlayOptions mpolylineOptions = new  PolylineOptions().points(mlist);
		//在地图上添加折线Option，用于显示  
	    mBaiduMap.addOverlay(mpolylineOptions);*/
		
    }
    
    /*public void initOverlay() 
    {
		// add marker overlay
		LatLng srcllA = new LatLng(31.26008014,121.44515243);
		LatLng srcllB = new LatLng(31.26075881,121.53679815);
		LatLng srcllC = new LatLng(31.24794249,121.58271245);
		// 将GPS设备采集的原始GPS坐标转换成百度坐标  
    	CoordinateConverter converter  = new CoordinateConverter();  
    	converter.from(CoordType.GPS);  
    	
    	// sourceLatLng待转换坐标  
    	converter.coord(srcllA);  
    	LatLng llA = converter.convert();
    	// sourceLatLng待转换坐标  
    	converter.coord(srcllB);  
    	LatLng llB = converter.convert();
    	// sourceLatLng待转换坐标  
    	converter.coord(srcllC);  
    	LatLng llC = converter.convert();
		
		ArrayList<BitmapDescriptor> giflistA = new ArrayList<BitmapDescriptor>();
		giflistA.add(bdA);
		giflistA.add(bdA);
		giflistA.add(bdA);
		OverlayOptions ooA = new MarkerOptions().position(llA).icons(giflistA)
				.zIndex(0).period(10).draggable(true);	
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
		
		ArrayList<BitmapDescriptor> giflistB = new ArrayList<BitmapDescriptor>();
		giflistB.add(bdB);
		giflistB.add(bdB);
		giflistB.add(bdB);
		OverlayOptions ooB = new MarkerOptions().position(llB).icons(giflistB)
				.zIndex(0).period(10).draggable(true);	
		mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
		
		ArrayList<BitmapDescriptor> giflistC = new ArrayList<BitmapDescriptor>();
		giflistC.add(bdC);
		giflistC.add(bdC);
		giflistC.add(bdC);
		OverlayOptions ooC = new MarkerOptions().position(llC).icons(giflistC)
				.zIndex(0).period(10).draggable(true);	
		mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));

		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) 
			{
				
			}

			public void onMarkerDragEnd(Marker marker) 
			{
				Toast.makeText(
						QBMapActivity.this,
						"拖拽结束，新位置：" + marker.getPosition().latitude + ", "
								+ marker.getPosition().longitude,
						Toast.LENGTH_LONG).show();
			}

			public void onMarkerDragStart(Marker marker) 
			{
				
			}
		});
	}*/
    
    /**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener 
	{

		@Override
		public void onReceiveLocation(BDLocation location) 
		{
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
			{
				return;
			}
				
			MyLocationData locData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(location.getDirection()).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) 
			{
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}
    
    @Override  
    protected void onDestroy() 
    {  
    	// 退出时销毁定位
    	mLocClient.stop();
    	// 关闭定位图层
    	mBaiduMap.setMyLocationEnabled(false);
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy(); 

     // 回收 bitmap 资源
        //bdA.recycle();
     	//bdB.recycle();
     	//bdC.recycle();
        for(int i=0; i<mbd.length; i++)
        {
        	mbd[i].recycle();
        }
    }  
    @Override  
    protected void onResume() 
    {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() 
    {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();    
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
