package cn.nwsuaf.clip;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.nwsuaf.read.TestCase;
import cn.nwsuaf.util.Boundary;
import cn.nwsuaf.util.Line;
import cn.nwsuaf.util.Point;
import cn.nwsuaf.util.PointDis;

/*描述：
 * 作用：此类负责直线的裁剪
 * 算法描述：
 * a:有交点的线段裁剪：
 * 1.先获取直线与多边形的所有交点，存储到PLine，然后按照距离line.startPoint 排序从小到大
 * 2.这些交点将直线分割成多个线段，判断线段的中点是否在边界内部
 * 3.如果在内部则存储，反之则抛弃
 * 4.判断点与线段的位置用向量法
 * b.无交点的线段舍或留：
 * 1.与边无交点
 * 2.起点是否在边界内部
 * */

public class CrossLine {
	private Boundary boundary;// 多边形
	private List<PointDis> PLine;// 存放交点line的起点的距离
	private List<Line> lineBoundary;// 存放多边形的边
	private Graphics2D g2;

	public CrossLine(Boundary boundary, Graphics2D g2) {
		PLine = new ArrayList<PointDis>();
		this.boundary = boundary;
		this.g2 = g2;
		lineBoundary = new ArrayList<Line>();

		for (int i = 0; i < boundary.vertexs.size() - 1; i++) {
			lineBoundary.add(new Line(boundary.vertexs.get(i), boundary.vertexs
					.get(i + 1)));
		}
	}

	public void getLineClip(Line line) {
		for (int i = 0; i < lineBoundary.size(); i++) {
			//计算line与多边形每条边界的交点
			getMarkLine(line, lineBoundary.get(i));
		}
		//若无交点
		if (PLine.isEmpty()) {
			//如果线段端点在多边形内部
			if (PtInPolygon(line.startPoint)) {
				//显示此线段
				g2.drawLine(line.startPoint.x, line.startPoint.y,
						line.endPoint.x, line.endPoint.y);
				TestCase.inBoundaryCount++;
			} else
				TestCase.outBoundaryCount++;
		} else {
			//按照PLine每个元素的distence值 从小到大排序
			Collections.sort(PLine);
			// 与line.startPoint最近的交点在PLine中的索引
			int indexMin = 0;
			int numberP = PLine.size();
			Point point;
			//计算起点与交点构成的线段是否在多边形内部
			solvePoint(line.startPoint, indexMin);
			for (int i = 0; i < numberP - 1; i++) {	
				//从PLine中连续取出两点
				int minD = indexMin;
				indexMin = (indexMin + 1) % numberP;
				//判断取出的两点是否相同 
				if (!Equal(PLine.get(minD).point, PLine.get(indexMin).point)) {	
					//计算线段中点
					point = getMid(PLine.get(minD).point,
							PLine.get(indexMin).point);
					//判断线段中点是否在多边形内部，如果在内部，则显示该线段
					if (PtInPolygon(point)) {							
						g2.drawLine(PLine.get(minD).point.x,
								PLine.get(minD).point.y,
								PLine.get(indexMin).point.x,
								PLine.get(indexMin).point.y);
					}
				}
				else
				{
					TestCase.pointCount--;
				}
			}
			//计算终点与交点构成的线段是否在多边形内部
			solvePoint(line.endPoint, indexMin);
		}
		TestCase.pointCount += PLine.size();
		PLine.clear();
	}

	/**
	 * 判断两线段位置关系，并求出交点（如果存在）。
	 * 
	 * @param line
	 * @param lineB
	 */
	private void getMarkLine(Line line, Line lineB) {
		// 线段首尾不重合
		if (!Equal(line.startPoint, line.endPoint)
				|| !Equal(lineB.startPoint, lineB.endPoint)) {
			int mul = (lineB.endPoint.y - lineB.startPoint.y)
					* (line.endPoint.x - line.startPoint.x)
					- (lineB.endPoint.x - lineB.startPoint.x)
					* (line.endPoint.y - line.startPoint.y);// 向量外积，平行为0
			//如不平行
			if (mul != 0) {

				int markOnLineS = pointONline(line.startPoint, lineB);
				int markOnLineE = pointONline(line.endPoint, lineB);
				int markOnLineSB = pointONline(lineB.startPoint, line);
				int markOnLineEB = pointONline(lineB.endPoint, line);
				
				//端点在另一线段上 则该端点为交点
				if (markOnLineS == 0) {
					PLine.add(new PointDis(line.startPoint, line.startPoint.x,
							line.startPoint.y));
				} else if (markOnLineE == 0) {
					PLine.add(new PointDis(line.endPoint, line.startPoint.x,
							line.startPoint.y));
				} else if (markOnLineSB == 0) {
					PLine.add(new PointDis(lineB.startPoint, line.startPoint.x,
							line.startPoint.y));
				} else if (markOnLineEB == 0) {
					PLine.add(new PointDis(lineB.endPoint, line.startPoint.x,
							line.startPoint.y));
				} 
				//如果线段相交
				else if (markOnLineS * markOnLineE == -1
						&& intersect(line.startPoint, line.endPoint,
								lineB.startPoint, lineB.endPoint)) {
					
					//定比分点法计算交点
					int s1 = fArea(line.startPoint, line.endPoint,
							lineB.startPoint);
					int s2 = fArea(line.startPoint, line.endPoint,
							lineB.endPoint);
					float x = ((lineB.endPoint.x * s1 + lineB.startPoint.x * s2) / (s1 + s2));
					float y = ((lineB.endPoint.y * s1 + lineB.startPoint.y * s2) / (s1 + s2));
					PLine.add(new PointDis(new Point(x, y), line.startPoint.x,
							line.startPoint.y));
				}
			}// end if
		}
	}

