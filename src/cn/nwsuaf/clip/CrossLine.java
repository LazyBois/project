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

/*������
 * ���ã����ฺ��ֱ�ߵĲü�
 * �㷨������
 * a:�н�����߶βü���
 * 1.�Ȼ�ȡֱ�������ε����н��㣬�洢��PLine��Ȼ���վ���line.startPoint �����С����
 * 2.��Щ���㽫ֱ�߷ָ�ɶ���߶Σ��ж��߶ε��е��Ƿ��ڱ߽��ڲ�
 * 3.������ڲ���洢����֮������
 * 4.�жϵ����߶ε�λ����������
 * b.�޽�����߶��������
 * 1.����޽���
 * 2.����Ƿ��ڱ߽��ڲ�
 * */

public class CrossLine {
	private Boundary boundary;// �����
	private List<PointDis> PLine;// ��Ž���line�����ľ���
	private List<Line> lineBoundary;// ��Ŷ���εı�
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
			//����line������ÿ���߽�Ľ���
			getMarkLine(line, lineBoundary.get(i));
		}
		//���޽���
		if (PLine.isEmpty()) {
			//����߶ζ˵��ڶ�����ڲ�
			if (PtInPolygon(line.startPoint)) {
				//��ʾ���߶�
				g2.drawLine(line.startPoint.x, line.startPoint.y,
						line.endPoint.x, line.endPoint.y);
				TestCase.inBoundaryCount++;
			} else
				TestCase.outBoundaryCount++;
		} else {
			//����PLineÿ��Ԫ�ص�distenceֵ ��С��������
			Collections.sort(PLine);
			// ��line.startPoint����Ľ�����PLine�е�����
			int indexMin = 0;
			int numberP = PLine.size();
			Point point;
			//��������뽻�㹹�ɵ��߶��Ƿ��ڶ�����ڲ�
			solvePoint(line.startPoint, indexMin);
			for (int i = 0; i < numberP - 1; i++) {	
				//��PLine������ȡ������
				int minD = indexMin;
				indexMin = (indexMin + 1) % numberP;
				//�ж�ȡ���������Ƿ���ͬ 
				if (!Equal(PLine.get(minD).point, PLine.get(indexMin).point)) {	
					//�����߶��е�
					point = getMid(PLine.get(minD).point,
							PLine.get(indexMin).point);
					//�ж��߶��е��Ƿ��ڶ�����ڲ���������ڲ�������ʾ���߶�
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
			//�����յ��뽻�㹹�ɵ��߶��Ƿ��ڶ�����ڲ�
			solvePoint(line.endPoint, indexMin);
		}
		TestCase.pointCount += PLine.size();
		PLine.clear();
	}

	/**
	 * �ж����߶�λ�ù�ϵ����������㣨������ڣ���
	 * 
	 * @param line
	 * @param lineB
	 */
	private void getMarkLine(Line line, Line lineB) {
		// �߶���β���غ�
		if (!Equal(line.startPoint, line.endPoint)
				|| !Equal(lineB.startPoint, lineB.endPoint)) {
			int mul = (lineB.endPoint.y - lineB.startPoint.y)
					* (line.endPoint.x - line.startPoint.x)
					- (lineB.endPoint.x - lineB.startPoint.x)
					* (line.endPoint.y - line.startPoint.y);// ���������ƽ��Ϊ0
			//�粻ƽ��
			if (mul != 0) {

				int markOnLineS = pointONline(line.startPoint, lineB);
				int markOnLineE = pointONline(line.endPoint, lineB);
				int markOnLineSB = pointONline(lineB.startPoint, line);
				int markOnLineEB = pointONline(lineB.endPoint, line);
				
				//�˵�����һ�߶��� ��ö˵�Ϊ����
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
				//����߶��ཻ
				else if (markOnLineS * markOnLineE == -1
						&& intersect(line.startPoint, line.endPoint,
								lineB.startPoint, lineB.endPoint)) {
					
					//���ȷֵ㷨���㽻��
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

	private boolean Equal(Point p1, Point p2)// �������
	{
		if (p1.x == p2.x && p1.y == p2.y) {
			return true;
		} else {
			return false;
		}
	}

	private void solvePoint(Point point, int index) {
		// �������Ͷ˵��غ�
		if (Equal(point, PLine.get(index).point))
		{
			return;
		}
		//����˵����ڲ������߶����ڲ�
		if (PtInPolygon(point)) {
			// lineClip.add(new Line(point, PLine.get(index).point));
			g2.drawLine(point.x, point.y, PLine.get(index).point.x,
					PLine.get(index).point.y);
		}
	}

	private boolean PtInPolygon(Point p) {
		//�����Ե�pΪ�˵㣬ˮƽ���������ߣ������εĽ������
		int nCross = 0;	//�������
		int nCount = boundary.vertexs.size() - 1;
		for (int i = 0; i < nCount; i++) {
			//����ÿ����������ߵĽ������
			Point p1 = boundary.vertexs.get(i);
			Point p2 = boundary.vertexs.get((i + 1) % nCount);
			//�ų����ཻ
			if (p1.y == p2.y || p.y < Math.min(p1.y, p2.y)
					|| p.y >= Math.max(p1.y, p2.y)) {
				continue;
			}
			
			//���㽻�������
			double x = (double) ((p.y - p1.y) * (p2.x - p1.x))
					/ (double) (p2.y - p1.y) + p1.x;
			//������������Ҳ�
			if (x > p.x) {
				nCross++;
			}
		}
		//��Ϊ�����򷵻�true
		return (nCross % 2 == 1);
	}

	private Point getMid(Point pointA, Point pointB) {
		//�����߶�AB���е�
		Point point = new Point((pointA.x + pointB.x) / 2,
				(pointA.y + pointB.y) / 2);
		return point;
	}

	private int fArea(Point p1, Point p2, Point p3) {
		//�����ɵ�p1\p2\p3���ɵ������ε����
		return Math.abs((p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y)
				* (p3.x - p1.x));
	}

	private int pointONline(Point point, Line line) {
		/*
		 * �жϵ���ֱ�ߵ�λ�ù�ϵ����� ��-1�� ���Ҳࣨ1�� �����ϣ�0�� ���ӳ��ߺͷ��ӳ����ϣ�2��
		 */
		//�߶�line��Χ
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
		if (E < 0) {// E>0����࣬E=0������
			return -1;
		} else if (E > 0) {
			return 1;
		} else {
			if (point.x < xMin || point.x > xMax || point.y < yMin
					|| point.y > yMax) {// ������lineΪ�Խ��ߵľ����ⲿ
				return 2;
			} else {
				return 0;
			}

		}
	}

	// aa, bbΪһ���߶����˵� cc, ddΪ��һ���߶ε����˵� �ཻ����true, ���ཻ����false
	private boolean intersect(Point aa, Point bb, Point cc, Point dd) {
		//ȷ���߶ξ������غ�
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
		//ȷ���߶ζ˵�����һ�߶�����
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
