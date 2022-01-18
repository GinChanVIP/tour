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


import com.shujia.common.grid.Geography;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.*;


/**
 * 几何模型 多边形
 *
 * @author dingjingbo
 */
public class Polygon {
    private Point2D.Double peakPoint;
    private GeneralPath generalPath;
    private List<Point2D.Double> boundary;
    private List<Point2D.Double> boundaryConvert;

    /**
     * 构造方法
     */
    public Polygon() {
        super();
    }

    /**
     * 构造方法
     *
     * @param boundary 边界点（经纬度）
     */
    public Polygon(List<Point2D.Double> boundary) {
        super();
        init(boundary);
    }

    /**
     * 构造方法
     *
     * @param boundary 边界点（经纬度）
     */
    public Polygon(String boundary) {
        this(DataFormatUtil.stringBoundary2List(boundary));
    }

    private void init(List<Point2D.Double> boundary) {
        this.boundary = boundary;
        List<Point2D.Double> points = new ArrayList<Point2D.Double>();
        int i = 0;
        for (Point2D.Double point : boundary) {
            i++;
            if (i == 1) {
                this.peakPoint = point;
            }
            points.add(Geography.longilati2Decare(peakPoint, point));
        }
        this.boundaryConvert = points;
        drawMyself();
    }

    public Polygon(Circle circle) {
        List<Point2D.Double> points = new ArrayList<Point2D.Double>();
        for (int i = 0; i < 360; i++) {
            double val = 111319.55 * Math.cos(circle.getP().y * Math.PI / 180);
            double lon = circle.getP().x + (circle.getR() * Math.sin(i * Math.PI / 180)) / val;
            double lat = circle.getP().y + (circle.getR() * Math.cos(i * Math.PI / 180)) / 111133.33;
            points.add(new Point2D.Double(lon, lat));
        }
        init(points);
    }

    public void setPeakPoint(Point2D.Double peakPoint) {
        this.peakPoint = peakPoint;
    }

    public Point2D.Double getPeakPoint() {
        return peakPoint;
    }

    public GeneralPath getGeneralPath() {
        return generalPath;
    }

    public List<Point2D.Double> getBoundary() {
        return boundary;
    }

    /**
     * 设置多边形边界点
     *
     * @param boundary 边界点（经纬度）
     */
    public void setBoundary(List<Point2D.Double> boundary) {
        init(boundary);
    }

    public List<Point2D.Double> getBoundaryConvert() {
        return boundaryConvert;
    }

    /**
     * 构建多边形
     */
    public void drawMyself() {
        GeneralPath p = new GeneralPath();

        Point2D.Double first = boundaryConvert.get(0);
        p.moveTo(first.x, first.y);

        for (int i = 1; i < boundaryConvert.size(); i++) {
            p.lineTo(boundaryConvert.get(i).x, boundaryConvert.get(i).y);
        }
        p.lineTo(first.x, first.y);

        p.closePath();

        this.generalPath = p;
    }

    /**
     * 计算面积
     *
     * @return 面积（平方米）
     */
    public double getArea() {
        return Math.abs(getSignedArea());
    }

    /**
     * 计算有向面积
     *
     * @return 面积（平方米）
     */
    public double getSignedArea() {
        //S = 0.5 * ( (x0*y1-x1*y0) + (x1*y2-x2*y1) + ... + (xn*y0-x0*yn) )
        double area = 0.00;
        for (int i = 0; i < boundaryConvert.size(); i++) {
            if (i < boundaryConvert.size() - 1) {
                Point2D.Double p1 = boundaryConvert.get(i);
                Point2D.Double p2 = boundaryConvert.get(i + 1);
                area += p1.getX() * p2.getY() - p2.getX() * p1.getY();
            } else {
                Point2D.Double pn = boundaryConvert.get(i);
                Point2D.Double p0 = boundaryConvert.get(0);
                area += pn.getX() * p0.getY() - p0.getX() * pn.getY();
            }
        }
        area = area / 2.00;
        return area;
    }

