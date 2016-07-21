package cn.nwsuaf.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title 多边形类
 * @Description 记录着多边形的各顶点坐标
 * @author 刘永浪
 * @date 2015年5月27日 下午11:35:04
 */
public class Boundary {
	public List<Point> vertexs;

	public Boundary() {
		vertexs = new ArrayList<Point>();
	}
}
