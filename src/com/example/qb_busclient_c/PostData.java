package com.example.qb_busclient_c;

import java.io.BufferedReader;
//import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
//import java.net.URL;
//import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;

public class PostData 
{
	public static HttpClient httpc=new DefaultHttpClient();
	
//	public static String postFormsToURLBin(String actionUrl, Map<String,String> params)
//	{
//try{
//			
//			String BOUNDARY ="---------7d4a6d158c9";
//			String MULTI="multipart/form-data";
//			
//			URL url=new URL(actionUrl);
//			
//			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//			conn.setConnectTimeout(5*1000);
//			conn.setDoInput(true);
//			conn.setDoOutput(true);
//			conn.setUseCaches(false);
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("Connecton","Keep-Alive");
//			conn.setRequestProperty("Charset","UTF-8");
//			conn.setRequestProperty("Content-Type",MULTI +"; boundary="+BOUNDARY);
//			
//			StringBuilder sb=new StringBuilder();
//			for(Map.Entry<String,String> entry : params.entrySet())
//			{
//				sb.append("--");
//				sb.append(BOUNDARY);
//				sb.append("\r\n");
//				sb.append("Content-Disposition: form-data; name=\""+URLEncoder.encode(entry.getKey())+"\"\r\n\r\n");
//				sb.append(URLEncoder.encode(entry.getValue()));
//				sb.append("\r\n");
//				
//			}
//			// conn.connect();
//			DataOutputStream outS=new DataOutputStream(conn.getOutputStream());
//			outS.write(sb.toString().getBytes());
//			byte[] end_data =("--"+BOUNDARY+"--\r\n").getBytes();
//			outS.write(end_data);
//			outS.flush();
//			int cah=conn.getResponseCode();
//			if(cah!=200)
//				throw new RuntimeException("URL request failed!");
//			InputStream ins=conn.getInputStream();
//			int ch;
//			StringBuilder b=new StringBuilder();
//			while((ch=ins.read())!=-1)
//			{
//				b.append((char)ch);
//			}
//			outS.close();
//			conn.disconnect();
//			return b.toString();
//		
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//			return "";
//		}
//	
//	}

	public static String postFormsToURL(String actionUrl, Map<String,String> params)
	{
		/*
		
		
		*/
		
//		byte[] b=new byte[4096];
		
		
		List<NameValuePair> lst_params=new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> entry: params.entrySet())
		{
			lst_params.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
		}
		
		try 
		{
			UrlEncodedFormEntity entity;
			entity=new UrlEncodedFormEntity(lst_params,HTTP.UTF_8);
			HttpPost httppost;
			URI myuri=null;
			try 
			{
				myuri=new URI(actionUrl);
			} 
			catch (URISyntaxException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httppost=new HttpPost(myuri);
			
			httppost.setEntity(entity);
			
			HttpResponse response;
			response=httpc.execute(httppost);
			
			
            StringBuilder builder = new StringBuilder(); 
            BufferedReader bufferedReader2 = new BufferedReader( 
                    new InputStreamReader(response.getEntity().getContent())); 
//            String str2 = ""; 
            for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) 
            { 
                builder.append(s); 
            } 
            
            return builder.toString();
            
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
				
	}

}
