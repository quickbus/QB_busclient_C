package com.example.qb_busclient_c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;






import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class JSONPaser 
{
	static public ArrayList<String> aryRoutines;
	public static final int Max_Route_Num=100;
	public static final int Max_Route_Point_Num=1000;
	public static final int Max_Route_Station_Num=26;
	
	public JSONPaser()
	{
		aryRoutines=null;
	}
	
	static List<Map<String,Object>> putJSONToArray(String response) throws JSONException
	{

		List<Map<String,Object>>  list = new ArrayList<Map<String, Object>>(); 			
		JSONObject item = new JSONObject(response);
		Iterator<?> it = item.keys();
		String[] line_id = new String[Max_Route_Num];
		int route_num=0;
		int i,j;
		String temp;
		
		while(it.hasNext())
		{	
			if(route_num < Max_Route_Num)
			{
				line_id[route_num] = (String)it.next();
				route_num++;
			}
			
		}
		
		for(j=0;j<route_num-1;j++)
		{
			for(i=j+1;i<route_num;i++)
			{
				if(Integer.parseInt(line_id[j])>Integer.parseInt(line_id[i]))//数组元素大小按升序排列           
				{                 
					temp=line_id[j];                 
					line_id[j]=line_id[i];                 
					line_id[i]=temp;
				}         
			} 
		}
		
		for(j=0;j<route_num;j++)
		{
			Map<String,Object> map = new HashMap<String, Object>(); // 存放到MAP里面
			map.put("pic", R.drawable.qb_busclient);
			map.put("id", line_id[j]);
			map.put("name", (String)(item.get(line_id[j])));
			list.add(map);
		}
			
		return list;
	
	}
	
	static List<LatLng> putRoutePointLatLngJSONToArray(String response) throws JSONException
	{

		List<LatLng> list = new ArrayList<LatLng>();
		JSONArray jsonArr = new JSONArray(response);
		JSONObject item = new JSONObject();
		JSONObject pointInfo = new JSONObject();
//		String[] sequence_id = new String[Max_Route_Point_Num];
//		String[][] sequence_LatLng = new String[Max_Route_Point_Num][2];
		int sequence_num=0;
		int i;
		// 将GPS设备采集的原始GPS坐标转换成百度坐标  
    	CoordinateConverter converter  = new CoordinateConverter();  
    	converter.from(CoordType.GPS); 
		
		sequence_num = jsonArr.length();
			
		if(sequence_num < Max_Route_Point_Num)
		{
			for(i=0; i<sequence_num; i++)
			{
				item=jsonArr.getJSONObject(i);
				pointInfo=(JSONObject) item.get("ViewUserRouteMap");
				LatLng cenpt = new LatLng(Double.valueOf(pointInfo.getString("latitude")), Double.valueOf(pointInfo.getString("longitude")));
		    	// sourceLatLng待转换坐标  
		    	converter.coord(cenpt);  
				list.add(converter.convert());
			}
			
		}
			
		return list;
	
	}
	
	static List<Map<String,Object>> putRouteStationLatLngJSONToArray(String response) throws JSONException
	{

		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String stationName = null;
		JSONArray jsonArr = new JSONArray(response);
		JSONObject item = new JSONObject();
		JSONObject pointInfo = new JSONObject();
//		String[] sequence_id = new String[Max_Route_Point_Num];
//		String[][] sequence_LatLng = new String[Max_Route_Point_Num][2];
		int sequence_num=0;
		int i;
		// 将GPS设备采集的原始GPS坐标转换成百度坐标  
    	CoordinateConverter converter  = new CoordinateConverter();  
    	converter.from(CoordType.GPS); 
		
		sequence_num = jsonArr.length();
			
		if(sequence_num < Max_Route_Station_Num)
		{
			for(i=0; i<sequence_num; i++)
			{
				item=jsonArr.getJSONObject(i);
				pointInfo=(JSONObject) item.get("ViewUserRouteDetail");
				LatLng cenpt = new LatLng(Double.valueOf(pointInfo.getString("station_lat")), Double.valueOf(pointInfo.getString("station_lng")));
				stationName = pointInfo.getString("station_name");
				// sourceLatLng待转换坐标  
		    	converter.coord(cenpt); 
		    	Map<String,Object> map = new HashMap<String, Object>();
		    	map.put("LatLng", converter.convert());
		    	map.put("stationName", stationName);
				list.add(map);
			}
			
		}
			
		return list;
	
	}
	
	static List<Map<String,Object>> putDesJSONToArray(String response) throws JSONException
	{

		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>(); 			
		JSONObject item = new JSONObject(response);
		Iterator<?> it = item.keys();
		String line_info = new String();
		
		while(it.hasNext())
		{	
			Map<String,Object> map = new HashMap<String, Object>();
			line_info = (String)it.next();
			map.put("line_id", line_info);
			
			JSONObject loc = item.getJSONObject(line_info);

			map.put("linename", loc.getString("linename"));
			map.put("lo", loc.getDouble("lo"));
			map.put("la", loc.getDouble("la"));
			
			//android中用 == 判断string是否 相等需小心。特别是json的key
/*			String temp = (String)item.get(line_info);
			JSONObject loc = new JSONObject(temp);
			Iterator<?> iloc = loc.keys();
			while(iloc.hasNext())
			{
				line_info = (String)iloc.next();
				if(line_info == "linename")
				{
					map.put("linename", (String)(loc.get(line_info)));
				}
				else if(line_info == "lo")
				{
					map.put("lo", (Double)(loc.get(line_info)));
				}
				else if(line_info == "la")
				{
					map.put("la", (Double)(loc.get(line_info)));
				}
			}*/ //wrong way
			
			list.add(map);
			
		}
		
		return list;
	
	}
/*	static List<Map<String,String>> putJSONToArray(String response) throws JSONException
	{

		List<Map<String,String>>  list = new ArrayList<Map<String, String>>(); 
		String s1=response.substring(1);
		String s2=s1.substring(0,s1.length()-1);
		String[] sa=s2.split(",");
		int cnt=sa.length;
		for(int i=0;i<cnt;i++)
		{
			/*int pos=sa[i].indexOf("{");
			String pairs=sa[i].substring(pos);
			String pairs1=pairs.replaceAll(":",",");
			*/
			
			
			/* sa[i]="16":"\ uxxxx" */
/*			String[] pp=sa[i].split(":");
			String relid=pp[0].substring(1,pp[0].length()-1);
			String jsnname=pp[1].substring(0,pp[1].length());
			String jsn="{\"name\":"+jsnname+"}";
;			
			JSONObject item =new JSONObject(jsn);
			
			Map<String,String> map = new HashMap<String, String>(); // 存放到MAP里面
			map.put("id", relid);
			map.put("name", item.getString("name"));
			list.add(map);
			
		}
		return list;
	
	}
*/
//	static List<Map<String,String>> putJSONToArrayReg(String response) throws JSONException
//	{
//		/**   
//	     * 获取类型复杂的JSON数据   
//	     *数据形式：   
//	        {"name":"小猪",   
//	         "age":23,   
//	         "content":{"questionsTotal":2,   
//	                    "questions": [ { "question": "what's your name?", "answer": "小猪"},{"question": "what's your age", "answer": "23"}]   
//	                   }   
//	        }   
//	     * @param path  网页路径   
//	     * @return  返回List   
//	     * @throws Exception
//	     */
//
//		
//		List<Map<String,String>>  list = new ArrayList<Map<String, String>>(); 
//		JSONArray jsonArray = new JSONArray(response); //数据直接为一个数组形式，所以可以直接 用android提供的框架JSONArray读取JSON数据，转换成Array
//		for (int i = 0; i < jsonArray.length(); i++) {
//			JSONObject item = jsonArray.getJSONObject(i); //每条记录又由几个Object对象组成
//			//int id = item.getInt("id"); 	// 获取对象对应的值
//			String key=item.keys().next().toString();
//			String name = item.getString("name");
//			int id=0;
//			Map<String,String> map = new HashMap<String, String>(); // 存放到MAP里面
//			map.put("id", id + "");
//			map.put("name", name);
//			list.add(map);
//		}
//
//		return list;
//	}
	
}