    /**
     * 内部随机点
     *
     * @return 随机点
     */
    public Point2D.Double randomPoint() {
        //找出包含多边形的矩形的四个端点
        double maxLongi = 0d;
        double minLongi = Double.MAX_VALUE;
        double maxLati = 0d;
        double minLati = Double.MAX_VALUE;
        for (Point2D.Double point : boundary) {
            if (point.getX() < minLongi) {
                minLongi = point.getX();
            }
            if (point.getX() > maxLongi) {
                maxLongi = point.getX();
            }
            if (point.getY() < minLati) {
                minLati = point.getY();
            }
            if (point.getY() > maxLati) {
                maxLati = point.getY();
            }
        }
        //随机一个在里面的点
        while (true) {
            Point2D.Double randomPoint = nextRandomPoint(maxLongi, minLongi, maxLati, minLati);
            if (randomPoint != null && contains(randomPoint)) {
                return randomPoint;
            }
        }
    }

    /**
     * 内部随机点
     *
     * @return 随机点
     */
    public Point2D.Double randomPoint(double seedLongi, double seedLati) {
        //找出包含多边形的矩形的四个端点
        double maxLongi = 0d;
        double minLongi = Double.MAX_VALUE;
        double maxLati = 0d;
        double minLati = Double.MAX_VALUE;
        for (Point2D.Double point : boundary) {
            if (point.getX() < minLongi) {
                minLongi = point.getX();
            }
            if (point.getX() > maxLongi) {
                maxLongi = point.getX();
            }
            if (point.getY() < minLati) {
                minLati = point.getY();
            }
            if (point.getY() > maxLati) {
                maxLati = point.getY();
            }
        }
        //随机一个在里面的点
        while (true) {
            Point2D.Double randomPoint = nextRandomPoint(seedLongi, seedLati, maxLongi, minLongi, maxLati, minLati);
            if (randomPoint != null && contains(randomPoint)) {
                return randomPoint;
            }
        }
    }

    /**
     * 获取内切圆向外扩大指定范围的圆
     *
     * @param length 扩大范围，单位m
     * @return
     */
    public Circle enlarge(double length) {
        double maxLongi = 0d;
        double minLongi = Double.MAX_VALUE;
        double maxLati = 0d;
        double minLati = Double.MAX_VALUE;
        for (Point2D.Double point : boundary) {
            if (point.getX() < minLongi) {
                minLongi = point.getX();
            }
            if (point.getX() > maxLongi) {
                maxLongi = point.getX();
            }
            if (point.getY() < minLati) {
                minLati = point.getY();
            }
            if (point.getY() > maxLati) {
                maxLati = point.getY();
            }
        }
        double centerLongi = (minLongi + maxLongi) / 2;
        double centerLati = (minLati + maxLati) / 2;
        double r = Geography.calculateLength(minLongi, maxLati, maxLongi, minLati) / 2;
        return new Circle(centerLongi, centerLati, r + length);
    }

    /**
     * 根据重心等比例扩大指定范围
     */
    public Polygon enlargeM(double len) {
        List<Point2D.Double> points = new ArrayList<Point2D.Double>();
        Point2D.Double gravityPoint = getGravityPoint();
        Point2D.Double gravityPointDecare = Geography.longilati2Decare(this.getPeakPoint(), gravityPoint);
        double maxlength = 0d;
        for (int i = 0; i < this.boundary.size(); i++) {
            double length = Geography.calculateLength(gravityPoint.x, gravityPoint.y, this.boundary.get(i).x, this.boundary.get(i).y);
            if (length > maxlength) {
                maxlength = length;
            }
        }
        double minMultiple = len / maxlength;
        for (int i = 0; i < this.boundary.size(); i++) {
            Geography.longilati2Decare(getGravityPoint(), this.boundary.get(i));
            Point2D.Double pointDecare = Geography.longilati2Decare(this.getPeakPoint(), this.boundary.get(i));
            Line line = new Line(gravityPointDecare, pointDecare);
            double x = minMultiple * (pointDecare.x - gravityPointDecare.x) + pointDecare.x;
            if (CommonUtil.between(x, gravityPointDecare.x, pointDecare.x)) {
                x = -minMultiple * (pointDecare.x - gravityPointDecare.x) + pointDecare.x;
            }
            double y = line.getY(x);
            Point2D.Double decare2Longilati = Geography.decare2Longilati(this.getPeakPoint(), new Point2D.Double(x, y));
            points.add(decare2Longilati);
        }
        return new Polygon(points);
    }

