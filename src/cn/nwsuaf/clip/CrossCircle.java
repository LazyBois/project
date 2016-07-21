package cn.nwsuaf.clip;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.nwsuaf.read.TestCase;
import cn.nwsuaf.util.Boundary;
import cn.nwsuaf.util.Circle;
import cn.nwsuaf.util.Point;
import cn.nwsuaf.util.PointAngle;

/*
 描述:
 作用：用来获取圆在边界内部的弧线(由于考虑到效率问题，所以计算出结果就进行绘图，没有使用其他存储数据结构)
 算法：
 1.先获取圆与边界的所有交点，之后进行排序，按照点在当前圆中的角度，从小到大排（为了保证获取正确弧线）
 2.如果没有交点，或者只有一个交点（切点不算做交点），则判断圆心是否在边界内部，是则返回true，反之为false
 3.如果有交点>=2，一次获取两个交点，获取两点所代表的弧的中点（（起点的角度+右旋角度）/2 然后转化成直角坐标)
 判断该点所代表的弧线是否在多边形内部
 4.如果在内部，起始角度与右旋角度存入circleClip（存储裁剪后的弧线） （为了调用darwArc函数）
 */
public class CrossCircle {
	// 误差线
	private final float EPS = 0.00001f;
	// 多边形
	private Boundary boundary;
	// 存放交点
	public List<PointAngle> points;
	// 当前的圆
	public Circle circle;
	private Graphics2D g2;

	public CrossCircle(Boundary boundary, Graphics2D g2) {
		points = new ArrayList<PointAngle>();
		this.boundary = boundary;
		this.g2 = g2;
	}

	/**
	 * 裁剪圆circle
	 * 
	 * @param circle
	 *            当前圆
	 * @param bvxy
	 *            存放需要重复计算的数据
	 */
	public void getCircleClip(Circle circle, float[][] bvxy) {
		this.circle = circle;
		// 求出多边形所有边与圆的交点
		for (int i = 0, n = boundary.vertexs.size(); i < n - 1; i++) {
			LineInterCircle(boundary.vertexs.get(i),
					boundary.vertexs.get(i + 1), bvxy[i]);
		}

		// 记录交点的个数
		int len = points.size();
		// 交点总数更新
		TestCase.pointCount += len;

		// 如果相离或者相切时判断圆在多边形内部还是外部
		if (len == 0 || len == 1) {
			// 如果圆心在多边形内，那么存在两种情况，
			// 多边形被圆包围或者圆被多边形包围
			if (PtInPolygon(circle.center)) {
				// 多边形的第一个顶点坐标
				int x = boundary.vertexs.get(0).x - circle.center.x;
				int y = boundary.vertexs.get(0).y - circle.center.y;
				// 如果多边形的顶点在圆外则说明圆被多边形包围
				if (x * x + y * y >= circle.radius * circle.radius) {
					// 画出该圆
					g2.drawArc(circle.center.x - circle.radius, circle.center.y
							- circle.radius, 2 * circle.radius,
							2 * circle.radius, 0, 360);
					// 更新在多边形内部图形的数量
					TestCase.inBoundaryCount++;
					// circleClip.add(new Arc(0, 360, circle));
				}
				// 如果多边形被圆包围那么不需要画出该圆，只需更新在多边形外部图形的数量
				else
					TestCase.outBoundaryCount++;
			}
			// 如果圆就在多边形外部则不需要画出该圆，只需更新在多边形外部图形的数量
			else
				TestCase.outBoundaryCount++;
			// 将记录交点的集合清空(该集合为共享)
			points.clear();
		} else {
			// 如果多边形与圆相交，就对所以交点排序(按照偏向角由小到大)
			Collections.sort(points);
			// 计算出在多边形内部的有向弧
			for (int i = 0; i < len; i++)
				arcOn(points.get(i), points.get((i + 1) % len));
			points.clear();
		}
	}

	/**
	 * 添加在多边形内部的有向弧(逆时针方向)到集合circleClip中
	 * 
	 * @param startPoint
	 *            有向弧起始点
	 * @param endPoint
	 *            有向弧终点
	 */
	private void arcOn(PointAngle startPoint, PointAngle endPoint) {
		// 有向弧起点的偏向角
		int startAngle = startPoint.angle;
		// 有向弧中点偏向角
		int endAngle = endPoint.angle;
		int arcAngle = startAngle - endAngle;
		arcAngle = arcAngle >= 0 ? -arcAngle : -360 - arcAngle;
		int midArcAngle = arcAngle / 2;
		// 得到有向弧中点偏向角
		int a = startAngle + midArcAngle;

		Point mid = new Point();
		// 计算有向弧中点坐标
		mid.x = (int) (Math.cos(Math.toRadians(a)) * circle.radius + circle.center.x);
		mid.y = circle.center.y
				- (int) (Math.sin(Math.toRadians(a)) * circle.radius);

		// 如果中点在多边形内部，那么就画出该弧线，否则不进行其他操作
		if (PtInPolygon(mid))
			g2.drawArc(circle.center.x - circle.radius, circle.center.y
					- circle.radius, 2 * circle.radius, 2 * circle.radius,
					startAngle, arcAngle);
		// circleClip.add(new Arc(startAngle, arcAngle, circle));
	}

	/**
	 * 利用向量求线段与圆的交点,并存入points中
	 * 
	 * @param startPoint
	 *            线段起始点
	 * @param endPoint
	 *            线段终止点
	 */
	private void LineInterCircle(Point startPoint, Point endPoint, float[] bvxy) {
		float fDis = bvxy[2];
		float dx = bvxy[0];
		float dy = bvxy[1];

		float ex = circle.center.x - startPoint.x;
		float ey = circle.center.y - startPoint.y;

		float a = ex * dx + ey * dy;
		float a2 = a * a;

		float e2 = ex * ex + ey * ey;

		int r2 = circle.radius * circle.radius;

		if ((r2 - e2 + a2) >= 0) {
			float f = (float) Math.sqrt(r2 - e2 + a2);

			float t = a - f;
			// 如果在误差范围内则将焦点添加到集合中
			if (((t - 0.0) > -EPS) && (t - fDis) < EPS)
				points.add(new PointAngle(new Point(startPoint.x + t * dx,
						startPoint.y + t * dy), circle.center.x,
						circle.center.y));

			t = a + f;
			if (((t - 0.0) > -EPS) && (t - fDis) < EPS)
				points.add(new PointAngle(new Point(startPoint.x + t * dx,
						startPoint.y + t * dy), circle.center.x,
						circle.center.y));
		}
	}

	/**
	 * 判断点与多边形的位置关系(内部/外部)
	 * 
	 * @param point
	 *            待判断的点
	 * @return 如果该点存在于多边形内部则返回true否则返回false
	 */
	private boolean PtInPolygon(Point point) {
		// 记录该点引出的射线与多边形交点个数
		int nCross = 0;
		int nCount = boundary.vertexs.size() - 1;
		for (int i = 0; i < nCount; i++) {
			Point p1 = boundary.vertexs.get(i);
			Point p2 = boundary.vertexs.get((i + 1));
			// 如果射线与多边形某条边平行或者没有交点则continue
			if (p1.y == p2.y || point.y < Math.min(p1.y, p2.y)
					|| point.y >= Math.max(p1.y, p2.y)) {
				continue;
			}

			// 计算出交点处的横坐标
			double x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
			// 如果满足条件，是有效交点则交点个数+1
			if (x > point.x) {
				nCross++;
			}
		}
		// 如果交点个数是奇数那么该点就在多边形内部，否则在多边形外部
		return (nCross % 2 == 1);
	}

}
