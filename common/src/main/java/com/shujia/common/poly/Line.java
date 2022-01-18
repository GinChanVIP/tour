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

import java.awt.geom.Point2D;

/**
 * 数学模型 直线
 * @author dingjingbo
 */
public class Line {
	private double a;
	private double b;
	private double x1;
	/**
	 * 构造方法
	 * @param a 斜率
	 * @param b 常量
	 */
	public Line(double a, double b) {
		super();
		this.a = a;
		this.b = b;
	}
	/**
	 * 构造方法
	 * @param x1 点1横坐标
	 * @param y1 点1纵坐标
	 * @param x2 点2横坐标
	 * @param y2 点2纵坐标
	 */
	public Line(double x1, double y1, double x2, double y2){
		this.x1 = x1;
		this.a = (y1-y2)/(x1-x2);//斜率
		this.b = y1-a*x1;//常数
	}
	/**
	 * 构造方法
	 * @param point1 点1
	 * @param point2 点2
	 */
	public Line(Point2D.Double point1, Point2D.Double point2){
		this(point1.x,point1.y,point2.x,point2.y);
	}
	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}
	/**
	 * 根据横坐标获取纵坐标
	 * @param x 横坐标
	 * @return 纵坐标
	 */
	public double getY(double x){
		return a*x+b;
	}
	/**
	 * 根据纵坐标获取横坐标
	 * @param y 纵坐标
	 * @return 横坐标
	 */
	public double getX(double y){
		return (y-b)/a;
	}
	/**
	 * 求点到直线的最短距离
	 * @param x 点横坐标
	 * @param y 点纵坐标
	 * @return 最短距离
	 */
	public double getMinLength(double x,double y){
		Point2D.Double point = getMinPoint(x,y);
		double sqrt = Math.sqrt((y-point.y)*(y-point.y)+(x-point.x)*(x-point.x));
		return Math.abs(sqrt);
		
	}
	/**
	 * 点到直线的垂直点
	 * @param x 点横坐标
	 * @param y 点纵坐标
	 * @return 垂足点
	 */
	public Point2D.Double getMinPoint(double x,double y){
		
		if(Double.doubleToRawLongBits(this.a)==Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)||Double.doubleToRawLongBits(this.a)==Double.doubleToRawLongBits(Double.POSITIVE_INFINITY)){
			return new Point2D.Double(x1, y);
		}
		if(Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(0d)){
			return new Point2D.Double(x, b);
		}
		return new Point2D.Double((x+a*y-a*b)/(a-1), a*(x+a*y-a*b)/(a-1)+b); 		
	}
	/**
	 * 求两条直线的交点
	 * @param line 相交直线
	 * @return 交点
	 */
	public Point2D.Double getIntersectPoint(Line line){
		if(Double.doubleToRawLongBits(this.a)==Double.doubleToRawLongBits(line.a)){
			return null;
		}
		if((Double.doubleToRawLongBits(line.getA())==Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)
				||Double.doubleToRawLongBits(line.getA())==Double.doubleToRawLongBits(Double.POSITIVE_INFINITY))
				&&(Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)
				||Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(Double.POSITIVE_INFINITY))){
			return null;
		}
		double x = 0;
		double y = 0;
		if(Double.doubleToRawLongBits(line.getA())==Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)||Double.doubleToRawLongBits(line.getA())==Double.doubleToRawLongBits(Double.POSITIVE_INFINITY)){
			x = line.x1;
			y = getY(x);
		}else if(Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)||Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(Double.POSITIVE_INFINITY)){
			x = this.x1;
			y = line.getY(x);
		}else{
			x = (line.b-this.b)/(this.a-line.a);
			y = this.a*(line.b-this.b)/(this.a-line.a)+this.b;
		}
		return new Point2D.Double(x, y);
		
	}
	@Override
	public String toString() {
		return Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)||Double.doubleToRawLongBits(this.getA())==Double.doubleToRawLongBits(Double.POSITIVE_INFINITY)?"x="+x1:"y="+a+"x+"+b;
	}
	
}
