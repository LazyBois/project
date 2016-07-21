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
 ����:
 ���ã�������ȡԲ�ڱ߽��ڲ��Ļ���(���ڿ��ǵ�Ч�����⣬���Լ��������ͽ��л�ͼ��û��ʹ�������洢���ݽṹ)
 �㷨��
 1.�Ȼ�ȡԲ��߽�����н��㣬֮��������򣬰��յ��ڵ�ǰԲ�еĽǶȣ���С�����ţ�Ϊ�˱�֤��ȡ��ȷ���ߣ�
 2.���û�н��㣬����ֻ��һ�����㣨�е㲻�������㣩�����ж�Բ���Ƿ��ڱ߽��ڲ������򷵻�true����֮Ϊfalse
 3.����н���>=2��һ�λ�ȡ�������㣬��ȡ����������Ļ����е㣨�����ĽǶ�+�����Ƕȣ�/2 Ȼ��ת����ֱ������)
 �жϸõ�������Ļ����Ƿ��ڶ�����ڲ�
 4.������ڲ�����ʼ�Ƕ��������Ƕȴ���circleClip���洢�ü���Ļ��ߣ� ��Ϊ�˵���darwArc������
 */
public class CrossCircle {
	// �����
	private final float EPS = 0.00001f;
	// �����
	private Boundary boundary;
	// ��Ž���
	public List<PointAngle> points;
	// ��ǰ��Բ
	public Circle circle;
	private Graphics2D g2;

	public CrossCircle(Boundary boundary, Graphics2D g2) {
		points = new ArrayList<PointAngle>();
		this.boundary = boundary;
		this.g2 = g2;
	}

	/**
	 * �ü�Բcircle
	 * 
	 * @param circle
	 *            ��ǰԲ
	 * @param bvxy
	 *            �����Ҫ�ظ����������
	 */
	public void getCircleClip(Circle circle, float[][] bvxy) {
		this.circle = circle;
		// �����������б���Բ�Ľ���
		for (int i = 0, n = boundary.vertexs.size(); i < n - 1; i++) {
			LineInterCircle(boundary.vertexs.get(i),
					boundary.vertexs.get(i + 1), bvxy[i]);
		}

		// ��¼����ĸ���
		int len = points.size();
		// ������������
		TestCase.pointCount += len;

		// ��������������ʱ�ж�Բ�ڶ�����ڲ������ⲿ
		if (len == 0 || len == 1) {
			// ���Բ���ڶ�����ڣ���ô�������������
			// ����α�Բ��Χ����Բ������ΰ�Χ
			if (PtInPolygon(circle.center)) {
				// ����εĵ�һ����������
				int x = boundary.vertexs.get(0).x - circle.center.x;
				int y = boundary.vertexs.get(0).y - circle.center.y;
				// �������εĶ�����Բ����˵��Բ������ΰ�Χ
				if (x * x + y * y >= circle.radius * circle.radius) {
					// ������Բ
					g2.drawArc(circle.center.x - circle.radius, circle.center.y
							- circle.radius, 2 * circle.radius,
							2 * circle.radius, 0, 360);
					// �����ڶ�����ڲ�ͼ�ε�����
					TestCase.inBoundaryCount++;
					// circleClip.add(new Arc(0, 360, circle));
				}
				// �������α�Բ��Χ��ô����Ҫ������Բ��ֻ������ڶ�����ⲿͼ�ε�����
				else
					TestCase.outBoundaryCount++;
			}
			// ���Բ���ڶ�����ⲿ����Ҫ������Բ��ֻ������ڶ�����ⲿͼ�ε�����
			else
				TestCase.outBoundaryCount++;
			// ����¼����ļ������(�ü���Ϊ����)
			points.clear();
		} else {
			// ����������Բ�ཻ���Ͷ����Խ�������(����ƫ�����С����)
			Collections.sort(points);
			// ������ڶ�����ڲ�������
			for (int i = 0; i < len; i++)
				arcOn(points.get(i), points.get((i + 1) % len));
			points.clear();
		}
	}

	/**
	 * ����ڶ�����ڲ�������(��ʱ�뷽��)������circleClip��
	 * 
	 * @param startPoint
	 *            ������ʼ��
	 * @param endPoint
	 *            �����յ�
	 */
	private void arcOn(PointAngle startPoint, PointAngle endPoint) {
		// ��������ƫ���
		int startAngle = startPoint.angle;
		// �����е�ƫ���
		int endAngle = endPoint.angle;
		int arcAngle = startAngle - endAngle;
		arcAngle = arcAngle >= 0 ? -arcAngle : -360 - arcAngle;
		int midArcAngle = arcAngle / 2;
		// �õ������е�ƫ���
		int a = startAngle + midArcAngle;

		Point mid = new Point();
		// ���������е�����
		mid.x = (int) (Math.cos(Math.toRadians(a)) * circle.radius + circle.center.x);
		mid.y = circle.center.y
				- (int) (Math.sin(Math.toRadians(a)) * circle.radius);

		// ����е��ڶ�����ڲ�����ô�ͻ����û��ߣ����򲻽�����������
		if (PtInPolygon(mid))
			g2.drawArc(circle.center.x - circle.radius, circle.center.y
					- circle.radius, 2 * circle.radius, 2 * circle.radius,
					startAngle, arcAngle);
		// circleClip.add(new Arc(startAngle, arcAngle, circle));
	}

	/**
	 * �����������߶���Բ�Ľ���,������points��
	 * 
	 * @param startPoint
	 *            �߶���ʼ��
	 * @param endPoint
	 *            �߶���ֹ��
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
			// �������Χ���򽫽�����ӵ�������
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
	 * �жϵ������ε�λ�ù�ϵ(�ڲ�/�ⲿ)
	 * 
	 * @param point
	 *            ���жϵĵ�
	 * @return ����õ�����ڶ�����ڲ��򷵻�true���򷵻�false
	 */
	private boolean PtInPolygon(Point point) {
		// ��¼�õ����������������ν������
		int nCross = 0;
		int nCount = boundary.vertexs.size() - 1;
		for (int i = 0; i < nCount; i++) {
			Point p1 = boundary.vertexs.get(i);
			Point p2 = boundary.vertexs.get((i + 1));
			// �������������ĳ����ƽ�л���û�н�����continue
			if (p1.y == p2.y || point.y < Math.min(p1.y, p2.y)
					|| point.y >= Math.max(p1.y, p2.y)) {
				continue;
			}

			// ��������㴦�ĺ�����
			double x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
			// �����������������Ч�����򽻵����+1
			if (x > point.x) {
				nCross++;
			}
		}
		// ������������������ô�õ���ڶ�����ڲ��������ڶ�����ⲿ
		return (nCross % 2 == 1);
	}

}