    /**
     * 根据重心等距距扩大指定范围
     */
    public Polygon enlargeN(double len) {
        List<Point2D.Double> points = new ArrayList<Point2D.Double>();
        Point2D.Double gravityPoint = getGravityPoint();
        Point2D.Double gravityPointDecare = Geography.longilati2Decare(this.getPeakPoint(), gravityPoint);
        for (int i = 0; i < this.boundary.size(); i++) {
            double length = Geography.calculateLength(gravityPoint.x, gravityPoint.y, this.boundary.get(i).x, this.boundary.get(i).y);
            double minMultiple = len / length;
            Geography.longilati2Decare(getGravityPoint(), this.boundary.get(i));
            Point2D.Double pointDecare = Geography.longilati2Decare(this.getPeakPoint(), this.boundary.get(i));
            Line line = new Line(gravityPointDecare, pointDecare);
            double x = minMultiple * (pointDecare.x - gravityPointDecare.x) + pointDecare.x;
            if (CommonUtil.between(x, gravityPointDecare.x, pointDecare.x)) {
                x = -minMultiple * (pointDecare.x - gravityPointDecare.x) + pointDecare.x;
            }
            double y = line.getY(x);
            Point2D.Double decare2Longilati = Geography.decare2Longilati(this.getPeakPoint(), new Point2D.Double(x, y));
            points.add(decare2Longilati);
        }
        return new Polygon(points);
    }

    /**
     * 获取多边形重心点
     *
     * @return
     */
    public Point2D.Double getGravityPoint() {
        double area = 0.0;//多边形面积
        double Gx = 0.0, Gy = 0.0;// 重心的x、y
        for (int i = 1; i <= this.boundary.size(); i++) {
            double iLat = this.boundary.get(i % this.boundary.size()).y;
            double iLng = this.boundary.get(i % this.boundary.size()).x;
            double nextLat = this.boundary.get(i - 1).y;
            double nextLng = this.boundary.get(i - 1).x;
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
            area += temp;
            Gx += temp * (iLat + nextLat) / 3.0;
            Gy += temp * (iLng + nextLng) / 3.0;
        }
        Gx = Gx / area;
        Gy = Gy / area;
        if (area - 0 < 0.0000001) {
            return getCenterPointX();
        }
        return new Point2D.Double(Gy, Gx);
    }

    /**
     * 根据输入的地点坐标计算中心点
     *
     * @return
     */
    public Point2D.Double getCenterPointX() {
        int total = this.boundary.size();
        double X = 0, Y = 0, Z = 0;
        for (Point2D.Double g : this.boundary) {
            double lat, lon, x, y, z;
            lat = g.y * Math.PI / 180;
            lon = g.x * Math.PI / 180;
            x = Math.cos(lat) * Math.cos(lon);
            y = Math.cos(lat) * Math.sin(lon);
            z = Math.sin(lat);
            X += x;
            Y += y;
            Z += z;
        }
        X = X / total;
        Y = Y / total;
        Z = Z / total;
        double Lon = Math.atan2(Y, X);
        double Hyp = Math.sqrt(X * X + Y * Y);
        double Lat = Math.atan2(Z, Hyp);
        return new Point2D.Double(Lon * 180 / Math.PI, Lat * 180 / Math.PI);
    }

    /**
     * 根据输入的地点坐标计算中心点（适用于400km以下的场合）
     *
     * @return
     */
    public Point2D.Double getCenterPointS() {
        //以下为简化方法（400km以内）
        int total = this.boundary.size();
        double lat = 0, lon = 0;
        for (Point2D.Double g : this.boundary) {
            lat += g.y * Math.PI / 180;
            lon += g.x * Math.PI / 180;
        }
        lat /= total;
        lon /= total;
        return new Point2D.Double(lon * 180 / Math.PI, lat * 180 / Math.PI);
    }

    /**
     * 随机生成一个矩形内的点
     *
     * @param maxLongi 最大经度
     * @param minLongi 最小经度
     * @param maxLati  最大纬度
     * @param minLati  最小纬度
     * @return 矩形内的随机点
     */
    private Point2D.Double nextRandomPoint(double maxLongi, double minLongi,
                                           double maxLati, double minLati) {
        int a = 100000000;
        int r1 = Math.abs((int) ((maxLongi - minLongi) * a));
        int r2 = Math.abs((int) ((maxLati - minLati) * a));
        Random randomLongi = new Random();
        Random randomLati = new Random();
        int nextInt1 = randomLongi.nextInt(r1);
        int nextInt2 = randomLati.nextInt(r2);
        double nextLongi = Double.parseDouble(nextInt1 + ".0") / a;
        double nextLati = Double.parseDouble(nextInt2 + ".0") / a;
        return new Point2D.Double(minLongi + nextLongi, minLati + nextLati);
    }

