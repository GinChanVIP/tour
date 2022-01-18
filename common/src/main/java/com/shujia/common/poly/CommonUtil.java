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

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通用工具类
 * @author dingjingbo
 */
public class CommonUtil {
	
	private CommonUtil(){
		
	}
	/**
	 * 判断一个对象是否为空
	 * @param obj 判断对象
	 * @return 是否为空
	 */
	public static boolean isBlank(Object obj){
		if(obj instanceof String){
			String str = (String) obj;
			return !(str!=null&&!"".equals(str));
		}else{
			return obj==null;
		}
	}
	/**
	 * 日期格式转换
	 * @param date 日期
	 * @param pattern 模型
	 * @return 转换后的结果
	 */
	public static String convertDate2String(Date date,String pattern){
		if(date==null){
			return "";
		}
		SimpleDateFormat sf= new SimpleDateFormat(pattern);
		return sf.format(date);
	}
	/**
	 * 日期格式转换 默认yyyy-MM-dd
	 * @param date 日期
	 * @return 转换后的结果
	 */
	public static String convertDate2String(Date date){
		return convertDate2String(date, "yyyy-MM-dd");
	}
	/**
	 * 字符串转化成日期
	 * @param date 字符串日期
	 * @return 转换后的结果
	 */
	public static Date convertString2Date(String date){
		try {
			SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd");
			return sf.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 字符串连接
	 * @param strs 字符串集合
	 * @param joinStr 连接字符
	 * @return 连接后的字符
	 */
	public static String join(List<String> strs,String joinStr){
		if(strs==null){
			return null;
		}
		int i = 0; 
		StringBuilder sb = new StringBuilder();
		for(String str : strs){
			if(i==0){
				sb.append(str);
			}else{
				sb.append(joinStr);
				sb.append(str);
			}
			i++;
		}
		return sb.toString();
	}
	/**
	 * 字符串连接
	 * @param strs 字符串数组
	 * @param joinStr 连接字符
	 * @return 连接后的字符
	 */
	public static String join(String[] strs,String joinStr){
		if(strs==null){
			return null;
		}
		int i = 0; 
		StringBuilder sb = new StringBuilder();
		for(String str : strs){
			if(i==0){
				sb.append(str);
			}else{
				sb.append(joinStr);
				sb.append(str);
			}
			i++;
		}
		return sb.toString();
	}
	/**
	 * 加载propertis配置文件,文件根目录为classpath
	 * @param path 配置文件路径
	 * @return 参数集合
	 */
	public static Properties loadProperties(String path,boolean classPath){
		try {
			InputStream in = null;
			if(classPath){
				in = CommonUtil.class.getClassLoader().getResourceAsStream(path);
			}else{
				in = new FileInputStream(path);
			}
			Properties prop = new Properties();
			prop.load(in);
			return prop;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 找出头N条
	 * @param count 统计数据
	 * @param n top n
	 * @return top n
	 */
	public static List<String> findTopN(Map<String, Integer> count,int n) {
		Set<String> aaa = new TreeSet<String>();
		for(Map.Entry<String, Integer> entry : count.entrySet()){
			String des = StringUtil.addAtFirst2Fixlength(entry.getValue()+"", "0", 10);
			aaa.add(des+"_"+entry.getKey());
		}
		List<String> bbb = new ArrayList<String>();
		int i = 0;
		for(String str : aaa){
			i++;
			if(i>aaa.size()-n-1){
				bbb.add(str.split("_")[1]);
			}
		}
		Collections.reverse(bbb);
		return bbb;
	}
	/**
	 * 判断n是否是s和e的范围内
	 * @param n 判断值
	 * @param s 端点值1
	 * @param e 端点值2
	 * @return 是否在该范围内
	 */
	public static boolean between(double n,double s,double e){
		if(s<e){
			return n>=s&&n<=e;
		}else if(s>e){
			return n>=e&&n<=s;
		}else{
			return Double.doubleToRawLongBits(n)==Double.doubleToRawLongBits(s);
		}
	}
	/**
	 * 保留小数位数
	 * @param num
	 * @param scale
	 * @return
	 */
	public static double decimal(double num,int scale){
		return BigDecimal.valueOf(num).divide(BigDecimal.valueOf(1), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
}
	
