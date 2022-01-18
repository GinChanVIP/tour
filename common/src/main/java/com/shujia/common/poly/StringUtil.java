/*********************************************************************
 * 
 * CHINA TELECOM CORPORATION CONFIDENTIAL
 * ______________________________________________________________
 * 
 *  [2015] - [2020] China Telecom Corporation Limited, 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of China Telecom Corporation and its suppliers,
 * if any. The intellectual and technical concepts contained 
 * herein are proprietary to China Telecom Corporation and its 
 * suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret  or 
 * copyright law. Dissemination of this information or 
 * reproduction of this material is strictly forbidden unless prior 
 * written permission is obtained from China Telecom Corporation.
 **********************************************************************/
package com.shujia.common.poly;


public class StringUtil {
	private StringUtil(){
		
	}
	/**
	 * 获取一个字符在某个字符串中出现的次数
	 * @param str 字符串
	 * @param c 字符
	 * @return 出现的次数
	 */
	public static int numOfChar(String str,char c){
		int num = 0;
		char[] chars = str.toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]==c){
				num++;
			}
		}
		return num;
	}
	/**
	 * 找出字符串中某个字符出现第n次的索引
	 * @param str
	 * @param c
	 * @param n
	 * @return
	 */
	public static int indexOf(String str,char c,int n){
		int num = 0;
		char[] chars = str.toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]==c){
				num++;
				if(num==n){
					return i;
				}
			}
		}
		return -1;
	}
	/**
	 * 在某个字符串的某索引处添加字符串,若指定索引大于原字符串索引则返回原字符串
	 * @param str 原字符串
	 * @param index 索引
	 * @param s 要添加的字符串
	 * @return
	 */
	public static String insertStr(String str,int index,String s){
		if(index>str.length()-1){
			return str;
		}else{
			String s1 = str.substring(0,index);
			String s2 = str.substring(index);
			String newStr = s1+s+s2;
			return newStr;
		}
	}
	/**
	 * 在某个字符串前面添加某个字符到指定长度
	 * @param src 源字符串
	 * @param addstr 要添加的支付
	 * @param fixlength 添加到的长度
	 * @return
	 */
	public static String addAtFirst2Fixlength(String src,String addstr,int fixlength){
		int length = src.length();//原字符串长度
		int bc = fixlength-length;//需要补的长度
		String bbb = "";
		for(int i =0;i<bc;i++){
			bbb=bbb+addstr;
		}
		String newStr = bbb+src;
		return newStr;
	}
	/**
	 * 比较两个字符串的相识度
	 * @param str1 比较字符串
	 * @param str2 被比较字符串
	 * @return 相识度
	 */
	public static double similar(String str1,String str2){
		char[] charArray1 = str1.toCharArray();
		char[] charArray2 = str2.toCharArray();
		int count = 0;
		for(char c1 : charArray1){
			for(char c2 : charArray2){
				if(c1==c2){
					count++;
					break;
				}
			}
		}
		return Double.parseDouble(count+"")/str1.length();
	}
}
