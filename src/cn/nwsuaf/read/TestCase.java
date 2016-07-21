package cn.nwsuaf.read;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.nwsuaf.util.Boundary;
import cn.nwsuaf.util.Circle;
import cn.nwsuaf.util.Line;
import cn.nwsuaf.util.Point;

public class TestCase {

	public static final int CANVAS_WIDTH = 800;
	public static final int CANVAS_HEIGHT = 600;

	public static List<Line> lines = new ArrayList<Line>();
	public static List<Circle> circles = new ArrayList<Circle>();
	public static Boundary boundary = new Boundary();

	public static boolean hasOutCanvasData = false;
	private static double startTime = 0;
	// 交点个数
	public static int pointCount = 0;
	// 在多边形内部的图形个数
	public static int inBoundaryCount = 0;
	public static int outBoundaryCount = 0;
	public static double useTime = 0;
	public static String CaseID = "";
	public static boolean isUseMonitor = true;
	public static Map<Thread, StackTraceElement[]> beginThreadMaps = null;

	public static void ClearTestCaseData() {
		CaseID = "";
		hasOutCanvasData = false;
		lines.clear();
		circles.clear();
		boundary.vertexs.clear();
	}

	public static boolean IsPointOutCanvas(Point point) {
		if (point.x < 0 || point.y < 0 || point.x > CANVAS_WIDTH
				|| point.y > CANVAS_HEIGHT) {
			return true;
		}
		return false;
	}

	public static boolean IsLineOutCanvas(Line line) {
		if (IsPointOutCanvas(line.startPoint)
				|| IsPointOutCanvas(line.endPoint)) {
			return true;
		}
		return false;
	}

	public static boolean IsCircleOutCanvas(Circle circle) {
		if (IsPointOutCanvas(circle.center)) {
			return true;
		}
		if ((circle.center.x - circle.radius) < 0
				|| (circle.center.x + circle.radius) > CANVAS_WIDTH
				|| (circle.center.y - circle.radius) < 0
				|| (circle.center.y + circle.radius) > CANVAS_HEIGHT) {
			return true;
		}
		return false;
	}

	public static boolean IsBoundaryOutCanvas(Boundary boundary) {
		for (int i = 0; i < boundary.vertexs.size(); i++) {
			if (IsPointOutCanvas(boundary.vertexs.get(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean LoadTestCaseData(String xmlPath, String caseID)
			throws Exception {
		CaseID = caseID;
		BigXmlParser bigXmlParser = new BigXmlParser();
		SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
		InputStream in = new FileInputStream(xmlPath);
		sax.parse(in, bigXmlParser);
		in.close();
		return bigXmlParser.IsReadOk();
	}

	public static void BeginMonitor() {
		if (!isUseMonitor)
			return;
		beginThreadMaps = Thread.getAllStackTraces();
		Date startDate = new Date();
		startTime = startDate.getTime();
	}

	public static void EndMonitor() {
		if (!isUseMonitor)
			return;
		Date endDate = new Date();
		double endTime = endDate.getTime();
		useTime = endTime - startTime;
		isUseMonitor = false;
		Map<Thread, StackTraceElement[]> endThreadMaps = Thread
				.getAllStackTraces();
		for (Thread endTh : endThreadMaps.keySet()) {
			if (!beginThreadMaps.containsKey(endTh) && endTh.isAlive()) {
				JOptionPane.showMessageDialog(null, "新建线程仍在运行:"
						+ endTh.getClass().getName(), "线程提示",
						JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
	}

}