	private boolean Equal(Point p1, Point p2)// 两点相等
	{
		if (p1.x == p2.x && p1.y == p2.y) {
			return true;
		} else {
			return false;
		}
	}

	private void solvePoint(Point point, int index) {
		// 如果交点和端点重合
		if (Equal(point, PLine.get(index).point))
		{
			return;
		}
		//如果端点在内部，则线段在内部
		if (PtInPolygon(point)) {
			// lineClip.add(new Line(point, PLine.get(index).point));
			g2.drawLine(point.x, point.y, PLine.get(index).point.x,
					PLine.get(index).point.y);
		}
	}

	private boolean PtInPolygon(Point p) {
		//计算以点p为端点，水平向右做射线，与多边形的交点个数
		int nCross = 0;	//交点个数
		int nCount = boundary.vertexs.size() - 1;
		for (int i = 0; i < nCount; i++) {
			//计算每条边雨该射线的交点个数
			Point p1 = boundary.vertexs.get(i);
			Point p2 = boundary.vertexs.get((i + 1) % nCount);
			//排除不相交
			if (p1.y == p2.y || p.y < Math.min(p1.y, p2.y)
					|| p.y >= Math.max(p1.y, p2.y)) {
				continue;
			}
			
			//计算交点横坐标
			double x = (double) ((p.y - p1.y) * (p2.x - p1.x))
					/ (double) (p2.y - p1.y) + p1.x;
			//交点横坐标在右侧
			if (x > p.x) {
				nCross++;
			}
		}
		//若为奇数则返回true
		return (nCross % 2 == 1);
	}

	private Point getMid(Point pointA, Point pointB) {
		//计算线段AB的中点
		Point point = new Point((pointA.x + pointB.x) / 2,
				(pointA.y + pointB.y) / 2);
		return point;
	}

	private int fArea(Point p1, Point p2, Point p3) {
		//计算由点p1\p2\p3构成的三角形的面积
		return Math.abs((p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y)
				* (p3.x - p1.x));
	}

	private int pointONline(Point point, Line line) {
		/*
		 * 判断点与直线的位置关系在左侧 （-1） 在右侧（1） 在线上（0） 在延长线和反延长线上（2）
		 */
		//线段line范围
		int xMax = line.startPoint.x > line.endPoint.x ? line.startPoint.x
				: line.endPoint.x;
		int yMax = line.startPoint.y > line.endPoint.y ? line.startPoint.y
				: line.endPoint.y;

		int xMin = line.startPoint.x < line.endPoint.x ? line.startPoint.x
				: line.endPoint.x;
		int yMin = line.startPoint.y < line.endPoint.y ? line.startPoint.y
				: line.endPoint.y;
		
		double E = (line.endPoint.x - line.startPoint.x)
				* (point.y - line.startPoint.y) - (point.x - line.startPoint.x)
				* (line.endPoint.y - line.startPoint.y);
		if (E < 0) {// E>0在左侧，E=0在线上
			return -1;
		} else if (E > 0) {
			return 1;
		} else {
			if (point.x < xMin || point.x > xMax || point.y < yMin
					|| point.y > yMax) {// 点在以line为对角线的矩形外部
				return 2;
			} else {
				return 0;
			}

		}
	}

	// aa, bb为一条线段两端点 cc, dd为另一条线段的两端点 相交返回true, 不相交返回false
	private boolean intersect(Point aa, Point bb, Point cc, Point dd) {
		//确定线段矩形有重合
		if (Math.max(aa.x, bb.x) < Math.min(cc.x, dd.x)) {
			return false;
		}
		if (Math.max(aa.y, bb.y) < Math.min(cc.y, dd.y)) {
			return false;
		}
		if (Math.max(cc.x, dd.x) < Math.min(aa.x, bb.x)) {
			return false;
		}
		if (Math.max(cc.y, dd.y) < Math.min(aa.y, bb.y)) {
			return false;
		}
		//确定线段端点在另一线段两侧
		if (mult(cc, bb, aa) * mult(bb, dd, aa) < 0) {
			return false;
		}
		if (mult(aa, dd, cc) * mult(dd, bb, cc) < 0) {
			return false;
		}
		return true;
	}

	private float mult(Point a, Point b, Point c) {
		return (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y);
	}
}
