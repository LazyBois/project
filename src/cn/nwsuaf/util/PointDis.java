package cn.nwsuaf.util;

public class PointDis implements Comparable<PointDis> {
	public Point point;
	public int distence;

	public PointDis() {

	}

	public PointDis(Point P, int x, int y) {
		this.point = P;
		distence = (P.x - x) * (P.x - x) + (P.y - y) * (P.y - y);
	}

	public int compareTo(PointDis PD) {
		// TODO Auto-generated method stub
		return this.distence - PD.distence;
	}
}
