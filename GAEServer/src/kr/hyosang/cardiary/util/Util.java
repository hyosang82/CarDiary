package kr.hyosang.cardiary.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import kr.hyosang.cardiary.Define;
import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.data.model.json.daum.Coord2Addr;

public class Util {
	public static boolean isUser() {
		UserService us = UserServiceFactory.getUserService();
		if(us.isUserLoggedIn()) {
			User u = us.getCurrentUser();
			MyUser mu = MyUser.getUser(u.getEmail());
			
			if(mu != null) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int parseInt(String s, int defValue) {
		try {
			return Integer.parseInt(s, 10);
		}catch(NumberFormatException e) {
		}
		
		return defValue;
	}
	
	public static double parseDouble(String s, double defValue) {
		try {
			return Double.parseDouble(s);
		}catch(NumberFormatException e) {
		}
		
		return defValue;
	}
	
	public static long parseLong(String s, long defValue) {
		try {
			return Long.parseLong(s, 10);
		}catch(NumberFormatException e) {
		}
		
		return defValue;
	}
	
	public static boolean isEmpty(String str) {
		if(str != null) {
			if(str.length() > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static long[] getDateRange(String year, String month) {
		int y = parseInt(year, 2015);
		int m = parseInt(month, 1);
		
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m - 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		long [] val = new long[2];
		val[0] = c.getTimeInMillis();
		
		c.add(Calendar.MONTH, 1);
		val[1] = c.getTimeInMillis();
		
		return val;
	}
	
	public static String getWebContent(String url) {
		HttpURLConnection conn = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			
			conn.setDoOutput(false);
			conn.setDoInput(true);
			
			conn.connect();
			
			InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");
			char [] buf = new char[1024];
			int nRead;
			
			StringBuffer sb = new StringBuffer();
			
			while( (nRead = reader.read(buf)) > 0) {
				sb.append(new String(buf, 0, nRead));
			}
			
			return sb.toString();		
		}catch(IOException e) {
		}finally {
			if(conn != null) { conn.disconnect(); }
		}
		
		return "";
	}
	
	public static String reverseGeocode(double lat, double lng) {
		String urlFmt = "https://apis.daum.net/local/geo/coord2addr?apikey=" + Define.APIKEY_DAUM + "&format=fullname&inputCoordSystem=WGS84&output=json&latitude=%.6f&longitude=%.6f";
		String url = String.format(urlFmt, lat, lng);
		String result = Util.getWebContent(url);
		
		Logger.getLogger("Util").info("Result : " + result);
		
		Gson gson = new Gson();
		Coord2Addr data = gson.fromJson(result, Coord2Addr.class);
		
		return data.fullName;
	}
		
}
