package cn.nwsuaf.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cn.nwsuaf.clip.CrossCircle;
import cn.nwsuaf.clip.CrossLine;
import cn.nwsuaf.read.TestCase;
import cn.nwsuaf.util.Circle;
import cn.nwsuaf.util.Line;
import cn.nwsuaf.util.Point;

@SuppressWarnings("serial")
public class NewJFrame extends javax.swing.JFrame {
	public static int INFO_HEIGHT = 60;
	public static final String TESTDATA_XML1 = "TestData1.xml";
	public static final String TESTDATA_XML2 = "TestData2.xml";
	private JMenuBar jMenuBar1;
	private JButton jButton1;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JMenuItem jMenuItem5;
	private JMenuItem jMenuItem4;
	private JMenuItem jMenuItem3;
	private JMenuItem jMenuItem2;
	private JMenuItem jMenuItem1;
	private JMenu jMenu2;
	private JMenu jMenu1;
	private DrawPanel drawPanel;
	private ClipPanel clipPanel;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NewJFrame inst = new NewJFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public NewJFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			this.setTitle("Demo_ClipView_Java");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setLayout(null);
			this.setResizable(false);
			this.setJMenuBar(jMenuBar1);
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenu1.setText("���ܲ���");
					{
						jMenuItem1 = new JMenuItem();
						jMenu1.add(jMenuItem1);
						jMenuItem1.setText("TestCase1");
						jMenuItem1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								DrawTestCase(TESTDATA_XML1, "1");
							}
						});
					}
					{
						jMenuItem2 = new JMenuItem();
						jMenu1.add(jMenuItem2);
						jMenuItem2.setText("TestCase2");
						jMenuItem2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								DrawTestCase(TESTDATA_XML1, "2");
							}
						});
					}
					{
						jMenuItem3 = new JMenuItem();
						jMenu1.add(jMenuItem3);
						jMenuItem3.setText("TestCase3");
						jMenuItem3.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								DrawTestCase(TESTDATA_XML1, "3");
							}
						});
					}
					{
						jMenuItem4 = new JMenuItem();
						jMenu1.add(jMenuItem4);
						jMenuItem4.setText("TestCase4");
						jMenuItem4.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								DrawTestCase(TESTDATA_XML1, "4");
							}
						});
					}
				}
				{
					jMenu2 = new JMenu();
					jMenuBar1.add(jMenu2);
					jMenu2.setText("Ч�ʲ���");
					{
						jMenuItem5 = new JMenuItem();
						jMenu2.add(jMenuItem5);
						jMenuItem5.setText("TestCase5");
						jMenuItem5.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								DrawTestCase(TESTDATA_XML2, "5");
							}
						});
					}
				}
			}
			pack();
			this.setSize(TestCase.CANVAS_WIDTH + 6, TestCase.CANVAS_HEIGHT
					+ INFO_HEIGHT + this.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ShowChangesAfterCliped() {
		jButton1.setEnabled(false);
		jLabel1.setText("");
		jLabel2.setText("ͼ�βü���ϣ���ʱ��"
				+ String.format("%.3f", TestCase.useTime / 1000) + " �룡");
	}

	private ActionListener btnListener = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			TestCase.isUseMonitor = true;
			getContentPane().remove(drawPanel);
			clipPanel = new ClipPanel();
			clipPanel.setBounds(0, 0, TestCase.CANVAS_WIDTH,
					TestCase.CANVAS_HEIGHT);
			getContentPane().add(clipPanel);
			getContentPane().repaint();
		}
	};

	private void DrawTestCase(String xmlName, String caseID) {
		getContentPane().removeAll();
		getContentPane().setBounds(0, 25, TestCase.CANVAS_WIDTH,
				TestCase.CANVAS_HEIGHT);
		jButton1 = new JButton();
		jButton1.setBounds(50, TestCase.CANVAS_HEIGHT + 10, 100, 30);
		getContentPane().add(jButton1);
		jButton1.setText("�ü���ʾ");
		jButton1.addActionListener(btnListener);
		jButton1.setEnabled(false);

		jLabel1 = new JLabel();
		jLabel1.setBounds(160, TestCase.CANVAS_HEIGHT + 15, 180, 20);
		jLabel1.setText("ͼ�λ�����...");
		getContentPane().add(jLabel1);
		getContentPane().validate();

		TestCase.ClearTestCaseData();
		try {
			if (!TestCase.LoadTestCaseData(xmlName, caseID)) {
				JOptionPane.showMessageDialog(null, "��ȡ���ݳ���",
						"Demo_ClipView_Java", JOptionPane.ERROR_MESSAGE);
				jLabel1.setText("");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "��ȡ���ݳ���",
					"Demo_ClipView_Java", JOptionPane.ERROR_MESSAGE);
			jLabel1.setText("");
			e.printStackTrace();
			return;
		}

		drawPanel = new DrawPanel();
		drawPanel
				.setBounds(0, 0, TestCase.CANVAS_WIDTH, TestCase.CANVAS_HEIGHT);
		getContentPane().add(drawPanel);

		if (TestCase.hasOutCanvasData) {
			jLabel1.setText("���ڳ����߽����ݣ�����");
		} else {
			jButton1.setEnabled(true);
			jLabel1.setText("ͼ�λ�����ϣ�");

			jLabel2 = new JLabel();
			jLabel2.setBounds(160, TestCase.CANVAS_HEIGHT + 5, 600, 15);
			jLabel2.setText("");
			getContentPane().add(jLabel2);
			jLabel3 = new JLabel();
			jLabel3.setBounds(160, TestCase.CANVAS_HEIGHT + 20, 600, 40);
			jLabel3.setText("");
			getContentPane().add(jLabel3);
		}
		getContentPane().validate();
	}

	private class DrawPanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// ���û�������ɫ
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, TestCase.CANVAS_WIDTH, TestCase.CANVAS_HEIGHT);

			// ����
			g2.setColor(Color.GREEN);
			for (int i = 0; i < TestCase.lines.size(); i++) {
				Line line = TestCase.lines.get(i);
				g2.drawLine(line.startPoint.x, line.startPoint.y,
						line.endPoint.x, line.endPoint.y);
			}

			// ��Բ
			g2.setColor(Color.BLUE);
			for (int j = 0; j < TestCase.circles.size(); j++) {
				Circle circle = TestCase.circles.get(j);
				Ellipse2D ellipse = new Ellipse2D.Double();
				ellipse.setFrameFromCenter(circle.center.x, circle.center.y,
						circle.center.x + circle.radius, circle.center.y
								+ circle.radius);
				g2.draw(ellipse);
			}

			// �������
			g2.setColor(Color.RED);
			int nPoints = TestCase.boundary.vertexs.size() + 1;
			int[] xPoints = new int[nPoints];
			int[] yPoints = new int[nPoints];
			for (int k = 0; k < TestCase.boundary.vertexs.size(); k++) {
				Point point = TestCase.boundary.vertexs.get(k);
				if (k == 0) {
					xPoints[nPoints - 1] = point.x;
					yPoints[nPoints - 1] = point.y;
				}
				xPoints[k] = point.x;
				yPoints[k] = point.y;
			}
			g2.drawPolyline(xPoints, yPoints, nPoints);
		}
	}

	private class ClipPanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, TestCase.CANVAS_WIDTH, TestCase.CANVAS_HEIGHT);

			TestCase.BeginMonitor();
			// TODO �ڴ˴���ɲü��㷨�Ͳü�����ʾ���򣬲��޸���Ҫ��ʾ��ͼ����Ϣ
			int bvn = TestCase.boundary.vertexs.size() - 1;
			// ���ڴ�Ŷ���θ��ߵ�λ�����ͳ���(�����ظ����㣬Ӱ��Ч��)
			float[][] bvxy = new float[bvn][3];
			for (int i = 0; i < bvn; i++) {
				Point startPoint = TestCase.boundary.vertexs.get(i);
				Point endPoint = TestCase.boundary.vertexs.get(i + 1);
				// ����θ��ߵĳ���
				float fDis = (float) Math.sqrt((endPoint.x - startPoint.x)
						* (endPoint.x - startPoint.x)
						+ (endPoint.y - startPoint.y)
						* (endPoint.y - startPoint.y));
				// ������ߵĵ�λ��������
				bvxy[i][0] = (endPoint.x - startPoint.x) / fDis;
				bvxy[i][1] = (endPoint.y - startPoint.y) / fDis;
				bvxy[i][2] = fDis;
			}

			g2.setColor(Color.GREEN);
			CrossLine cl = new CrossLine(TestCase.boundary, g2);
			// fro lines
			for (int k = 0, m = TestCase.lines.size(); k < m; k++)
				cl.getLineClip(TestCase.lines.get(k));

			g2.setColor(Color.BLUE);
			CrossCircle c2 = new CrossCircle(TestCase.boundary, g2);

			for (int k = 0, n = TestCase.circles.size(); k < n; k++)
				c2.getCircleClip(TestCase.circles.get(k), bvxy);

			// �������
			g2.setColor(Color.RED);
			int[] xPoints = new int[bvn + 2];
			int[] yPoints = new int[bvn + 2];
			for (int k = 0, n = bvn + 1; k < n; k++) {
				Point point = TestCase.boundary.vertexs.get(k);
				if (k == 0) {
					xPoints[bvn + 1] = point.x;
					yPoints[bvn + 1] = point.y;
				}
				xPoints[k] = point.x;
				yPoints[k] = point.y;
			}
			g2.drawPolyline(xPoints, yPoints, bvn + 2);

			TestCase.EndMonitor();
			ShowChangesAfterCliped();
			jLabel3.setText("<html>��"
					+ (TestCase.lines.size() + TestCase.circles.size())
					+ "��ͼ�Σ�1���ü��߽磬����"
					+ TestCase.inBoundaryCount
					+ "���ڱ߽��ڲ���"
					+ (TestCase.lines.size() + TestCase.circles.size()
							- TestCase.inBoundaryCount - TestCase.outBoundaryCount)
					+ "����߽��ཻ(����" + TestCase.pointCount + "������)��"
					+ TestCase.outBoundaryCount
					+ "��λ�ڱ߽��ⲿ(ע��λ�ڱ߽��ϵ��߶ι����ⲿ)��<html/>");
			TestCase.pointCount = 0;
			TestCase.inBoundaryCount = 0;
			TestCase.outBoundaryCount = 0;
		}
	}
}
