package cn.nwsuaf.util;

public class Line {
	public Point startPoint;
	public Point endPoint;

	public Line() {
		startPoint = new Point();
		endPoint = new Point();
	}

	public Line(Point sPoint, Point ePoint) {
		this.startPoint = sPoint;
		this.endPoint = ePoint;
	}
}