    /**
     * 随机生成一个矩形内的点 靠近种子点分布
     *
     * @param seedLongi 种子点经度
     * @param seedLati  种子点纬度
     * @param maxLongi  最大经度
     * @param minLongi  最小经度
     * @param maxLati   最大纬度
     * @param minLati   最小纬度
     * @return
     */
    public Point2D.Double nextRandomPoint(double seedLongi, double seedLati, double maxLongi, double minLongi,
                                          double maxLati, double minLati) {
        Point2D.Double point = nextRandomPoint(maxLongi, minLongi, maxLati, minLati);
        double maxLength = Geography.calculateLength(maxLongi, maxLati, minLongi, minLati);
        double length = Geography.calculateLength(seedLongi, seedLati, point.x, point.y);
        double df = 1 / maxLength;//权重衰减因子，每米点的密度衰减
        Random random = new Random();
        int nextInt = random.nextInt(10);
        if (nextInt < (1 - length * df) * 10) {
            return point;
        } else {
            return null;
        }
    }

    /**
     * 是否包含一个点
     *
     * @param point 点
     * @return 是否包含
     */
    public boolean contains(Point2D.Double point) {
        Point2D.Double convertCoord = Geography.longilati2Decare(peakPoint, point);
        return this.getGeneralPath().contains(convertCoord);
    }

    /**
     * 是否包含一个点
     *
     * @param x 经度
     * @param y 纬度
     * @return 是否包含
     */
    public boolean contains(double x, double y) {
        Point2D.Double convertCoord = Geography.longilati2Decare(peakPoint, new Point2D.Double(x, y));
        return this.getGeneralPath().contains(convertCoord.x, convertCoord.y);
    }

