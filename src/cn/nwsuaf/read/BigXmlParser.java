package cn.nwsuaf.read;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.nwsuaf.util.Circle;
import cn.nwsuaf.util.Line;
import cn.nwsuaf.util.Point;

public class BigXmlParser extends DefaultHandler {
	private Circle circle = null;
	private Line line = null;
	private boolean isNeedCase = false;
	private boolean isLine = false;
	private boolean isCircle = false;
	private String curTag = "";

	private boolean isPointOk = true;
	private boolean isGetCase = false;

	private void XmlValueToPoint(String value, Point point) {
		String[] coords = value.split(",");
		if (coords.length != 2) {
			isPointOk = false;
			return;
		}
		point.x = Integer.parseInt(coords[0].trim());
		point.y = TestCase.CANVAS_HEIGHT - Integer.parseInt(coords[1].trim());// 调整CAD与窗口显示的坐标系一致
	}

	public boolean IsReadOk() {
		if (isPointOk && isGetCase)
			return true;
		return false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		curTag = qName;
		if (qName.equalsIgnoreCase("TestCase")) {
			for (int i = 0; i < attributes.getLength(); i++) {
				String attrName = attributes.getQName(i);
				if (attrName.equalsIgnoreCase("ID")
						&& TestCase.CaseID.equalsIgnoreCase(attributes
								.getValue(i))) {
					isNeedCase = true;
					isGetCase = true;
				}
			}
		}
		if (!isNeedCase)
			return;
		if (qName.equalsIgnoreCase("Entity")
				|| qName.equalsIgnoreCase("Boundary")) {
			for (int i = 0; i < attributes.getLength(); i++) {
				String attrName = attributes.getQName(i);
				if (attrName.equalsIgnoreCase("Type")) {
					String type = attributes.getValue(i);
					if (type.equalsIgnoreCase("Line")) {
						line = new Line();
						isLine = true;
					} else if (type.equalsIgnoreCase("Circle")) {
						circle = new Circle();
						isCircle = true;
					}
				}

			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (!isNeedCase || curTag.isEmpty())
			return;
		String value = new String(ch, start, length);
		if ("StartPoint".equalsIgnoreCase(curTag)) {
			XmlValueToPoint(value, line.startPoint);
		} else if ("EndPoint".equalsIgnoreCase(curTag)) {
			XmlValueToPoint(value, line.endPoint);
		} else if ("CenterPoint".equalsIgnoreCase(curTag)) {
			XmlValueToPoint(value, circle.center);
		} else if ("Radius".equalsIgnoreCase(curTag)) {
			circle.radius = Integer.parseInt(value);
		} else if ("Vertex".equalsIgnoreCase(curTag)) {
			Point point = new Point();
			XmlValueToPoint(value, point);
			TestCase.boundary.vertexs.add(point);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		curTag = "";
		if (!isNeedCase)
			return;
		if (qName.equalsIgnoreCase("TestCase"))
			isNeedCase = false;
		else if (qName.equalsIgnoreCase("Entity") && isLine) {
			isLine = false;
			if (TestCase.IsLineOutCanvas(line)) {
				TestCase.hasOutCanvasData = true;
				return;
			}
			TestCase.lines.add(line);
		} else if (qName.equalsIgnoreCase("Entity") && isCircle) {
			isCircle = false;
			if (TestCase.IsCircleOutCanvas(circle)) {
				TestCase.hasOutCanvasData = true;
				return;
			}
			TestCase.circles.add(circle);
		} else if (qName.equalsIgnoreCase("Boundary")
				&& TestCase.IsBoundaryOutCanvas(TestCase.boundary)) {
			TestCase.hasOutCanvasData = true;
			TestCase.boundary.vertexs.clear();
		}
	}

}
