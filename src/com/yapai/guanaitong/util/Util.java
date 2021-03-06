package com.yapai.guanaitong.util;

public class Util {
	public static boolean IsStringValuble(String str){
		return str != null && str.length() != 0 
				&& ! "null".equals(str) && ! "NULL".equals(str)
				&& ! "NEED_LOGIN".equals(str) ;
	}
	
	public static boolean IsStringValuble2(String str){
		return IsStringValuble(str) && ! "NEED_LOGIN".equals(str) ;
	}
	
	public static String getSuffix(String path) {
		if (path == null || path.indexOf(".") == -1) {
			return ""; // 如果为路径null或者没有"."就返回""
		}
		return path.substring(path.lastIndexOf(".") + 1).trim()
				.toLowerCase();
	}
	

}
