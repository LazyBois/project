package cn.nwsuaf.util;

/**
 * @Title 描述： 封装一个点类，包括该点的坐标和偏向角
 * @Description 实现Comparable接口主要为了安装偏向角大小排序
 * @author 刘永浪
 * @date 2015年5月27日 下午11:32:10
 */
public class PointAngle implements Comparable<PointAngle> {
	// 用于记录点的坐标
	public Point P;
	// 用于记录点的偏向角
	public int angle;

	public PointAngle() {

	}

	public PointAngle(Point P, int centerX, int centerY) {
		this.P = P;
		angle = turnAngle(centerX, centerY);
	}

	/**
	 * 将点P转化成以（x,y）为原点的偏向角角度（0-360）
	 * 
	 * @param x
	 *            点的横坐标
	 * @param y
	 *            点的纵坐标
	 * @return 偏向角大小
	 */
	public int turnAngle(int x, int y) {
		int Angle = (int) (Math.atan2(y - P.y, P.x - x) / Math.PI * 180);

		return Angle >= 0 ? Angle : Angle + 360;

	}

	public int compareTo(PointAngle p) {
		return p.angle - this.angle;
	}
}
