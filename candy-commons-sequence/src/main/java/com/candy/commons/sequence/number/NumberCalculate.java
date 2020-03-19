package com.candy.commons.sequence.number;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class NumberCalculate {

	private static String[] HEX_CHAR_ARR = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private static Map<String, Integer> HEX_CHAR_MAP = new HashMap<String, Integer>();

	static {

		for (int i = 0; i < HEX_CHAR_ARR.length; i++) {
			HEX_CHAR_MAP.put(HEX_CHAR_ARR[i]+"_T", i);
		}
	}

	/**
	 * 十进制转换任意进制
	 *
	 * @param value
	 * @param hexNum
	 * @return
	 */
	public static String ten2N(String value, int hexNum) {
		char[] charArr = (value + "").toCharArray();
		String newValue = "";
		long refValue = Long.parseLong(value);
		for (int i = charArr.length; i >= 0; i--) {
			long remainder = refValue % hexNum; // 余数
			// numberSys为系统默认的进制，size取商用来把size转换为36进制
			long quotient = refValue / hexNum; // 商
			newValue = HEX_CHAR_ARR[Integer.parseInt(remainder + "")] + newValue;
			if (quotient < hexNum) {
				newValue = HEX_CHAR_ARR[Integer.parseInt(quotient + "")] + newValue;
				break;
			}
			refValue = quotient;
		}
		return newValue;
	}
	
	
	/**
	 * 十进制转换任意进制
	 * @param value
	 * @param hexNum
	 * @return
	 */
	public static String ten2N(Long value, int hexNum) {
		char[] charArr = (value+"").toCharArray();
		String newValue = "";
		for (int i = charArr.length; i >= 0; i--) {
			long refValue = value;
			long remainder = refValue % hexNum; // 余数
			// numberSys为系统默认的进制，size取商用来把size转换为36进制
			long quotient = refValue / hexNum; // 商

			newValue = HEX_CHAR_ARR[Integer.parseInt(remainder + "")] + newValue;
			if (quotient < hexNum) {
				newValue = HEX_CHAR_ARR[Integer.parseInt(quotient + "")] + newValue;
				break;
			}
			value = quotient;
		}
		return newValue;
	}
	
	public static void main(String[] args) {
//		for (int i = 0; i < 100; i++) {
//			System.out.println(ten2N(i+"", 36));
//		}
		System.out.println(NumberCalculate.n2ten("F", 36));
		System.out.println(NumberCalculate.ten2N("10000", 8));
		
	}

	/**
	 * 任意进制转换10进制
	 * 
	 * @param value
	 * @param hexNum
	 * @return
	 */
	public static String n2ten(String value, int hexNum) {
		if (StringUtils.isBlank(value)) {
			return "0";
		}

		String pattern = "^[A-Z0-9]+$";
		boolean isMatch = Pattern.matches(pattern, value);
		if (!isMatch) {
			throw new RuntimeException("要转换的数据：" + value + "要转换的数据的格式不正确！只能包括A-Z，0-9");
		}

		char[] charArr = value.toCharArray();
		Long newValue = 0l;
		for (int i = 0; i < charArr.length; i++) {
			int charValue = HEX_CHAR_MAP.get(charArr[i] + "_T");
			long squareValue = 1;
			for (int j = 0; j < charArr.length - i - 1; j++) {
				squareValue = squareValue * hexNum;
			}
			newValue += squareValue * charValue;
		}
		return newValue + "";
	}

	// 获取指定位数的随机字符串(包含小写字母、大写字母、数字,0<length)
	public static String getRandomString(int length) {
		// 随机字符串的随机字符库
		String KeyString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuffer sb = new StringBuffer();
		int len = KeyString.length();
		for (int i = 0; i < length; i++) {
			sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
		}
		return sb.toString();
	}

}
