package com.lewa.weather.entity;

import java.util.Arrays;
import java.util.LinkedList;

public class PinYinUtil {
	public static LinkedList<String> getAllPinYin(String name) {

		LinkedList<String> queue = new LinkedList<String>();
		LinkedList<String> queue2 = new LinkedList<String>();
		
		if (name == null || name.length() == 0) {
			return queue;
		}
		queue.add("");
		queue2.add("");

		for (int i = 0; i < name.length(); i++) {
			int size = queue.size();
			String[] arr = getPinYin(name.charAt(i));
			
			Arrays.sort(arr);
			while (size-- != 0) {
				String tmp = queue.poll();

				if (arr == null) {
					queue.addLast(tmp);
				} else {
					for (int j = 0; j < arr.length; j++) {
						if (j > 0 && arr[j].equals(arr[j - 1])) {
							continue;
						}
						queue.addLast(tmp + arr[j]);
					}
				}
			}
			
			size = queue2.size();
			while (size-- != 0) {
				String tmp = queue2.poll();
				
				if (arr == null) {
					queue2.addLast(tmp);
				} else {
					for (int j = 0; j < arr.length; j++) {
						if (j > 0 && arr[j].charAt(0) == arr[j - 1].charAt(0)) {
							continue;
						}
						queue2.addLast(tmp + arr[j].charAt(0));
					}
				}
			}
		}
		queue.addAll(queue2);
		return queue;
	}

	public static String[] getPinYin(char ch) {
//		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//
//		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//
//		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//
//		format.setVCharType(HanyuPinyinVCharType.WITH_V);
//
//		String[] ret;
//		try {
//
//			if (java.lang.Character.toString(ch).matches("[\\u4E00-\\u9FA5]+")) {
//				ret = PinyinHelper.toHanyuPinyinStringArray(ch, format);
//				if (ret == null) {
//					return new String[] {ch + ""};
//				}
//				return ret;
//			} else
//				return new String[] { ch + "" };
//		} catch (BadHanyuPinyinOutputFormatCombination e) {
			return new String[] { ch + "" };
//		}
	}

	public static String getOne(String name) {
		if (name == null || name.length() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			sb.append(getPinYin(name.charAt(i))[0]);
		}
		return sb.toString();
	}

	public static String getOneFirst(String name) {
		if (name == null || name.length() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			sb.append(getPinYin(name.charAt(i))[0].charAt(0));
		}
		return sb.toString();
	}
}
