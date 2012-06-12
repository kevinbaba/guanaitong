package com.yapai.guanaitong.util;

public class Util {
	public static boolean IsStringValuble(String str){
		return str.length() != 0 && ! "null".equals(str) && ! "NULL".equals(str);
	}
}