    /**
     * 判断一个多边形是否包含另一个多边形
     *
     * @param polygon
     * @return
     */
    public boolean contains(Polygon polygon) {
        for (Point2D.Double p : polygon.getBoundary()) {
            if (!this.contains(p)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Circle circle) {
        return !circle.intersect(this) && this.contains(circle.getP());
    }

    /**
     * 是否与另外一个多变形相交
     *
     * @param polygon 多边形
     * @return 是否相交
     */
    public boolean intersect(Polygon polygon) {
        if (!contains(polygon) && !polygon.contains(this)) {
            for (Point2D.Double p : polygon.getBoundary()) {
                if (this.contains(p)) {
                    return true;
                }
            }
            for (Point2D.Double p : this.getBoundary()) {
                if (polygon.contains(p)) {
                    return true;
                }
            }
            for (int m = 0; m < polygon.getBoundary().size() - 1; m++) {
                Point2D.Double points = polygon.getBoundary().get(m);
                Point2D.Double pointe = polygon.getBoundary().get(m + 1);
                Set<Point2D.Double> intersectPoints = intersectPoints(points, pointe);
                if (!intersectPoints.isEmpty()) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }

    }

    /**
     * 计算一个多边形与另一个多边形相交面积
     *
     * @param polygon
     * @return
     */
    public double intersectArea(Polygon polygon) {
        Polygon intersectPolygon = intersectPolygon(polygon);
        return intersectPolygon == null ? 0 : intersectPolygon.getArea();
    }

    /**
     * 计算一个多边形与另一个多边形相交面积
     *
     * @param polygon
     * @return
     */
    public Polygon intersectPolygon(Polygon polygon) {
        if (contains(polygon)) {
            return polygon;
        } else if (polygon.contains(this)) {
            return this;
        } else if (!intersect(polygon)) {
            return null;
        }
        Set<Point2D.Double> intersectPoints = new HashSet<Point2D.Double>();
        for (int m = 0; m < polygon.getBoundary().size() - 1; m++) {
            Point2D.Double points = polygon.getBoundary().get(m);
            Point2D.Double pointe = polygon.getBoundary().get(m + 1);
            intersectPoints.addAll(intersectPoints(points, pointe));
        }
        Point2D.Double points = polygon.getBoundary().get(polygon.getBoundary().size() - 1);
        Point2D.Double pointe = polygon.getBoundary().get(0);
        intersectPoints.addAll(intersectPoints(points, pointe));
        for (Point2D.Double p : this.getBoundary()) {
            if (polygon.contains(p)) {
                intersectPoints.add(p);
            }
        }
        for (Point2D.Double p : polygon.getBoundary()) {
            if (this.contains(p)) {
                intersectPoints.add(p);
            }
        }
        for (Point2D.Double p : intersectPoints) {
            Polygon pc = new Polygon(new Circle(p, 1));
            List<Point2D.Double> inpoints = new ArrayList<Point2D.Double>();
            for (Point2D.Double p1 : pc.getBoundary()) {
                if (this.contains(p1) && polygon.contains(p1)) {
                    inpoints.add(p1);
                }
            }
            if (inpoints.size() > 2) {
                Point2D.Double selectp = inpoints.get(inpoints.size() / 2);
                return sort(selectp, intersectPoints);
            }
        }
        return null;
    }

    private Polygon sort(Point2D.Double pi, Set<Point2D.Double> intersectPoints) {
        Map<String, Point2D.Double> treeMap = new TreeMap<String, Point2D.Double>();
        for (Point2D.Double p : intersectPoints) {
            if (p.equals(pi)) {
                continue;
            }
            double calculateAngle = Geography.calculateAngle(pi.x, pi.y, p.x, p.y);
            String[] split = (calculateAngle + "").split("\\.");
            String fixlength = StringUtil.addAtFirst2Fixlength(split[0], "0", 30) + "." + split[1];
            treeMap.put(fixlength, p);
        }
        List<Point2D.Double> a = new ArrayList<Point2D.Double>();
        for (Map.Entry<String, Point2D.Double> entry : treeMap.entrySet()) {
            a.add(entry.getValue());
        }
        return new Polygon(a);
    }

    /**
     * 求多边形与某条线段的交点
     */
    public Set<Point2D.Double> intersectPoints(Point2D.Double point1, Point2D.Double point2) {
        Set<Point2D.Double> ps = new HashSet<Point2D.Double>();//相交点
        Point2D.Double pointls = Geography.longilati2Decare(this.getPeakPoint(), point1);
        Point2D.Double pointle = Geography.longilati2Decare(this.getPeakPoint(), point2);
        Line line = new Line(pointls, pointle);
        for (int m = 0; m < this.getBoundaryConvert().size() - 1; m++) {
            Point2D.Double points = this.getBoundaryConvert().get(m);
            Point2D.Double pointe = this.getBoundaryConvert().get(m + 1);
            Line line2 = new Line(points, pointe);
            Point2D.Double intersectionPoint = line.getIntersectPoint(line2);
            if ((intersectionPoint != null) && (CommonUtil.between(intersectionPoint.x, points.x, pointe.x)) && (CommonUtil.between(intersectionPoint.y, points.y, pointe.y))) {
                if ((CommonUtil.between(intersectionPoint.x, pointls.x, pointle.x)) && (CommonUtil.between(intersectionPoint.y, pointls.y, pointle.y)) && (CommonUtil.between(intersectionPoint.x, points.x, pointe.x)) && (CommonUtil.between(intersectionPoint.y, points.y, pointe.y))) {
                    ps.add(Geography.decare2Longilati(this.getPeakPoint(), intersectionPoint));
                }
            }
        }
        Point2D.Double points = this.getBoundaryConvert().get(this.getBoundaryConvert().size() - 1);
        Point2D.Double pointe = this.getBoundaryConvert().get(0);
        Line line2 = new Line(points, pointe);
        Point2D.Double intersectionPoint = line.getIntersectPoint(line2);
        if ((intersectionPoint != null) && (CommonUtil.between(intersectionPoint.x, points.x, pointe.x)) && (CommonUtil.between(intersectionPoint.y, points.y, pointe.y))) {
            if ((CommonUtil.between(intersectionPoint.x, pointls.x, pointle.x)) && (CommonUtil.between(intersectionPoint.y, pointls.y, pointle.y)) && (CommonUtil.between(intersectionPoint.x, points.x, pointe.x)) && (CommonUtil.between(intersectionPoint.y, points.y, pointe.y))) {
                ps.add(Geography.decare2Longilati(this.getPeakPoint(), intersectionPoint));
            }
        }
        return ps;
    }

    @Deprecated
    public Point2D.Double getCenterPoint() {
        Point2D.Double point0 = getBoundaryConvert().get(0);
        Point2D.Double point1 = getBoundaryConvert().get(1);
        Point2D.Double point2 = getBoundaryConvert().get(getBoundaryConvert().size() - 1);
        Line line1 = new Line(point0, point1);
        Line line2 = new Line(point0, point2);
        Line line3 = new Line(point1, point2);
        double a = (line1.getA() + line2.getA())
                / (1.0D - line1.getA() * line2.getA());
        double b = point0.y - a * point0.x;
        Line line4 = new Line(a, b);
        Point2D.Double intersectionPoint = line4.getIntersectPoint(line3);
        return Geography.decare2Longilati(getBoundary().get(0), intersectionPoint);
    }

    public static void main(String[] args) {

        String boundary = "117.18294 31.848502,117.183571 31.848315,117.185324 31.847719,117.185799 31.8474,117.187003 31.846552,117.187482 31.846443,117.187654 31.846443,117.1877 31.846153,117.187741 31.845951,117.188478 31.843394,117.188152 31.843348,117.187203 31.843327,117.18688 31.843258,117.186203 31.843029,117.185563 31.842814,117.185444 31.842739,117.185347 31.842647,117.185365 31.842395,117.185802 31.841367,117.186252 31.840176,117.18922 31.840389,117.18966 31.839601,117.189852 31.83855,117.189857 31.838151,117.189802 31.837929,117.189749 31.837721,117.18967 31.837522,117.189554 31.837306,117.189328 31.837054,117.189029 31.836728,117.187515 31.835252,117.186092 31.833766,117.185817 31.833364,117.185698 31.833025,117.185587 31.832613,117.185549 31.832352,117.1855 31.831892,117.183536 31.831911,117.181442 31.831921,117.179594 31.831922,117.179543 31.831937,117.179513 31.831984,117.179447 31.832904,117.179365 31.833851,117.179359 31.834226,117.179377 31.834357,117.179423 31.834583,117.179441 31.834818,117.179539 31.835264,117.179518 31.835285,117.179482 31.835286,117.179177 31.83521,117.17905 31.835145,117.178926 31.835042,117.178826 31.834964,117.178692 31.834873,117.178573 31.834826,117.178433 31.834806,117.17812 31.83483,117.177907 31.83485,117.177689 31.834853,117.176599 31.834723,117.175869 31.834604,117.175743 31.834585,117.175596 31.834579,117.175455 31.83459,117.174738 31.834783,117.174319 31.83493,117.174001 31.835057,117.173307 31.835207,117.17306 31.835283,117.172288 31.835696,117.17186 31.835921,117.171486 31.836145,117.171458 31.836187,117.171446 31.836235,117.171443 31.836499,117.171424 31.836642,117.171328 31.836802,117.170525 31.83756,117.170002 31.838087,117.16975 31.838502,117.169349 31.838871,117.169232 31.839137,117.169105 31.839321,117.168831 31.839918,117.168711 31.840646,117.168718 31.840781,117.168743 31.840878,117.168803 31.840972,117.168849 31.841042,117.167447 31.841288,117.167357 31.841272,117.167307 31.841206,117.167118 31.840858,117.167035 31.840808,117.166945 31.840801,117.166316 31.840941,117.165382 31.840924,117.163677 31.841549,117.164266 31.843439,117.164404 31.843638,117.16484 31.844064,117.165635 31.844817,117.166243 31.845405,117.166744 31.846003,117.167006 31.846454,117.167124 31.846723,117.167577 31.847837,117.167838 31.848189,117.168456 31.848893,117.168673 31.849098,117.168925 31.8493,117.169225 31.849466,117.1696 31.849624,117.17101 31.850006,117.17191 31.850212,117.174397 31.850708,117.174608 31.850733,117.174976 31.850738,117.176838 31.850647,117.177117 31.850588,117.1775 31.850484,117.179331 31.849864,117.179775 31.849573,117.179986 31.849464,117.181193 31.849009,117.18294 31.848502";
        System.out.println(boundary);
        Polygon polygon = new Polygon(boundary);

        long start = System.currentTimeMillis();

        boolean contains = polygon.contains(119.176846, 31.844419);

        long end = System.currentTimeMillis();

        System.out.println(contains);
        System.out.println(end - start);

    }
}
