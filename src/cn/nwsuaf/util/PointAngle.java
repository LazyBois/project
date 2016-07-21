package cn.nwsuaf.util;

/**
 * @Title ������ ��װһ�����࣬�����õ�������ƫ���
 * @Description ʵ��Comparable�ӿ���ҪΪ�˰�װƫ��Ǵ�С����
 * @author ������
 * @date 2015��5��27�� ����11:32:10
 */
public class PointAngle implements Comparable<PointAngle> {
	// ���ڼ�¼�������
	public Point P;
	// ���ڼ�¼���ƫ���
	public int angle;

	public PointAngle() {

	}

	public PointAngle(Point P, int centerX, int centerY) {
		this.P = P;
		angle = turnAngle(centerX, centerY);
	}

	/**
	 * ����Pת�����ԣ�x,y��Ϊԭ���ƫ��ǽǶȣ�0-360��
	 * 
	 * @param x
	 *            ��ĺ�����
	 * @param y
	 *            ���������
	 * @return ƫ��Ǵ�С
	 */
	public int turnAngle(int x, int y) {
		int Angle = (int) (Math.atan2(y - P.y, P.x - x) / Math.PI * 180);

		return Angle >= 0 ? Angle : Angle + 360;

	}

	public int compareTo(PointAngle p) {
		return p.angle - this.angle;
	}
}
